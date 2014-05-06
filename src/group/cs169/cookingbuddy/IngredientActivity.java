package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class IngredientActivity extends BaseActivity implements AsyncResponse, OnItemSelectedListener {

	HTTPTask task;
	ListView ingredientList;
	ArrayList<Ingredient> ingredientData;
	String user;
	final Context ctx = this;
	final Activity act = this;
	IngredientAdapter adapter;
	Object mActionMode;
	Ingredient selectedIngredient;
	Spinner ingSortSpinner;
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
		ingSortSpinner = (Spinner) findViewById(R.id.ing_sort);
		ingSortSpinner.setOnItemSelectedListener(this);
		ArrayList<Object> container = new ArrayList<Object>();
		JSONObject param = new JSONObject();
		try {
			param.put(Constants.JSON_USERNAME, user);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//THIS PART CHANGES TO CUSTOM FONT
		ArrayList<TextView> allItems = new ArrayList();
		allItems.add((TextView) findViewById(R.id.btn_1));
		allItems.add((TextView) findViewById(R.id.kitchenwelcome));
		updateText(allItems);
		//END CHANGING TO CUSTOM FONT
		container.add(param);
		container.add(Constants.INGREDIENT_LIST_URL);
		task = new HTTPTask();
		task.caller = this;
		task.dialog = new ProgressDialog(this);
		task.callingActivity = Constants.INGREDIENT_ACTIVITY;
		task.execute(container);	
		//AKHIL: Setting the Adapter
		adapter = new IngredientAdapter(this,ingredientData);
		ingredientList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
				SparseBooleanArray selected = adapter.getSelectedIds();
				switch (item.getItemId()) {

				case R.id.cont_delete:
					deleteItems();
					mode.finish();
					return true;
				case R.id.cont_update:
					if (selected.size() == 1){
						updateItem();
						mode.finish();
					} else{
						Toast.makeText(ctx, "Can only update one ingredient at a time", Toast.LENGTH_SHORT).show();  					
					}
					return true;
				case R.id.cont_selectAll:
					selectAll();
				case R.id.cont_search:
					if (selected.size() == 1) {
						Ingredient ing = (Ingredient) adapter.getItem(selected.keyAt(0));
						startSearch(ing.name, false, null, false);
					} else {
						Toast.makeText(ctx, "Can only search one ingredient at a time", Toast.LENGTH_SHORT).show();
					}	
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
		for (int i =  0; i < ingredientList.getChildCount();i++) {
			ingredientList.setItemChecked(i, true);
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

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int selectedPosition,
			long arg3) {
		int alphabetically = 1;
		int dateEarly = 2;
		int dateLate = 3;		
		int quanthl = 4;
		int quantlh = 5;
		if (selectedPosition == alphabetically) {
			Collections.sort(ingredientData,new NameSort());
			adapter.notifyDataSetChanged();
		} else if (selectedPosition == dateEarly) {
			Collections.sort(ingredientData,new DateSort(true));
			adapter.notifyDataSetChanged();
		} else if (selectedPosition == dateLate) {
			Collections.sort(ingredientData,new DateSort(false));
			adapter.notifyDataSetChanged();
		} else if (selectedPosition == quanthl) {
			Collections.sort(ingredientData,new QuantitySort(false));
			adapter.notifyDataSetChanged();
		} else if (selectedPosition == quantlh) {
			Collections.sort(ingredientData,new QuantitySort(true));
			adapter.notifyDataSetChanged();			
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		return;
	}


	public class NameSort implements Comparator<Ingredient> {
		@Override
		public int compare(Ingredient i1, Ingredient i2) {
				String name1 = i1.name;
				String name2 = i2.name;
				return name1.compareToIgnoreCase(name2);
		}
	}
	public class DateSort implements Comparator<Ingredient> {
		
		int ascendingModifier;
		/**
		 * 
		 * @param isAscending - true if the dates are ordered increasingly (Early first)
		 */
		public DateSort (boolean isAscending) {
			ascendingModifier = (isAscending) ? 1 : -1;
		}
		@Override
		public int compare(Ingredient i1, Ingredient i2) {
				String date1 = i1.expDate;
				String date2 = i2.expDate;
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy",Locale.US);
				try {
					Date d1 = sdf.parse(date1);
					Date d2 = sdf.parse(date2);
					int output =  d1.compareTo(d2);
					return output * ascendingModifier;
				} catch (ParseException e) {
					Log.d("IngredientActivity","DateSort broke");					
					e.printStackTrace();
					return 0;
				}
		}
	}
	public class QuantitySort implements Comparator<Ingredient> {
		
		int asc;
		/**
		 * 
		 * @param ascending true if sort is to be in ascending order - 1,2,3...
		 */
		public QuantitySort (boolean ascending) {
			asc = (ascending) ? 1 : -1;
		}
		
		@Override
		public int compare(Ingredient i1, Ingredient i2) {
				double q1 = i1.quantity;
				double q2 = i2.quantity;
				double diff = q1 - q2;
				int output = 0;
				if (diff < 0) output = -1;
				if (diff > 0) output = 1;
				return output * asc;
		}
	}
	
	protected class Ingredient {
		String name;
		String unit;
		String amountUnit;
		double quantity;
		String expDate;
		Date date;

		public Ingredient(String name, double amount, String unit, String date) {
			this.name = name;
			this.unit = unit;
			setDate(date);
			setQuantity(amount);
		}

		public void setQuantity(double amount) {
			this.quantity = amount;
			String temp = new java.text.DecimalFormat("#").format(amount);
			this.amountUnit = temp + " " + unit;
		}
		
		@SuppressWarnings("deprecation")
		public void setDate(String eDate) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);
			try {
				date = sdf.parse(eDate);
				expDate = String.format("%d/%d/%d",date.getMonth()+1,date.getDate(),date.getYear()-100);
			} catch (ParseException e) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy",Locale.US);
				try {
					date = sdf2.parse(eDate);
					expDate = String.format("%d/%d/%d",date.getMonth()+1,date.getDate(),date.getYear()-100);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}


}
