package com.nguyenshane.creativecolors;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PushConfirmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras != null ? extras.getString("com.parse.Data") : "";
        JSONObject jObject;
        String oppId = "", oppName = "";
        try {
                jObject = new JSONObject(message);
                oppId = jObject.getString("myId");      
                oppName = jObject.getString("myName"); 
                
        } catch (JSONException e) {
                e.printStackTrace();
        }
        
        Intent newintent = new Intent(context, MainActivity.class);
        newintent.putExtra("oppId", oppId);
        Log.d("PCR", oppId);
        newintent.putExtra("confirmed", true);
        newintent.putExtra("oppName", oppName);
        newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newintent);	

}

}
