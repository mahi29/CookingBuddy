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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class SearchResultActivity extends Activity implements AsyncResponse {
	
	private TextView txtQuery;
	public HTTPTask task;
	ListView searchResults;
	public ArrayList<Recipe> listData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        txtQuery = (TextView) findViewById(R.id.searchQuery);
        searchResults = (ListView) findViewById(R.id.searchList);
        handleIntent(getIntent());
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}
	
    /**
     * Handling intent data
     */
    @SuppressWarnings("unchecked")
	private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<Object> container = new ArrayList<Object>();
            JSONObject param = new JSONObject();
            try {
				param.put(Constants.SEARCH_KEYWORD, query);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			container.add(param);
			container.add(Constants.SEARCH_URL);
			task = new HTTPTask();
			task.caller = this;
			task.execute(container);
			//Send out back-end call
        }
 
    }

	@Override
	public void processFinish(String output, String callingMethod) {
		//Handle displaying of search results here
		//TODO: Replace with objects from the back-end
		JSONObject result;
		JSONArray names = null;
		JSONArray images = null;
		listData = new ArrayList<Recipe>();
		
		try {
			result = new JSONObject(output);
			names = result.getJSONArray("recipe_name");
			images = result.getJSONArray("smallImageUrls");
			for (int i = 0; i < names.length(); i++) {
				String name = names.getString(i);
				JSONArray imageArray = images.getJSONArray(i);
				String image = imageArray.getString(0);
				Recipe temp = new Recipe(name, image, Constants.DEFAULT_RATING, this);
				listData.add(temp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		final ArrayList<Recipe> data = listData;
		SearchAdapter adapter = new SearchAdapter(this,data);
		searchResults.setAdapter(adapter);
		searchResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Recipe recipe = data.get(position);
				//TODO: Launch intent from here to RecipeInstruction.class;
			}
			
		});
	}
}
