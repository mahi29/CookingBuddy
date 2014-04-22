package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Recipe implements AsyncResponse, Serializable {
	
	String name;
	String imgUrl;
	transient Bitmap img;
	String rating;
	int userRating;
	transient Context context;
	String id;
	transient HTTPTask task;
	String yield;
	String prepTime; //in seconds	
	String instructionUrl;
	/**
	 * 
	 * @param name - Recipe name
	 * @param id - recipe id
	 * @param imageUrl - URL of the image; can be null
	 * @param rating - Recipe rating
	 * @param ctx - Context of calling class
	 */
	public Recipe(String name, String id, String imageUrl, Context ctx) {
		this.name = name;
		this.context = ctx;
		this.id = id;
		
		this.imgUrl = imageUrl;
		if (imageUrl == null || imageUrl.equals("")) {
			img  = BitmapFactory.decodeResource(context.getResources(), Constants.DEFAULT_PICTURE);
		} else {
			DownloadTask task = new DownloadTask(this);
			task.execute(imageUrl);
		}
		

	}
	
	@SuppressWarnings("unchecked")
	private void getRecipeData() {
		ArrayList<Object> container = new ArrayList<Object>();
		JSONObject param = new JSONObject();
		try {
			param.put(Constants.RECIPE_ID, id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		container.add(param);
		container.add(Constants.RECIPE_DATA);
		task = new HTTPTask();
		task.caller = this;
		task.callingActivity = Constants.RECIPE_CLASS;
		task.execute(container);
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
				
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return roundedImage;
		}
		
		@Override
		protected void onPostExecute (Bitmap result) {
			recipeInstance.img = result;
			getRecipeData();
		}
	}

	@Override
	public void processFinish(String output, String callingMethod) {
		try {
			JSONObject result = new JSONObject(output);
			yield = (String) result.getString("yield");
			prepTime = (String) result.getString("totalTimeInSeconds");
			rating = (String) result.getString("rating");
			if (rating.equals("null")) rating = "0";
			if (prepTime.equals("null")) prepTime = "0";
			SearchResultActivity sra = (SearchResultActivity) context;
			sra.listData.add(this);
			sra.adapter.notifyDataSetChanged();
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	

}
