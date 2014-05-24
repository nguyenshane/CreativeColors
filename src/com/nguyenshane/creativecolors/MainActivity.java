package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
//import com.parse.R;

public class MainActivity extends Activity {
	static final String LOG_TAG = "MainActivity";
	public enum Colors {GREEN, RED, YELLOW, BLUE}
	ParseObject post;
	ParseUser currentUser;
	ParseQuery<ParseObject> query;
	
	//Buttons 
	ImageButton imageButton;

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
		//changing to glow effect after press
		imageButton = (ImageButton)findViewById(R.id.button1);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            imageButton.setBackgroundResource(R.drawable.green_gem_glow);
	        }
	    };
	    imageButton.setOnClickListener(imgButtonHandler);

		
		//Parse Code
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
		//changing to glow effect after press
		imageButton = (ImageButton)findViewById(R.id.button3);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            imageButton.setBackgroundResource(R.drawable.yellow_gem_glow);
	        }
	    };
	    imageButton.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 1); //pushing notification to the cloud
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

	public void onClickButton2(View v){
		//changing to glow effect after press
		imageButton = (ImageButton)findViewById(R.id.button4);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            imageButton.setBackgroundResource(R.drawable.blue_gem_glow);
	        }
	    };
	    imageButton.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 2); //pushing notifications to the cloud
			      gameScore.saveInBackground();
			    }
			  }
			});
	}

	public void onClickButton3(View v){
		//changing to glow effect after press
		imageButton = (ImageButton)findViewById(R.id.button2);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            imageButton.setBackgroundResource(R.drawable.red_gem_glow);
	        }
	    };
	    imageButton.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			  public void done(ParseObject gameScore, ParseException e) {
			    if (e == null) {
			      gameScore.put("button", 3);
			      gameScore.saveInBackground();
			    }
			  }
			});
	}
	
	/**These methods are for notifying the other client that
	 * a new pattern has been submitted **/
	
	//Button 0
	public void buttonGlow0(){
		
	}
	
	//Button 1
	public void buttonGlow1(){
		
	}
	
	//Button 2
	public void buttonGlow2(){
		
	}
	
	//Button 3
	public void buttonGlow3(){
	
	}
}
