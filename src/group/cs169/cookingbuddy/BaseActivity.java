package group.cs169.cookingbuddy;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import group.cs169.cookingbuddy.Constants;

public class BaseActivity extends Activity {

	
	public BaseActivity() {

	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
    	bar.setHomeButtonEnabled(true);
		bar.setDisplayShowHomeEnabled(true);
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
        	logout(this);
        	return true;
        case R.id.account:
        	goToAccount(this);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
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
	
	/** Called when 'Account' button is clicked in the menu*/
	public void goToAccount(Context ctx) {
		Intent intent = new Intent(ctx, AccountActivity.class);
		ctx.startActivity(intent);
	}
	
	/**Called when 'Log Out' button is clicked*/
	public void logout(Context ctx) {
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(Constants.JSON_USERNAME);
		editor.commit();
		Log.d("BaseActivity","Logout Called");
		Intent intent = new Intent(ctx, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		ctx.startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("BaseActivity","onPause()");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
