package com.pirhoalpha.ubiplug_oaq;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * This class creates Splash Screen of the application
 */
public class SplashActivity extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private TextView textView;
    private Animation animFadeIn;
	private DatabaseReaderHelper mDbHelper;
	private int sdk = android.os.Build.VERSION.SDK_INT;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        textView = (TextView)findViewById(R.id.lblLogo);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.zoom_in);
        animFadeIn.setDuration(2000);
        textView.startAnimation(animFadeIn);
        if(sdk>=14)textView.setAlpha((float) 0.7);
        textView.setTextColor(Color.rgb(220, 220, 180));
        
        //textView.setVisibility(View.INVISIBLE);
        
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/roboto-condensed-bold.ttf");
        textView.setTypeface(tf);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	mDbHelper = new DatabaseReaderHelper(getBaseContext());
            	
            	if(mDbHelper.getData()==null){
            		Intent i = new Intent(SplashActivity.this, MainActivity.class);
                	finish();
                	startActivity(i);	
                	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        		}else{
            		Intent i = new Intent(SplashActivity.this, ViewActivity.class);
                	finish();
                	startActivity(i);
                	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        		}
            }
        }, SPLASH_TIME_OUT);
    }
 
    @Override
    public void onBackPressed() {
       return;
    }	
    
}