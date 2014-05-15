/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * This fragment class is for the cpmpare fragment on the KnowMore activity.
 * It uses compare_fragment xml file.
 *
 */
public class CompareDataFragment extends Fragment {
    
	private ProgressDialog dialog;
	private String particleData;
	private String chemicalData;
	private String gaseousData;
	private String overallData;
	
	private TextView cityHome;
	private TextView city1;
	private TextView city2;
	private TextView city3;
	private TextView city4;
	private TextView city5;
	private TextView city6;
	
	private ProgressBar progHome;
	private ProgressBar pCity1;
	private ProgressBar pCity2;
	private ProgressBar pCity3;
	private ProgressBar pCity4;
	private ProgressBar pCity5;
	private ProgressBar pCity6;
	
	private int val_home, val1, val2, val3, val4, val5, val6;

	private HashMap<String, HashMap<String,String>> pollutionData;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.compare_data_fragment, container, false);
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	// Setting up process dialog
    	dialog = new ProgressDialog(getActivity());
    	dialog.setMessage("Please wait");
    	dialog.setCancelable(true);
    	dialog.show();
    	
    	// Getting views from xml
    	cityHome = (TextView)getActivity().findViewById(R.id.city_home);
    	city1 = (TextView)getActivity().findViewById(R.id.city1);
    	city2 = (TextView)getActivity().findViewById(R.id.city2);
    	city3 = (TextView)getActivity().findViewById(R.id.city3);
    	city4 = (TextView)getActivity().findViewById(R.id.city4);
    	city5 = (TextView)getActivity().findViewById(R.id.city5);
    	city6 = (TextView)getActivity().findViewById(R.id.city6);
    	progHome = (ProgressBar)getActivity().findViewById(R.id.prog_home);
    	pCity1 = (ProgressBar)getActivity().findViewById(R.id.prog1);
    	pCity2 = (ProgressBar)getActivity().findViewById(R.id.prog2);
    	pCity3 = (ProgressBar)getActivity().findViewById(R.id.prog3);
    	pCity4 = (ProgressBar)getActivity().findViewById(R.id.prog4);
    	pCity5 = (ProgressBar)getActivity().findViewById(R.id.prog5);
    	pCity6 = (ProgressBar)getActivity().findViewById(R.id.prog6);
    	
    	// Configuring home
    	val_home = ViewActivity.aq;
    	cityHome.setText("Your location [" + String.valueOf(val_home) + "%]");
    	progHome.setProgressDrawable(getActivity().getResources().getDrawable(
    			getAppropriateProgressBar(val_home)));
    	
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
    		SharedPreferences data_prefs = getActivity().getSharedPreferences(Constants.DATA_CACHE_PREFS_NAME, 0);
    		if(data_prefs.getBoolean("compare_data_cached", false)){
    			pollutionData = (HashMap<String, HashMap<String, String>>)parser.parse(data_prefs.getString("compare_cities_data", ""), containerFactory);
    			Log.v("Data", pollutionData.toString());
    			
    			String [] keys = pollutionData.keySet().toArray(new String[0]);
			    val1 = Integer.parseInt(pollutionData.get(keys[0]).get("aq"));
			    val2 = Integer.parseInt(pollutionData.get(keys[1]).get("aq"));
			    val3 = Integer.parseInt(pollutionData.get(keys[2]).get("aq"));
			    val4 = Integer.parseInt(pollutionData.get(keys[3]).get("aq"));
			    val5 = Integer.parseInt(pollutionData.get(keys[4]).get("aq"));
			    val6 = Integer.parseInt(pollutionData.get(keys[5]).get("aq"));
			    city1.setText((CharSequence) keys[0] +" ["+String.valueOf(val1)+"%]");
			    city2.setText((CharSequence) keys[1] +" ["+String.valueOf(val2)+"%]");
			    city3.setText((CharSequence) keys[2] +" ["+String.valueOf(val3)+"%]");
			    city4.setText((CharSequence) keys[3] +" ["+String.valueOf(val4)+"%]");
			    city5.setText((CharSequence) keys[4] +" ["+String.valueOf(val5)+"%]");
			    city6.setText((CharSequence) keys[5] +" ["+String.valueOf(val6)+"%]");
			    
				// Setting the background drawable of the progress bars
			    pCity1.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val1)));
			    pCity2.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val2)));
			    pCity3.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val3)));
			    pCity4.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val4)));
			    pCity5.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val5)));
			    pCity6.setProgressDrawable(getActivity().getResources().getDrawable(
		    			getAppropriateProgressBar(val6)));
    		}
    		
    	}catch(Exception pe){
    		Log.v("ParseError", pe.toString());
    	}
    	updateUI(); //Loading the data from the server
    }

    /**
     * This method takes pm25 data and returns its percentage
     * @param data
     * @return
     */
    private int getPercentage(int data){
    	return (int) (100-(Math.min(Double.valueOf(data), 480.0)/5.0));
    }
   
    /**
     * This method takes percentage value and returns appropriate colored
     * progressbar layout
     * @param value
     * @return progressbar drawable
     */
    private int getAppropriateProgressBar(int value){
    	if(value<50)return R.drawable.custom_progressbar_red;
    	else if(value>=50 && value<80)return R.drawable.custom_progressbar_yellow;
    	else return R.drawable.custom_progressbar_green;
    }
    
    /**
     * This method loads the data from the server
     * @param ctx
     */
    private void updateUI(){
		dialog.dismiss();
		
		// Animating the progress bars
		ObjectAnimator animHome = ObjectAnimator.ofInt(progHome, "progress", val_home);
		ObjectAnimator animation1 = ObjectAnimator.ofInt(pCity1, "progress", val1);
		ObjectAnimator animation2 = ObjectAnimator.ofInt(pCity2, "progress", val2);
		ObjectAnimator animation3 = ObjectAnimator.ofInt(pCity3, "progress", val3);
		ObjectAnimator animation4 = ObjectAnimator.ofInt(pCity4, "progress", val4);
		ObjectAnimator animation5 = ObjectAnimator.ofInt(pCity5, "progress", val5);
		ObjectAnimator animation6 = ObjectAnimator.ofInt(pCity6, "progress", val6);
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


    public void reportError(final String message){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					getActivity().runOnUiThread(new Runnable (){
						@Override
						public void run() {
							final String url = new String(DatabaseReader.AirData.ERROR_REPORT_URL);
							final int DEFAULT_TIMEOUT = 100 * 1000;
					    	try {
					        	AsyncHttpClient client = new AsyncHttpClient();
					        	client.setTimeout(DEFAULT_TIMEOUT);
			        	    	RequestParams params = new RequestParams();
			        	    	params.put("dev_id", Secure.getString(getActivity().getApplicationContext().getContentResolver(),Secure.ANDROID_ID));
			        	    	params.put("message", message);
			  					params.put("version", getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
			        	    	client.post(getActivity(),url,params,new AsyncHttpResponseHandler() 
									{
								    @Override
								    public void onSuccess(String response) {
								    	//Do Nothing
								    }
								    @Override
								    public void onFailure(Throwable error, String response){
								    	Toast.makeText(getActivity(), "Error could not be reported.", Toast.LENGTH_SHORT).show();
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
}
