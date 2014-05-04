package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends BaseActivity {

	ArrayList<Recipe> suggestions;
	String username;
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	String regID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		username = prefs.getString(Constants.JSON_USERNAME, "username");
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
