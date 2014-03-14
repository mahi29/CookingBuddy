package group.cs169.cookingbuddy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class IngredientActivity extends Activity implements AsyncResponse {
	
	HTTPTask task;
	ListView ingredientList;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredient);
		task = new HTTPTask();
		task.caller = this;
		Intent i = getIntent();
		String username = i.getStringExtra("username");
		ingredientList = (ListView) findViewById(R.id.ingredientList);
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
	public void processFinish(String output, String callingMethod) {
		ArrayList<Ingredient> data = null;
		IngredientAdapter adapter = new IngredientAdapter(this,data);
		ingredientList.setAdapter(adapter);
	}
	protected class Ingredient {
		String name;
		String unit;
		String amount;
		
		public Ingredient(String name, String amount, String unit) {
			this.name = name;
			this.amount = amount + " " + unit;
			this.unit = unit;
		}
	}
}
