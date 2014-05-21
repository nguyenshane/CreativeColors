package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.*;

public class MainActivity extends Activity {
	static final String LOG_TAG = "MainActivity";
	public enum Colors {GREEN, RED, YELLOW, BLUE}
	ParseObject post;
	ParseUser currentUser;
	ParseQuery<ParseObject> query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		query = ParseQuery.getQuery("Post");


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClickButton0(View v){
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 0);
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

	public void onClickButton1(View v){
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 1);
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

	public void onClickButton2(View v){
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 2);
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

	public void onClickButton3(View v){
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 3);
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

}
