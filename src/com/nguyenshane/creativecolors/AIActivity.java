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
import android.os.Vibrator;
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

public class AIActivity extends Activity {

	static final String LOG_TAG = "MainActivity";
	private ParseUser currentUser;
	private int score = 0;
	private ArrayList<Integer> myArrayButton, oppArrayButton;
	private BroadcastReceiver pushReceiver;
	private SoundPool soundPool;
	private int sound0, sound1, sound2, sound3, soundlose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the Parse object
		currentUser = ParseUser.getCurrentUser();

		oppArrayButton = new ArrayList<Integer>();
		myArrayButton = new ArrayList<Integer>();

		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		sound0 = soundPool.load(this, R.raw.green, 1);
		sound1 = soundPool.load(this, R.raw.yellow, 1);
		sound2 = soundPool.load(this, R.raw.blue, 1);
		sound3 = soundPool.load(this, R.raw.red, 1);
		soundlose = soundPool.load(this, R.raw.lose, 1);
		
		nextTurn();
	}

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

	public void nextTurn(){
		disableButtons();
		setStatus(2); 
		int random = (int) (Math.random() * 4);
		Log.d(LOG_TAG, "Ran " + random);
		oppArrayButton.add(random);
		glowButtonArray(oppArrayButton,800);		
	}
	


	public void checkPattern(){
		disableButtons();
			if (myArrayButton.size() < oppArrayButton.size()){
				if (myArrayButton.get(myArrayButton.size()-1) == oppArrayButton.get(myArrayButton.size()-1)){
					Log.d(LOG_TAG,"I'm correct");
					enableButtons();
				} else	{
					score = myArrayButton.size();
					setStatus(3);
					pushLose();
					myArrayButton.clear();
					Log.d(LOG_TAG,"I lose");
					throwreplay();
				}
			}
			else if (myArrayButton.size() == oppArrayButton.size()){
				if (myArrayButton.get(myArrayButton.size()-1) == oppArrayButton.get(myArrayButton.size()-1)){
					myArrayButton.clear();
					nextTurn();
				} else	{
					score = myArrayButton.size();
					setStatus(3);
					pushLose();
					myArrayButton.clear();
					Log.d(LOG_TAG,"I lose");
					throwreplay();
				}
			}
			else if (myArrayButton.size() > oppArrayButton.size()+1){
				Log.d(LOG_TAG,"I lose because I pressed too much");
				myArrayButton.clear();
				pushLose();
				throwreplay();
			}
	}

	// Set Replay Dialog
	private void throwreplay(){
		vibrate();
		LayoutInflater factory = LayoutInflater.from(this);
		final View replayDialogView = factory.inflate(
				R.layout.replay, null);
		final AlertDialog replayDialog = new AlertDialog.Builder(this).create();
		replayDialog.setView(replayDialogView);
		replayDialog.show();

		((ImageView) replayDialog.findViewById(R.id.replaytitle)).setImageResource(R.drawable.youlose);

		replayDialog.findViewById(R.id.playagain).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//reset
				myArrayButton.clear();
				oppArrayButton.clear();
				setStatus(0);
				enableButtons();
				replayDialog.dismiss();
				nextTurn();
			}
		});
		replayDialog.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//go back
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
			tv.setText("Computer's turn");
			tv.setBackgroundResource(R.drawable.yellow_button);
			break;
		case 2: 
			tv.setText("Showing computer's move");
			tv.setBackgroundResource(R.drawable.red_button);
			break;
		case 3: 
			tv.setText("You lose after " + score + " move(s)!");
			tv.setBackgroundResource(R.drawable.red_button);
			break;
		}	
	}

	public void pushLose(){
		Log.d(LOG_TAG,"I'm pushing my lose");
		disableButtons();
		soundPool.play(soundlose, 1f, 1f, 1, 0, 1f);
		currentUser.increment("score", Math.round(score));
		currentUser.saveInBackground();
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
		new CountDownTimer(duration*(arrayButton.size()+2), duration){
			int count = -1;

			public void onTick(long remainingTimeMillis){
				if(count>-1)
				glowButton(arrayButton.get(count), duration);
				count++;
			}
			public void onFinish(){
				enableButtons();
				setStatus(0);
				vibrate();
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
	
	public void vibrate(){
		// Get instance of Vibrator from current Context
		Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 300 milliseconds
		mVibrator.vibrate(500);
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
