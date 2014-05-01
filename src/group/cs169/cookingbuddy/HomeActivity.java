package group.cs169.cookingbuddy;

import group.cs169.cookingbuddy.HTTPTask.AsyncResponse;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class HomeActivity extends BaseActivity implements AsyncResponse {

	ArrayList<Recipe> suggestions;
	String username;
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	String regID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		prefs = this.getSharedPreferences(Constants.SHARED_PREFS_USERNAME, Context.MODE_PRIVATE);
		username = prefs.getString(Constants.JSON_USERNAME, "username");
//		gcm = GoogleCloudMessaging.getInstance(this);
//		regID = getGCMRegId(this);
//		if (regID.isEmpty()) {
//			RegisterTask task = new RegisterTask(this);
//			task.execute();
//		}
	}


	private String getGCMRegId(Context context) {
		String reg = prefs.getString(Constants.GCM_REG_ID, "");
		return reg;
	}


	/**Called when 'My Kitchen' button is pressed*/
	public void kitchenButton(View view) {
		Intent intent = new Intent(this, IngredientActivity.class);
		startActivity(intent);
	}
	/**Called when 'Completed Recipes' button is clicked*/
	public void recipeButton(View view) {
		Intent intent = new Intent(this, HistoryActivity.class);
		startActivity(intent);	
	}
	
	@Override
	public void processFinish(String output, String callingMethod) {
		// TODO Auto-generated method stub
		
	}
	
	public class RegisterTask extends AsyncTask<Void, Void, String> {
		
		Context context;
		public RegisterTask (Context context) {
			this.context = context;
		}
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) gcm = GoogleCloudMessaging.getInstance(context);
                regID = gcm.register(Constants.GCM_SENDER_ID);
                prefs.edit().putString(Constants.GCM_REG_ID, regID);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @SuppressWarnings("unchecked")
		@Override
        protected void onPostExecute(String msg) {
        	JSONObject json = new JSONObject ();
        	ArrayList<Object> container = new ArrayList<Object>();
        	Log.d("HomeActivity","GCM Registration ID: " + regID);
    		try {
    			json.put(Constants.JSON_USERNAME, prefs.getString(Constants.JSON_USERNAME, ""));
    			json.put(Constants.GCM_REG_ID, regID);
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    		container.add(json);
    		container.add(Constants.ADD_REG_ID_URL);
        	HTTPTask task = new HTTPTask();
        	task.caller = (AsyncResponse) context;
        	task.execute(container);
        }
	}


}
