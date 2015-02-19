package diario.alimentare.coda;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import diario.alimentare.coda.example.*;
import diario.alimentare.coda.queue.*;

import java.util.*;

public class MainActivity extends Activity {
    Button send;
    Button refresh;
    ListView list;
    AsyncQueueManager<DataObject> coda;

    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.button_send);
        refresh = (Button) findViewById(R.id.button_refresh);
        list = (ListView) findViewById(R.id.listView);

        coda = new AsyncQueueManager<DataObject>(getApplicationContext(), new DataObjectServerSideAdapter());
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                coda.submit(new DataObject("comando #" + (count++)));
                refresh.performClick();
            }
        });

        refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                List<DataObject> waitingQueue = coda.getWaitingQueue();
               
                
                ArrayAdapter<DataObject> as = new ArrayAdapter<DataObject>(getApplicationContext(), android.R.layout.simple_list_item_1, waitingQueue);
                list.setAdapter(as);

            }
        });
        
        refresh.performClick();

    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
