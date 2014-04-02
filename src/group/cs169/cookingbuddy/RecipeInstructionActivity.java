package group.cs169.cookingbuddy;

import java.io.InputStream;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

public class RecipeInstructionActivity extends Activity {
	
	String imgUrl;
	String name;
	Bitmap img;
	int rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipeinstruction);
		Intent intent = getIntent();
		imgUrl = intent.getStringExtra("image");
		name = intent.getStringExtra("name");
		rating = intent.getIntExtra("rating", 1);
		
		if (imgUrl == null || imgUrl.equals("")) {
			img  = BitmapFactory.decodeResource(this.getResources(), Constants.DEFAULT_PICTURE);
		} else {
			DownloadTask task = new DownloadTask(img);
			task.execute(imgUrl);
		}
		
		TextView recipeName = (TextView) findViewById(R.id.recipename);
		recipeName.setText(name);
		ImageView image = (ImageView) findViewById(R.id.recipeimage);
		image.setImageBitmap(img);
		
		
		

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
	
	private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

		Bitmap map;
		
		public DownloadTask(Bitmap bmap) {
			this.map = bmap;
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
			return roundedImage;
		}
		
		@Override
		protected void onPostExecute (Bitmap result) {
			map = result;
		}
	}



}
