//Some notes:
// Status : 0 = offline, 1 = ready to play, 2 = playing


package com.nguyenshane.creativecolors;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

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
	private int score = 0;
	private boolean isMyTurn, isQuest, pushLose = false, restartGame = false;
	private ArrayList<Integer> myArrayButton, oppArrayButton;
	private BroadcastReceiver pushReceiver, pushReceiver2;
	private SoundPool soundPool;
	private int sound0, sound1, sound2, sound3, soundwin, soundlose;
	private ImageButton imageButton;
	private String oppId, myId, oppName, myName;
	private AlertDialog replayDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		query = ParseQuery.getQuery("Post");
		currentUser = ParseUser.getCurrentUser();

		oppArrayButton = new ArrayList<Integer>();
		myArrayButton = new ArrayList<Integer>();

		isQuest = true;
		isMyTurn = true;
		myId = "ch" + ParseUser.getCurrentUser().getObjectId();
		oppId = "";	oppName = "";
		myName = ParseUser.getCurrentUser().getUsername();

		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		sound0 = soundPool.load(this, R.raw.green, 1);
		sound1 = soundPool.load(this, R.raw.yellow, 1);
		sound2 = soundPool.load(this, R.raw.blue, 1);
		sound3 = soundPool.load(this, R.raw.red, 1);
		soundwin = soundPool.load(this, R.raw.win, 1);
		soundlose = soundPool.load(this, R.raw.lose, 1);

		newGame();

		// Pull confirm replay
		IntentFilter intentFilter = new IntentFilter("pushedConfirmedRestart");
		pushReceiver2 = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				isQuest = true;
				replayDialog.dismiss();
				unregisterReceiver(pushReceiver);
				oppArrayButton.clear();
				myArrayButton.clear();
				pushLose = false;
				score = 0;
				enableButtons();
				setStatus(0);
				pullOppArray();
			}
		};
		registerReceiver(pushReceiver2, intentFilter);
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
		if (pushReceiver!=null)
			try{
				unregisterReceiver(pushReceiver);
				unregisterReceiver(pushReceiver2);
			}catch (Exception e) {
				// No handle needed
			}
		super.onPause();
	}

	@Override
	protected void onResume() {
		currentUser.put("status", 2);
		currentUser.saveInBackground();
		super.onResume();
	}

	public void nextTurn(){
		// next turn is my turn
		if (!isMyTurn){
			enableButtons();
			isMyTurn = true;	
			setStatus(0);
			if (!isQuest && oppArrayButton.size() == 1) {
				setStatus(2); glowButtonArray(oppArrayButton,1100);
			}
		}
		// next turn is opp turn
		else { 
			disableButtons();
			//pullOppArray();
			isMyTurn = false;
			if(!isQuest){
				setStatus(2);
				glowButtonArray(oppArrayButton,1100);
			}
			nextTurn();
		}
	}

	// restart game
	public void restartGame(){
		unregisterReceiver(pushReceiver);
		oppArrayButton.clear();
		myArrayButton.clear();
		pushLose = false;
		score = 0;

		Log.d(LOG_TAG,"I'm sending push to restart");
		isQuest = false;
		// Push confirm replay
		// send push back confirm to inviter
		try {
			JSONObject object = new JSONObject();
			object.put("action", "pushedConfirmedRestart");   
			//object.put("restartGame", true);
			ParsePush pushToOpp = new ParsePush();
			pushToOpp.setData(object);
			pushToOpp.setChannel(oppId);
			pushToOpp.sendInBackground(new SendCallback() {
				@Override
				public void done(ParseException e) {
					// ready for first turn
					setStatus(1);
					// waiting for first reply
					pullOppArray();
					disableButtons();
					replayDialog.dismiss();
					// Something wrong with push
					if (e != null) ;	
				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

	// Set Replay Dialog
	private void throwreplay(boolean isWon){
		LayoutInflater factory = LayoutInflater.from(this);
		final View replayDialogView = factory.inflate(
				R.layout.replay, null);
		replayDialog = new AlertDialog.Builder(this).create();
		replayDialog.setView(replayDialogView);
		replayDialog.show();

		if(isWon) ((ImageView) replayDialog.findViewById(R.id.replaytitle)).setImageResource(R.drawable.youwon);
		else ((ImageView) replayDialog.findViewById(R.id.replaytitle)).setImageResource(R.drawable.youlose);

		if(!isQuest){
			Button bt = (Button) replayDialog.findViewById(R.id.playagain);
			bt.setText("Waiting...");
			bt.setEnabled(false);

		} else{
			replayDialog.findViewById(R.id.playagain).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					restartGame = true;
					//Set waiting and disable button
					Button bt = (Button) replayDialog.findViewById(R.id.playagain);
					bt.setText("Waiting...");
					bt.setEnabled(false);
					restartGame();
				}
			});
		}
		replayDialog.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//your business logic 
				replayDialog.dismiss();
				finish();
			}
		});
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
			tv.setText("Your score is: " + score);
			tv.setBackgroundResource(R.drawable.red_button);
			break;
		case 4: 
			tv.setText("Your score is: " + score);
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
						// waiting for first reply
						pullOppArray();
						// Something wrong with push
						if (e != null) ;	
					}
				});
			} catch (JSONException e) {e.printStackTrace();}
		}
	}

	public void pushLose(){
		Log.d(LOG_TAG,"I'm pushing my lose");
		throwreplay(false);
		disableButtons();
		soundPool.play(soundlose, 1f, 1f, 1, 0, 1f);
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
		throwreplay(true);
		soundPool.play(soundwin, 1f, 1f, 1, 0, 1f);
		myArrayButton.clear();
		disableButtons();
	}


	public void pushMyArray(){
		Log.d(LOG_TAG,"I'm pushing");
		score = myArrayButton.size();
		setStatus(1);
		disableButtons();

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
					//pullOppArray();

					//Something wrong with push
					if (e != null) ;

				}
			});
		} catch (JSONException e) {e.printStackTrace();}

	}

	public void pullOppArray(){
		IntentFilter intentFilter = new IntentFilter("pushedArrayButton");

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
				if (myArrayButton.size() == oppArrayButton.size() && myArrayButton.equals(oppArrayButton)) {
					pushMyArray();
					disableButtons();
				} else {
					Log.d(LOG_TAG,"I'm correct");
					enableButtons();
				}
			} else {
				setStatus(3);
				pushLose();
				myArrayButton.clear();
				Log.d(LOG_TAG,"I lose");
			}
		}

	}

	public void onClickButton0(View v){	
		soundPool.play(sound0, 1f, 1f, 1, 0, 1f);
		myArrayButton.add(0);
		checkPattern();
	}

	public void onClickButton1(View v){
		soundPool.play(sound1, 1f, 1f, 1, 0, 1f);
		myArrayButton.add(1);
		checkPattern();
	}

	public void onClickButton2(View v){
		soundPool.play(sound2, 1f, 1f, 1, 0, 1f);
		myArrayButton.add(2);
		checkPattern();
	}

	public void onClickButton3(View v){
		soundPool.play(sound3, 1f, 1f, 1, 0, 1f);
		myArrayButton.add(3);
		checkPattern();
	}

	public void glowButtonArray(final ArrayList<Integer> arrayButton, final long duration){
		disableButtons();
		// show status of showing glow
		setStatus(2);
		new CountDownTimer(duration*(arrayButton.size()+1), duration){
			int count = 0;

			public void onTick(long remainingTimeMillis){
				glowButton(arrayButton.get(count), duration);
				//Log.d(LOG_TAG,arrayButton.get(count).toString());
				count++;
			}
			public void onFinish(){
				enableButtons();
				setStatus(0);
			}
		}.start();
		// show status of my turn

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
		//duration -= 100;
		switch(buttonId) {
		case 0: 
			ImageButton ib0 = (ImageButton) findViewById(R.id.button0);
			ib0.setBackgroundResource(R.drawable.green_gem_glow);	
			soundPool.play(sound0, 1f, 1f, 1, 0, 1f);
			new CountDownTimer(duration-100, duration-100){
				public void onTick(long remainingTimeMillis){}
				public void onFinish(){					
					ImageButton ib0 = (ImageButton) findViewById(R.id.button0);
					ib0.setBackgroundResource(R.drawable.green_button_controller);
				}
			}.start();
			break;
		case 1: 
			ImageButton ib1 = (ImageButton) findViewById(R.id.button1);
			ib1.setBackgroundResource(R.drawable.yellow_gem_glow);
			soundPool.play(sound1, 1f, 1f, 1, 0, 1f);
			new CountDownTimer(duration-100, duration-100){
				public void onTick(long remainingTimeMillis){}
				public void onFinish(){
					ImageButton ib1 = (ImageButton) findViewById(R.id.button1);
					ib1.setBackgroundResource(R.drawable.yellow_button_controller);
				}
			}.start();
			break;
		case 2: 
			ImageButton ib2 = (ImageButton) findViewById(R.id.button2);
			ib2.setBackgroundResource(R.drawable.blue_gem_glow);	
			soundPool.play(sound2, 1f, 1f, 1, 0, 1f);
			new CountDownTimer(duration-100, duration-100){
				public void onTick(long remainingTimeMillis){}
				public void onFinish(){
					ImageButton ib2 = (ImageButton) findViewById(R.id.button2);
					ib2.setBackgroundResource(R.drawable.blue_button_controller);
				}
			}.start();
			break;
		case 3: 
			ImageButton ib3 = (ImageButton) findViewById(R.id.button3);
			ib3.setBackgroundResource(R.drawable.red_gem_glow);
			// Sound
			soundPool.play(sound3, 1f, 1f, 1, 0, 1f);
			new CountDownTimer(duration-100, duration-100){
				public void onTick(long remainingTimeMillis){}
				public void onFinish(){

					ImageButton ib3 = (ImageButton) findViewById(R.id.button3);
					ib3.setBackgroundResource(R.drawable.red_button_controller);
				}
			}.start();
			break;
		}
	}
}
