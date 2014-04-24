package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class IngredientActivity extends BaseActivity implements AsyncResponse {

	HTTPTask task;
	ListView ingredientList;
	ArrayList<Ingredient> ingredientData;
	String user;
	final Context ctx = this;
	final Activity act = this;
	IngredientAdapter adapter;
	Object mActionMode;
	Ingredient selectedIngredient;
	final static Integer ADD_ING = 0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ingredient);
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		user = prefs.getString(Constants.JSON_USERNAME, "username");
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
		task.dialog = new ProgressDialog(this);
		task.callingActivity = Constants.INGREDIENT_ACTIVITY;
		task.execute(container);	
		//AKHIL: Setting the Adapter
		adapter = new IngredientAdapter(this,ingredientData);
		ingredientList.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				int checkedCount = ingredientList.getCheckedItemCount();
				mode.setTitle(checkedCount + " Selected");
				adapter.toggleSelection(position);

			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				/*
				 * Here we will need to handle the multiple delete case, if we
				 * have more than one update item selected at a time, we 
				 * must limit it so that we can only update one at a time.
				 */
				switch (item.getItemId()) {

				case R.id.cont_delete:
					deleteItems();
					mode.finish();
					return true;
				case R.id.cont_update:
					SparseBooleanArray selected = adapter.getSelectedIds();
					if (selected.size() == 1){
						updateItem();
						mode.finish();
					}
					else{
						Toast.makeText(ctx, "Can only update one ingredient at a time", Toast.LENGTH_SHORT).show();  					
					}
					return true;
				case R.id.cont_selectAll:
					selectAll();
					return true;
				default:
					return false;

				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.contextual, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				adapter.removeSelection();
				mActionMode = null;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
		});

	}

	private void selectAll() {
		//Trying to highlight all ingredients here...
		for (int i =  0; i < ingredientData.size();i++) {

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
		//AKHIL: Setting the choice mode to multiple
		ingredientList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//
		ingredientList.setAdapter(adapter);

		ingredientList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
					long id) {
				//adapter.resetBooleanArray();
				selectedIngredient = ingredientData.get(position);
				if (mActionMode != null) return false;
				//mActionMode = act.startActionMode(mActionModeCallback);
				return true;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void deleteItems() {
		//selectedIngredient = ingredientData.get(position);
		SparseBooleanArray selected = adapter.getSelectedIds();
		selectedIngredient = null;
		// Captures all selected ids with a loop

		for (int i = (selected.size() - 1); i >= 0; i--) {
			if (selected.valueAt(i)) {
				selectedIngredient = (Ingredient) adapter.getItem(selected.keyAt(i));            
				if(ingredientData.remove(selectedIngredient)) {
					adapter.notifyDataSetChanged();	
				}
				try {
					JSONObject param = new JSONObject();
					param.put(Constants.JSON_USERNAME,user);
					param.put(Constants.INGREDIENT_NAME,selectedIngredient.name);
					param.put(Constants.EXPIRATION,selectedIngredient.expDate);
					ArrayList<Object> container = new ArrayList<Object>();
					container.add(param);
					container.add(Constants.REMOVE_INGREDIENT_URL);
					task = new HTTPTask();
					task.caller = (AsyncResponse) ctx;
					task.execute(container);

				} catch (JSONException e) {
					e.printStackTrace();
				}                                                
			}
		}

	}

	/** Updates an the quantity of an ingredient and updates the database appropriately*/
	private void updateItem() {
		//AKHIL: New part added so that we can have multiple items
		SparseBooleanArray selected = adapter.getSelectedIds();
		selectedIngredient = null;
		int i = selected.size() - 1;
		selectedIngredient = (Ingredient) adapter.getItem(selected.keyAt(i));
		LayoutInflater li = LayoutInflater.from(this);
		View ingredientPrompt = li.inflate(R.layout.update_prompt, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText newQuantity = (EditText) ingredientPrompt.findViewById(R.id.update_quant);
		builder.setView(ingredientPrompt);
		builder.setCancelable(true);
		builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String q = newQuantity.getText().toString().trim();
				double newQ = Double.parseDouble(q);
				ingredientData.remove(selectedIngredient);
				selectedIngredient.setQuantity(newQ);
				ingredientData.add(selectedIngredient);
				adapter.notifyDataSetChanged();
				try {
					JSONObject param = new JSONObject();
					param.put(Constants.JSON_USERNAME,user);
					param.put(Constants.INGREDIENT_NAME,selectedIngredient.name);
					param.put(Constants.QUANTITY, newQ);
					param.put(Constants.UNIT,selectedIngredient.unit);
					param.put(Constants.EXPIRATION,selectedIngredient.expDate);
					ArrayList<Object> container = new ArrayList<Object>();
					container.add(param);
					container.add(Constants.UPDATE_INGREDIENT_URL);
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
	
	@Override
	public void processFinish(String output, String callingMethod) { 
		String errCode = Constants.ERROR_CODE;
		try {
			JSONObject out = new JSONObject(output);
			errCode = out.getString(Constants.JSON_STANDARD_RESPONSE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (callingMethod.equals(Constants.INGREDIENT_LIST_URL)) {
			populateList(output);
			return;
		}
		if (callingMethod.equals(Constants.REMOVE_INGREDIENT_URL)) {
			if (errCode.equals(Constants.SUCCESS)) {
				Toast.makeText(this, "Ingredient Successfully Deleted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Sorry, there was some error!", Toast.LENGTH_SHORT).show();
			}
			return;
		} 
		if (callingMethod.equals(Constants.UPDATE_INGREDIENT_URL)) {
			if (errCode.equals(Constants.SUCCESS)) {
				Toast.makeText(this, "Ingredient Quantity Successfully Updated", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Sorry, there was some error!", Toast.LENGTH_SHORT).show();
			}
			return;
		}
		// AKHIL: Adding the Remove all case
		if (callingMethod.equals(Constants.REMOVE_ALL)) {
			if (errCode.equals(Constants.SUCCESS)){
				Toast.makeText(this, "All Ingredients Removed", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(this, "Sorry, there was some error!", Toast.LENGTH_SHORT).show();
			}
		}
		//
		else{
			Toast.makeText(this, "Ingredient Successfully Added", Toast.LENGTH_SHORT).show();
		}
	}

	public void addIngredient(View v) {
		Intent i = new Intent(this, AddIngredientActivity.class);
		startActivityForResult(i,ADD_ING);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_ING) {
			if (resultCode == RESULT_CANCELED) return;
			String name = data.getStringExtra(Constants.INGREDIENT_NAME);
			String quantity = data.getStringExtra(Constants.QUANTITY);
			String unit = data.getStringExtra(Constants.UNIT);
			String expDate = data.getStringExtra(Constants.EXPIRATION);
			Ingredient tmp = new Ingredient(name,Double.parseDouble(quantity),unit,expDate);
			ingredientData.add(tmp);
			adapter.notifyDataSetChanged();

			try {
				JSONObject param = new JSONObject();
				param.put(Constants.JSON_USERNAME,user);
				param.put(Constants.INGREDIENT_NAME,name);
				param.put(Constants.QUANTITY, quantity);
				param.put(Constants.UNIT,unit);
				param.put(Constants.EXPIRATION,expDate);
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
	}
	
	//this is the remove all view
	//AKHIL:
	@SuppressWarnings("unchecked")
	public void removeAll(View v) {
		ingredientData.clear();
		adapter.notifyDataSetChanged();

		try {
			JSONObject param = new JSONObject();
			param.put(Constants.JSON_USERNAME,user);
			ArrayList<Object> container = new ArrayList<Object>();
			container.add(param);
			container.add(Constants.REMOVE_ALL);
			task = new HTTPTask();
			task.caller = (AsyncResponse) ctx;
			task.execute(container);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}




	protected class Ingredient {
		String name;
		String unit;
		String amountUnit;
		double quantity;
		String expDate;

		public Ingredient(String name, double amount, String unit, String date) {
			this.name = name;
			this.amountUnit = amount + " " + unit;
			this.unit = unit;
			this.quantity = amount;
			this.expDate = date;
		}

		public void setQuantity(double amount) {
			this.quantity = amount;
			this.amountUnit = amount + " " + unit;
		}
	}

}
