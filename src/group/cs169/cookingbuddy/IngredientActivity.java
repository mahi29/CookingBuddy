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
import android.widget.ListView;
import android.widget.SearchView;

public class IngredientActivity extends Activity implements AsyncResponse {
	
	HTTPTask task;
	ListView ingredientList;
	ArrayList<Ingredient> ingredientData;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredient);
		task = new HTTPTask();
		task.caller = this;
		Intent i = getIntent();
		String username = i.getStringExtra(Constants.JSON_USERNAME);
		ingredientList = (ListView) findViewById(R.id.ingredientList);
		ingredientData = new ArrayList<Ingredient>();
        ArrayList<Object> container = new ArrayList<Object>();
		JSONObject param = new JSONObject();
		try {
			param.put("username", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		container.add(param);
		container.add(Constants.SEARCH_URL);
		task.execute(container);		
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
	public void processFinish(String output, String callingMethod) {
		
		try {
			JSONObject result = new JSONObject(output);
			JSONArray ingArray = result.getJSONArray(Constants.INGREDIENT_KEY);
			for (int i = 0; i < ingArray.length(); i++) {
				JSONObject jO = ingArray.getJSONObject(i);
				String name = jO.getString(Constants.INGREDIENT_NAME);
				String expDate = jO.getString(Constants.EXPIRATION);
				double quantity = jO.getDouble(Constants.QUANTITY);
				String unit = jO.getString(Constants.UNIT);
				Ingredient temp = new Ingredient (name,quantity,unit,expDate);
				ingredientData.add(temp);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ArrayList<Ingredient> data = ingredientData;
		IngredientAdapter adapter = new IngredientAdapter(this,data);
		ingredientList.setAdapter(adapter);
	}
	protected class Ingredient {
		String name;
		String unit;
		String amount;
		String expDate;
		
		public Ingredient(String name, double amount, String unit, String date) {
			this.name = name;
			this.amount = amount + " " + unit;
			this.unit = unit;
			this.expDate = date;
		}
	}
}
