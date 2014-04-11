package com.pirhoalpha.ubiplug_oaq;



import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener, android.location.LocationListener{
	
	private Map<String,String> newData;
	double lat;
	double lng;
	private DatabaseReaderHelper mDbHelper;
	private ProgressDialog dialog;
	private Boolean dataAdded=false;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(Build.VERSION.SDK_INT>=15)getActionBar().setIcon(R.drawable.home);
		mDbHelper = new DatabaseReaderHelper(getBaseContext());
		
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			Toast.makeText(this, "Please turn on Network based location service.", Toast.LENGTH_SHORT).show();
		}else{
			//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
		
		dialog = new ProgressDialog(MainActivity.this);
		dialog.setMessage("Preparing for the first use.");
		dialog.show();
		Timer timer = new Timer("message_changer",true);
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.setMessage("Preparing for the first use. If you are indoors, try moving to a window.");	
					}
				});
			}
		}, 10*1000);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnCancelListener(new OnCancelListener () {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
				return;
			}
		});

		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
			locationclient = new LocationClient(this,this,this);
			locationclient.connect();
		}
		else{
			final String appPackageName = "com.google.android.gms";// getPackageName() from Context or Activity object
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
			}
			Toast.makeText(this, "You must have Google Play services to use this app", Toast.LENGTH_LONG).show();
			reportError("Device must have Google Play services to use this app");
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
	}
	
	@Override
	 public void onLocationChanged(Location location) {
		Log.v("LOCATION", "location received");
		locationReceived(location);
		locationclient.removeLocationUpdates(this);
	 }


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Google play services not available", Toast.LENGTH_SHORT).show();
		reportError("Google play services not available. "+arg0.toString());
		finish();
	}

	@Override
	public void onConnected(Bundle arg0) {
		if(locationclient!=null && locationclient.isConnected()){
			Location loc = locationclient.getLastLocation();
			if(loc!=null){
				locationReceived(loc);
			}else{
				locationrequest = LocationRequest.create();
				locationrequest.setInterval(2000);
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
		Log.i("Location", "Disconnected");
	}
	
	
	private void locationReceived(Location location){
		if(!dataAdded){
			lat =  location.getLatitude();
		    lng = location.getLongitude();
		    Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable (){
							@Override
							public void run() {
								final String url = new String(DatabaseReader.AirData.BASE_URL);
								final int DEFAULT_TIMEOUT = 100 * 1000;
						    	try {
						        	AsyncHttpClient client = new AsyncHttpClient();
						        	client.setTimeout(DEFAULT_TIMEOUT);
				        	    	RequestParams params = new RequestParams();
				        	    	params.put("lat", String.valueOf(lat));
				        	    	params.put("lng", String.valueOf(lng));
				  					client.post(MainActivity.this,url,params,new AsyncHttpResponseHandler() 
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
									    		newData = (Map)parser.parse(response, containerFactory);
									    		//mDbHelper.flushData();
									    		int newRowId = mDbHelper.addData(newData);
									    		if(newRowId!=-1){
									    			//Toast.makeText(MainActivity.this, String.valueOf(newRowId), Toast.LENGTH_SHORT).show();
									    		}else{
									    			Toast.makeText(MainActivity.this, "Data not added", Toast.LENGTH_SHORT).show();
									    		}
									    		dialog.dismiss();
									    		
									    		try {
									    			OutputStreamWriter outputStreamWriter = new OutputStreamWriter
									    					(MainActivity.this.openFileOutput(UserDataManager.FILENAME, Context.MODE_PRIVATE));
									    			outputStreamWriter.write("name::email::1::1::0::0");
									    			outputStreamWriter.close();
												}catch (IOException e) {
											        Log.e("UserDataManager", "File not found to write in: " + e.toString());
											    } 
									    		
									    		Intent i = new Intent(MainActivity.this,ViewActivity.class);
									    		finish();
									    		startActivity(i);
									    		overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
									    		//TODO
									    				
									    		
									    	}catch(ParseException pe){
									    		Log.v("PARSE_ERROR",pe.toString());
									    		Log.v("data",response);
									    		reportError("MainActivity "+pe.toString()+" "+response);
									    		finish();
									    	}
									    }
									    
									    @Override
									    public void onFailure(Throwable error, String response){
									    	Log.v("INTERNET_ERROR", "Could not connect to internet "+response);
									    	reportError("MainActivity "+error.toString()+" "+response);
									    	dialog.dismiss();
									    	finish();
									    	
									    }
									    
									});	
						        	    	
						        	    
						       	}
						    	catch(Exception e){
						    		Log.v("INTERNET", "Unable to connect"+e.toString());
						    		reportError(e.toString());
						    		finish();
						    	}
							}
							
						});
						
						
					} catch (Exception e) {
						Log.v("error",e.toString());
						reportError(e.toString());
						Toast.makeText(MainActivity.this, "Current location not available", Toast.LENGTH_SHORT).show();
						finish();
					}
					
				}
		    };
		    
		    new Handler().postDelayed(r, 1);
		    dataAdded = true;
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
			        	    	client.post(MainActivity.this,url,params,new AsyncHttpResponseHandler() 
									{
								    @Override
								    public void onSuccess(String response) {

								    }
								    @Override
								    public void onFailure(Throwable error, String response){
								    	Toast.makeText(MainActivity.this, "Error could not be reported.", Toast.LENGTH_SHORT).show();
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


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}


