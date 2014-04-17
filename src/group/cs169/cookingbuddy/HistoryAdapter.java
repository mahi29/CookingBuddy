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
	LayoutInflater inflater;
	
	public HistoryAdapter(Context arg1, ArrayList<String> completedRecipes, ArrayList<String> creationDates){
		context = arg1;
		this.completedRecipes = completedRecipes;
		dates = creationDates;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d("HistoryAdapter", "Made it to the adapter's constructor");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return completedRecipes.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return completedRecipes.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = inflater.inflate(R.layout.history_item, null);
		View rowView = inflater.inflate(R.layout.history_item, parent, false);
		TextView recipeName = (TextView) rowView.findViewById(R.id.recipenamefield);
		recipeName.setText(completedRecipes.get(position));
		//Log.d("INSIDE THE GETVIEW METHOD", "Recipe to be filled is " + completedRecipes.get(position));
		TextView dateCreated = (TextView) rowView.findViewById(R.id.creationdate);
		dateCreated.setText(dates.get(position));
		//Log.d("INSIDE THE GETVIEW METHOD", "Date to be filled is " + dates.get(position));
		TextView rating = (TextView) rowView.findViewById(R.id.userrating);
		rating.setText("5");
		return rowView;
	}
	
	

}
