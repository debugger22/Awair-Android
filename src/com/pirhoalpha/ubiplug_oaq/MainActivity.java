/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		android.location.LocationListener {

	private Boolean dataAdded = false;
	private ProgressDialog dialog;
	// Variables to hold latitude and longitude
	double lat;
	double lng;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Map<String, String> new_data;

	/**
	 * This method is automatically called when the device receives location
	 * from the provider.
	 * 
	 * @param location
	 */
	private void locationReceived(Location location) {
		if (!this.dataAdded) {
			// Get latitude and longitude
			this.lat = location.getLatitude();
			this.lng = location.getLongitude();
			// Get hour of the day
			final String hour = String.valueOf(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								final String url = new String(
										Constants.DATA_URL);
								final int DEFAULT_TIMEOUT = 90 * 1000; // 1.5
																		// minutes
																		// timeout
								try {
									AsyncHttpClient client = new AsyncHttpClient();
									client.setTimeout(DEFAULT_TIMEOUT);
									// Prepare request parameters
									RequestParams params = new RequestParams();
									params.put("lat", String
											.valueOf(MainActivity.this.lat));
									params.put("lng", String
											.valueOf(MainActivity.this.lng));
									params.put("hh", hour);
									params.put("mm", "00");
									client.post(MainActivity.this, url, params,
											new AsyncHttpResponseHandler() {
												@Override
												public void onFailure(
														Throwable error,
														String response) {
													Log.v("INTERNET_ERROR",
															"Could not connect to internet "
																	+ response);
													reportError("MainActivity "
															+ error.toString()
															+ " " + response);
													MainActivity.this.dialog
															.dismiss();
													finish();
												}

												@Override
												public void onSuccess(
														String response) {
													JSONParser parser = new JSONParser();
													ContainerFactory containerFactory = new ContainerFactory() {
														@Override
														public List creatArrayContainer() {
															return new LinkedList();
														}

														@Override
														public Map createObjectContainer() {
															return new LinkedHashMap();
														}
													};
													try {
														MainActivity.this.new_data = (Map) parser
																.parse(response,
																		containerFactory);

														// Caching data in the
														// shared preferences
														SharedPreferences data_prefs = getSharedPreferences(
																Constants.DATA_CACHE_PREFS_NAME,
																0);
														SharedPreferences.Editor data_prefs_editor = data_prefs
																.edit();
														data_prefs_editor
																.putString(
																		"aq",
																		MainActivity.this.new_data
																				.get("aq"));
														data_prefs_editor
																.putString(
																		"total",
																		MainActivity.this.new_data
																				.get("total"));
														data_prefs_editor
																.putString(
																		"greenery",
																		MainActivity.this.new_data
																				.get("greenery"));
														data_prefs_editor
																.putString(
																		"uv",
																		String.valueOf(Math
																				.round(Float
																						.valueOf(MainActivity.this.new_data
																								.get("uv")) * 100.0) / 100.0));
														data_prefs_editor
																.putString(
																		"city_name",
																		MainActivity.this.new_data
																				.get("city_name"));
														data_prefs_editor
																.putString(
																		"address",
																		MainActivity.this.new_data
																				.get("address"));
														data_prefs_editor
																.commit();

														// Changing the value of
														// installed boolean
														// from false to true
														SharedPreferences settings = getSharedPreferences(
																Constants.PREFS_NAME,
																0);
														SharedPreferences.Editor editor = settings
																.edit();
														editor.putBoolean(
																"installed",
																true);
														editor.commit();

														MainActivity.this.dialog
																.dismiss(); // Removing
																			// the
																			// dialog

														Intent i = new Intent(
																MainActivity.this,
																ViewActivity.class);
														finish();
														startActivity(i);

														// overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

													} catch (ParseException pe) {
														Log.v("PARSE_ERROR",
																pe.toString());
														Log.v("data", response);
														reportError("MainActivity "
																+ pe.toString()
																+ " "
																+ response);
														finish();
													}
												}
											});
								} catch (Exception e) {
									Log.v("INTERNET",
											"Unable to connect" + e.toString());
									reportError(e.toString());
									finish();
								}
							}
						});
					} catch (Exception e) {
						Log.v("error", e.toString());
						reportError(e.toString());
						Toast.makeText(MainActivity.this,
								"Current location not available",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			};

			new Handler().postDelayed(r, 1);
			this.dataAdded = true;
		}
	}

	/**
	 * This method is called when location service gets connected. After
	 * connection it requests for longitude and latitude.
	 */
	@Override
	public void onConnected(Bundle arg0) {
		if (this.locationclient != null && this.locationclient.isConnected()) {
			Location loc = this.locationclient.getLastLocation();
			if (loc != null) {
				locationReceived(loc);
			} else {
				this.locationrequest = LocationRequest.create();
				this.locationrequest.setInterval(2000);
				this.locationrequest.setFastestInterval(1000);
				this.locationrequest
						.setPriority(LocationRequest.PRIORITY_LOW_POWER);
				this.locationclient.requestLocationUpdates(
						this.locationrequest, this);
				Log.v("LOCATION", "QUERY STARTED");
			}

		} else {
			Log.i("Location", "NOT CONNECTED");
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Google play services not available",
				Toast.LENGTH_SHORT).show();
		reportError("Google play services not available. " + arg0.toString());
		finish();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Get handle of LocationManager
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// If LocationService is disabled, warn user to enable it and open
		// network settings.
		// Otherwise register for location updates.
		if (!locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			startActivity(new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			Toast.makeText(this,
					"Please turn on Network based location service.",
					Toast.LENGTH_SHORT).show();
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}

		// Show a process dialog.
		this.dialog = new ProgressDialog(MainActivity.this);
		this.dialog.setMessage("Preparing for the first use.");
		this.dialog.setCancelable(true);
		this.dialog.setCanceledOnTouchOutside(false);
		this.dialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
				return;
			}
		});
		this.dialog.show();
		// Update message if it is taking too long,
		// more than 20 seconds
		Timer timer = new Timer("message_changer", true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity.this.dialog
								.setMessage("Preparing for the first use."
										+ "If you are indoors, try moving to a window.");
					}
				});
			}
		}, 20 * 1000);

		// Check for GooglePlayServices and connect location client.
		// If not found, open Play store to install it.
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			this.locationclient = new LocationClient(this, this, this);
			this.locationclient.connect();
		} else {
			final String appPackageName = "com.google.android.gms";// getPackageName()
																	// from
																	// Context
																	// or
																	// Activity
																	// object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ appPackageName)));
			}
			Toast.makeText(this,
					"You must have Google Play services to use this app",
					Toast.LENGTH_LONG).show();
			reportError("Device must have Google Play services to use this app.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDisconnected() {
		Log.i("Location", "Disconnected");
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v("LOCATION", "location received");
		locationReceived(location);
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
	protected void onStart() {
		super.onStart();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method reports error back to the server for analysis.
	 * 
	 * @param message
	 */
	public void reportError(final String message) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							final String url = new String(
									Constants.ERROR_REPORT_URL);
							final int DEFAULT_TIMEOUT = 100 * 1000;
							try {
								AsyncHttpClient client = new AsyncHttpClient();
								client.setTimeout(DEFAULT_TIMEOUT);
								RequestParams params = new RequestParams();
								params.put("dev_id", Secure.getString(
										getApplicationContext()
												.getContentResolver(),
										Secure.ANDROID_ID));
								params.put("message", message);
								params.put(
										"version",
										getPackageManager().getPackageInfo(
												getPackageName(), 0).versionName);
								client.post(MainActivity.this, url, params,
										new AsyncHttpResponseHandler() {
											@Override
											public void onFailure(
													Throwable error,
													String response) {
												Toast.makeText(
														MainActivity.this,
														"Couldn't connect to Awair servers. Please try again.",
														Toast.LENGTH_SHORT)
														.show();
											}

											@Override
											public void onSuccess(
													String response) {

											}
										});
							} catch (Exception e) {
								Log.v("INTERNET",
										"Unable to connect" + e.toString());
							}
						}

					});
				} catch (Exception e) {
					Log.v("error", e.toString());
				}
			}
		};
		new Handler().postDelayed(r, 1);
	}
}
