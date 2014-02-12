package com.pirhoalpha.ubiplug_oaq;



import java.io.File;
import java.io.IOException;


import java.util.HashMap;
import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.analytics.tracking.android.EasyTracker;


import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.todddavies.components.progressbar.ProgressWheel;

//Weather classes
import zh.wang.android.apis.yweathergetter4a.MyLog;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo.ForecastInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather.SEARCH_MODE;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener,YahooWeatherInfoListener{
	private DatabaseReaderHelper mDbHelper;
	private HashMap<String,String> data;
	private Boolean GSMDataAdded = false;
	private Double lat=(double) 0;
	private Double lng=(double) 0;
	private Animation animFadeIn;
	private Animation animFadeIn1000;
	private Animation animFadeOut;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private AdView adView;
	private RelativeLayout layout;
	private LinearLayout adLayout;
	private FrameLayout viewFrame;
	private ProgressWheel pwoverall;
	private ImageView background;
	private ImageView backgroundBlurred;
	private ImageView gaseousImage;
	private ImageView particleImage;
	private ImageView chemicalImage;
	private TextView gaseousText;
	private TextView particleText;
	private TextView chemicalText;
	private LinearLayout particleContainer;
	private LinearLayout chemicalContainer;
	private LinearLayout gaseousContainer;
	
	
	//some random variables
    private static final int FRAME_TIME_MS = 1;
    private static final String KEY = "i";
	private static final String MY_AD_UNIT_ID = "a152dd01083ade1";	//Awair ad unit id
    boolean isRunning = false;
    private String deviceId;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "com.pirhoalpha.ubiplug_oaq";
    private static final String PROPERTY_APP_VERSION = "5";
    public String emailId;
    String SENDER_ID = "487700552253"; //Awair project Id
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    GoogleCloudMessaging gcm;
    String regid;
    
	//Data variables
	public String date;
	public String city_name;
	public int minpm25=0, pm25=0, maxpm25=0;
	public int mino3=0, o3=0, maxo3=0;
	public int minno2=0, no2=0, maxno2=0;
	public int minso2=0, so2=0, maxso2=0;
	public int minco=0, co=0, maxco=0;
	public int mindew=0, dew=0, maxdew=0;
	public int minwind=0, wind=0, maxwind=0;
	public int mintemperature=0, temperature=0, maxtemperature=0;
	public int minpressure=0, pressure=0, maxpressure=0;
	public int minhumidity=0, humidity=0, maxhumidity=0;
	
	//View variables
	private TextView lblAirQuality;
	private TextView lblParticlePollutantsHeading;
	private TextView lblGaseousPollutantsHeading;
	private TextView lblChemicalPollutantsHeading;
	private MenuItem mnuShowCity;
	private ScrollView container;
	private MenuItem mnuGetDetails;
	
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i= msg.getData().getInt(KEY);
                pwoverall.setProgress((int)(i*3.6));
                pwoverall.setText(""+i+"%");
            } catch (Exception err) {
            	Log.v("Thread Error",err.toString());
            }
        }
    };
   
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if(android.os.Build.VERSION.SDK_INT>=15){
			getActionBar().setIcon(R.drawable.home);
			getActionBar().setTitle(R.string.app_name);
		}

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//-------------------------------------------Registering for GCM--------------------------------------
		context = ViewActivity.this;	
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
			
            if (regid.isEmpty()) {
                registerInBackground();
            }
	    }else{
	    	Toast.makeText(this, "Your device does not support push notifications", Toast.LENGTH_SHORT).show();
	    }
		
		//-------------------------------------------Registering for GCM--------------------------------------
		
		Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/open-sans-regular.ttf");
		Typeface tfhl = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/helvetica-light.ttf");
		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
			locationclient = new LocationClient(this,this,this);
			locationclient.connect();
		}
		else{
			Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
		}
		
		
		mDbHelper = new DatabaseReaderHelper(getBaseContext());
		data = (HashMap<String, String>) mDbHelper.getData();
		mDbHelper.close();
		
		
		//Data variables assignment
		city_name = (String)data.get("city_name");
		date = (String)data.get("date");
		String[] temp;
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_PM25).split(" ");
			pm25 = Integer.parseInt(temp[0]);
			minpm25 = Integer.parseInt(temp[2]);
			maxpm25 = Integer.parseInt(temp[1]);
			if(minpm25>pm25)minpm25 = pm25-pm25/2;
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_O3).split(" ");
			o3 = Integer.parseInt(temp[0]);
			mino3 = Integer.parseInt(temp[2]);
			maxo3 = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_NO2).split(" ");
			no2 = Integer.parseInt(temp[0]);
			minno2 = Integer.parseInt(temp[2]);
			maxno2 = Integer.parseInt(temp[1]);
			
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_SO2).split(" ");
			so2 = Integer.parseInt(temp[0]);
			minso2 = Integer.parseInt(temp[2]);
			maxso2 = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_CO).split(" ");
			co = Integer.parseInt(temp[0]);
			minco = Integer.parseInt(temp[2]);
			maxco = Integer.parseInt(temp[1]);
				
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_DEW).split(" ");
			dew = Integer.parseInt(temp[0]);
			mindew = Integer.parseInt(temp[2]);
			maxdew = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_WIND).split(" ");
			wind = Integer.parseInt(temp[0]);
			minwind = Integer.parseInt(temp[2]);
			maxwind = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_TEMPERATURE).split(" ");
			temperature = Integer.parseInt(temp[0]);
			mintemperature = Integer.parseInt(temp[2]);
			maxtemperature = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_PRESSURE).split(" ");
			pressure = Integer.parseInt(temp[0]);
			minpressure = Integer.parseInt(temp[2]);
			maxpressure = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		try{
			temp = data.get(DatabaseReader.AirData.COLUMN_NAME_HUMIDITY).split(" ");
			humidity = Integer.parseInt(temp[0]);
			minhumidity = Integer.parseInt(temp[2]);
			maxhumidity = Integer.parseInt(temp[1]);
		}catch(Exception e){
			Log.v("ValueError", e.toString());
		}
		
		//data.clear(); //Freed some space
		
		//View variables assignment
		viewFrame = (FrameLayout)findViewById(R.id.view_frame);
		adLayout = (LinearLayout)findViewById(R.id.ad_layout);
		lblAirQuality = (TextView)findViewById(R.id.lblAirQuality);
		lblParticlePollutantsHeading = (TextView)findViewById(R.id.particle_pollutants_heading);
		lblGaseousPollutantsHeading = (TextView)findViewById(R.id.gaseous_pollutants_heading);
		lblChemicalPollutantsHeading = (TextView)findViewById(R.id.chemical_pollutants_heading);
		container = (ScrollView)findViewById(R.id.container_activity_view);
		pwoverall = (ProgressWheel) findViewById(R.id.pw_spinner);
		pwoverall.setTextColor(Color.WHITE);
		pwoverall.setRimColor(Color.WHITE);
		background = (ImageView)findViewById(R.id.normal_image);
		backgroundBlurred = (ImageView)findViewById(R.id.blurred_image);
		particleImage = (ImageView)findViewById(R.id.particle_pollutants_image);
		chemicalImage = (ImageView)findViewById(R.id.chemical_pollutants_image);
		gaseousImage = (ImageView)findViewById(R.id.gaseous_pollutants_image);
		particleText = (TextView)findViewById(R.id.particle_pollutants_value);
		chemicalText = (TextView)findViewById(R.id.chemical_pollutants_value);
		gaseousText = (TextView)findViewById(R.id.gaseous_pollutants_value);
		particleContainer = (LinearLayout)findViewById(R.id.particle_pollutants_container);
		chemicalContainer = (LinearLayout)findViewById(R.id.chemical_pollutants_container);
		gaseousContainer = (LinearLayout)findViewById(R.id.gaseous_pollutants_container);
	
		
		adView = new AdView(this);
		adView.setAdUnitId(MY_AD_UNIT_ID);
	    adView.setAdSize(AdSize.SMART_BANNER);
	    layout = (RelativeLayout)findViewById(R.id.view_layout);
	    RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(
	    	    RelativeLayout.LayoutParams.WRAP_CONTENT, 
	    	    RelativeLayout.LayoutParams.WRAP_CONTENT);
	    lay.addRule(RelativeLayout.ALIGN_BOTTOM);
	    adLayout.addView(adView);
	    final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    deviceId = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
	    //Log.v("DEVICEID", deviceId);
	    /*
	    AdRequest adRequest = new AdRequest.Builder()
	    	.addTestDevice(deviceId)
	    	.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    	.build();
	    // Load the adView with the ad request.
	    adView.loadAd(adRequest);
	   	*/
	    
		//Styling
		lblParticlePollutantsHeading.setTypeface(tf);
		lblGaseousPollutantsHeading.setTypeface(tf);
		lblChemicalPollutantsHeading.setTypeface(tf);
		lblAirQuality.setTypeface(tf);
		particleText.setTypeface(tfhl);
		chemicalText.setTypeface(tfhl);
		gaseousText.setTypeface(tfhl);
		
		if(android.os.Build.VERSION.SDK_INT<14){
			particleContainer.setBackground(null);
			chemicalContainer.setBackground(null);
			gaseousContainer.setBackground(null);
		}
		
		//Animations
		animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.fade_in);
		animFadeIn1000 = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.fade_in);
		animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),  R.anim.fade_out);
		animFadeIn.setDuration(1000);
		pwoverall.setAnimation(animFadeIn1000);
		pwoverall.setVisibility(View.INVISIBLE);
		updateUi();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_view_activity, menu);
		mnuShowCity = (MenuItem)menu.findItem(R.id.mnuShowCity);
		//mnuGetDetails = (MenuItem)menu.findItem(R.id.mnuGetDetails);
		mnuShowCity.setTitle(city_name);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.mnuShowCity:
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(data.get("address"))
	        			.setTitle("Your location")
	        			.setIcon(getApplicationContext().getResources().getDrawable(R.drawable.location_icon))
	        	       .setCancelable(false)
	        	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	break;
	        case R.id.mnuKnowMore:
	        	Intent i1 = new Intent(ViewActivity.this,KnowMore.class);
	        	startActivity(i1);
	     		overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	    		break;
	        case R.id.mnuRecommend:
	        	Intent sendIntent = new Intent();
	        	sendIntent.setAction(Intent.ACTION_SEND);
	        	sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey install this app to know about air quality around you. " +
	        			"https://play.google.com/store/apps/details?id=com.pirhoalpha.ubiplug_oaq");
	        	sendIntent.setType("text/plain");
	        	startActivity(Intent.createChooser(sendIntent, "Share Awair with your loved ones"));
	        	break;
	    }
		return true;
	}
	

	@SuppressLint("NewApi")
	private void updateUi(){
		
		final int temp = (int) (100-(Math.min(Double.valueOf(pm25), 480.0)/5.0));
		
		if (temp>80){
			background.setImageDrawable(getResources().getDrawable(R.drawable.bg_green));
			backgroundBlurred.setImageDrawable(getResources().getDrawable(R.drawable.bg_green_blurred));
			
		}
			
		else if(temp<=80 && temp>50){
			background.setImageDrawable(getResources().getDrawable(R.drawable.bg_yellow));
			backgroundBlurred.setImageDrawable(getResources().getDrawable(R.drawable.bg_yellow_blurred));
		}
		else if(temp<=50){ 
			background.setImageDrawable(getResources().getDrawable(R.drawable.bg_red));
			backgroundBlurred.setImageDrawable(getResources().getDrawable(R.drawable.bg_red_blurred));
		}
		
		float devisor = 4;
		if (temp>=80)devisor=(float) 0.5;
		if (temp<80 && temp>=50)devisor=4;
		pwoverall.setBarColor(Color.rgb((int) (255-temp/devisor), 0+Math.min(temp*4, 200), 0));
		if(temp<40)pwoverall.setBarColor(Color.rgb(180, 0, 0));
		if(android.os.Build.VERSION.SDK_INT>=15){
			pwoverall.animate();
		}else{
			pwoverall.setVisibility(View.VISIBLE);
		}

		Thread wheel = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i <= temp && isRunning; i+=1) {
                        Thread.sleep(FRAME_TIME_MS*(int)(i/1.5));	//controlling speed of the animation
                        Bundle data= new Bundle();
                        data.putInt(KEY, i);
                        Message message = handler.obtainMessage();
                        message.setData(data);
                        handler.sendMessage(message);
                    }
                    //background.setAlpha((float)0.0);
                    
                }
                catch (Throwable t) {
                }
            }
        });
        isRunning = true;
        wheel.start();
        animFadeIn.setDuration(4000);
        animFadeOut.setDuration(4000);
        backgroundBlurred.setAnimation(animFadeIn);
        background.setAnimation(animFadeOut);
        if(android.os.Build.VERSION.SDK_INT>=15){
        	backgroundBlurred.animate();
            background.animate();
        }
       
        //---------------------Specific pollutants-------------------------------
        if(pm25!=0){
        	particleText.setText(Html.fromHtml(String.valueOf(pm25) + " <small>μg/m<sup><small>3</small></sup></small>"));
        	if(pm25<50){
        		particleImage.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        	}else if(pm25>=50 && pm25<100){
        		particleImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_yellow));
        	}else{
        		particleImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_red));
        	}
        }else{
        	particleText.setText("NA");
        }
        if(o3!=0){
        	gaseousText.setText(Html.fromHtml(String.valueOf(o3)+" <small>μg/m<sup><small>3</small></sup></small>"));
        	if(o3<50){
        		gaseousImage.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        	}else if(o3>=50 && o3<100){
        		gaseousImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_yellow));
        	}else{
        		gaseousImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_red));
        	}
        }else{
        	gaseousText.setText("NA");
        }
        if(no2!=0 || co!=0){
        	int total;
        	if(no2!=0 && co!=0){
        		total = (no2+co)/2;
        		chemicalText.setText(Html.fromHtml(String.valueOf(total) + " <small>μg/m<sup><small>3</small></sup></small>"));
        	}
        	else {
        		total = (no2+co);
        		chemicalText.setText(Html.fromHtml(String.valueOf(total) + " <small>μg/m<sup><small>3</small></sup></small>"));
        	}
        	
        	if(total<50){
        		chemicalImage.setImageDrawable(getResources().getDrawable(R.drawable.ok));
        	}else if(total>=50 && total<100){
        		chemicalImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_yellow));
        	}else{
        		chemicalImage.setImageDrawable(getResources().getDrawable(R.drawable.caution_red));
        	}
        }else{
        	chemicalText.setText("NA");
        }
	}
	
	
	 @Override
	 public void onLocationChanged(Location location) {
		 Log.v("LOCATION", "location received");
		 locationReceived(location);
		 locationclient.removeLocationUpdates(this);
	 }
	 
	   
	  
	  private boolean checkPlayServices() {
		    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		    if (resultCode != ConnectionResult.SUCCESS) {
		        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
		            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
		                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
		        } else {
		            Log.i("GCM", "This device is not supported.");
		            reportError("Device is not supported for google play services");
		            finish();
		        }
		        return false;
		    }
		    return true;
		}
	  
	  @SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		    final SharedPreferences prefs = getGCMPreferences(context);
		    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		    if (registrationId.isEmpty()) {
		        Log.i("GCM", "Registration not found.");
		        return "";
		    }
		    // Check if app was updated; if so, it must clear the registration ID
		    // since the existing regID is not guaranteed to work with the new
		    // app version.
		    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		    int currentVersion = getAppVersion(context);
		    if (registeredVersion != currentVersion) {
		        Log.i("GCM", "App version changed.");
		        return "";
		    }
		    return registrationId;
		}
	  
	  /**
	   * @return Application's {@code SharedPreferences}.
	   */
	  private SharedPreferences getGCMPreferences(Context context) {
	      // This sample app persists the registration ID in shared preferences, but
	      // how you store the regID in your app is up to you.
	      return getSharedPreferences(ViewActivity.class.getSimpleName(),
	              Context.MODE_PRIVATE);
	  }
	  
	  /**
	   * @return Application's version code from the {@code PackageManager}.
	   */
	  private static int getAppVersion(Context context) {
	      try {
	          PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	          return packageInfo.versionCode;
	      } catch (NameNotFoundException e) {
	          // should never happen
	          throw new RuntimeException("Could not get package name: " + e);
	      }
	  }
	  
	  /**
	   * Registers the application with GCM servers asynchronously.
	   * <p>
	   * Stores the registration ID and app versionCode in the application's
	   * shared preferences.
	   */
	  private void registerInBackground() {
		  Log.v("GCM", "registration called");
	      new AsyncTask<Void,Void,String>() {
	          @Override
	          protected String doInBackground(Void... params) {
	              String msg = "";
	              try {
	                  if (gcm == null) {
	                      gcm = GoogleCloudMessaging.getInstance(context);
	                  }
	                  Log.v("GCM", "registration called");
	                  regid = gcm.register(SENDER_ID);
	                  Log.v("GCM", "regid: "+regid);
	                  msg = "Device registered, registration ID=" + regid;
	                  sendRegistrationIdToBackend();
	                  storeRegistrationId(context, regid);
	              } catch (IOException ex) {
	            	  Log.v("GCM",ex.getMessage());
	                  msg = "Error :" + ex.getMessage();
	              }
	              return msg;
	          }

	          protected void onPostExecute(String msg) {

	          }
	      }.execute(null, null, null);
	  }
	  
	  private void sendRegistrationIdToBackend() {
	    	String url = "http://www.ubiplug.com:8080/ubiair/gcm/device/add/";	//TODO to be changed in future
	    	RequestParams params = new RequestParams();
	    	params.put("reg_id", regid);
	    	params.put("dev_id", deviceId);
	    	
	    	Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
	    	Account[] accounts = AccountManager.get(context).getAccounts();
	    	for (Account account : accounts) {
	    	    if (emailPattern.matcher(account.name).matches()) {
	    	        emailId = account.name;
	    	    }
	    	}
	    	
	    	params.put("email_id", emailId);
		  try {
	        	AsyncHttpClient client = new AsyncHttpClient();
	        	client.setTimeout(50*1000);
	        	client.post(ViewActivity.this,url,params,new AsyncHttpResponseHandler() 
	        	{
	        	    @Override
	        	    public void onSuccess(String response) {
	        	    	Log.v("GCM", response);
	        	    }
	        	    
	    	    	@Override
	        	    public void onFailure(Throwable error, String response){
	        	    	Log.v("GCM", "Could not connect to internet "+response);
	        	    	reportError("Could not connect to internet "+response);
	        	    	
	        	    }
	        	    
	           	});
	       	}
	    	catch(Exception e){
	    		Log.v("INTERNET", "Unable to connect"+e.toString());
	    		reportError(e.toString());
	    		finish();
	    	}
		}
		
	  
	  private void storeRegistrationId(Context context, String regId) {
		    final SharedPreferences prefs = getGCMPreferences(context);
		    int appVersion = getAppVersion(context);
		    Log.i("GCM", "Saving regId on app version " + appVersion);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString(PROPERTY_REG_ID, regId);
		    editor.putInt(PROPERTY_APP_VERSION, appVersion);
		    editor.commit();
		}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Google play services not available", Toast.LENGTH_SHORT).show();
		reportError("Google play services not available. "+arg0.toString());
	}

	@Override
	public void onConnected(Bundle arg0) {
		if(locationclient!=null && locationclient.isConnected()){
			Location loc = locationclient.getLastLocation();
			if(loc!=null){
				locationReceived(loc);
			}else{
				locationrequest = LocationRequest.create();
				locationrequest.setInterval(5000);
				locationrequest.setFastestInterval(1000);
				locationrequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
				locationclient.requestLocationUpdates(locationrequest, this);
				Log.v("LOCATION", "QUERY STARTED");
			}			
			
		}else{
			Log.i("Location", "NOT CONNECTED");
		}
	}

	@Override
	public void onDisconnected() {
		
	}
	
	
	private void locationReceived(Location location){
		if(!GSMDataAdded){
			lat =  location.getLatitude();
		    lng = location.getLongitude();
		    
		    Log.v("LOCATION",String.valueOf(lat)+" " +String.valueOf(lng));
		    Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable (){
							@Override
							public void run() {
								//Getting weather info
						        try{
						        	//mYahooWeather.queryYahooWeatherByLatLon(getApplicationContext(), String.valueOf(lat), String.valueOf(lng), ViewActivity.this);
						        }catch(Exception e){
						        	Log.v("Weather", e.toString());
						        }
						        //getActionBar().setSubtitle("Updating...");
								final String url = new String(DatabaseReader.AirData.BASE_URL);
								final int DEFAULT_TIMEOUT = 100 * 1000;
						    	try {
						        	AsyncHttpClient client = new AsyncHttpClient();
						        	client.setTimeout(DEFAULT_TIMEOUT);
				        	    	RequestParams params = new RequestParams();
				        	    	params.put("lat", String.valueOf(lat));
				        	    	params.put("lng", String.valueOf(lng));
				        	    	params.put("dev_id", deviceId);
			        				client.post(ViewActivity.this,url,params,new AsyncHttpResponseHandler() 
									{
									    @Override
											    public void onSuccess(String response) {
											    	JSONParser parser=new JSONParser();
											    	ContainerFactory containerFactory = new ContainerFactory(){
													    public List creatArrayContainer() {
													      return new LinkedList();
													    }
													    public Map createObjectContainer() {
													      return new LinkedHashMap();
													    }                
												  };
											    	try{
											    		data = (HashMap<String, String>)parser.parse(response, containerFactory);
											    		mDbHelper = new DatabaseReaderHelper(getBaseContext());
											    		mDbHelper.flushData();
											    		
											    		int newRowId = mDbHelper.addData(data);
											    		if(newRowId!=-1){
											    			//Toast.makeText(ViewActivity.this, String.valueOf(newRowId), Toast.LENGTH_SHORT).show();
											    			//getActionBar().setSubtitle("");
											    			if(android.os.Build.VERSION.SDK_INT>=15)mnuShowCity.setTitle(data.get("city_name"));
											    			if(android.os.Build.VERSION.SDK_INT>=15)mnuShowCity.setEnabled(true);
											    			if(!(Integer.parseInt(data.get("pm25").split(" ")[0])==pm25)){
											    				pm25 = Integer.parseInt(data.get("pm25").split(" ")[0]);
											    				try{
											    					o3 = Integer.parseInt(data.get("o3").split(" ")[0]);
											    				}catch(Exception e){
											    					Log.v("ValueError", e.toString());
											    				}
											    				try{
											    					no2 = Integer.parseInt(data.get("no2").split(" ")[0]);
											    				}catch(Exception e){
											    					Log.v("ValueError", e.toString());
											    				}
											    				try{    				
											    					so2 = Integer.parseInt(data.get("so2").split(" ")[0]);
											    				}catch(Exception e){
											    					Log.v("ValueError", e.toString());
											    				}
											    				try{
											    					co = Integer.parseInt(data.get("co").split(" ")[0]);
											    				}catch(Exception e){
											    					Log.v("ValueError", e.toString());
											    				}
											    				updateUi();
											    			}
											    			
											    		}else{
											    			Toast.makeText(ViewActivity.this, "Data not added", Toast.LENGTH_SHORT).show();
											    			//getActionBar().setSubtitle(date);
											    			
											    		}
											    		mDbHelper.close();
											    				
											    		
											    	}catch(Exception pe){
											    		Log.v("PARSE_ERROR",pe.toString());
											    		reportError(pe.toString());
											    		//getActionBar().setSubtitle("");
											    	}
						        	    	
						        	    }
						        	    
						    	    	@Override
						        	    public void onFailure(Throwable error, String response){
						        	    	Log.v("INTERNET_ERROR1", "Could not connect to internet "+response);
						        	    	reportError(error.toString());
						        	    	//getActionBar().setSubtitle("");
						        	    	
						        	    }
						        	    
						           	});
						       	}
						    	catch(Exception e){
						    		Log.v("INTERNET", "Unable to connect"+e.toString());
						    		reportError(e.toString());
						    		//getActionBar().setSubtitle("");
						    	}
							}
							
						});
						
						
					} catch (Exception e) {
						Log.v("error",e.toString());
						Toast.makeText(ViewActivity.this, "Current location not available", Toast.LENGTH_SHORT).show();
						reportError(e.toString());
					}
					
				}
		    };
		    
		    new Handler().postDelayed(r, 1);
		    GSMDataAdded = true;
		}
	}

	@Override
	public void gotWeatherInfo(WeatherInfo weatherInfo) {
		if (weatherInfo != null) {
			final ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(0);
            Log.v("Weather", forecastInfo.getForecastText());
		}
	  
	}
	
	public void reportError(final String message){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					runOnUiThread(new Runnable (){
						@Override
						public void run() {
							final String url = new String(DatabaseReader.AirData.ERROR_REPORT_URL);
							final int DEFAULT_TIMEOUT = 100 * 1000;
					    	try {
					        	AsyncHttpClient client = new AsyncHttpClient();
					        	client.setTimeout(DEFAULT_TIMEOUT);
			        	    	RequestParams params = new RequestParams();
			        	    	params.put("dev_id", Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID));
			        	    	params.put("message", message);
			  					params.put("version", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			        	    	client.post(ViewActivity.this,url,params,new AsyncHttpResponseHandler() 
									{
								    @Override
								    public void onSuccess(String response) {
								    	Toast.makeText(ViewActivity.this, "Error has been reported. We'll fix this issue in next release.", Toast.LENGTH_LONG).show();
								    }
								    @Override
								    public void onFailure(Throwable error, String response){
								    	Toast.makeText(ViewActivity.this, "Error could not be reported.", Toast.LENGTH_SHORT).show();
								    }
								    
								});	
					        	    	
					        	    
					       	}
					    	catch(Exception e){
					    		Log.v("INTERNET", "Unable to connect"+e.toString());
					    	}
						}
						
					});
					
					
				} catch (Exception e) {
					Log.v("error",e.toString());
				}
				
			}
	    };
	    
	    new Handler().postDelayed(r, 1);
	}
	
}



//Animation listener class
class MyAnimationListener implements AnimationListener{

	@Override
	public void onAnimationEnd(Animation arg0) {
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		
	}
	
}


