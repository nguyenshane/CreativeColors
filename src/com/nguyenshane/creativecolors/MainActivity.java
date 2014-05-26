// Some notes:
// Status : 0 = offline, 1 = ready to play, 2 = my turn, 3 = opp turn, 4 = I won, 5 = opp won

package com.nguyenshane.creativecolors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
>>>>>>> 585bf678de856b5eb195faaa27cb86da033cf7d2
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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



	public void onClickButton0(View v){
		//changing to glow effect after press
		ib = (ImageButton)findViewById(R.id.button1);
	    View.OnClickListener imgButtonHandler = new View.OnClickListener() {
	        public void onClick(View v) {
	            ib.setBackgroundResource(R.drawable.green_gem_glow);
	        }
	    };
	    
	    ib.setOnClickListener(imgButtonHandler);
	}
	    
	public void checkTurn(){
		int status = currentUser.getInt("status");
		if(status == 2) isMyTurn = true;
		else isMyTurn = false;
	}

	public void nextTurn(){
		// next turn is my turn
		if (!isMyTurn){
			//currentUser.put("status", 2);
			enableButtons();
			isMyTurn = true;	
		}
		// next turn is opp turn
		else { 
			//currentUser.put("status", 3);
			disableButtons();
			pullOppArray();
			isMyTurn = false;
			glowButtonArray(oppArrayButton,1000);
			nextTurn();
		}
		/*
		try {
			currentUser.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
					myArrayButton.clear();
					pullOppArray();
					
					//Something wrong with push
					if (e != null) ;
					
				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

	public void pullOppArray(){
		IntentFilter intentFilter = new IntentFilter("pushedArrayButton");
		BroadcastReceiver pushReceiver;
		pushReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Bundle extras = intent.getExtras();
				String message = extras != null ? extras.getString("com.parse.Data") : ""; 
				
				// Parsing JSON to ArrayButton
				JSONObject jObject;
				try {
					jObject = new JSONObject(message);
					Gson gson = new Gson();
					Type arrayButtonType = new TypeToken<ArrayList<Integer>>() {}.getType();
					oppArrayButton = gson.fromJson(jObject.getString("pushedArrayButton"),arrayButtonType);
					
					Log.d(LOG_TAG,"oppArrayButton pulled: " + oppArrayButton);
					
					
				} catch (JSONException e) {	e.printStackTrace();} 
				
				
				// Switch back to my turn
				nextTurn();
				
				
			}
		};
		registerReceiver(pushReceiver, intentFilter);
		
		
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

		CountDownTimer timer = new CountDownTimer(duration-100, duration-100){
			public void onTick(long remainingTimeMillis){}
			public void onFinish(){
				ib.setBackgroundResource(Rcontroller);
			}
		}.start();

	}

}
