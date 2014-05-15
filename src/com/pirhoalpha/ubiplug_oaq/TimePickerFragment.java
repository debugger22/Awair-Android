/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Use the current time as the default values for the picker
	SharedPreferences data_prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
	final Calendar c = Calendar.getInstance();
	int hour = c.get(Calendar.HOUR_OF_DAY);
	int minute = c.get(Calendar.MINUTE);
	
	// Create a new instance of TimePickerDialog and return it
	return new TimePickerDialog(getActivity(), this, hour, minute,
	DateFormat.is24HourFormat(getActivity()));
	}
	
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		SharedPreferences data_prefs = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
		SharedPreferences.Editor prefs_editor = data_prefs.edit();
		prefs_editor.putInt("notification_hour", hourOfDay);
		prefs_editor.putInt("notification_minute", minute);
		prefs_editor.commit();
		
		//Registering alarmmanager for notifications on set time
	 	Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	    calendar.set(Calendar.MINUTE, minute);
	    Intent intent = new Intent(getActivity(), ClientNotificationService.class);
	    PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);
	    AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
	    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000 , pintent);
	    Log.v("Alarm", "Alarm registered");
	}
}