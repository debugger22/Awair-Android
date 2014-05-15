/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import java.util.HashMap;


import com.google.analytics.tracking.android.EasyTracker;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HealthTipsActivity extends Activity{
	
	private HashMap<String,String> data;
	private TextView title;
	private TextView lblMessage;
	private TextView lblRecommendation;
	private Button cmdUpdate;
	private Button cmdView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_tips);
	    
		
	}
	

	@SuppressLint("NewApi")
	@Override
	protected void onStart(){
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		if(android.os.Build.VERSION.SDK_INT>=15)getActionBar().setHomeButtonEnabled(true);
		Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/open-sans-regular.ttf");
		data = (HashMap<String,String>)getIntent().getSerializableExtra("data");
		
		
		title = (TextView)findViewById(R.id.lblTitle);
		lblMessage = (TextView)findViewById(R.id.lblMessage);
		lblRecommendation = (TextView)findViewById(R.id.lblRecommendation);
		cmdUpdate = (Button)findViewById(R.id.cmdUpdate);
		cmdView = (Button)findViewById(R.id.cmdView);
		
		title.setTypeface(tf);
		lblMessage.setTypeface(tf);
		lblRecommendation.setTypeface(tf);
		
		Log.v("NOTF_TYPE",data.get("type"));
		if(data.get("type").intern()=="gas_hike"){
			title.setText("Increased level of "+data.get("sub_type"));
			lblMessage.setText(data.get("message"));
			lblRecommendation.setText(data.get("recommendation"));
		}else if(data.get("type").intern()=="update_available"){
			title.setText(data.get("title"));
			lblMessage.setText(data.get("message"));
			cmdUpdate.setVisibility(View.VISIBLE);
			cmdUpdate.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					final String appPackageName = "com.pirhoalpha.ubiplug_oaq";// getPackageName() from Context or Activity object
					try {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
					}
					finish();
				}
				
			});  
			
		}else if(data.get("type").intern()=="inactivity"){
			title.setText(data.get("title"));
			lblMessage.setText(data.get("message"));
			cmdView.setVisibility(View.VISIBLE);
			cmdView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent i = new Intent(HealthTipsActivity.this,ViewActivity.class);
		            startActivity(i);
		        	finish();
				}
				
			});
		}else{
			title.setText(data.get("title"));
			lblMessage.setText(data.get("message"));
			cmdView.setVisibility(View.VISIBLE);
			cmdView.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent i = new Intent(HealthTipsActivity.this,ViewActivity.class);
		            startActivity(i);
		        	finish();
				}
				
			});			
		}
	}

	@Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	    finish();
	  }
	
	@Override
	  public void onPause() {
	    super.onPause();
	    finish();
	  }
	@Override
	  public void onDestroy() {
	    super.onDestroy();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	    finish();
	  }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed in the action bar.
	            // Create a simple intent that starts the hierarchical parent activity and
	            // use NavUtils in the Support Package to ensure proper handling of Up.
	            Intent i = new Intent(this,ViewActivity.class);
	            startActivity(i);
	        	finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
