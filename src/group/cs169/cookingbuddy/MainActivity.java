package group.cs169.cookingbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Testing comment
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Called when the 'Sign Up' button is clicked from Home Screen
	 */
	public void signUp(View v) {
		
	}
	/** Called when the 'Log In' button is clicked from the Home Screen*/
	public void logIn(View v) {
		
	}

}
