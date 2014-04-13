package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

public class SearchResultActivity extends Activity implements AsyncResponse, OnItemSelectedListener {

	private Spinner searchSort;
	public HTTPTask task;
	ListView searchResults;
	public ArrayList<Recipe> listData;
	public Context ctx;
	public SearchAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		searchSort = (Spinner) findViewById(R.id.search_sort);
		searchSort.setOnItemSelectedListener(this);
		searchResults = (ListView) findViewById(R.id.searchList);
		ctx = this;
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
        case R.id.logout:
        	HomeActivity.logout(this);            
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
			task.dialog = new ProgressDialog(this);
			task.callingActivity = Constants.SEARCH_ACTIVITY;
			task.execute(container);
		}

	}

	@Override
	public void processFinish(String output, String callingMethod) {

		//Handle displaying of search results here
		JSONObject result;
		JSONArray names = null;
		JSONArray images = null;
		JSONArray ids = null;
		listData = new ArrayList<Recipe>();
		adapter = new SearchAdapter(this,listData);
		searchResults.setAdapter(adapter);
		ProgressDialog recipePD = new ProgressDialog(this);
		recipePD.setMessage("Showing the Recipes. Please Wait...");
		recipePD.show();
		try {
			result = new JSONObject(output);
			ids = result.getJSONArray("recipe_id");
			names = result.getJSONArray("recipe_name");
			images = result.getJSONArray("smallImageUrls");
			for (int i = 0; i < names.length(); i++) {
				String name = names.getString(i);
				String id = ids.getString(i);
				JSONArray imageArray = images.getJSONArray(i);
				String image = imageArray.getString(0);
				new Recipe(name, id, image, this);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		recipePD.dismiss();
		final ArrayList<Recipe> data = listData;
		searchResults.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				
				Recipe recipe = data.get(position);
				Intent intent = new Intent(ctx, RecipeInstructionActivity.class);
				intent.putExtra("name",recipe.name);
				intent.putExtra("rating", recipe.rating);
				intent.putExtra("image", recipe.imgUrl);
				startActivity(intent);
			}

		});
	}
	
	private void sortByPrep() {
		Collections.sort(listData,new PrepSort());
		adapter.notifyDataSetChanged();
	}
	
	private void sortByTime() {
		Collections.sort(listData,new RatingSort());
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		//Prep
		if (arg2 == 1) {
			sortByPrep();
		} else if (arg2 == 2) { //Rating
			sortByTime();
		}
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public class RatingSort implements Comparator<Recipe> {
		@Override
		public int compare(Recipe r1, Recipe r2) {
			int rating1 = Integer.parseInt(r1.rating);
			int rating2 = Integer.parseInt(r2.rating);
			return rating2 - rating1; 
			// Sorts by descending order - i.e 5,4,3,2,1.
			// If you want ascend sort => return rating1 - rating2 
			// Sort of a  hacky solution. 
		}
	}
	
	public class PrepSort implements Comparator<Recipe> {
		@Override
		public int compare(Recipe r1, Recipe r2) {
			int time1 = Integer.parseInt(r1.prepTime);
			int time2 = Integer.parseInt(r2.prepTime);
			return time1 - time2;
		}
	}		
}
