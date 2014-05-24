// Some notes:
// Status : 0 = offline, 1 = ready to play, 2 = my turn, 3 = opp turn, 4 = I won, 5 = opp won

package com.nguyenshane.creativecolors;

import java.util.ArrayList;
import java.util.Timer;

import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.parse.SendCallback;

public class MainActivity extends Activity {
	static final String LOG_TAG = "MainActivity";
	public enum Colors {GREEN, RED, YELLOW, BLUE}
	private ParseObject post;
	private ParseUser currentUser, currOpp;
	private ParseQuery<ParseObject> query;
	private ImageButton ib;
	private int Rid, Rcontroller, Rglow;
	private boolean isMyTurn, isQuest;
	private ArrayList<Integer> myArrayButton, oppArrayButton;
	private String oppChannel;


	//Buttons 
	ImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		query = ParseQuery.getQuery("Post");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		PushService.subscribe(this, "pJDqv2DPP4", MainActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();

		oppArrayButton = new ArrayList<Integer>();
		myArrayButton = new ArrayList<Integer>();
		oppArrayButton.add(0);
		oppArrayButton.add(1);
		oppArrayButton.add(2);
		oppArrayButton.add(3);


		glowButtonArray(oppArrayButton,1000);

		isQuest = true;
		isMyTurn = true;
		oppChannel = "pJDqv2DPP4";

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

	public void checkTurn(){
		int status = currentUser.getInt("status");
		if(status == 2) isMyTurn = true;
		else isMyTurn = false;
	}

	public void nextTurn(){
		if (isMyTurn){
			currentUser.put("status", 2);
			enableButtons();
		}
		else { 
			currentUser.put("status", 3);
			disableButtons();
		}
	}


	//setup new game
	public void newGame(){
		currentUser = ParseUser.getCurrentUser();

		Intent intent = getIntent();
		isMyTurn = intent.getBooleanExtra("isMyTurn",true);
		isQuest = intent.getBooleanExtra("isQuest", true);

	}

	public void pushMyArray(){
		Log.d(LOG_TAG,"I'm pushing");

		try {
			JSONObject object = new JSONObject();
			//object.put("alert", "Alert");
			//object.put("title", "pushedArrayButton");
			object.put("action", "pushedArrayButton");   
			object.put("pushedArrayButton", myArrayButton);
			ParsePush pushToOpp = new ParsePush();
			pushToOpp.setData(object);
			pushToOpp.setChannel(oppChannel);

			pushToOpp.sendInBackground(new SendCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) ;
					//Something wrong with push
				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

	public void checkPattern(){
		disableButtons();
		if(isQuest){
			if (myArrayButton.size() <= oppArrayButton.size()){
				if (myArrayButton.get(myArrayButton.size()-1) == oppArrayButton.get(myArrayButton.size()-1)){
					Log.d(LOG_TAG,"I'm correct");
				} else	Log.d(LOG_TAG,"I lose");
			}
			if (myArrayButton.size() == oppArrayButton.size()+1){
				pushMyArray();
			}
			if (myArrayButton.size() > oppArrayButton.size()+1){
				Log.d(LOG_TAG,"I lose because I pressed too much");
			}
		}
		else {
			if (myArrayButton.get(myArrayButton.size()-1) == oppArrayButton.get(myArrayButton.size()-1)){
				if (myArrayButton.size() == oppArrayButton.size() && myArrayButton.equals(oppArrayButton)) pushMyArray();
				Log.d(LOG_TAG,"I'm correct");
			} else Log.d(LOG_TAG,"I lose");
		}
		enableButtons();
	}

	public void onClickButton0(View v){	
		//Parse Code
		/*query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 0);
					pObj.saveInBackground();
				}
			}
		});*/
		myArrayButton.add(0);
		checkPattern();

	}

	public void onClickButton1(View v){
		/*query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 1); //pushing notification to the cloud
					pObj.saveInBackground();
				}
			}
		});*/
		myArrayButton.add(1);
		checkPattern();
	}

	public void onClickButton2(View v){
		/*query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 2); //pushing notifications to the cloud
					pObj.saveInBackground();
				}
			}
		});*/
		myArrayButton.add(2);
		checkPattern();
	}

	public void onClickButton3(View v){
		/*query.getInBackground("gVEyPd7NMM", new GetCallback<ParseObject>() {
			public void done(ParseObject pObj, ParseException e) {
				if (e == null) {
					pObj.put("button", 3);
					pObj.saveInBackground();
				}
			}
		});*/
		myArrayButton.add(3);
		checkPattern();
	}

	public void glowButtonArray(final ArrayList<Integer> arrayButton, final long duration){
		disableButtons();

		CountDownTimer timer2 = new CountDownTimer(duration*(arrayButton.size()+1), duration){
			int count = 0;

			public void onTick(long remainingTimeMillis){
				glowButton(arrayButton.get(count), duration);
				Log.d(LOG_TAG,arrayButton.get(count).toString());
				count++;

			}
			public void onFinish(){
				enableButtons();
			}
		}.start();

	}

	public void disableButtons(){
		ImageButton tmp = (ImageButton) findViewById(R.id.button0);
		tmp.setClickable(false); tmp.setEnabled(false);
		tmp = (ImageButton) findViewById(R.id.button1);
		tmp.setClickable(false); tmp.setEnabled(false);
		tmp = (ImageButton) findViewById(R.id.button2);
		tmp.setClickable(false); tmp.setEnabled(false);
		tmp = (ImageButton) findViewById(R.id.button3);
		tmp.setClickable(false); tmp.setEnabled(false);
	}

	public void enableButtons(){
		ImageButton tmp = (ImageButton) findViewById(R.id.button0);
		tmp.setClickable(true); tmp.setEnabled(true);
		tmp = (ImageButton) findViewById(R.id.button1);
		tmp.setClickable(true); tmp.setEnabled(true);
		tmp = (ImageButton) findViewById(R.id.button2);
		tmp.setClickable(true); tmp.setEnabled(true);
		tmp = (ImageButton) findViewById(R.id.button3);
		tmp.setClickable(true); tmp.setEnabled(true);
	}

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
		/*button.setOnTouchListener(new OnTouchListener() {
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
	}); */

	}
}
