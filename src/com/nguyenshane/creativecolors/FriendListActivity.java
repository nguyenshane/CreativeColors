package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseQueryAdapter;

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
		//getMenuInflater().inflate(R.menu.activity_meal_list, menu);
		return true;
	}

	/*
	 * Refreshing the list will be controlled from the Action Bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {

		case R.id.action_refresh: {
			updateMealList();
			break;
		}

		case R.id.action_favorites: {
			showFavorites();
			break;
		}

		case R.id.action_new: {
			newMeal();
			break;
		}
		}*/
		return super.onOptionsItemSelected(item);
	}

	private void updateFriendList() {
		friendAdapter.loadObjects();
		setListAdapter(friendAdapter);
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
