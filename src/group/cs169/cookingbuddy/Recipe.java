package group.cs169.cookingbuddy;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Recipe {
	String name;
	Bitmap img;
	int rating;
	Context context;
	/**
	 * 
	 * @param name - Recipe name
	 * @param imageUrl - URL of the image; can be null
	 * @param rating - Recipe rating
	 */
	public Recipe(String name, String imageUrl, int rating, Context ctx) {
		this.name = name;
		this.context = ctx;
		//this.rating = rating;
		this.rating = R.drawable.rating;
		if (imageUrl == null || imageUrl.equals("")) {
			img  = BitmapFactory.decodeResource(context.getResources(), Constants.DEFAULT_PICTURE);
		} else {
			DownloadTask task = new DownloadTask(this);
			task.execute(imageUrl);
		}
	}
	
	private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

		Recipe recipeInstance;
		
		public DownloadTask(Recipe recipe) {
			this.recipeInstance = recipe;
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
				//TODO round the image corners here
				
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return roundedImage;
		}
		
		@Override
		protected void onPostExecute (Bitmap result) {
			recipeInstance.img = result;
		}
	}
}
