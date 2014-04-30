package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.IngredientActivity.Ingredient;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class IngredientAdapter extends BaseAdapter {

	Context context;
	ArrayList<Ingredient> data;
	private SparseBooleanArray mSelectedItemsIds;
	LayoutInflater inflater;
	public IngredientAdapter(Context context, ArrayList<Ingredient> data) {
		this.context = context;
		this.data = data;
		mSelectedItemsIds = new SparseBooleanArray();
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
		String amtText = item.amountUnit;
		name.setText(item.name);
		amount.setText(amtText);
		exp.setText(item.expDate);
		ArrayList<TextView> allItems = new ArrayList();
		allItems.add((TextView) convertView.findViewById(R.id.ingredientName));
		allItems.add((TextView) convertView.findViewById(R.id.ingredientAmount));
		allItems.add((TextView) convertView.findViewById(R.id.expirationDate));
		updateText(allItems);
		return convertView;
	}
	
	public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
	
	public void selectView(int position, boolean value) {
        if (value){
            mSelectedItemsIds.put(position, value);
        }
        else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }
	
	public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
 
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    
    public void resetBooleanArray() {
    	mSelectedItemsIds = new SparseBooleanArray();
    }
    
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    
    private void updateText(ArrayList<TextView> allItems){
		Typeface font = Typeface.createFromAsset(context.getAssets(), "VintageOne.ttf");
		for (TextView t:allItems){
			t.setTypeface(font);
		}
	}

}
