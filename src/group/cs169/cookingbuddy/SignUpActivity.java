package group.cs169.cookingbuddy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity implements AsyncResponse{
	
	public EditText mUser;
	public EditText mPass1;
	public EditText mPass2;
	private HTTPTask httpTask;
	protected final static String USERNAME = "user";
	protected final static String PASSWORD = "password";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mUser = (EditText) findViewById(R.id.userfield);
		mPass1 = (EditText) findViewById(R.id.passfield);
		mPass2 = (EditText) findViewById(R.id.reenterpassfield);
		
		httpTask = new HTTPTask();
		httpTask.caller = this;

	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void signup(View view){
		
		String username = mUser.getText().toString().trim();
		String password = mPass1.getText().toString().trim();
		String password2 = mPass2.getText().toString().trim();
		
		
		if(!password.equals(password2)){
			TextView view1 = (TextView) findViewById(R.id.signupmessage);
			view1.setText("Passwords do not Match!");
		}
		else{
			
			JSONObject json = new JSONObject();
			try {
				json.put(USERNAME,username);
				json.put(PASSWORD,password);
				ArrayList<Object> container = new ArrayList<Object>();
				//The JSONObject and path must be added in this order! JSONObject first, path second
				container.add(json);
				container.add(Constants.ADD_USER_URL);
				httpTask.execute(container);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		//What is the errorCode that is being sent back from the backend? Is it output?
		if (output == "1"){
			Intent i = new Intent(this, HomeActivity.class);
	         startActivity(i);
		}
		// TODO Auto-generated method stub
		
	}

}
