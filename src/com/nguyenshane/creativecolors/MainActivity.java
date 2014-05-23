package com.nguyenshane.creativecolors;

import android.app.Activity;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.R;

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
		imageButton= (ImageButton)findViewById(R.id.button1);
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
	
    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            imageButton.setBackgroundResource(R.drawable.green_gem_glow);
        }
    };

	public void onClickButton1(View v){
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
	
	//Button GLOW EFFECT//
	button.setOnTouchListener(new OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                // 0x6D6D6D sets how much to darken - tweak as desired
	                setColorFilter(v, 0x6D6D6D);
	                break;
	            // remove the filter when moving off the button
	            // the same way a selector implementation would 
	            case MotionEvent.ACTION_MOVE:
	                Rect r = new Rect();
	                v.getLocalVisibleRect(r);
	                if (!r.contains((int) event.getX(), (int) event.getY())) {
	                    setColorFilter(v, null);
	                }
	                break;
	            case MotionEvent.ACTION_OUTSIDE:
	            case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_UP:
	                setColorFilter(v, null);
	                break;
	        }
	        return false;
	    }

	    private void setColorFilter(View v, Integer filter) {
	        if (filter == null) v.getBackground().clearColorFilter();
	        else {
	            // To lighten instead of darken, try this:
	            LightingColorFilter lighten = new LightingColorFilter(0xFFFFFF, filter);
	            v.getBackground().setColorFilter(lighten);
	        }
	        // required on Android 2.3.7 for filter change to take effect (but not on 4.0.4)
	        v.getBackground().invalidateSelf();
	    }
	});

	}
}
