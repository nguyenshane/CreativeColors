package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.PushService;

public class FriendListActivity extends ListActivity {

	private FriendListAdapter friendAdapter;
	private ParseUser currentUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		ListView lv = getListView();
		
		lv.setClickable(true);
		View header = getLayoutInflater().inflate(R.layout.header_friend_list, null);
		lv.addHeaderView(header);
		
		// Set to online
		currentUser = ParseUser.getCurrentUser();
		currentUser.put("status", 1);
		currentUser.saveInBackground();
		
		// Subscribe to channel
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
		String channel;
		channel = "ch" + ParseUser.getCurrentUser().getObjectId();
		PushService.subscribe(this, channel, MainActivity.class);
		
		// Subclass of ParseQueryAdapter
		friendAdapter = new FriendListAdapter(this);
		
		
		
		
		// Set view
		setListAdapter(friendAdapter);
		
	}
	
	@Override
	protected void onResume() {
		updateFriendList();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friendlist, menu);
		return true;
	}
	
	/*
	 * Refreshing the list will be controlled from the Action Bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menuRefresh: {
			updateFriendList();
			break;
		}

		case R.id.menuLogout: {
			logout();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateFriendList() {
		friendAdapter.loadObjects();
		setListAdapter(friendAdapter);
	}
	
	private void logout() {
		// Log the user out
		ParseUser.getCurrentUser().put("status", 0);
		currentUser.saveInBackground();
		ParseUser.logOut();
		
		com.facebook.Session fbs = com.facebook.Session.getActiveSession();
		if (fbs == null) {
		    fbs = new com.facebook.Session(this);
		    com.facebook.Session.setActiveSession(fbs);
		  }
		  fbs.closeAndClearTokenInformation();
		// Back to login activity
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new game has been done, update
			// the list of friends
			updateFriendList();
		}
	}

}
