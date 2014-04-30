package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

public class HistoryActivity extends BaseActivity implements AsyncResponse {
	
	HTTPTask httpTask;
	ArrayList<String> strings;
	ArrayList<String> creationDates;
	ArrayList<Integer> ratings;
	ListView listview;
	HistoryAdapter adapter;
	
	@SuppressWarnings({ "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		strings = new ArrayList<String>();
		SharedPreferences userDetails = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, MODE_PRIVATE);
		String username = userDetails.getString(Constants.JSON_USERNAME, "ERROR");
		listview = (ListView) findViewById(R.id.history_list);
		JSONObject json = new JSONObject();
		
		try {
			json.put(Constants.JSON_USERNAME,username);
			ArrayList<Object> container = new ArrayList<Object>();
			//The JSONObject and path must be added in this order! JSONObject first, path second
			container.add(json);
			container.add(Constants.HISTORY_URL);
			//This call will get the ratings
			//container.add(Constants.HISTORY_URL_TEMP);
			httpTask = new HTTPTask();
			httpTask.caller = this;
			httpTask.execute(container);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		//Call the DB with the username as a key to get the completed list of recipes
		//This array needs to be populated with strings from the JSON
		
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		// TODO Parse the incoming JSON object that represents the recipes the user has made
		try {
			JSONObject jsonObject = new JSONObject(output);
			JSONArray jsonArray = jsonObject.getJSONArray(Constants.COMPLETED_USER_RECIPES);
			int arrayLength = jsonArray.length();
			strings = new ArrayList<String>();
			creationDates = new ArrayList<String>();
			ratings = new ArrayList<Integer>();
			Random random = new Random();
			
			for (int i = 0; i < arrayLength; i++){
				
				strings.add(jsonArray.getJSONObject(i).getString("recipe_name"));
				creationDates.add(jsonArray.getJSONObject(i).getString("date_created"));
				ratings.add((int) Math.ceil(random.nextInt(5)+1));
				//This code retrieve ratings from the DB
				//ratings.add(jsonArray.getJSONObject(i).getInt("rating"));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//int arrayListLength = strings.size();
		//String stringArray[] = new String[arrayListLength];
		
//		for (int k = 0; k < arrayListLength; k++){
//			stringArray[k] = strings.get(k);
//		}
		
		adapter = new HistoryAdapter(this, strings, creationDates, ratings);
		listview.setAdapter(adapter);
	}

}
