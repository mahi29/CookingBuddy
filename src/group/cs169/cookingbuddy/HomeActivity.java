package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends BaseActivity{

	ArrayList<Recipe> suggestions;
	String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		username = prefs.getString(Constants.JSON_USERNAME, "username");
		//TextView welcome = (TextView) findViewById(R.id.welcomeText);
		//welcome.setText("Welcome " + username);
	}





	/**Called when 'My Kitchen' button is pressed*/
	public void kitchenButton(View view) {
		Intent intent = new Intent(this, IngredientActivity.class);
		startActivity(intent);
	}
	/**Called when 'Completed Recipes' button is clicked*/
	public void recipeButton(View view) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);
		
	}
}
