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
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	/** Called when the 'Sign Up' button is clicked from Home Screen */
	@SuppressWarnings("unchecked")
	public void signUp(View v) {
		username = userField.getText().toString().trim();
		password = userField.getText().toString().trim();
		JSONObject json = new JSONObject();
		try {
			json.put(USERNAME,username);
			json.put(PASSWORD,password);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(HTTPTask.ADD_USER);
			httpTask.execute(container);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/** Called when the 'Log In' button is clicked from the Home Screen*/
	@SuppressWarnings("unchecked")
	public void logIn(View v) {
		username = userField.getText().toString().trim();
		password = userField.getText().toString().trim();
		JSONObject json = new JSONObject();
		try {
			json.put(USERNAME,username);
			json.put(PASSWORD,password);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(HTTPTask.LOGIN_USER);
			httpTask.execute(container);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void logInCallback(String output) {
		JSONObject out;
		try {
			out = new JSONObject(output);
			int errCode = Integer.parseInt(out.getString("errCode"));
			if (errCode == 1) {
				Intent i = new Intent(this, HomeActivity.class);
				i.putExtra(USERNAME, username);
				startActivity(i);
			} else {
				//INCORRECT CREDENTIALS
			}
		} catch (JSONException e) {
				e.printStackTrace();
		} 
	}

	private void signUpCallback(String output) {
		JSONObject out;
		try {
			out = new JSONObject(output);
			int errCode = Integer.parseInt(out.getString("errCode"));
			if (errCode == 1) {
				Intent i = new Intent(this, HomeActivity.class);
				i.putExtra(USERNAME, username);
				startActivity(i);
			} else {
				//ERROR SIGNING UP???
			}
		} catch (JSONException e) {
				e.printStackTrace();
		} 
	}
	@Override
	public void processFinish(String output, String callingMethod) {
		if (callingMethod.equals(HTTPTask.LOGIN_USER)) {
			logInCallback(output);
		} else if(callingMethod.equals(HTTPTask.ADD_USER)) {
			signUpCallback(output);
		}
	}

}
