/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
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

public class ClientNotificationService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	public static Double lat = (double) 0;
	public static Double lng = (double) 0;
	private HashMap<String, String> data;
	private boolean GSMDataAdded = false;
	private Location location;
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private final int NOTIFICATION_ID = 001;

	private void locationReceived(Location location) {
		if (!this.GSMDataAdded) {
			lat = location.getLatitude();
			lng = location.getLongitude();
			final String hour = String.valueOf(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
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
				client.post(this, url, params, new AsyncHttpResponseHandler() {
					@Override
					public void onFailure(Throwable error, String response) {
						Log.v("INTERNET_ERROR1",
								"Could not connect to internet " + response);
					}

					@Override
					public void onSuccess(String response) {
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
							ClientNotificationService.this.data = (HashMap<String, String>) parser
									.parse(response, containerFactory);
							String msg = "Air quality around you is "
									+ ClientNotificationService.this.data
											.get("aq") + "%. Have a nice day!";
							SharedPreferences data_prefs = getSharedPreferences(
									Constants.PREFS_NAME, 0);
							boolean checked = data_prefs.getBoolean(
									"notification", false);
							if (checked) {
								sendNotification("Air Quality", msg,
										"general_purpose", "");
							}

						} catch (Exception pe) {
							Log.v("PARSE_ERROR", pe.toString());
						}
					}
				});
			} catch (Exception e) {
				Log.v("INTERNET", "Unable to connect" + e.toString());
			}
		}
		this.GSMDataAdded = true;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (this.locationclient != null && this.locationclient.isConnected()) {
			Location loc = this.locationclient.getLastLocation();
			if (loc != null) {
				locationReceived(loc);
			} else {
				this.locationrequest = LocationRequest.create();
				this.locationrequest.setInterval(5000);
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

	}

	@Override
	public void onCreate() {
		Log.v("Service", "Service started");
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			this.locationclient = new LocationClient(this, this, this);
			this.locationclient.connect();
		} else {
			Toast.makeText(this, "Google Play Service Error " + resp,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		Log.v("LOCATION", "location received");
		locationReceived(this.location);
		this.locationclient.removeLocationUpdates(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO do something useful
		return Service.START_NOT_STICKY;
	}

	/**
	 * Generates notification
	 * 
	 * @param title
	 * @param msg
	 * @param type
	 * @param sub_type
	 */
	@SuppressLint("NewApi")
	private void sendNotification(String title, String msg, String type,
			String sub_type) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, ViewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_NOTIFICATION);
		if (type.intern() == "gas_hike") {
			title = "Increased " + sub_type + " level";
		}
		// Typical notification configuration
		Builder builder = new Notification.Builder(this);
		mNotificationManager.notify(this.NOTIFICATION_ID, builder.build());
		builder.setContentTitle(title)
				.setSmallIcon(R.drawable.logo_action)
				.setLargeIcon(
						BitmapFactory.decodeResource(this.getResources(),
								R.drawable.ic_launcher)).setAutoCancel(true)
				.setLights(Color.argb(1, 0, 200, 0), 100, 100)
				.setContentText(msg).setPriority(Notification.PRIORITY_LOW)
				.setDefaults(Notification.DEFAULT_ALL)
				.setTicker(title + "\n" + msg).setContentIntent(contentIntent);
		builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
		Notification notification = new Notification.BigTextStyle(builder)
				.bigText(msg).build();
		mNotificationManager.notify(this.NOTIFICATION_ID, notification);
	}
}
