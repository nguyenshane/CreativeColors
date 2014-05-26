package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class FriendListActivity extends ListActivity {

	//private ParseQueryAdapter<User> mainAdapter;
	private FriendListAdapter friendAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getListView().setClickable(true);
		
		// Subclass of ParseQueryAdapter
		friendAdapter = new FriendListAdapter(this);

		// Set view
		setListAdapter(friendAdapter);
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


	private void newGame() {
		//Intent i = new Intent(this, NewMealActivity.class);
		//startActivityForResult(i, 0);
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
