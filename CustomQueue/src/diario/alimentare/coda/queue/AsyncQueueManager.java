package diario.alimentare.coda.queue;

import android.content.*;
import android.net.*;
import android.util.*;

import java.io.*;
import java.util.*;

import diario.alimentare.coda.queue.QueueDatabaseHelper.QueueDatabaseHelperObject;

public class AsyncQueueManager<T> {
    private final Context ctx;
    private final ServerSideAdapter<T> serverSideAdapter;
    private final QueueDatabaseHelper<T> helper;

    /**
     * oggetti scartati nel momento della conversione JSON
     */
    private final Object blocco = new Object();
    private SenderThread thread;

    public AsyncQueueManager(Context ctx, ServerSideAdapter<T> serverSideAdapter) {
        this.ctx = ctx;
        this.serverSideAdapter = serverSideAdapter;
        helper = new QueueDatabaseHelper<T>(ctx);

        //avvia il thread
        thread = new SenderThread();
        thread.start();

        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        cm.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
            @Override
            public void onNetworkActive() {
            	Log.d(getClass().getName(), "LISTENER CONNESSIONE DI NUOVO ATTIVA");
                _resumeThread();
            }
        });
    }

    /**
     * lista di attesa
     *
     * @return
     */
    public synchronized List<T> getWaitingQueue() {
        return helper.getAllObject();
    }

    /**
     * aggiunge un nuovo oggetto alla coda
     *
     * @param obj
     */
    public synchronized void submit(T obj) {
        helper.enqueue(obj);
        _resumeThread();
    }

    /**
     * risveglia il thread eventualmente in attesa
     */
    private synchronized void _resumeThread() {
        synchronized (blocco) {
            blocco.notifyAll();
        }
    }


    /**
     * interrompe l'invio, restituendo la lista degli elementi non inviati
     *
     * @return
     */
    public void stop() {
        if (thread != null) {
            thread.exitThread();
            thread = null;
        }
    }

    private boolean isNetworkActive(ConnectivityManager cm) {
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo == null) return false;
        return activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED || activeNetworkInfo.getState() == NetworkInfo.State.CONNECTING;
    }

    private class SenderThread extends Thread {
    	
        private volatile boolean endThread = false;

        public void run() {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            while (!endThread) {
                try {
                     {

                        if (!isNetworkActive(cm)) {
                            Log.d(getClass().getName(), "NETWORK NON ATTIVO");
                            //blocco.wait();
                            Thread.sleep(1000);
                            continue;
                        } else {
                            Log.d(getClass().getName(), "NETWORK ATTIVO " + cm.getActiveNetworkInfo());
                        }
                    }

                    QueueDatabaseHelperObject<T> objxx= helper.getNextObject();
                    if (objxx !=null) {
                        Log.d(getClass().getName(), "PROCESSAMENTO OGGETTO #" + objxx.rowid);
                        T obj = objxx.object;
                        if (serverSideAdapter.send(obj)){
                        	helper.removeObject(objxx.rowid);
                        }else{
                        	Log.d(getClass().getName(), "FAILURE OGGETTO #" + objxx.rowid+" - Server adpter false!");
                        }
                    } else {
                        //oggetti finiti
                        Log.d(getClass().getName(), "CODA VUOTA");
                        synchronized (blocco) {
                            blocco.wait();
                        }

                    }


                } catch (InterruptedException e) {
                    // passa il controllo al while (probabile stop thread)
                    continue;
                } catch (Exception e) {
                    Log.d(getClass().getName(), "ECCEZIONE " + e.getMessage());
                    e.printStackTrace();
                    //eccezione
                    continue;
                }


            }
            Log.d(getClass().getName(), "THREAD TERMINATO");
        }

        /**
         * interrompe il thread
         */
        public void exitThread() {
            Log.d(getClass().getName(), "RICHIESTA TERMINAZIONE THREAD");
            endThread = true;
            synchronized (blocco) {
                blocco.notifyAll();
            }
            interrupt();
        }
    }

}
