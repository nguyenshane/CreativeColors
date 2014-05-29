//Some notes:
// Status : 0 = offline, 1 = ready to play, 2 = playing


package com.nguyenshane.creativecolors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SendCallback;

public class MainActivity extends Activity {
	
	
	static final String LOG_TAG = "MainActivity";
	public enum Colors {GREEN, RED, YELLOW, BLUE} // AI
	private ParseObject post;
	private ParseUser currentUser, currOpp;
	private ParseQuery<ParseObject> query;
	private ImageButton ib;
	private int Rid, Rcontroller, Rglow, score = 0;
	private boolean isMyTurn, isQuest, pushLose = false;
	private ArrayList<Integer> myArrayButton, oppArrayButton;
	private String oppChannel;
	

	private String oppId, myId, oppName, myName;


	//Buttons 
	ImageButton imageButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		query = ParseQuery.getQuery("Post");
		currentUser = ParseUser.getCurrentUser();

		oppArrayButton = new ArrayList<Integer>();
		myArrayButton = new ArrayList<Integer>();

		/*oppArrayButton.add(0);
		oppArrayButton.add(1);
		oppArrayButton.add(2);
		oppArrayButton.add(3);

		glowButtonArray(oppArrayButton,1000);*/

		isQuest = true;
		isMyTurn = true;
		myId = "ch" + ParseUser.getCurrentUser().getObjectId();
		oppId = "";
		oppName = "";
		myName = ParseUser.getCurrentUser().getUsername();

		newGame();

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
	
//-------------------AI - COLOR Enums -------------------//
	
	//Computer enters random color pattern for user
	public Colors[] insertCode(){
		Random rand = new Random(); 
		Colors[] code = new Colors[8]; //perhaps this should be a fixed value? 
		for(int i = 0; i < 8; i++){
			int c = rand.nextInt(3); //inserts random number from 0 to 3
			if(c == 0){
				code[i] = Colors.GREEN;
			}else if(c == 1){
				code[i] = Colors.RED;
			}else if(c == 2){
				code[i] = Colors.YELLOW;
			}else{
				code[i] = Colors.BLUE;
			}
		}
		return code; //return the new array of pattern inputs
	}
	
	
//-------------------------------------------------------//

	@Override
	protected void onPause() {
		currentUser.put("status", 1);
		currentUser.saveInBackground();
		super.onPause();
	}

	@Override
	protected void onResume() {
		currentUser.put("status", 2);
		currentUser.saveInBackground();
		super.onResume();
	}

	public void checkTurn(){
		int status = currentUser.getInt("status");
		if(status == 2) isMyTurn = true;
		else isMyTurn = false;
	}

	public void nextTurn(){
		// next turn is my turn
		if (!isMyTurn){
			enableButtons();
			isMyTurn = true;	
			setStatus(0);
			if (!isQuest && oppArrayButton.size() == 1) {
				setStatus(2); glowButtonArray(oppArrayButton,1000);
			}
		}
		// next turn is opp turn
		else { 
			disableButtons();
			pullOppArray();
			isMyTurn = false;
			if(!isQuest){
				setStatus(2);
				glowButtonArray(oppArrayButton,1000);
			}
			nextTurn();
		}
	}

	public void setStatus(int status){
		// 0: my turn, 1: opp turn, 2: showing opp turn, 3: lose, 4: won
		TextView tv = (TextView) findViewById(R.id.textViewStatus);
		switch(status) {
		case 0: 
			tv.setText("Your turn");
			tv.setBackgroundResource(R.drawable.green_button);
			break;
		case 1: 
			tv.setText(oppName + "'s turn");
			tv.setBackgroundResource(R.drawable.yellow_button);
			break;
		case 2: 
			tv.setText("Showing " + oppName + "'s move");
			tv.setBackgroundResource(R.drawable.red_button);
			break;
		case 3: 
			tv.setText("You lose!");
			tv.setBackgroundResource(R.drawable.red_button);
			break;
		case 4: 
			tv.setText("You won!");
			tv.setBackgroundResource(R.drawable.blue_button);
			break;
		}	
	}


	//setup new game
	public void newGame(){

		// setup 'invitee' to be the Simon (Quest), 
		// 'inviter' to be Follower 
		Intent intent = getIntent();
		if (intent.getBooleanExtra("confirmed",false)==true){
			isMyTurn = false;
			isQuest = false;
			oppId = intent.getStringExtra("oppId");
			oppName = intent.getStringExtra("oppName");
			setStatus(1);
			disableButtons();
			// waiting for first reply
			pullOppArray();
		}
		else if (intent.getBooleanExtra("invitation",false)==true){
			setStatus(0);
			oppId = intent.getStringExtra("oppId");
			oppName = intent.getStringExtra("oppName");
			
			// send push back confirm to inviter
			try {
				JSONObject object = new JSONObject();
				object.put("action", "pushedConfirm");   
				object.put("myId", myId);
				object.put("myName", myName);

				ParsePush pushToOpp = new ParsePush();
				pushToOpp.setData(object);
				pushToOpp.setChannel(oppId);
				pushToOpp.sendInBackground(new SendCallback() {
					@Override
					public void done(ParseException e) {
						// ready for first turn
						setStatus(0);
						// Something wrong with push
						if (e != null) ;	
					}
				});
			} catch (JSONException e) {e.printStackTrace();}
		}
	}

	public void pushLose(){
		Log.d(LOG_TAG,"I'm pushing my lose");
		disableButtons();
		try {
			JSONObject object = new JSONObject();
			object.put("action", "pushedArrayButton");
			object.put("pushedLose", true);
			object.put("pushedArrayButton", myArrayButton);
			ParsePush pushToOpp = new ParsePush();
			pushToOpp.setData(object);
			pushToOpp.setChannel(oppId);

			pushToOpp.sendInBackground(new SendCallback() {
				@Override
				public void done(ParseException e) {
					currentUser.increment("score", Math.round(score/2));
					myArrayButton.clear();
					//Something wrong with push
					if (e != null) ;			
				}
			});
		} catch (JSONException e) {e.printStackTrace();}
	}

	public void setWin(){
		currentUser.increment("score", Math.round(score));
		setStatus(4);
		myArrayButton.clear();
		disableButtons();
	}


	public void pushMyArray(){
		Log.d(LOG_TAG,"I'm pushing");
		score = myArrayButton.size();
		setStatus(1);

		try {
			JSONObject object = new JSONObject();
			object.put("action", "pushedArrayButton");   
			object.put("pushedArrayButton", myArrayButton);
			object.put("pushedLose", false);
			ParsePush pushToOpp = new ParsePush();
			pushToOpp.setData(object);
			pushToOpp.setChannel(oppId);

			pushToOpp.sendInBackground(new SendCallback() {
				@Override
				public void done(ParseException e) {
					pullOppArray();

					//Something wrong with push
					if (e != null) ;

				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

	public void pullOppArray(){
		//pullLose();
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
					//Log.d(LOG_TAG,"message pulled: " + message);
					//pushLose = jObject.getBoolean("pushedLose");
					Gson gson = new Gson();
					Type arrayButtonType = new TypeToken<ArrayList<Integer>>() {}.getType();
					oppArrayButton = gson.fromJson(jObject.getString("pushedArrayButton"),arrayButtonType);
					pushLose = jObject.getBoolean("pushedLose");

					Log.d(LOG_TAG,"pushedLose pulled: " + jObject.getString("pushedLose"));


				} catch (JSONException e) {	e.printStackTrace();} 

				if(pushLose == false){
					// Switch back to my turn
					myArrayButton.clear();
					nextTurn();
					Log.d(LOG_TAG,"I'm here");
					
				} else setWin();
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
					enableButtons();
				} else	{
					setStatus(3);
					pushLose();
					myArrayButton.clear();
					Log.d(LOG_TAG,"I lose");
				}
			}
			if (myArrayButton.size() == oppArrayButton.size()+1){
				pushMyArray();
				myArrayButton.clear();
				disableButtons();
			}
			if (myArrayButton.size() > oppArrayButton.size()+1){
				Log.d(LOG_TAG,"I lose because I pressed too much");
				myArrayButton.clear();
				pushLose();
			}
		}
		else {
			if (myArrayButton.get(myArrayButton.size()-1) == oppArrayButton.get(myArrayButton.size()-1)){
				if (myArrayButton.size() == oppArrayButton.size() && myArrayButton.equals(oppArrayButton)) pushMyArray();
				Log.d(LOG_TAG,"I'm correct");
				enableButtons();
			} else {
				setStatus(3);
				pushLose();
				myArrayButton.clear();
				Log.d(LOG_TAG,"I lose");
			}
		}

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
		myArrayButton.add(1);
		checkPattern();
	}

	public void onClickButton2(View v){
		myArrayButton.add(2);
		checkPattern();
	}

	public void onClickButton3(View v){
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
				setStatus(0);
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

		CountDownTimer timer = new CountDownTimer(duration-100, duration-100){
			public void onTick(long remainingTimeMillis){}
			public void onFinish(){
				ib.setBackgroundResource(Rcontroller);
			}
		}.start();

	}
}
