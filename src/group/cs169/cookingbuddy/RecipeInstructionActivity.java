package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

public class RecipeInstructionActivity extends Activity implements AsyncResponse {
	
	String imgUrl;
	String name;
	Bitmap img;
	Recipe recipe;
	String rating;
	Context ctx;
	String username;
	HTTPTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipeinstruction);
		Intent intent = getIntent();
		imgUrl = intent.getStringExtra("image");
		name = intent.getStringExtra("name");
		rating = intent.getStringExtra("rating");
		recipe = (Recipe) intent.getSerializableExtra("recipe");
		
		SharedPreferences prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		username = prefs.getString(Constants.JSON_USERNAME, "username");
		//Log.d("RecipeInstructionActivity", "Username: " + username);
		ctx = this;
		
		if (imgUrl == null || imgUrl.equals("")) {
			img  = BitmapFactory.decodeResource(this.getResources(), Constants.DEFAULT_PICTURE);
		} else {
			DownloadTask task = new DownloadTask();
			//Log.d("RecipeInstructionActivity", "About to call execute() with URL " + imgUrl);
			task.execute(imgUrl);
		}
		
		TextView recipeName = (TextView) findViewById(R.id.recipename);
		recipeName.setText(name);
		
		TextView estimatedPrepTime = (TextView) findViewById(R.id.estimatedpreptime);
		estimatedPrepTime.setText(recipe.prepTime);
		
		TextView yield = (TextView) findViewById(R.id.yield);
		yield.setText(recipe.yield);		
		
		ImageView image = (ImageView) findViewById(R.id.recipeimage);
		Log.d("RecipeInstructionActivity","Image URL is " + imgUrl);
		Drawable drawable = new BitmapDrawable(ctx.getResources(),img);
		image.setImageDrawable(drawable);
		

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
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Triggered when the 'Make Recipe' button is clicked on Recipe Screen
	 */
	
	public void makeRecipe (View v) {
		LayoutInflater li = LayoutInflater.from(this);
		View recipePrompt = li.inflate(R.layout.recipe_prompt, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(recipePrompt);
		RatingBar rating = (RatingBar) recipePrompt.findViewById(R.id.recipe_rating);
		builder.setCancelable(true);
		final float ratingValue = rating.getRating();
		builder.setPositiveButton("Finish!", new DialogInterface.OnClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
//				Intent intent = new Intent(ctx,HistoryActivity.class);
//				intent.putExtra("userrating", ratingValue);
//				intent.putExtra("recipe", recipe);
//				startActivity(intent);
				
				//String query = intent.getStringExtra(SearchManager.QUERY);
				ArrayList<Object> container = new ArrayList<Object>();
				JSONObject param = new JSONObject();
				Calendar c = Calendar.getInstance();
				try {
					param.put(Constants.JSON_USERNAME, username);
					//Log.d("Filling in the JSON", "Username is " + username);
					param.put(Constants.RECIPE_NAME, recipe.name);
					//Log.d("Filling in the JSON", "Recipe name is " + recipe.name);
					param.put(Constants.CURRENT_DATE, c.get(Calendar.MONTH) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR));
					//Log.d("Filling in the JSON", "Date is " + c.get(Calendar.DATE));
					param.put(Constants.RATING, ratingValue);
					//Log.d("Filling in the JSON", "Rating is " + ratingValue);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				container.add(param);
				container.add(Constants.MAKE_RECIPE_URL);
				task = new HTTPTask();
				task.caller = (AsyncResponse) ctx;
				//task.dialog = new ProgressDialog(this);
				//task.callingActivity = Constants.SEARCH_ACTIVITY;
				task.execute(container);
				// TODO Auto-generated method stub
			}
		});
		AlertDialog alertDialog = builder.create(); 
		alertDialog.show();
	}
	private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

	
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Log.d("Inside RecInstAct doInBack", "URL is " + urldisplay);
			Bitmap image = null;
			//Bitmap roundedImage = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				image = BitmapFactory.decodeStream(in);
				//roundedImage = ImageRounder.getRoundedCornerBitmap(image,15);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			
			//map = image;
			return image;
		}
		
		@Override
		protected void onPostExecute (Bitmap result) {
			Log.d("Inside Download Task", "Fuck my life");
			//return result
			
		}
	}
	
	@Override
	public void processFinish(String output, String callingMethod) {
		
		Log.d("RecipeInstructionAcitivty", "Made it to process finish");
		Intent intent = new Intent(ctx,HistoryActivity.class);
		intent.putExtra("username", username);
		startActivity(intent);
				
	}



}
