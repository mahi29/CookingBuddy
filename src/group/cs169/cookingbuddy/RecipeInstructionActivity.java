package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class RecipeInstructionActivity extends BaseActivity implements AsyncResponse {
	
	String imgUrl;
	String name;
	Bitmap imgBitmap;
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
		ctx = this;
		
		if (imgUrl == null || imgUrl.equals("")) {
			imgBitmap  = BitmapFactory.decodeResource(this.getResources(), Constants.DEFAULT_PICTURE);
		} else {
			modifyURL();
			DownloadTask task = new DownloadTask(this);
			task.execute(imgUrl);
		}
		
		TextView recipeName = (TextView) findViewById(R.id.recipename);
		recipeName.setText(name);
		
		TextView estimatedPrepTime = (TextView) findViewById(R.id.estimatedpreptime);
		int time = Integer.parseInt(recipe.prepTime);
		int hours = time/3600;
		int minutes = (time - (3600*(hours)))/60;
		//int seconds = time - (60*(minutes));
		//estimatedPrepTime.setText(recipe.prepTime);
		if (time > 0 && time < 3600 && minutes != 0){
			estimatedPrepTime.setText("Estimated prep time: " + minutes + " minutes");
		}
		else if (time > 3600 && hours > 1 && minutes != 0){
			estimatedPrepTime.setText("Estimated prep time: " + hours + " hours and " + minutes + " minutes");
		}
		else if (time > 3600 && hours == 1){
			estimatedPrepTime.setText("Estimated prep time: " + hours + " hour and " + minutes + " minutes");
		}
		else if (time > 3600 && hours > 1 && minutes == 0){
			estimatedPrepTime.setText("Estimated prep time: " + hours + " hours");
		}
		else if (time > 3600 && hours == 1 && minutes == 0){
			estimatedPrepTime.setText("Estimated prep time: " + hours + " hour");
		}
		else {
			estimatedPrepTime.setText("Estimated prep time is not available");
		}
		TextView yield = (TextView) findViewById(R.id.yield);
		String recipeYield = recipe.yield;
		if (recipeYield == null) {
			recipeYield = "Not Available";
		}
		yield.setText("Yield: " + recipe.yield);
		
		//TextView url = (TextView) findViewById(R.id.instructionurl);
		//url.setText("Recipe Instructions " + recipe.instructionUrl);
		
		WebView webview = (WebView) findViewById(R.id.recipewebview);
		webview.setWebViewClient(new Callback());
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl(recipe.instructionUrl);
		Log.d("Inside RecipeInstructionActivity", "URL is " + recipe.instructionUrl);
		
		ImageView image = (ImageView) findViewById(R.id.recipeimage);
		image.setImageBitmap(imgBitmap);
		
		//THIS PART CHANGES TO CUSTOM FONT
		ArrayList<TextView> allItems = new ArrayList<TextView>();
		allItems.add((TextView) findViewById(R.id.estimatedpreptime));
		allItems.add((TextView) findViewById(R.id.yield));
		//allItems.add((TextView) findViewById(R.id.instructionurl));
		updateText(allItems);
		ArrayList<TextView> allItems2 = new ArrayList<TextView>();
		allItems2.add((TextView) findViewById(R.id.recipename));
		allItems2.add((TextView) findViewById(R.id.makerecipe));
		updateTextLarge(allItems2);
		//END CHANGING TO CUSTOM FONT
		
	}
	/**
	 * Takes in the URL and makes it a larger image URL
	 * Does so by removing *s=90 and replacing it to s=270
	 */
	private void modifyURL() {
		String tempURL = imgUrl.substring(0,imgUrl.length()-2);
		tempURL += "9999";
		imgUrl = tempURL;
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
					param.put(Constants.RECIPE_NAME, recipe.name);
					param.put(Constants.CURRENT_DATE, c.get(Calendar.MONTH) + "/" + c.get(Calendar.DATE) + "/" + c.get(Calendar.YEAR));
					param.put(Constants.RATING, ratingValue);
					
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
			}
		});
		AlertDialog alertDialog = builder.create(); 
		alertDialog.show();
	}
	private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

		RecipeInstructionActivity rIA;
		
		public DownloadTask(RecipeInstructionActivity ctx) {
			this.rIA = ctx;
		}
		
		@Override
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap image = null;
			Bitmap roundedImage = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				image = BitmapFactory.decodeStream(in);
				roundedImage = ImageRounder.getRoundedCornerBitmap(image,15);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			
			//map = image;
			return roundedImage;
		}
		
		@Override
		protected void onPostExecute (Bitmap result) {
			rIA.imgBitmap = result;
			ImageView image = (ImageView) findViewById(R.id.recipeimage);
			image.setImageBitmap(imgBitmap);
			
		}
	}
	
	@Override
	public void processFinish(String output, String callingMethod) {
		Intent intent = new Intent(ctx,HistoryActivity.class);
		startActivity(intent);		
	}
	
private class Callback extends WebViewClient {
	
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}
}

	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "Arctik.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}
	
	private void updateTextLarge(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}


}
