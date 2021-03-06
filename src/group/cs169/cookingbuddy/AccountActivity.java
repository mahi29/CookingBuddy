package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends BaseActivity implements AsyncResponse{

	TextView msg;
	EditText oldPass;
	EditText newPass;
	EditText confNewPass;
	String username;	
	HTTPTask httpTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		username = prefs.getString(Constants.JSON_USERNAME, "username");
		msg = (TextView) findViewById(R.id.acctName);
		msg.setText(username+"'s Account");
		oldPass = (EditText) findViewById(R.id.acctOldPassText);
		newPass = (EditText) findViewById(R.id.acctNewPassText);
		confNewPass = (EditText) findViewById(R.id.acctconfirmPassText);
		//changing to custom font
		ArrayList<TextView> allItems = new ArrayList();
		allItems.add((TextView) findViewById(R.id.acctName));
		allItems.add((TextView) findViewById(R.id.acctOldPass));
		allItems.add((TextView) findViewById(R.id.acctNewPass));
		allItems.add((TextView) findViewById(R.id.acctconfirmPass));
		allItems.add((TextView) findViewById(R.id.acctButton));
		updateText(allItems);
	}
	
	@SuppressWarnings("unchecked")
	public void changePassword(View v) {
		String oldP = oldPass.getText().toString().trim();
		String newP = newPass.getText().toString().trim();
		String cnP = confNewPass.getText().toString().trim();
		if (!newP.equals(cnP)) {
			Toast.makeText(this, "Your passwords do not match", Toast.LENGTH_LONG).show();
			return;
		}
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.JSON_USERNAME,username);
			json.put(Constants.JSON_PASSWORD,oldP);
			json.put(Constants.JSON_NEW_PASSWORD,newP);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(Constants.CHANGE_PASSWORD);
			httpTask = new HTTPTask();
			httpTask.caller = this;
			httpTask.execute(container);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		if (output.equals(Constants.ERROR_CODE)) {
			Toast.makeText(this, "Error! Sorry, something broke...",Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Password successfully changed",Toast.LENGTH_LONG).show();
			Intent i = new Intent(this, HomeActivity.class);
			startActivity(i);
		}
	}
	
	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}

}
