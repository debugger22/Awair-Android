package com.pirhoalpha.ubiplug_oaq;


import java.util.ArrayList;

import java.util.HashMap;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * @author mrsud
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
    	
    	getCitiesData(getActivity()); //Loading the data from the server
    	
    	// Getting pollution data from intent
    	particleData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[0];
    	chemicalData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[1];
    	gaseousData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[2];
    	overallData = String.valueOf((int) (100-(Math.min(Double.valueOf(particleData), 480.0)/5.0)));
    	
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
    	
    	// Resetting text of textviews
    	cityHome.setText("Your location");
    	city1.setText("");
    	city2.setText("");
    	city3.setText("");
    	city4.setText("");
    	city5.setText("");
    	city6.setText("");
    	
    	// Setting value of home location progressbar
    	progHome.setProgressDrawable(getActivity().getResources().getDrawable(
    			getAppropriateProgressBar(getPercentage(Integer.parseInt(particleData)))));
    	progHome.setProgress(getPercentage(Integer.parseInt(particleData)));
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
    private void getCitiesData(final Context ctx){
	    	final String url = new String("http://www.ubiplug.com:8080/ubiair/comparecities/");
			final int DEFAULT_TIMEOUT = 100 * 1000;
	    	try {
	        	AsyncHttpClient client = new AsyncHttpClient();
	        	client.setTimeout(DEFAULT_TIMEOUT);
		    	RequestParams params = new RequestParams();
		    	params.put("lat", String.valueOf(ViewActivity.lat));
		    	params.put("lng", String.valueOf(ViewActivity.lng));
		    	client.post(ctx,url,params,new AsyncHttpResponseHandler() 
				{
				    @SuppressWarnings("unchecked")
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
						    try {
						    	
						    	HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>)parser.parse(response, containerFactory);
						    	
								city1.setText((CharSequence) data.keySet().toArray()[0]);
								city2.setText((CharSequence) data.keySet().toArray()[1]);
								city3.setText((CharSequence) data.keySet().toArray()[2]);
								city4.setText((CharSequence) data.keySet().toArray()[3]);
								city5.setText((CharSequence) data.keySet().toArray()[4]);
								city6.setText((CharSequence) data.keySet().toArray()[5]);
								
								pCity1.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[0]).get("pm25").split(" ")[0])));
								pCity2.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[1]).get("pm25").split(" ")[0])));
								pCity3.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[2]).get("pm25").split(" ")[0])));
								pCity4.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[3]).get("pm25").split(" ")[0])));
								pCity5.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[4]).get("pm25").split(" ")[0])));
								pCity6.setProgress(getPercentage(Integer.parseInt(data.get(
										data.keySet().toArray()[5]).get("pm25").split(" ")[0])));
								
						    	pCity1.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[0]).get("pm25").split(" ")[0])))));
						    	pCity2.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[1]).get("pm25").split(" ")[0])))));
						    	pCity3.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[2]).get("pm25").split(" ")[0])))));
						    	pCity4.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[3]).get("pm25").split(" ")[0])))));
						    	pCity5.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[4]).get("pm25").split(" ")[0])))));
						    	pCity6.setProgressDrawable(getActivity().getResources().getDrawable(
						    			getAppropriateProgressBar(getPercentage(Integer.parseInt(data.get(
												data.keySet().toArray()[5]).get("pm25").split(" ")[0])))));
								dialog.dismiss();
								
								
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								Log.v("JSON PARSE ERROR", e.toString()+response);
								dialog.dismiss();
							}        	    	
	        	    }
	        	    
	    	    	@Override
	        	    public void onFailure(Throwable error, String response){
	        	    	Log.v("INTERNET_ERROR1", "Something went wrong "+response);	        	    
						dialog.dismiss();
	        	    }
	        	    
	           	});
	       	}
	    	catch(Exception e){
	    		Log.v("INTERNET", "Unable to connect"+e.toString());
	    		//getActionBar().setSubtitle("");
	    	}
		}
    
    // Not being used
    /*
    private void getData(final Context ctx,Double lat, Double lng){
    	final String url = DatabaseReader.AirData.BASE_URL;
		final int DEFAULT_TIMEOUT = 100 * 1000;
    	try {
        	AsyncHttpClient client = new AsyncHttpClient();
        	client.setTimeout(DEFAULT_TIMEOUT);
	    	RequestParams params = new RequestParams();
	    	params.put("lat", String.valueOf(lat));
	    	params.put("lng", String.valueOf(lng));
	    	client.post(ctx,url,params,new AsyncHttpResponseHandler() 
			{
	    		@SuppressWarnings("unchecked")
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
				    try {
				    	pollutionData = (HashMap<String, HashMap<String, String>>)parser.parse(response, containerFactory);
					} catch (ParseException e) {
						Log.v("JSON PARSE ERROR", e.toString()+response);
					}        	    	
	    		}

    	    	@Override
        	    public void onFailure(Throwable error, String response){
        	    	Log.v("INTERNET_ERROR1", "Something went wrong "+response);
        	    }
           	});
       	}
    	catch(Exception e){
    		Log.v("INTERNET", "Unable to connect"+e.toString());
    	}
    }
    */
}
