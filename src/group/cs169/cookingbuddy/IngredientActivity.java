package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class IngredientActivity extends Activity implements AsyncResponse {
	
	HTTPTask task;
	ListView ingredientList;
	ArrayList<Ingredient> ingredientData;
	String user;
	final Context ctx = this;
	IngredientAdapter adapter;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredient);
		Intent i = getIntent();
		user = i.getStringExtra(Constants.JSON_USERNAME);
		ingredientList = (ListView) findViewById(R.id.ingredientList);
		ingredientData = new ArrayList<Ingredient>();
        ArrayList<Object> container = new ArrayList<Object>();
		JSONObject param = new JSONObject();
		try {
			param.put(Constants.JSON_USERNAME, user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		container.add(param);
		container.add(Constants.INGREDIENT_LIST_URL);
		task = new HTTPTask();
		task.caller = this;
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
        case R.id.logout:
        	HomeActivity.logout(this);            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void populateList(String output) {
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
		//ArrayList<Ingredient> data = ingredientData;
		adapter = new IngredientAdapter(this,ingredientData);
		ingredientList.setAdapter(adapter);
		
		ingredientList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Ingredient ing = ingredientData.get(position);
				Log.d("IngredientActivity","Name: " + ing.name);
			}
		});
    }
	
	@Override
	public void processFinish(String output, String callingMethod) {
		if (callingMethod.equals(Constants.INGREDIENT_LIST_URL)) {
			populateList(output);
		}  else {
			Toast.makeText(this, "Ingredient Successfully Added", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void addIngredient(View v) {
		LayoutInflater li = LayoutInflater.from(this);
		View ingredientPrompt = li.inflate(R.layout.ingredient_prompt, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(ingredientPrompt);
		final EditText nameField = (EditText) ingredientPrompt.findViewById(R.id.prompt_name);
		final EditText amtField = (EditText) ingredientPrompt.findViewById(R.id.prompt_amt);
		final EditText unitField = (EditText) ingredientPrompt.findViewById(R.id.prompt_unit);
		final EditText expField = (EditText) ingredientPrompt.findViewById(R.id.prompt_exp);
		
		builder.setCancelable(true);
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String ingName = nameField.getText().toString().trim();
				String amt = amtField.getText().toString().trim();
				String unit = unitField.getText().toString().trim();
				String exp = expField.getText().toString().trim();
				Ingredient tmp = new Ingredient(ingName,Double.parseDouble(amt),unit,exp);
				ingredientData.add(tmp);
				adapter.notifyDataSetChanged();
				
				try {
					JSONObject param = new JSONObject();
					param.put(Constants.JSON_USERNAME,user);
					param.put(Constants.INGREDIENT_NAME,ingName);
					param.put(Constants.QUANTITY, amt);
					param.put(Constants.UNIT,unit);
					param.put(Constants.EXPIRATION,exp);
					ArrayList<Object> container = new ArrayList<Object>();
					container.add(param);
					container.add(Constants.ADD_INGREDIENT_URL);
					task = new HTTPTask();
					task.caller = (AsyncResponse) ctx;
					task.execute(container);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			
			}
		});
		AlertDialog alertDialog = builder.create(); 
		alertDialog.show();
		

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
