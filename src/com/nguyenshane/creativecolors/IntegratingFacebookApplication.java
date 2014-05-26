package com.nguyenshane.creativecolors;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class IntegratingFacebookApplication extends Application {

	static final String TAG = "MyApp";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "InbppWy3s2AhSQ585W2L2pxGp0vKGLnSAW9suDZB", 
				"GMCm8VP1h7COKokS7Ue1cUVAMezkMdPRPMEn0UuX");
		
		// Set Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

	}

}