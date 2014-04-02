package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

public class HomeActivity extends Activity implements AsyncResponse {

	ArrayList<Recipe> suggestions;
	String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Intent i = getIntent();
		username = i.getStringExtra(Constants.JSON_USERNAME);
		TextView welcome = (TextView) findViewById(R.id.welcomeText);
		welcome.setText("Welcome " + username);
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
        case android.R.id.home:
        	Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        case R.id.logout:
        	HomeActivity.logout(this);
        	return true;
        case R.id.account:
        	goToAccount();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	/**Called when 'My Kitchen' button is pressed*/
	public void kitchenButton(View view) {
		Intent intent = new Intent(this, IngredientActivity.class);
		intent.putExtra(Constants.JSON_USERNAME, username);
		startActivity(intent);
	}
	/**Called when 'Completed Recipes' button is clicked*/
	public void recipeButton(View view) {
		Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(Constants.JSON_USERNAME, username);
		startActivity(intent);
		
	}
	
	/**Called when 'Log Out' button is clicked*/
	public static void logout(Context ctx) {
		Intent intent = new Intent(ctx, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		ctx.startActivity(intent);
	}
	
	/** Populates the suggestions ArrayList with data for the suggestion GridView*/
	private void populateData() {
		suggestions = new ArrayList<Recipe>();
		
		
	}
	/** Called when 'Account' button is clicked in the menu*/
	public void goToAccount() {
		Intent intent = new Intent(this, AccountActivity.class);
		intent.putExtra(Constants.JSON_USERNAME, username);
		startActivity(intent);
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		
	}
}
