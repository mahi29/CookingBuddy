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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class HTTPTask extends AsyncTask<ArrayList<Object>, Void, String> {
	

	public AsyncResponse caller = null;
	public String method;
	public ProgressDialog dialog;
	public String callingActivity;
	public Context context;
	
	@Override
	protected String doInBackground(ArrayList<Object>... container) {
		HttpURLConnection urlConn = null;
		String result = Constants.ERROR_CODE;
		/*The input must have the ArrayList formatted in a proper manner
		 *The JSONObject MUST be inserted BEFORE the path name. 
		 */
		ArrayList<Object> holder = container[0];
		JSONObject param = (JSONObject) holder.get(0);
		String path = (String) holder.get(1);
		method = path;
		try {
			URL url;
			String address = Constants.BASE_URL+path;
			url = new URL (address);
			//Create the connection
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
			urlConn.setRequestMethod("POST");
			urlConn.setChunkedStreamingMode(0);
			urlConn.setRequestProperty("Content-Type","application/json");  
			if (path.equals(Constants.VERIFY_URL)  || path.equals(Constants.LOGOUT_URL)) {
				urlConn.setRequestProperty("Accept", "application/json");
				SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				String cookie = prefs.getString(Constants.COOKIE_PREFS, "defCookie");
				urlConn.setRequestProperty("Cookie",cookie);
			}
			urlConn.connect();  
			//Send the POST request to the back-end
			byte[] outputBytes = param.toString().getBytes("UTF-8");
			OutputStream os = urlConn.getOutputStream();
			os.write(outputBytes);
			os.flush();
			os.close();
			if (path.equals(Constants.LOGIN_USER_URL) || path.equals(Constants.ADD_USER_URL)) {
				context = (Context) caller;
				String cookie = urlConn.getHeaderField("Set-Cookie");
				//Log.d("HTTPTask",cookie);
				SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.COOKIE_PREFS, cookie).commit();
			}
			//Read the incoming JSON from the back-end
			StringBuilder builder = new StringBuilder();
			InputStream is = urlConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
	        while ((line = reader.readLine()) != null) {
	            builder.append(line);
	        }
	        is.close();
	        result = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			if(urlConn !=null)  urlConn.disconnect(); 
		}
		return result;	
	}

	protected void onPostExecute(String result) {
		if(dialog != null && dialog.isShowing()) dialog.dismiss(); 
		caller.processFinish(result,method);
	}
	
	@Override
	protected void onPreExecute() {
		if (dialog != null) {
			String message = "Please wait...";
			if (callingActivity.equals(Constants.SIGNUP_ACTIVITY)) {
				message = "You are being created. Please wait...";
			} else if (callingActivity.equals(Constants.MAIN_ACTIVITY)) {
				message = "Granting permission. Please wait...";
			} else if (callingActivity.equals(Constants.SEARCH_ACTIVITY)) {
				message = "Searching the world for you. Please wait...";
			} else if (callingActivity.equals(Constants.INGREDIENT_ACTIVITY)) {
				message = "Pulling your Ingredient List now. Please wait...";
			}
			dialog.setMessage(message);
			dialog.show();
		}
	}
	
	public interface AsyncResponse {
	    void processFinish(String output, String callingMethod);
	}
	
}

