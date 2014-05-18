/**
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/*
 import com.facebook.*;
 import com.facebook.model.*;
 import com.facebook.widget.LoginButton;
 */

public class SettingsFragment extends Fragment {

	private CheckBox chkBoxNotification;
	// private UserDataManager userDataManagerReader;
	private Button cmdSetNotificationTime;
	private TextView currentNotificationTime;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/roboto-regular.ttf");
		this.chkBoxNotification = (CheckBox) getActivity().findViewById(
				R.id.chkBoxNotifications);
		this.cmdSetNotificationTime = (Button) getActivity().findViewById(
				R.id.cmdSetNotificationTime);
		this.currentNotificationTime = (TextView) getActivity().findViewById(
				R.id.current_notification_time);
		this.currentNotificationTime.setTypeface(tf);

		SharedPreferences data_prefs = getActivity().getSharedPreferences(
				Constants.PREFS_NAME, 0);
		boolean checked = data_prefs.getBoolean("notification", false);
		this.chkBoxNotification.setChecked(checked);
		this.cmdSetNotificationTime.setEnabled(checked);

		int hour = data_prefs.getInt("notification_hour", 0);
		int minute = data_prefs.getInt("notification_minute", 0);

		// Converting 24 Hr format into 12 Hr format
		StringBuilder sb = new StringBuilder();
		if (hour >= 12) {
			sb.append(hour - 12).append(":").append(minute).append(" PM");
		} else {
			sb.append(hour).append(":").append(minute).append(" AM");
		}
		this.currentNotificationTime.setText("Currently: " + sb.toString());

		final SharedPreferences.Editor prefs_editor = data_prefs.edit();
		this.chkBoxNotification
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						prefs_editor.putBoolean("notification", isChecked);
						SettingsFragment.this.cmdSetNotificationTime
								.setEnabled(isChecked);
						prefs_editor.commit();
					}
				});

		this.cmdSetNotificationTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getActivity().getSupportFragmentManager(),
						"timePicker");

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.settings_fragment, container,
				false);
		return rootView;
	}

}