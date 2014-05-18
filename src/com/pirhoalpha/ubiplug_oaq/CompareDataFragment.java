/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * This fragment class is for the cpmpare fragment on the KnowMore activity. It
 * uses compare_fragment xml file.
 * 
 */
public class CompareDataFragment extends Fragment {

	private TextView city1;
	private TextView city2;
	private TextView city3;
	private TextView city4;
	private TextView city5;
	private TextView city6;
	private TextView cityHome;
	private ProgressDialog dialog;

	private ProgressBar pCity1;
	private ProgressBar pCity2;
	private ProgressBar pCity3;
	private ProgressBar pCity4;
	private ProgressBar pCity5;
	private ProgressBar pCity6;
	private HashMap<String, HashMap<String, String>> pollutionData;

	private ProgressBar progHome;

	private int val_home, val1, val2, val3, val4, val5, val6;

	/**
	 * This method takes percentage value and returns appropriate colored
	 * progressbar layout
	 * 
	 * @param value
	 * @return progressbar drawable
	 */
	private int getAppropriateProgressBar(int value) {
		if (value < 50) {
			return R.drawable.custom_progressbar_red;
		} else if (value >= 50 && value < 80) {
			return R.drawable.custom_progressbar_yellow;
		} else {
			return R.drawable.custom_progressbar_green;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Setting up process dialog
		this.dialog = new ProgressDialog(getActivity());
		this.dialog.setMessage("Please wait");
		this.dialog.setCancelable(true);
		this.dialog.show();

		// Getting views from xml
		this.cityHome = (TextView) getActivity().findViewById(R.id.city_home);
		this.city1 = (TextView) getActivity().findViewById(R.id.city1);
		this.city2 = (TextView) getActivity().findViewById(R.id.city2);
		this.city3 = (TextView) getActivity().findViewById(R.id.city3);
		this.city4 = (TextView) getActivity().findViewById(R.id.city4);
		this.city5 = (TextView) getActivity().findViewById(R.id.city5);
		this.city6 = (TextView) getActivity().findViewById(R.id.city6);
		this.progHome = (ProgressBar) getActivity()
				.findViewById(R.id.prog_home);
		this.pCity1 = (ProgressBar) getActivity().findViewById(R.id.prog1);
		this.pCity2 = (ProgressBar) getActivity().findViewById(R.id.prog2);
		this.pCity3 = (ProgressBar) getActivity().findViewById(R.id.prog3);
		this.pCity4 = (ProgressBar) getActivity().findViewById(R.id.prog4);
		this.pCity5 = (ProgressBar) getActivity().findViewById(R.id.prog5);
		this.pCity6 = (ProgressBar) getActivity().findViewById(R.id.prog6);

		// Configuring home
		this.val_home = ViewActivity.aq;
		this.cityHome.setText("Your location [" + String.valueOf(this.val_home)
				+ "%]");
		this.progHome.setProgressDrawable(getActivity().getResources()
				.getDrawable(getAppropriateProgressBar(this.val_home)));

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
			SharedPreferences data_prefs = getActivity().getSharedPreferences(
					Constants.DATA_CACHE_PREFS_NAME, 0);
			if (data_prefs.getBoolean("compare_data_cached", false)) {
				this.pollutionData = (HashMap<String, HashMap<String, String>>) parser
						.parse(data_prefs.getString("compare_cities_data", ""),
								containerFactory);
				Log.v("Data", this.pollutionData.toString());

				String[] keys = this.pollutionData.keySet().toArray(
						new String[0]);
				this.val1 = Integer.parseInt(this.pollutionData.get(keys[0])
						.get("aq"));
				this.val2 = Integer.parseInt(this.pollutionData.get(keys[1])
						.get("aq"));
				this.val3 = Integer.parseInt(this.pollutionData.get(keys[2])
						.get("aq"));
				this.val4 = Integer.parseInt(this.pollutionData.get(keys[3])
						.get("aq"));
				this.val5 = Integer.parseInt(this.pollutionData.get(keys[4])
						.get("aq"));
				this.val6 = Integer.parseInt(this.pollutionData.get(keys[5])
						.get("aq"));
				this.city1.setText((CharSequence) keys[0] + " ["
						+ String.valueOf(this.val1) + "%]");
				this.city2.setText((CharSequence) keys[1] + " ["
						+ String.valueOf(this.val2) + "%]");
				this.city3.setText((CharSequence) keys[2] + " ["
						+ String.valueOf(this.val3) + "%]");
				this.city4.setText((CharSequence) keys[3] + " ["
						+ String.valueOf(this.val4) + "%]");
				this.city5.setText((CharSequence) keys[4] + " ["
						+ String.valueOf(this.val5) + "%]");
				this.city6.setText((CharSequence) keys[5] + " ["
						+ String.valueOf(this.val6) + "%]");

				// Setting the background drawable of the progress bars
				this.pCity1.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val1)));
				this.pCity2.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val2)));
				this.pCity3.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val3)));
				this.pCity4.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val4)));
				this.pCity5.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val5)));
				this.pCity6.setProgressDrawable(getActivity().getResources()
						.getDrawable(getAppropriateProgressBar(this.val6)));
			} else {
				Toast.makeText(getActivity(),
						"Data isn't available yet, please try again later.",
						Toast.LENGTH_LONG).show();
			}

		} catch (Exception pe) {
			Log.v("ParseError", pe.toString());
		}
		updateUI(); // Loading the data from the server
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.compare_data_fragment,
				container, false);
		return rootView;
	}

	public void reportError(final String message) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					getActivity().runOnUiThread(new Runnable() {
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
										getActivity().getApplicationContext()
												.getContentResolver(),
										Secure.ANDROID_ID));
								params.put("message", message);
								params.put(
										"version",
										getActivity()
												.getPackageManager()
												.getPackageInfo(
														getActivity()
																.getPackageName(),
														0).versionName);
								client.post(getActivity(), url, params,
										new AsyncHttpResponseHandler() {
											@Override
											public void onFailure(
													Throwable error,
													String response) {
												Toast.makeText(
														getActivity(),
														"Couldn't connect to Awair servers.",
														Toast.LENGTH_SHORT)
														.show();
											}

											@Override
											public void onSuccess(
													String response) {
												// Do Nothing
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

	/**
	 * This method loads the data from the server
	 * 
	 * @param ctx
	 */
	private void updateUI() {
		this.dialog.dismiss();

		// Animating the progress bars
		ObjectAnimator animHome = ObjectAnimator.ofInt(this.progHome,
				"progress", this.val_home);
		ObjectAnimator animation1 = ObjectAnimator.ofInt(this.pCity1,
				"progress", this.val1);
		ObjectAnimator animation2 = ObjectAnimator.ofInt(this.pCity2,
				"progress", this.val2);
		ObjectAnimator animation3 = ObjectAnimator.ofInt(this.pCity3,
				"progress", this.val3);
		ObjectAnimator animation4 = ObjectAnimator.ofInt(this.pCity4,
				"progress", this.val4);
		ObjectAnimator animation5 = ObjectAnimator.ofInt(this.pCity5,
				"progress", this.val5);
		ObjectAnimator animation6 = ObjectAnimator.ofInt(this.pCity6,
				"progress", this.val6);
		animHome.setDuration(1500); // 1.5 second
		animHome.setInterpolator(new DecelerateInterpolator());
		animHome.start();
		animation1.setDuration(1500); // 1.5 second
		animation1.setInterpolator(new DecelerateInterpolator());
		animation1.start();
		animation2.setDuration(1500); // 1.5 second
		animation2.setInterpolator(new DecelerateInterpolator());
		animation2.start();
		animation3.setDuration(1500); // 1.5 second
		animation3.setInterpolator(new DecelerateInterpolator());
		animation3.start();
		animation4.setDuration(1500); // 1.5 second
		animation4.setInterpolator(new DecelerateInterpolator());
		animation4.start();
		animation5.setDuration(1500); // 1.5 second
		animation5.setInterpolator(new DecelerateInterpolator());
		animation5.start();
		animation6.setDuration(1500); // 1.5 second
		animation6.setInterpolator(new DecelerateInterpolator());
		animation6.start();

	}
}
