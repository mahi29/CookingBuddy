package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity implements AsyncResponse {
	
	public String username;
	public String password;
	public EditText userField;
	public EditText passField;
	protected final static String USERNAME = "user";
	protected final static String PASSWORD = "password";
	HTTPTask httpTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		userField = (EditText) findViewById(R.id.userText);
		passField = (EditText) findViewById(R.id.passwordText);
		getActionBar().hide();
		
		
	}

	/** Called when the 'Sign Up' button is clicked from Home Screen */
	@SuppressWarnings("unchecked")
	public void signUp(View v) {
         Intent i = new Intent(this,SignUpActivity.class);
         startActivity(i);
	}
	
	/** Called when the 'Log In' button is clicked from the Home Screen*/
	@SuppressWarnings("unchecked")
	public void logIn(View v) {
		username = userField.getText().toString().trim();
		password = passField.getText().toString().trim();
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.JSON_USERNAME,username);
			json.put(Constants.JSON_PASSWORD,password);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(Constants.LOGIN_USER_URL);
			httpTask = new HTTPTask();
			httpTask.caller = this;
			httpTask.callingActivity = Constants.MAIN_ACTIVITY;
			httpTask.dialog = new ProgressDialog(this);
			httpTask.execute(container);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void logInCallback(String output) {
		JSONObject out;
		try {
			out = new JSONObject(output);
			String errCode = out.getString(Constants.JSON_STANDARD_RESPONSE);
			if (errCode.equals(Constants.SUCCESS)) {
				SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.JSON_USERNAME, username).commit();
				Intent i = new Intent(this, HomeActivity.class);
				startActivity(i);
			} else {
				Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
				e.printStackTrace();
		} 
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		if (output.equals(Constants.ERROR_CODE)){
			String iomsg = "Could not connect to the Internet";
			Toast.makeText(this,iomsg,Toast.LENGTH_SHORT).show();
		} else {
			logInCallback(output);
		}
	}

}
