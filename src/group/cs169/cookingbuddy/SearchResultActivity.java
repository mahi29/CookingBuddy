package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResultActivity extends Activity implements AsyncResponse {
	
	private TextView txtQuery;
	public HTTPTask task;
	ListView searchResults;
	
	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		task.caller = this;
        getActionBar().setDisplayHomeAsUpEnabled(true);
        txtQuery = (TextView) findViewById(R.id.searchQuery);
        searchResults = (ListView) findViewById(R.id.searchList);
        handleIntent(getIntent());
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
				param.put("q", query);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			container.add(param);
			container.add(HTTPTask.SEARCH);
			task.execute(container);
			//Send out back-end call
        }
 
    }

	@Override
	public void processFinish(String output, String callingMethod) {
		//Handle displaying of search results here
		//TODO: Replace with objects from the back-end
		final ArrayList<Recipe> data = null; 
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
