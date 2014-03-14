package group.cs169.cookingbuddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.os.AsyncTask;

public class HTTPTask extends AsyncTask<ArrayList<Object>, Void, String> {
	

	public AsyncResponse caller = null;
	public String method;
	
	@Override
	protected String doInBackground(ArrayList<Object>... container) {
		HttpURLConnection urlConn = null;
		String result = "-100";
		/*The input must have the ArrayList formatted in a proper manner
		 *The JSONObject MUST be inserted BEFORE the path name. 
		 */
		ArrayList<Object> holder = container[0];
		JSONObject param = (JSONObject) holder.get(0);
		String path = (String) holder.get(1);
		method = path;
		try {
			URL url;
			String address = Constants.ADD_INGREDIENT_URL+path;
			url = new URL (address);
			//Create the connection
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
			urlConn.setRequestMethod("POST");
			urlConn.setChunkedStreamingMode(0);
			urlConn.setRequestProperty("Content-Type","application/json");   
			urlConn.connect();  
			//Send the POST request to the back-end
			byte[] outputBytes = param.toString().getBytes("UTF-8");
			OutputStream os = urlConn.getOutputStream();
			os.write(outputBytes);
			os.flush();
			os.close();
			//Read the incoming JSON from the back-end
			StringBuilder builder = new StringBuilder();
			InputStream is = urlConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
	        while ((line = reader.readLine()) != null) {
	            builder.append(line);
	        }
	        result = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			if(urlConn !=null)  urlConn.disconnect(); 
		}
		return result;	
	}

	protected void onPostExecute(String result) {
		 caller.processFinish(result,method);
	}
	
	public interface AsyncResponse {
	    void processFinish(String output, String callingMethod);
	}
	
}

