package com.nguyenshane.creativecolors;

import java.util.ArrayList;
import java.util.Timer;

import android.app.Activity;
<<<<<<< HEAD
=======
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;
>>>>>>> 4fadb09014a52cb44f13c3d22704480be6a6c9b4
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
//import com.parse.R;

public class MainActivity extends Activity {
	static final String LOG_TAG = "MainActivity";
	public enum Colors {GREEN, RED, YELLOW, BLUE}
	private ParseObject post;
	private ParseUser currentUser;
	private ParseQuery<ParseObject> query;
	private ImageButton ib;
	private int Rid, Rcontroller, Rglow;

	//Buttons 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		query = ParseQuery.getQuery("Post");

		ArrayList<Integer> arrayButton = new ArrayList<Integer>();
		arrayButton.add(0);
		arrayButton.add(1);
		arrayButton.add(2);
		arrayButton.add(3);
		arrayButton.add(2);
		arrayButton.add(3);
		arrayButton.add(1);
		arrayButton.add(0);
		
		glowButtonArray(arrayButton,1000);
		
		//glowButton(0,1000);
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
		ib = (ImageButton)findViewById(R.id.button1);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            ib.setBackgroundResource(R.drawable.green_gem_glow);
	        }
	    };
	    ib.setOnClickListener(imgButtonHandler);

		//Parse Code
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 0);
					pObj.saveInBackground();
				}
			}
		});
	}


	public void onClickButton1(View v){
		//changing to glow effect after press
		ib = (ImageButton)findViewById(R.id.button3);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            ib.setBackgroundResource(R.drawable.yellow_gem_glow);
	        }
	    };
	    ib.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 1); //pushing notification to the cloud
					pObj.saveInBackground();
				}
			}
		});
	}

	public void onClickButton2(View v){
		//changing to glow effect after press
		ib = (ImageButton)findViewById(R.id.button4);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            ib.setBackgroundResource(R.drawable.blue_gem_glow);
	        }
	    };
	    ib.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 2); //pushing notifications to the cloud
					pObj.saveInBackground();
				}
			}
		});
	}

	public void onClickButton3(View v){
		//changing to glow effect after press
		ib = (ImageButton)findViewById(R.id.button2);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            ib.setBackgroundResource(R.drawable.red_gem_glow);
	        }
	    };
	    ib.setOnClickListener(imgButtonHandler);
	    
	    //Parse
		query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 3);
					pObj.saveInBackground();
				}
			}
		});
	}
	
	public void glowButtonArray(final ArrayList<Integer> arrayButton, final long duration){
		//glowButton(arrayButton.get(0), duration);
		
		CountDownTimer timer2 = new CountDownTimer(duration*(arrayButton.size()+1), duration){
			int count = 0;
			
			public void onTick(long remainingTimeMillis){
				glowButton(arrayButton.get(count), duration);
				Log.d(LOG_TAG,arrayButton.get(count).toString());
				count++;
				
			}
			public void onFinish(){
				//glowButton(arrayButton.get(count), duration);
				//Log.d(LOG_TAG,arrayButton.get(count).toString());
			}
		}.start();
		
	}

	/**This method is for notifying the other client that
	 * a new pattern has been submitted - The glow effect**/
	public void glowButton(int buttonId, long duration){
		switch(buttonId) {
			case 0: Rid = R.id.button0; 
					Rcontroller = R.drawable.green_button_controller; 
					Rglow = R.drawable.green_gem_glow;
					break;
			case 1: Rid = R.id.button1; 
					Rcontroller = R.drawable.yellow_button_controller;
					Rglow = R.drawable.yellow_gem_glow;
					break;
			case 2: Rid = R.id.button2; 
					Rcontroller = R.drawable.blue_button_controller;
					Rglow = R.drawable.blue_gem_glow;
					break;
			case 3: Rid = R.id.button3; 
					Rcontroller = R.drawable.red_button_controller;
					Rglow = R.drawable.red_gem_glow;
					break;
			default:Rid = R.id.button0; 
					Rcontroller = R.drawable.green_button_controller;
					Rglow = R.drawable.green_gem_glow;
					break;
		}

		ib = (ImageButton) findViewById(Rid);
		ib.setBackgroundResource(Rglow);
		
		CountDownTimer timer = new CountDownTimer(duration-5, duration-5){
			public void onTick(long remainingTimeMillis){}
			public void onFinish(){
				ib.setBackgroundResource(Rcontroller);
			}
		}.start();

	}

}
