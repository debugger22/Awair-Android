package com.pirhoalpha.ubiplug_oaq;

import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GasDetail extends Activity{

	private TextView gasName;
	private TextView gasIntro;
	private TextView gasEffects;
	private LinearLayout container;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gas_detail);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(android.os.Build.VERSION.SDK_INT>16)getActionBar().setIcon(getResources().getDrawable(R.drawable.logo_action));
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/open-sans-regular.ttf");
		container = (LinearLayout)findViewById(R.id.container_gas_detail);
		container.setBackgroundColor(Color.WHITE);
		gasName = (TextView)findViewById(R.id.txtGasName);
		gasIntro = (TextView)findViewById(R.id.txtGasIntro);
		gasEffects = (TextView)findViewById(R.id.txtGasEffects);
		gasName.setTypeface(tf);
		gasIntro.setTypeface(tf);
		gasEffects.setTypeface(tf);
		String[] data = (String[])getIntent().getSerializableExtra("data");
		gasName.setText(data[0]);
		gasIntro.setText(data[1]);
		gasEffects.setText(data[2]);
		getActionBar().setTitle(data[0]);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    getActionBar().setHomeButtonEnabled(true);
	    
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed in the action bar.
	            // Create a simple intent that starts the hierarchical parent activity and
	            // use NavUtils in the Support Package to ensure proper handling of Up.
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
}
