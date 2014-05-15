/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */


package com.pirhoalpha.ubiplug_oaq;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.analytics.tracking.android.EasyTracker;


import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.sbstrm.appirater.Appirater;
import com.todddavies.components.progressbar.ProgressWheel;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.location.Location;
import android.location.LocationManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity is the main activity which opens after the ScreenSaver
 * (Not in the case of first use).
 *
 */
public class ViewActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener{
	private HashMap<String,String> data;
	private Boolean GSMDataAdded = false;
	// Variables to hold values of latitude and longitude
	// These are public because it is alsobeing used in 
	// Comparision Fragment
	public static Double lat=(double) 0;
	public static Double lng=(double) 0;
	private MenuItem mnuShowCity;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private ProgressWheel aq_spinner;
	private ProgressWheel greenery_spinner;
	private ProgressWheel uv_spinner;
	private ScrollView container;
	
	
	// Some useful variables
    private static final int FRAME_TIME_MS = 1;

    // Unique identifiers for multiple inter thread messages
    private static final String KEY_AQ = "aq";
    private static final String KEY_GREENERY = "greenery";
    private static final String KEY_UV = "uv";

    // Booleans to represent status of donuts
    boolean isRunningAq = false;
    boolean isRunningGreenery = false;
    boolean isRunningUv = false;

    private String deviceId;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "com.pirhoalpha.ubiplug_oaq";
    private static final String PROPERTY_APP_VERSION = "32";
    public String emailId;
    String SENDER_ID = "487700552253"; //Awair project Id
    AtomicInteger msgId = new AtomicInteger();
    Context context;
    GoogleCloudMessaging gcm;
    String regid;
    
    
	//Data variables
	public static String city_name;
	public static String address;
	public static int aq;
	public int greenery;
	public float uv;
	public static int total;
	
	//View variables
	private TextView lbl_aq;
	private TextView lbl_greenery;
	private TextView lbl_uv;
	private TextView lbl_aq_value;
	private TextView lbl_greenery_value;
	private TextView lbl_uv_value;

	
	// Thread handler for AQ spinner
	@SuppressLint("HandlerLeak")
	Handler aq_wheel_handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i= msg.getData().getInt(KEY_AQ);
                aq_spinner.setProgress((int)(i*3.6));
                lbl_aq_value.setText(""+i+"%");
            } catch (Exception err) {
            	Log.v("Thread Error",err.toString());
            }
        }
    };

    // Thread handler for Greenery spinner
	@SuppressLint("HandlerLeak")
	Handler greenery_wheel_handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i= msg.getData().getInt(KEY_GREENERY);
                greenery_spinner.setProgress((int)(i*3.6));
                lbl_greenery_value.setText(""+i+"%");
            } catch (Exception err) {
            	Log.v("Thread Error",err.toString());
            }
        }
    };

    // Thread handler for UV spinner
	@SuppressLint("HandlerLeak")
	Handler uv_wheel_handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                int i= msg.getData().getInt(KEY_UV);
                uv_spinner.setProgress((int)(i*3.6));
                
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
		
		//App Rater
		RateMeMaybe rmm = new RateMeMaybe(this);
		rmm.setPromptMinimums(10, 2, 10, 2);
		rmm.setDialogMessage("You really seem to like this app, "
		                +"since you have already used it %totalLaunchCount% times! "
		                +"It would be great if you take a moment to rate it.");
		rmm.setDialogTitle("Rate this app");
		rmm.setPositiveBtn("Rate Awair");
		rmm.setRunWithoutPlayStore(true);
		rmm.run();
		
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        // Start downloading data for compare cities
        cache_compare_data();
        
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
		Typeface tfhl = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/helvetica_neue_reg.ttf");
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
			locationclient = new LocationClient(this,this,this);
			locationclient.connect();
		}
		else{
			Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
		}
		
		// Assigning values to data variables
		SharedPreferences data_prefs = getSharedPreferences(Constants.DATA_CACHE_PREFS_NAME, 0);
		city_name = data_prefs.getString("city_name", null);
		address = data_prefs.getString("address", null);
		aq = Integer.parseInt(data_prefs.getString("aq", "0"));
		uv = Float.valueOf((data_prefs.getString("uv", "0")));
		total = Integer.parseInt(data_prefs.getString("total", "0"));
		greenery = Integer.parseInt(data_prefs.getString("greenery", "0"));
		
		// View variables assignments
		container = (ScrollView)findViewById(R.id.container_activity_view);
		lbl_aq_value = (TextView)findViewById(R.id.lbl_air_quality_value);
		lbl_greenery_value = (TextView)findViewById(R.id.lbl_greenery_value);
		lbl_uv_value = (TextView)findViewById(R.id.lbl_uv_value);
		aq_spinner = (ProgressWheel) findViewById(R.id.aq_spinner);
		greenery_spinner = (ProgressWheel) findViewById(R.id.greenery_spinner);
		uv_spinner = (ProgressWheel) findViewById(R.id.uv_spinner);
		
		
		aq_spinner.setRimColor(Color.WHITE);
		greenery_spinner.setRimColor(Color.WHITE);
		uv_spinner.setRimColor(Color.WHITE);
		
		// Setting TypeFaces
		lbl_aq_value.setTypeface(tfhl);
		lbl_greenery_value.setTypeface(tfhl);
		lbl_uv_value.setTypeface(tfhl);
		
		// Donuts OnClickListeners
		aq_spinner.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i1 = new Intent(ViewActivity.this,KnowMore.class);
	        	i1.putExtra("which_fragment", 0);
	        	startActivity(i1);
			}
		});

		greenery_spinner.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {				
				Intent i1 = new Intent(ViewActivity.this,KnowMore.class);
	        	i1.putExtra("which_fragment", 1);
	        	startActivity(i1);
			}
		});
		
		uv_spinner.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent i1 = new Intent(ViewActivity.this,KnowMore.class);
	        	i1.putExtra("which_fragment", 2);
	        	startActivity(i1);
			}
		});
		
	    final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    deviceId = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
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
		mnuShowCity.setTitle(city_name);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	      
	    	case R.id.mnuShowCity:
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(address)
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
	        	
	    	case R.id.mnuCompare:
	    		Intent i = new Intent(ViewActivity.this,KnowMore.class);
	        	i.putExtra("which_fragment", 3);
	        	startActivity(i);
	     		//overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	     		break;
	        /*
	    	case R.id.mnuKnowMore:
	        	Intent i1 = new Intent(ViewActivity.this,KnowMore.class);
	        	startActivity(i1);
	     		//overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	    		break;
	    	*/	
	        case R.id.mnuTweet:
	        	String tweetUrl = "https://twitter.com/intent/tweet?text="
	        					+ "Just checked the ambient air quality around " + city_name + " using @AirAwair. ";
	        	Uri uri = Uri.parse(tweetUrl);
	        	Intent tweet =  new Intent(Intent.ACTION_VIEW, uri);
	        	//tweet.putExtra(Intent.EXTRA_STREAM, screenShot(container));
	        	//tweet.setType("image/jpeg");
	        	startActivity(tweet);
	    		break;
	    	/*	
	        case R.id.mnuRecommend:
	        	Intent sendIntent = new Intent();
	        	sendIntent.setAction(Intent.ACTION_SEND);
	        	sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey install this app to know about air quality around you. " +
	        			"https://play.google.com/store/apps/details?id=com.pirhoalpha.ubiplug_oaq");
	        	sendIntent.setType("text/plain");
	        	startActivity(Intent.createChooser(sendIntent, "Share Awair with your loved ones"));
	        	break;
	    	*/
	    }
		return true;
	}
	

	@SuppressLint("NewApi")
	private void updateUi(){

		if (total>80){
			container.setBackground(getResources().getDrawable(R.drawable.bg_green_blurred));
		}
			
		else if(total<=70 && total>50){
			container.setBackground(getResources().getDrawable(R.drawable.bg_yellow_blurred));
		}
		else if(total<=50){ 
			container.setBackground(getResources().getDrawable(R.drawable.bg_red_blurred));
		}

		float devisor = 4;
		if (aq>=80)devisor=(float) 0.5;
		if (aq<80 && aq>=50)devisor=4;
		aq_spinner.setBarColor(Color.rgb((int) (255 - aq /devisor), 0+Math.min(aq*4, 200), 0));
		if(aq<40)aq_spinner.setBarColor(Color.rgb(180, 0, 0));

		if (greenery>=80)devisor=(float) 0.5;
		if (greenery<80 && greenery>=20)devisor=4;
		greenery_spinner.setBarColor(Color.rgb((int) (255 - greenery /devisor), 0+Math.min(greenery*4, 200), 0));
		if(greenery<20)greenery_spinner.setBarColor(Color.rgb(180, 0, 0));

		if (uv<=4)uv_spinner.setBarColor(Color.rgb(0, 200, 0));
		if (uv<8 && uv>=5)uv_spinner.setBarColor(Color.rgb(250, 250, 0));
		if(uv>8)uv_spinner.setBarColor(Color.rgb(180, 0, 0));

		Thread aq_wheel = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i <= aq && isRunningAq; i+=1) {
                        Thread.sleep(FRAME_TIME_MS*(int)(i/1.5));	//controlling speed of the animation
                        Bundle data= new Bundle();
                        data.putInt(KEY_AQ, i);
                        Message message = aq_wheel_handler.obtainMessage();
                        message.setData(data);
                        aq_wheel_handler.sendMessage(message);
                    }
                }
                catch (Throwable t) {
                }
            }
        });
        isRunningAq = true;
        aq_wheel.start();
        
		Thread greenery_wheel = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i <= greenery && isRunningGreenery; i+=1) {
                        Thread.sleep(FRAME_TIME_MS*(int)(i/1.5));	//controlling speed of the animation
                        Bundle data= new Bundle();
                        data.putInt(KEY_GREENERY, i);
                        Message message = greenery_wheel_handler.obtainMessage();
                        message.setData(data);
                        greenery_wheel_handler.sendMessage(message);
                    }
                    
                }
                catch (Throwable t) {
                }
            }
        });
        isRunningGreenery = true;
        greenery_wheel.start();
        
        lbl_uv_value.setText(String.valueOf(uv));
		Thread uv_wheel = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i <= (int)(uv*100/15) && isRunningUv ; i+=1) {
                        Thread.sleep(FRAME_TIME_MS*(int)(i/1.5));	//controlling speed of the animation
                        Bundle data= new Bundle();
                        data.putInt(KEY_UV, i);
                        Message message = uv_wheel_handler.obtainMessage();
                        message.setData(data);
                        uv_wheel_handler.sendMessage(message);
                    }
                    
                }
                catch (Throwable t) {
                }
            }
        });
        isRunningUv = true;
        uv_wheel.start();
     
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
	  
	  /**
	   * Stores registration id on the server
	   */
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
		
	  /**
	   * Stores reg id in shared preferences
	   * @param context
	   * @param regId
	   */
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
		reportError("Google play services not available. OrgError: "+arg0.toString());
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
		    final String hour = String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		    Log.v("LOCATION",String.valueOf(lat)+" " +String.valueOf(lng));
		    Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable (){
							@Override
							public void run() {
								final String url = new String(Constants.DATA_URL);
								final int DEFAULT_TIMEOUT = 100 * 1000;
						    	try {
						        	AsyncHttpClient client = new AsyncHttpClient();
						        	client.setTimeout(DEFAULT_TIMEOUT);
				        	    	RequestParams params = new RequestParams();
				        	    	params.put("lat", String.valueOf(lat));
				        	    	params.put("lng", String.valueOf(lng));
				        	    	params.put("hh", hour);
				        	    	params.put("mm", "00");
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

									    		// Caching data in the shared preferences
									    		SharedPreferences data_prefs = getSharedPreferences(Constants.DATA_CACHE_PREFS_NAME, 0);
								    			SharedPreferences.Editor data_prefs_editor = data_prefs.edit();
								    			data_prefs_editor.putString("aq", data.get("aq"));
								    			data_prefs_editor.putString("total", data.get("total"));
								    			data_prefs_editor.putString("uv",
								    					String.valueOf(Math.round(Float.valueOf(data.get("uv")) * 100.0) / 100.0));
								    			data_prefs_editor.putString("greenery", data.get("greenery"));
								    			data_prefs_editor.putString("city_name", data.get("city_name"));
								    			data_prefs_editor.putString("address", data.get("address"));
								    			data_prefs_editor.commit();
								    			mnuShowCity.setEnabled(true);
								    			city_name = data.get("city_name");
								    			address = data.get("address");
								    			
								    			// Rounding off uv two two decimal places
								    			double rounduv = Math.round(Float.valueOf(data.get("uv")) * 100.0) / 100.0;
								    			// Reanimate only when any value changes
								    			if(aq != Integer.parseInt(data.get("aq")) ||
								    					total != Integer.parseInt(data.get("total")) ||
								    					//(int)uv != (int)rounduv ||
								    					greenery != Integer.parseInt(data.get("greenery")))
								    			{
									    			aq = Integer.parseInt(data.get("aq"));
									    			total = Integer.parseInt(data.get("total"));
									    			uv = (float) rounduv;
									    			greenery = Integer.parseInt(data.get("greenery"));
									    			updateUi();
								    			}
									    		
									    	}catch(Exception pe){
									    		Log.v("PARSE_ERROR",pe.toString());
									    		reportError("Error: "+pe.toString()+
							        	    			" Message: "+pe.getMessage()+" Response was: "+response);
									    	}
					        	    	
									    }
						        	    
						    	    	@Override
						        	    public void onFailure(Throwable error, String response){
						    	    		Toast.makeText(ViewActivity.this, "Could not connect to Awair server.", Toast.LENGTH_SHORT).show();
						        	    	Log.v("INTERNET_ERROR1", "Could not connect to internet "+response);
						        	    	reportError("Error: "+error.toString()+
						        	    			" Message: "+error.getMessage()+" Response was: "+response);
						        	    }
						           	});
						       	}
						    	catch(Exception e){
						    		Toast.makeText(ViewActivity.this, "Could not connect to Awair server.", Toast.LENGTH_SHORT).show();
						    		Log.v("INTERNET", "Unable to connect"+e.toString());
						    		reportError("Error: "+e.toString()+
				        	    			" Message: "+e.getMessage());
						    	}
							}
						});
						
						
					} catch (Exception e) {
						Log.v("error",e.toString());
						Toast.makeText(ViewActivity.this, "Current location not available", Toast.LENGTH_SHORT).show();
						reportError("Error: "+e.toString()+
	        	    			" Message: "+e.getMessage());
					}
				}
		    };
		    
		    new Handler().postDelayed(r, 1);
		    GSMDataAdded = true;
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
								    	//Do Nothing
								    }
								    @Override
								    public void onFailure(Throwable error, String response){
								    	Toast.makeText(ViewActivity.this, "Couldn't connect to Awair servers.", Toast.LENGTH_SHORT).show();
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
	
	private void cache_compare_data(){
		final String url = new String(Constants.COMPARE_CITIES_URL);
		final int DEFAULT_TIMEOUT = 100 * 1000;
    	try {
        	AsyncHttpClient client = new AsyncHttpClient();
        	client.setTimeout(DEFAULT_TIMEOUT);
	    	RequestParams params = new RequestParams();
	    	params.put("lat", String.valueOf(lat));
	    	params.put("lng", String.valueOf(lng));
			client.post(ViewActivity.this,url,params,new AsyncHttpResponseHandler() 
			{
			    @Override
			    public void onSuccess(String response) {
					
		    		// Caching compare data in the shared preferences
		    		SharedPreferences data_prefs = getSharedPreferences(Constants.DATA_CACHE_PREFS_NAME, 0);
	    			SharedPreferences.Editor data_prefs_editor = data_prefs.edit();
	    			data_prefs_editor.putString("compare_cities_data", response);
	    			data_prefs_editor.putBoolean("compare_data_cached", true);
	    			data_prefs_editor.commit();
			    }

    	    	@Override
        	    public void onFailure(Throwable error, String response){
        	    	Log.v("INTERNET_ERROR1", "Could not connect to internet "+response);
        	    	reportError("Error: "+error.toString()+
        	    			" Message: "+error.getMessage()+" Response was: "+response);
        	    }
           	});
       	}
    	catch(Exception e){
    		Log.v("INTERNET", "Unable to connect"+e.toString());
    		reportError("Error: "+e.toString()+
	    			" Message: "+e.getMessage());
    	}
	}
	
	public String screenShot(View view) {
	    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
	            view.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    view.draw(canvas);
	    FileOutputStream out = null;
	    try {
	           out = new FileOutputStream("tmp.jpeg");
	           bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	           try{
	               out.close();
	           } catch(Throwable ignore) {}
	    }
	    return "tmp.png";
	}
}
