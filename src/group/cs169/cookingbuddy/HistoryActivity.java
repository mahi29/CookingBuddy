package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

public class HistoryActivity extends Activity implements AsyncResponse {
	
	HTTPTask httpTask;
	ArrayList<String> strings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		strings = new ArrayList<String>();
		Intent i = getIntent();
		String username = i.getStringExtra("username");
		
		JSONObject json = new JSONObject();
		try {
			json.put("user",username);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		
		ArrayList<Object> container = new ArrayList<Object>();
		//The JSONObject and path must be added in this order! JSONObject first, path second
		container.add(json);
		container.add(Constants.HISTORY_URL);
		httpTask = new HTTPTask();
		httpTask.caller = this;
		httpTask.execute(container);
		
		//Call the DB with the username as a key to get the completed list of recipes
		//This array needs to be populated with strings from the JSON
		int arrayListLength = strings.size();
		String stringArray[] = new String[arrayListLength];
		for (int k = 0; k < arrayListLength; k++){
			stringArray[k] = strings.get(k);
		}
		
		ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.history_item, stringArray);
		ListView listView = (ListView) findViewById(R.id.listview1);
		listView.setAdapter(adapter);
		
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
        case R.id.logout:
        	HomeActivity.logout(this);            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	@Override
	public void processFinish(String output, String callingMethod) {
		// TODO Parse the incoming JSON object that represents the recipes the user has made
		try {
			JSONObject jsonObject = new JSONObject(output);
			JSONArray jsonArray = jsonObject.getJSONArray(Constants.COMPLETED_USER_RECIPES);
			int arrayLength = jsonArray.length();
			strings = new ArrayList<String>();
			for (int i = 0; i < arrayLength; i++){
				strings.add(jsonArray.getString(i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
