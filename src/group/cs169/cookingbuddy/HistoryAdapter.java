package group.cs169.cookingbuddy;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	
	Context context;
	ArrayList<String> completedRecipes;
	ArrayList<String> dates;
	ArrayList<Integer> ratings;
	LayoutInflater inflater;
	
	public HistoryAdapter(Context arg1, ArrayList<String> completedRecipes, ArrayList<String> creationDates, ArrayList<Integer> ratings){
		context = arg1;
		this.completedRecipes = completedRecipes;
		this.dates = creationDates;
		this.ratings = ratings;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return completedRecipes.size();
	}

	@Override
	public Object getItem(int arg0) {
		return completedRecipes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = inflater.inflate(R.layout.history_item, null);
		View rowView = inflater.inflate(R.layout.history_item, parent, false);
		TextView recipeName = (TextView) rowView.findViewById(R.id.recipenamefield);
		recipeName.setText(completedRecipes.get(position));
		TextView dateCreated = (TextView) rowView.findViewById(R.id.creationdate);
		dateCreated.setText(dates.get(position));
		TextView rating = (TextView) rowView.findViewById(R.id.userrating);
		rating.setText(ratings.get(position).toString());
		return rowView;
	}
	
	

}
