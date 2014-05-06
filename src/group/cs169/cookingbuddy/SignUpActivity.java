package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;
import group.cs169.cookingbuddy.MainActivity.RegisterTask;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
	public GoogleCloudMessaging gcm;
	public String regID;
	ProgressDialog dialog;
	SharedPreferences prefs;
	
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
				dialog = new ProgressDialog(this);
				String msg = "You are being created. Please wait...";
				dialog.setMessage(msg);
				dialog.show();
				httpTask = new HTTPTask();
				httpTask.caller = this;
				httpTask.execute(container);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void gcmStuff() {
		gcm = GoogleCloudMessaging.getInstance(this);
		regID = getGCMRegId(this);
		if (regID.isEmpty()) {
			RegisterTask task = new RegisterTask(this);
			task.execute();
		} else {
			sendIdToBackend();
		}
	}
	
	private String getGCMRegId(Context context) {
		String reg = prefs.getString(Constants.GCM_REG_ID, "");
		return reg;
	}
	
	@SuppressWarnings("unchecked")
	private void sendIdToBackend() {
    	JSONObject json = new JSONObject ();
    	ArrayList<Object> container = new ArrayList<Object>();
    	Log.d("MainActivity","GCM Registration ID: " + regID);
		try {
			json.put(Constants.JSON_USERNAME, prefs.getString(Constants.JSON_USERNAME, ""));
			json.put(Constants.GCM_REG_ID, regID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		container.add(json);
		container.add(Constants.ADD_REG_ID_URL);
    	HTTPTask task = new HTTPTask();
    	task.caller = this;
    	task.execute(container);   
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		String errCode;
		try {
			JSONObject out = new JSONObject(output);
			errCode = out.getString(Constants.JSON_STANDARD_RESPONSE);
			if (callingMethod.equals(Constants.ADD_REG_ID_URL)) {
				Log.d("MainActivity",output);
				if (dialog != null && dialog.isShowing()) dialog.dismiss();
				Intent i = new Intent(this, HomeActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
			} else if (errCode.equals(Constants.SUCCESS)) {
				prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.JSON_USERNAME, username).commit();
				gcmStuff();
			} else {
				Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
 	
public class RegisterTask extends AsyncTask<Void, Void, String> {
		
		Context context;
		boolean failed = false;
		
		public RegisterTask (Context context) {
			this.context = context;
		}
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) gcm = GoogleCloudMessaging.getInstance(context);
                regID = gcm.register(Constants.GCM_SENDER_ID);
                prefs.edit().putString(Constants.GCM_REG_ID, regID);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                failed = true;
            }
            return msg;
        }

		@Override
        protected void onPostExecute(String msg) {
        	if (failed) return;
        	sendIdToBackend();
        }
	}

}
