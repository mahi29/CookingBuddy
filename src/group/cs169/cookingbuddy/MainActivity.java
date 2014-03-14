package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
		httpTask = new HTTPTask();
		httpTask.caller = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
	}
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
        	onSearchRequested();
            return true;
        case android.R.id.home:
        	Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
		password = userField.getText().toString().trim();
		JSONObject json = new JSONObject();
		try {
			json.put(Constants.JSON_USERNAME,username);
			json.put(Constants.JSON_PASSWORD,password);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(Constants.LOGIN_USER_URL);
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
				Intent i = new Intent(this, HomeActivity.class);
				i.putExtra(USERNAME, username);
				startActivity(i);
			} else {
				Toast.makeText(this, "Invalid username and password", Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
				e.printStackTrace();
		} 
	}

	private void signUpCallback(String output) {

	}
	@Override
	public void processFinish(String output, String callingMethod) {
		if (callingMethod.equals(Constants.LOGIN_USER_URL)) {
			logInCallback(output);
		} else if(callingMethod.equals(Constants.ADD_USER_URL)) {
			signUpCallback(output);
		}
	}

}
