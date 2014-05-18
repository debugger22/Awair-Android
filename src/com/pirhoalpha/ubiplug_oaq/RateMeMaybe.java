/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

public class RateMeMaybe implements RateMeMaybeFragment.RMMFragInterface {
	public interface OnRMMUserChoiceListener {
		void handleNegative();

		void handleNeutral();

		void handlePositive();
	}

	static class PREF {
		private static final String DONT_SHOW_AGAIN = "PREF_DONT_SHOW_AGAIN";

		/**
		 * How many times the app was launched since the last prompt
		 */
		public static final String LAUNCHES_SINCE_LAST_PROMPT = "PREF_LAUNCHES_SINCE_LAST_PROMPT";
		public static final String NAME = "rate_me_maybe";
		/**
		 * Timestamp of when the app was launched for the first time
		 */
		public static final String TIME_OF_ABSOLUTE_FIRST_LAUNCH = "PREF_TIME_OF_ABSOLUTE_FIRST_LAUNCH";
		/**
		 * Timestamp of the last user prompt
		 */
		public static final String TIME_OF_LAST_PROMPT = "PREF_TIME_OF_LAST_PROMPT";
		/**
		 * How many times the app was launched in total
		 */
		public static final String TOTAL_LAUNCH_COUNT = "PREF_TOTAL_LAUNCH_COUNT";
	}

	private static final String TAG = "RateMeMaybe";

	/**
	 * Reset the launch logs
	 */
	public static void resetData(FragmentActivity activity) {
		activity.getSharedPreferences(PREF.NAME, 0).edit().clear().commit();
		Log.d(TAG, "Cleared RateMeMaybe shared preferences.");
	}

	private final FragmentActivity mActivity;
	private String mDialogMessage;
	private String mDialogTitle;
	private Boolean mHandleCancelAsNeutral = true;
	private int mIcon;

	private OnRMMUserChoiceListener mListener;
	private int mMinDaysUntilInitialPrompt = 0;

	private int mMinDaysUntilNextPrompt = 0;
	private int mMinLaunchesUntilInitialPrompt = 0;

	private int mMinLaunchesUntilNextPrompt = 0;

	private String mNegativeBtn;

	private String mNeutralBtn;

	private String mPositiveBtn;

	private final SharedPreferences mPreferences;

	private Boolean mRunWithoutPlayStore = false;

	public RateMeMaybe(FragmentActivity activity) {
		this.mActivity = activity;
		this.mPreferences = this.mActivity.getSharedPreferences(PREF.NAME, 0);
	}

	@Override
	public void _handleCancel() {
		if (this.mHandleCancelAsNeutral) {
			_handleNeutralChoice();
		} else {
			_handleNegativeChoice();
		}
	}

	@Override
	public void _handleNegativeChoice() {
		Editor editor = this.mPreferences.edit();
		editor.putBoolean(PREF.DONT_SHOW_AGAIN, true);
		editor.commit();
		if (this.mListener != null) {
			this.mListener.handleNegative();
		}
	}

	@Override
	public void _handleNeutralChoice() {
		if (this.mListener != null) {
			this.mListener.handleNeutral();
		}
	}

	@Override
	public void _handlePositiveChoice() {
		Editor editor = this.mPreferences.edit();
		editor.putBoolean(PREF.DONT_SHOW_AGAIN, true);
		editor.commit();

		try {
			this.mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id="
							+ this.mActivity.getPackageName())));
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this.mActivity, "Could not launch Play Store!",
					Toast.LENGTH_SHORT).show();
		}

		if (this.mListener != null) {
			this.mListener.handlePositive();
		}

	}

	/**
	 * Forces the dialog to show, even if the requirements are not yet met. Does
	 * not affect prompt logs. Use with care.
	 */
	public void forceShow() {
		showDialog();
	}

	/**
	 * @return the application name of the host activity
	 */
	private String getApplicationName() {
		final PackageManager pm = this.mActivity.getApplicationContext()
				.getPackageManager();
		ApplicationInfo ai;
		String appName;
		try {
			ai = pm.getApplicationInfo(this.mActivity.getPackageName(), 0);
			appName = (String) pm.getApplicationLabel(ai);
		} catch (final NameNotFoundException e) {
			appName = "(unknown)";
		}
		return appName;
	}

	public String getDialogMessage() {
		if (this.mDialogMessage == null) {
			return "If you like using "
					+ this.getApplicationName()
					+ ", it would be great"
					+ " if you took a moment to rate it in the Play Store. Thank you!";
		} else {
			return this.mDialogMessage.replace("%totalLaunchCount%", String
					.valueOf(this.mPreferences.getInt(PREF.TOTAL_LAUNCH_COUNT,
							0)));
		}
	}

	public String getDialogTitle() {
		if (this.mDialogTitle == null) {
			return "Rate " + getApplicationName();
		} else {
			return this.mDialogTitle;
		}
	}

	public int getIcon() {
		return this.mIcon;
	}

	public String getNegativeBtn() {
		if (this.mNegativeBtn == null) {
			return "Never";
		} else {
			return this.mNegativeBtn;
		}
	}

	public String getNeutralBtn() {
		if (this.mNeutralBtn == null) {
			return "Not now";
		} else {
			return this.mNeutralBtn;
		}
	}

	public String getPositiveBtn() {
		if (this.mPositiveBtn == null) {
			return "Rate it";
		} else {
			return this.mPositiveBtn;
		}
	}

	/**
	 * @return Whether Google Play Store is installed on device
	 */
	private Boolean isPlayStoreInstalled() {
		PackageManager pacman = this.mActivity.getPackageManager();
		try {
			pacman.getApplicationInfo("com.android.vending", 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * Normal way to update the launch logs and show the user prompt if the
	 * requirements are met.
	 */
	public void run() {
		if (this.mPreferences.getBoolean(PREF.DONT_SHOW_AGAIN, false)) {
			return;
		}

		if (!isPlayStoreInstalled()) {
			Log.d(TAG, "No Play Store installed on device.");
			if (!this.mRunWithoutPlayStore) {
				return;
			}
		}

		Editor editor = this.mPreferences.edit();

		int totalLaunchCount = this.mPreferences.getInt(
				PREF.TOTAL_LAUNCH_COUNT, 0) + 1;
		editor.putInt(PREF.TOTAL_LAUNCH_COUNT, totalLaunchCount);

		long currentMillis = System.currentTimeMillis();

		long timeOfAbsoluteFirstLaunch = this.mPreferences.getLong(
				PREF.TIME_OF_ABSOLUTE_FIRST_LAUNCH, 0);
		if (timeOfAbsoluteFirstLaunch == 0) {
			// this is the first launch!
			timeOfAbsoluteFirstLaunch = currentMillis;
			editor.putLong(PREF.TIME_OF_ABSOLUTE_FIRST_LAUNCH,
					timeOfAbsoluteFirstLaunch);
		}

		long timeOfLastPrompt = this.mPreferences.getLong(
				PREF.TIME_OF_LAST_PROMPT, 0);

		int launchesSinceLastPrompt = this.mPreferences.getInt(
				PREF.LAUNCHES_SINCE_LAST_PROMPT, 0) + 1;
		editor.putInt(PREF.LAUNCHES_SINCE_LAST_PROMPT, launchesSinceLastPrompt);

		if (totalLaunchCount >= this.mMinLaunchesUntilInitialPrompt
				&& currentMillis - timeOfAbsoluteFirstLaunch >= this.mMinDaysUntilInitialPrompt
						* DateUtils.DAY_IN_MILLIS) {
			// requirements for initial launch are met
			if (timeOfLastPrompt == 0 /* user was not yet shown a prompt */
					|| launchesSinceLastPrompt >= this.mMinLaunchesUntilNextPrompt
					&& currentMillis - timeOfLastPrompt >= this.mMinDaysUntilNextPrompt
							* DateUtils.DAY_IN_MILLIS) {
				editor.putLong(PREF.TIME_OF_LAST_PROMPT, currentMillis);
				editor.putInt(PREF.LAUNCHES_SINCE_LAST_PROMPT, 0);
				editor.commit();
				showDialog();
			} else {
				editor.commit();
			}
		} else {
			editor.commit();
		}

	}

	/**
	 * Sets an additional callback for when the user has made a choice.
	 * 
	 * @param listener
	 */
	public void setAdditionalListener(OnRMMUserChoiceListener listener) {
		this.mListener = listener;
	}

	/**
	 * Sets the message shown to the user. %totalLaunchCount% will be replaced
	 * with total launch count.
	 * 
	 * @param dialogMessage
	 *            The message shown
	 */
	public void setDialogMessage(String dialogMessage) {
		this.mDialogMessage = dialogMessage;
	}

	/**
	 * Sets the title of the dialog shown to the user
	 * 
	 * @param dialogTitle
	 */
	public void setDialogTitle(String dialogTitle) {
		this.mDialogTitle = dialogTitle;
	}

	/**
	 * @param handleCancelAsNeutral
	 *            Standard is true. If set to false, a back press (or other
	 *            things that lead to the dialog being cancelled), will be
	 *            handled like a negative choice (click on "Never").
	 */
	public void setHandleCancelAsNeutral(Boolean handleCancelAsNeutral) {
		this.mHandleCancelAsNeutral = handleCancelAsNeutral;
	}

	/**
	 * @param customIcon
	 *            Drawable id of custom icon
	 */
	public void setIcon(int customIcon) {
		this.mIcon = customIcon;
	}

	/**
	 * Sets name of button that makes the prompt never show again
	 * 
	 * @param negativeBtn
	 */
	public void setNegativeBtn(String negativeBtn) {
		this.mNegativeBtn = negativeBtn;
	}

	/**
	 * Sets name of neutral button
	 * 
	 * @param neutralBtn
	 */
	public void setNeutralBtn(String neutralBtn) {
		this.mNeutralBtn = neutralBtn;
	}

	/**
	 * Sets name of button that opens Play Store entry
	 * 
	 * @param positiveBtn
	 */
	public void setPositiveBtn(String positiveBtn) {
		this.mPositiveBtn = positiveBtn;
	}

	/**
	 * Sets requirements for when to prompt the user.
	 * 
	 * @param minLaunchesUntilInitialPrompt
	 *            Minimum of launches before the user is prompted for the first
	 *            time. One call of .run() counts as launch.
	 * @param minDaysUntilInitialPrompt
	 *            Minimum of days before the user is prompted for the first
	 *            time.
	 * @param minLaunchesUntilNextPrompt
	 *            Minimum of launches before the user is prompted for each next
	 *            time. One call of .run() counts as launch.
	 * @param minDaysUntilNextPrompt
	 *            Minimum of days before the user is prompted for each next
	 *            time.
	 */
	public void setPromptMinimums(int minLaunchesUntilInitialPrompt,
			int minDaysUntilInitialPrompt, int minLaunchesUntilNextPrompt,
			int minDaysUntilNextPrompt) {
		this.mMinLaunchesUntilInitialPrompt = minLaunchesUntilInitialPrompt;
		this.mMinDaysUntilInitialPrompt = minDaysUntilInitialPrompt;
		this.mMinLaunchesUntilNextPrompt = minLaunchesUntilNextPrompt;
		this.mMinDaysUntilNextPrompt = minDaysUntilNextPrompt;
	}

	/**
	 * Standard is false. Whether the run method is executed even if no Play
	 * Store is installed on device.
	 * 
	 * @param runWithoutPlayStore
	 */
	public void setRunWithoutPlayStore(Boolean runWithoutPlayStore) {
		this.mRunWithoutPlayStore = runWithoutPlayStore;
	}

	/**
	 * Actually show the dialog (if it is not currently shown)
	 */
	private void showDialog() {
		if (this.mActivity.getSupportFragmentManager().findFragmentByTag(
				"rmmFragment") != null) {
			// the dialog is already shown to the user
			return;
		}
		RateMeMaybeFragment frag = new RateMeMaybeFragment();
		frag.setData(getIcon(), getDialogTitle(), getDialogMessage(),
				getPositiveBtn(), getNeutralBtn(), getNegativeBtn(), this);
		frag.show(this.mActivity.getSupportFragmentManager(), "rmmFragment");

	}

}
