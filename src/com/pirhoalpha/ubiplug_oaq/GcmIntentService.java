/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;


import java.util.HashMap;


import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.util.Log;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private String TAG = "GCM";
    private HashMap<String,String> data;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @SuppressLint("NewApi")
	@Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        if (!extras.isEmpty()){
        	data = new HashMap<String, String>();
        	data.put("title", extras.getString("title"));
        	data.put("message", extras.getString("message"));
        	data.put("recommendation", extras.getString("recommendation"));
        	data.put("lastLocation", extras.getString("lastLocation", ""));
        	data.put("lastTime", extras.getString("lastTime", ""));
        	data.put("type",extras.getString("type",""));
        	data.put("sub_type",extras.getString("sub_type",""));
        	sendNotification(data.get("title"),data.get("message"),data.get("type"),data.get("sub_type"));
        }
      
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void sendNotification(String title, String msg, String type, String sub_type) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent =  new Intent(this, HealthTipsActivity.class);
        intent.putExtra("data", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); 
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        if(type.intern()=="gas_hike"){	
        	title = "Increased "+sub_type+ " level";
        }
        if(android.os.Build.VERSION.SDK_INT<16){
	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.logo_action)
	        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
	        .setContentTitle(title)
	        .setContentText(msg)
	        .setDefaults(Notification.DEFAULT_ALL)
	        .setStyle(new NotificationCompat.BigTextStyle());
	        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
	        mBuilder.setContentIntent(contentIntent);
	        mBuilder.setSound(uri);
	        mBuilder.setTicker(title+"\n"+msg);
	        mBuilder.setAutoCancel(true);
	        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }else{
        	Builder builder = new Notification.Builder(this);
        	mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        	builder.setContentTitle(title)
        		.setSmallIcon(R.drawable.logo_action)
        		.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher))
        		.setAutoCancel(true)
        		.setLights(Color.argb(1, 0, 200, 0), 100, 100)
        		.setContentText(msg)
        		.setPriority(Notification.PRIORITY_HIGH)
        		.setDefaults(Notification.DEFAULT_ALL)
        		.setTicker(title+"\n"+msg)
        		.addAction(R.drawable.hand, "Know More", contentIntent)
        		.setContentIntent(contentIntent);
        	builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        	Notification notification = new Notification.BigTextStyle(builder)
            .bigText(msg).build();
        	mNotificationManager.notify(NOTIFICATION_ID, notification);
        	
        }
        
    }
}