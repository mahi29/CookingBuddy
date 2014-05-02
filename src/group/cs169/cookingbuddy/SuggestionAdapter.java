package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SuggestionAdapter extends BaseAdapter {

	Context context;
	ArrayList<Recipe> suggestions;
	LayoutInflater inflater;
	
	public SuggestionAdapter(Context con, ArrayList<Recipe> suggestions) {
		context = con;
		this.suggestions = suggestions;
	}

	@Override
	public int getCount() {
		return suggestions.size();
	}

	@Override
	public Object getItem(int position) {
		return suggestions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.suggestion_grid, null);
		}
		Recipe item = suggestions.get(position);
		TextView name = (TextView) convertView.findViewById(R.id.recipe_name);
		//ImageView rating = (ImageView) convertView.findViewById(R.id.recipe_rating);
		ImageView image = (ImageView) convertView.findViewById(R.id.recipe_image);
		name.setText(item.name);
		//rating.setImageResource(item.rating);
		image.setImageBitmap(item.img);
		return convertView;
	}

}
