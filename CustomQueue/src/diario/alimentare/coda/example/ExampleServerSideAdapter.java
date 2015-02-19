package diario.alimentare.coda.example;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.R.bool;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import diario.alimentare.coda.queue.ServerSideAdapter;

/**
 * Created by Matteo on 2/02/15.
 */
public class ExampleServerSideAdapter implements ServerSideAdapter<String> {
	
	
    @Override
    public boolean send(String ob) throws Exception {
        System.out.println("SPEDISCO LA STRINGA in almeno 10 sec... " + ob);
        Thread.sleep(5000);
        System.out.println("INVIATA");
        return true;

        
        
        
    }
}
