package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity implements AsyncResponse {

	HTTPTask httpTask;
	public SplashActivity() {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getActionBar().hide();	
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		String username = prefs.getString(Constants.JSON_USERNAME, "");
		Log.d("Splash","User: " + username);
		if (username.equals("")) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			return;
		}
		JSONObject json = new JSONObject();
		try {
			ArrayList<Object> container = new ArrayList<Object>();
			json.put(Constants.JSON_USERNAME,username);
			container.add(json);
			container.add(Constants.VERIFY_URL);
			httpTask = new HTTPTask();
			httpTask.caller = this;
			httpTask.context = this;
	//		httpTask.callingActivity = Constants.MAIN_ACTIVITY;
	//		httpTask.dialog = new ProgressDialog(this);
			httpTask.execute(container);
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	private void goToPage(Class<?> javaClass) {
		Intent i = new Intent(this, javaClass);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}
	
	@Override
	public void processFinish(String output, String callingMethod) {
		JSONObject out;
		try {
			out = new JSONObject(output);
			String verification = out.getString(Constants.JSON_STANDARD_RESPONSE);
			if (verification.equals(Constants.SIGNED_IN)){
				goToPage(HomeActivity.class);
			} else {
				goToPage(MainActivity.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		

	}
}
