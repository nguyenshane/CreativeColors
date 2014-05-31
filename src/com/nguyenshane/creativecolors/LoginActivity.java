package com.nguyenshane.creativecolors;


import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	final static String LOG_TAG = "LoginActivity";
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		context = this;
		/*ImageView iv = (ImageView) findViewById(R.id.image);
		NonScalingBackgroundDrawable nsbd = 
				new NonScalingBackgroundDrawable(getApplicationContext(), iv, R.drawable.login);
		iv.setBackgroundDrawable(nsbd);*/
		
		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the another activity
			showMainActivity();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	public void onLoginFBButtonClicked(View v) {
		TextView tv = (TextView) findViewById(R.id.sign_in_fb);
		tv.setText("Signing in...");
		ProgressBar gb = (ProgressBar) findViewById(R.id.progress_bar);
		gb.setVisibility(View.VISIBLE);
		
		List<String> permissions = Arrays.asList("public_profile","email");

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				//LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(LOG_TAG,
							"Uh oh. The user cancelled the Facebook login.");
					TextView tv = (TextView) findViewById(R.id.sign_in_fb);
					tv.setText("Sign in using Facebook");
					ProgressBar gb = (ProgressBar) findViewById(R.id.progress_bar);
					gb.setVisibility(View.INVISIBLE);
				} else if (user.isNew()) {
					Log.d(LOG_TAG,
							"User signed up and logged in through Facebook!");
					// Fetch Facebook user info if the session is active
					Session session = ParseFacebookUtils.getSession();
					if (session != null && session.isOpened()) {
						makeMeRequest(true);
						showMainActivity();
					}
				} else {
					Log.d(LOG_TAG,
							"User logged in through Facebook!");
					makeMeRequest(false);
					showMainActivity();
				}
			}
		});
	}
	
	private void showMainActivity() {
		Intent intent = new Intent(context, FriendListActivity.class);
		startActivity(intent);		
	}


	private void makeMeRequest(final boolean newUser) {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					// Create a JSON object to hold the profile info
					JSONObject userProfile = new JSONObject();
					try {
						// Populate the JSON object
						userProfile.put("facebookId", user.getId());
						userProfile.put("name", user.getName());


						// Save the user profile info in a user property
						ParseUser currentUser = ParseUser.getCurrentUser();
						currentUser.put("profile", userProfile);
						currentUser.put("username", user.getName());
						
						if (newUser){
							userProfile.put("status", 1);
							userProfile.put("score", 0);
						}
						
						currentUser.saveInBackground();

						// Show another activity
						showMainActivity();


					} catch (JSONException e) {
						Log.d(LOG_TAG, "Error parsing returned user data.");
					}

				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(LOG_TAG,
								"The facebook session was invalidated. LOG OUT");
						TextView tv = (TextView) findViewById(R.id.sign_in_fb);
						tv.setText("Sign in using Facebook");
						ProgressBar gb = (ProgressBar) findViewById(R.id.progress_bar);
						gb.setVisibility(View.INVISIBLE);
						ParseUser.logOut();
					} else {
						Log.d(LOG_TAG, "Some other error: "
								+ response.getError().getErrorMessage());
					}
				}
			}
		});
		request.executeAsync();

	}

}
