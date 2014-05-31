package com.nguyenshane.creativecolors;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SendCallback;

/*
 * The FriendListAdapter is an extension of ParseQueryAdapter
 * that has a custom layout.
 */

public class FriendListAdapter extends ParseQueryAdapter<ParseUser> {
	static final String LOG_TAG = "FriendListActivity";

	public FriendListAdapter(Context context, final String objectId) {
		super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
			
			public ParseQuery<ParseUser> create() {
				// Here we can configure a ParseQuery to display
				ParseQuery query = ParseUser.getQuery();
				query.whereNotEqualTo("objectId", objectId);
				//query.orderByDescending("score");
				return query;
			}
		});
	}

	@Override
	public View getItemView(ParseUser user, View v, ViewGroup parent) {	
		if (v == null) {
			v = View.inflate(getContext(), R.layout.activity_friend_list, null);
		}

		super.getItemView(user, v, parent);
		
		String realname = "Guest", facebookId = null;
		int score = user.getInt("score");
		int status = user.getInt("status");
		final String oppId = "ch" + user.getObjectId();
		final String myId = "ch" + ParseUser.getCurrentUser().getObjectId();
		final String myName = ParseUser.getCurrentUser().getUsername();
		
		Log.d("FLA", Integer.toString(score));
		
		
		// Parsing JSON
		JSONObject jProfile;
		jProfile = new JSONObject();
		jProfile = user.getJSONObject("profile");

		try {
			realname = jProfile.getString("name");
			facebookId = jProfile.getString("facebookId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		ProfilePictureView userImage = (ProfilePictureView) v.findViewById(R.id.icon);
		if (facebookId!=null){
			userImage.setProfileId(facebookId);
		}else userImage.setProfileId(null);

		TextView realnameTextView = (TextView) v.findViewById(R.id.realname);
		realnameTextView.setText(realname);
		TextView scoreTextView = (TextView) v.findViewById(R.id.score);
		scoreTextView.setText(Integer.toString(score));
		TextView statusTextView = (TextView) v.findViewById(R.id.status);
		if(status == 0) statusTextView.setText("Offline");
		if(status == 1) statusTextView.setText("Online");
		if(status == 2) statusTextView.setText("Busy");
		
		final Button button = (Button) v.findViewById(R.id.buttonInvite);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform push
            	button.setText("Sending invitation...");
            	button.setEnabled(false);
            	pushMyInvitation(myId, oppId, myName);
            }
        });
		
		return v;
	}
	
	
	public void pushMyInvitation(String myId, String oppId, String myName){
		Log.d(LOG_TAG,"I'm pushing an Invitation");

		try {
			JSONObject object = new JSONObject();
			object.put("action", "pushedInvitation");   
			object.put("myId", myId);
			object.put("oppId", oppId);
			object.put("myName", myName);
			ParsePush pushToOpp = new ParsePush();
			pushToOpp.setData(object);
			pushToOpp.setChannel(oppId);

			pushToOpp.sendInBackground(new SendCallback() {
				@Override
				public void done(ParseException e) {

					
					//Something wrong with push
					if (e != null) ;
					
				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

}
