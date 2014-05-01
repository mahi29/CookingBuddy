package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	Context context;
	ArrayList<Recipe> data;
	LayoutInflater inflater;
	public SearchAdapter(Context context, ArrayList<Recipe> data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = inflater.inflate(R.layout.search_row, null);
		Recipe recipe = data.get(position);
		ImageView img = (ImageView) convertView.findViewById(R.id.search_image);
		RatingBar rating = (RatingBar) convertView.findViewById(R.id.search_rating);
		TextView name = (TextView) convertView.findViewById(R.id.search_name);
		img.setImageBitmap(recipe.img);
		name.setText(recipe.name);
		float rate = Float.parseFloat(recipe.rating);
		rating.setRating(rate);
		//rating.setImageResource(recipe.rating);
		ArrayList<TextView> allItems = new ArrayList<TextView>();
		allItems.add((TextView) convertView.findViewById(R.id.search_name));
		updateText(allItems);
		return convertView;
	}
	
	private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(context.getAssets(), "song.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}

}
