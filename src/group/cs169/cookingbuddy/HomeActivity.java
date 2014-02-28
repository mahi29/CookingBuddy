package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;

public class HomeActivity extends Activity {

	ArrayList<Suggestion> suggestions;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		GridView gridView = (GridView) findViewById(R.id.suggestionGrid);
		populateData();
		gridView.setAdapter(new SuggestionAdapter(this, suggestions));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	/**Called when 'My Kitchen' button is pressed*/
	public void kitchenButton(View view) {
		
	}
	/**Called when 'Completed Recipes' button is clicked*/
	public void recipeButton(View view) {
		
	}
	/**Called when 'Log Out' button is clicked*/
	public void logOut(View view) {
		
	}
	
	/** Populates the suggestions ArrayList with data for the suggestion GridView*/
	private void populateData() {
		suggestions = new ArrayList<Suggestion>();
		
	}
	protected class Suggestion {
		String name;
		Bitmap img;
		int rating;
		
		protected Suggestion(String name, Bitmap image, int rating) {
			this.name = name;
			this.rating = rating;
			this.img = image;
		}
		
	}
}
