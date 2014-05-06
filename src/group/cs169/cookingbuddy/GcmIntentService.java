package group.cs169.cookingbuddy;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
	
	String TAG = "GCMIntent";
	NotificationManager  notificationManager;
	public static final int NOTIFICATION_ID = 1;
	
	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG,"onHandleIntent");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { 
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Log.i(TAG, "Received: " + extras.toString());
				sendNotification(extras);	
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	private void sendNotification(Bundle bundle) {
		notificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		String msg = (String) bundle.get("message");
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, HomeActivity.class), 0);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.logo)
		.setAutoCancel(true)
		.setContentTitle("Cooking Buddy")
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setContentText(msg);
		mBuilder.setContentIntent(contentIntent);
		notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}


