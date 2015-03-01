package diario.alimentare.coda.example;

import org.apache.http.Header;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import diario.alimentare.coda.queue.ServerSideAdapter;

public class DataObjectServerSideAdapter implements
		ServerSideAdapter<DataObject> {
	final static String URL_SERVER = "url server ";

	private class Result {
		boolean send_flag = false;
	}

	@Override
	public boolean send(DataObject obj) throws Exception {
		//Gson gson = new Gson();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String objson = gson.toJson(obj);

		final Result ris = new Result();
		
		System.out.println("SPEDISCO L'OGGETTO ");
		SyncHttpClient c = new SyncHttpClient();
		RequestParams params = new RequestParams();
		params.setUseJsonStreamer(true);
		params.put("json", objson);
		System.out.println("JSOOOOONNNNN " + objson);
		System.out.println("PARAMS : " + params.toString());
		c.get(URL_SERVER, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				ris.send_flag = true;
				System.out.println("SUCCESS");

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				System.out.println("FALIURE");
				arg3.printStackTrace();
			}
		});
		System.out.println("FLAG  ---" + ris.send_flag);
		return ris.send_flag;
	}

}
