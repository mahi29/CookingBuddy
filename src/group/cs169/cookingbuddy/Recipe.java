package group.cs169.cookingbuddy;

import android.graphics.Bitmap;

public class Recipe {
	String name;
	Bitmap img;
	int rating;
	
	protected Recipe(String name, Bitmap image, int rating) {
		this.name = name;
		this.rating = rating;
		this.img = image;
	}
	
}
