package com.nguyenshane.creativecolors;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class PushInvitationReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String message = extras != null ? extras.getString("com.parse.Data") : "";
		JSONObject jObject;
		String oppId = "", myId = "", oppName = "";
		try {
			jObject = new JSONObject(message);
			oppId = jObject.getString("myId");    
			myId = jObject.getString("oppId");
			oppName = jObject.getString("myName");

		} catch (JSONException e) {
			e.printStackTrace();
		}		

		// Define on click notification
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra("invitation", true);
		resultIntent.putExtra("myId", myId);
		resultIntent.putExtra("oppId", oppId);
		resultIntent.putExtra("oppName", oppName);

		// Because clicking the notification opens a new ("special") activity, there's
		// no need to create an artificial back stack.
		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						context,
						0,
						resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		// Builds the notification
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.logo)
		.setContentTitle("Creative Colors")
		.setTicker("Creative Colors request")
		.setVibrate(new long[]{ 0, 500, 250, 500 })
		.setContentText(oppName + " invited you!")
		.setAutoCancel(true)
		.setContentIntent(resultPendingIntent);

		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Issue the notification
		mNotifyMgr.notify(1, mBuilder.build());


	}

}
