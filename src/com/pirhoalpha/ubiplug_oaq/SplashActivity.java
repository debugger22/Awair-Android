/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * This class creates Splash Screen of the application
 */
public class SplashActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	private Animation animFadeIn;
	private final int sdk = android.os.Build.VERSION.SDK_INT;
	private TextView textView;

	@Override
	public void onBackPressed() {
		return;
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_splash);
		this.textView = (TextView) findViewById(R.id.lblLogo);
		this.animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.zoom_in);
		this.animFadeIn.setDuration(2000);
		this.textView.startAnimation(this.animFadeIn);
		if (this.sdk >= 14) {
			this.textView.setAlpha((float) 0.7);
		}
		this.textView.setTextColor(Color.rgb(220, 220, 180));

		Typeface tf = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/roboto-condensed-bold.ttf");
		this.textView.setTypeface(tf);
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
				0);
		final boolean installed = settings.getBoolean("installed", false);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				if (!installed) {
					Intent i = new Intent(SplashActivity.this,
							MainActivity.class);
					finish();
					startActivity(i);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				} else {
					Intent i = new Intent(SplashActivity.this,
							ViewActivity.class);
					finish();
					startActivity(i);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			}
		}, SPLASH_TIME_OUT);
	}

}