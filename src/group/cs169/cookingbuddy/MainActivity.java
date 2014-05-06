package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AsyncResponse {
	
	public String username;
	public String password;
	public EditText userField;
	public EditText passField;
	protected final static String USERNAME = "user";
	protected final static String PASSWORD = "password";
	public ProgressDialog dialog;
	HTTPTask httpTask;
	public GoogleCloudMessaging gcm;
	public String regID;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		userField = (EditText) findViewById(R.id.userText);
		passField = (EditText) findViewById(R.id.passwordText);
		getActionBar().hide();
		//changing text
		ArrayList<TextView> allItems = new ArrayList();
		allItems.add((TextView) findViewById(R.id.title));
		allItems.add((TextView) findViewById(R.id.userString));
		allItems.add((TextView) findViewById(R.id.passwordString));
		allItems.add((TextView) findViewById(R.id.loginButton));
		allItems.add((TextView) findViewById(R.id.newUserString));
		allItems.add((TextView) findViewById(R.id.newUserButton));
		updateText(allItems);
	}

	/** Called when the 'Sign Up' button is clicked from Home Screen */
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
			dialog = new ProgressDialog(this);
			String msg = "Granting permission. Please wait...";
			dialog.setMessage(msg);
			dialog.show();
			httpTask = new HTTPTask();
			httpTask.caller = this;
			httpTask.callingActivity = Constants.MAIN_ACTIVITY;
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
				prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.JSON_USERNAME, username).commit();
				gcmStuff();
			} else {
				if (dialog != null && dialog.isShowing()) dialog.dismiss();
				Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
				e.printStackTrace();
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
	@Override
	public void processFinish(String output, String callingMethod) {
		if (callingMethod.equals(Constants.ADD_REG_ID_URL)) {
			Log.d("MainActivity",output);
			if (dialog != null && dialog.isShowing()) dialog.dismiss();
			Intent i = new Intent(this, HomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
		} else if (output.equals(Constants.ERROR_CODE)){
			if (dialog != null && dialog.isShowing()) dialog.dismiss();
			String iomsg = "Sorry. Something just broke...";
			Toast.makeText(this,iomsg,Toast.LENGTH_SHORT).show();
		} else {
			logInCallback(output);
		}
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

        @SuppressWarnings("unchecked")
		@Override
        protected void onPostExecute(String msg) {
        	if (failed) return;
        	sendIdToBackend();
        }
	}	
	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}

}
