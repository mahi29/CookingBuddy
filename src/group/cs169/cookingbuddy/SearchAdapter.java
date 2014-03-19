package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
		TextView name = (TextView) convertView.findViewById(R.id.search_name);
		//ImageView rating = (ImageView) convertView.findViewById(R.id.search_rating);
		img.setImageBitmap(recipe.img);
		name.setText(recipe.name);
		//rating.setImageResource(recipe.rating);
		return convertView;
	}

}
