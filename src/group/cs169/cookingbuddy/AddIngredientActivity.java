package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.DateDialog.DatePickerListener;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AddIngredientActivity extends Activity implements DatePickerListener {

	EditText nameField;
	EditText amtField;
	Spinner unitField; 
	TextView expField;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ingredient_prompt);
		getActionBar().hide();
		nameField = (EditText) findViewById(R.id.prompt_name);
		amtField = (EditText) findViewById(R.id.prompt_amt);
		unitField = (Spinner) findViewById(R.id.prompt_unit);
		expField = (TextView) findViewById(R.id.prompt_expirationDate);
		ArrayList<TextView> allItems = new ArrayList();
		allItems.add((TextView) findViewById(R.id.prompt_message));
		allItems.add((TextView) findViewById(R.id.prompt_expirationDate));
		allItems.add((TextView) findViewById(R.id.prompt_cancel));
		allItems.add((TextView) findViewById(R.id.prompt_finish));
		updateText(allItems);
		

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onFinish(String selectedDate) {
		Log.d("AddIngredient","Selected Date: " + selectedDate);
		expField.setText(selectedDate);
	}
	/**
	 * Called when the user clicks on the expiration date text
	 */
	public void setExpiration(View v) {
		 DialogFragment newFragment = new DateDialog();
		 newFragment.show(getFragmentManager(), "datePicker");
	}
	/**
	 * Called if the user presses 'Cancel'
	 */
	public void cancelAddition(View v) {
		Log.d("AddIngredient","Cancel pressed");		
		setResult(RESULT_CANCELED);
		finish();
	}
	/**
	 * Called if the user presses 'Finish'
	 */
	public void finishAddition (View v) {
		Log.d("AddIngredient","Finish pressed");
		String name = nameField.getText().toString().trim();
		String amt = amtField.getText().toString().trim();
		String units = unitField.getSelectedItem().toString().trim();
		String expDate = expField.getText().toString().trim();
		Intent i = new Intent();
		i.putExtra(Constants.INGREDIENT_NAME,name);
		i.putExtra(Constants.QUANTITY, amt);
		i.putExtra(Constants.UNIT,units);
		i.putExtra(Constants.EXPIRATION,expDate);
		setResult(RESULT_OK,i);
		finish();
		
	}
	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}
}
