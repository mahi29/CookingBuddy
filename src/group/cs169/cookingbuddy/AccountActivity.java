package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivity extends Activity implements AsyncResponse{

	TextView msg;
	EditText oldPass;
	EditText newPass;
	String username;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		Intent i = getIntent();
		username = i.getStringExtra(Constants.JSON_USERNAME);
		msg = (TextView) findViewById(R.id.acctName);
		msg.setText(username+"'s Account");
		msg = (EditText) findViewById(R.id.acctOldPassText);
		oldPass = (EditText) findViewById(R.id.acctNewPassText);
		newPass = (EditText) findViewById(R.id.acctconfirmPassText);
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		// TODO Auto-generated method stub
		
	}

}
