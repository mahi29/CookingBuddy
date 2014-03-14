package group.cs169.cookingbuddy;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class HistoryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		Intent i = getIntent();
		String username = i.getStringExtra("username");
		
		JSONObject json = new JSONObject();
		try {
			json.put("user",username);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		}
		ArrayList<Object> container = new ArrayList<Object>();
		//The JSONObject and path must be added in this order! JSONObject first, path second
		container.add(json);
		container.add(HTTPTask.LOGIN_USER);
		//httpTask.execute(container);
		
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
