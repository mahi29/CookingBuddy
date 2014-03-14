package group.cs169.cookingbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class HistoryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		//Call the DB with the username as a key to get the completed list of recipes
		
		String array[] = {"asdf","qwer"};
		ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.history_item,array);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
