package com.nguyenshane.creativecolors;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

/*
 * The FriendListAdapter is an extension of ParseQueryAdapter
 * that has a custom layout.
 */

public class FriendListAdapter extends ParseQueryAdapter<ParseUser> {

	public FriendListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<ParseUser>() {
			public ParseQuery<ParseUser> create() {
				// Here we can configure a ParseQuery to display
				ParseQuery query = ParseUser.getQuery();
				//query.orderByDescending("score");
				Log.d("FLA", query.toString());
				return query;
			}
		});
	}
	

	@Override
	public View getItemView(ParseUser user, View v, ViewGroup parent) {	
		String realname = "Guest", facebookId = null;
		int score = user.getInt("score");
		int status = user.getInt("status");
		
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
		return v;
	}

}
