package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.IngredientActivity.Ingredient;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IngredientAdapter extends BaseAdapter {

	Context context;
	ArrayList<Ingredient> data;
	LayoutInflater inflater;
	public IngredientAdapter(Context context, ArrayList<Ingredient> data) {
		this.context = context;
		this.data = data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) convertView = inflater.inflate(R.layout.ingredient_row, parent, false);
		Ingredient item = data.get(position);
		TextView name = (TextView) convertView.findViewById(R.id.ingredientName);
		TextView amount = (TextView) convertView.findViewById(R.id.ingredientAmount);
		TextView exp = (TextView) convertView.findViewById(R.id.expirationDate);
		String amtText = item.amount;
		name.setText(item.name);
		amount.setText(amtText);
		exp.setText(item.expDate);
		return convertView;
	}

}
