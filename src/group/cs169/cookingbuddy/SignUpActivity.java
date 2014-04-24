package group.cs169.cookingbuddy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity implements AsyncResponse{
	
	public EditText mUser;
	public EditText mPass1;
	public EditText mPass2;
	private HTTPTask httpTask;
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mUser = (EditText) findViewById(R.id.userfield);
		mPass1 = (EditText) findViewById(R.id.passwordField);
		mPass2 = (EditText) findViewById(R.id.reenterpassfield);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void signup(View view){
		
		username = mUser.getText().toString().trim();
		String password = mPass1.getText().toString().trim();
		String password2 = mPass2.getText().toString().trim();
		
		
		if(!password.equals(password2)){
			TextView view1 = (TextView) findViewById(R.id.signupmessage);
			view1.setText("Passwords do not Match!");
		}
		else{
			
			JSONObject json = new JSONObject();
			try {
				json.put(Constants.JSON_USERNAME,username);
				json.put(Constants.JSON_PASSWORD,password);
				ArrayList<Object> container = new ArrayList<Object>();
				//The JSONObject and path must be added in this order! JSONObject first, path second
				container.add(json);
				container.add(Constants.ADD_USER_URL);
				httpTask = new HTTPTask();
				httpTask.caller = this;
				httpTask.dialog = new ProgressDialog(this);
				httpTask.callingActivity = Constants.SIGNUP_ACTIVITY;
				httpTask.execute(container);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		String errCode;
		try {
			JSONObject out = new JSONObject(output);
			errCode = out.getString(Constants.JSON_STANDARD_RESPONSE);
			if (errCode.equals(Constants.SUCCESS)) {
				Intent i = new Intent(this, HomeActivity.class);
				SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.JSON_USERNAME, username).commit();
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
			} else {
				Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
