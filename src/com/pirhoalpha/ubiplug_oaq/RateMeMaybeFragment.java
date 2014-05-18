/**
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RateMeMaybeFragment extends DialogFragment implements
		OnClickListener, OnCancelListener {

	public interface RMMFragInterface {
		void _handleCancel();

		void _handleNegativeChoice();

		void _handleNeutralChoice();

		void _handlePositiveChoice();
	}

	private int customIcon;
	private String message;
	private RMMFragInterface mInterface;
	private String negativeBtn;
	private String neutralBtn;
	private String positiveBtn;

	private String title;

	@Override
	public void onCancel(DialogInterface dialog) {
		this.mInterface._handleCancel();
	}

	@Override
	public void onClick(DialogInterface dialog, int choice) {
		switch (choice) {
		case DialogInterface.BUTTON_POSITIVE:
			this.mInterface._handlePositiveChoice();
			break;
		case DialogInterface.BUTTON_NEUTRAL:
			this.mInterface._handleNeutralChoice();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			this.mInterface._handleNegativeChoice();
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Fragment including variables will survive orientation changes
		this.setRetainInstance(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (this.customIcon != 0) {
			builder.setIcon(this.customIcon);
		}
		builder.setTitle(this.title);
		builder.setMessage(this.message);
		builder.setPositiveButton(this.positiveBtn, this);
		builder.setNeutralButton(this.neutralBtn, this);
		builder.setNegativeButton(this.negativeBtn, this);
		builder.setOnCancelListener(this);
		AlertDialog alert = builder.create();

		return alert;
	}

	@Override
	public void onDestroyView() {
		// Work around bug:
		// http://code.google.com/p/android/issues/detail?id=17423
		Dialog dialog = getDialog();

		if (dialog != null && getRetainInstance()) {
			dialog.setDismissMessage(null);
		}

		super.onDestroyView();
	}

	public void setData(int customIcon, String title, String message,
			String positiveBtn, String neutralBtn, String negativeBtn,
			RMMFragInterface myInterface) {
		this.customIcon = customIcon;
		this.title = title;
		this.message = message;
		this.positiveBtn = positiveBtn;
		this.neutralBtn = neutralBtn;
		this.negativeBtn = negativeBtn;
		this.mInterface = myInterface;
	}

}