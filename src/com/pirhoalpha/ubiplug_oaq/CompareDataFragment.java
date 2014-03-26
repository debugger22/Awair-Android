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


public class CompareDataFragment extends Fragment {
    
	private Map<String, HashMap<String, String>> sortedData;
	private ArrayList<String> cities;
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
    	cities = new ArrayList<String>();
    	getCitiesData(getActivity());
    	particleData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[0];
    	chemicalData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[1];
    	gaseousData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[2];
    	overallData = String.valueOf((int) (100-(Math.min(Double.valueOf(particleData), 480.0)/5.0)));
    	
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
    	
    	cityHome.setText("Your location");
    	progHome.setProgress(getPercentage(Integer.parseInt(particleData)));
    	
    }
   
    
    private int getPercentage(int data){
    	return (int) (100-(Math.min(Double.valueOf(data), 480.0)/5.0));
    }
    
    
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
						    	//Although the data is not sorted
						    	HashMap<String, HashMap<String, String>> sortedData = (HashMap<String, HashMap<String, String>>)parser.parse(response, containerFactory);
								//sortedData = new TreeMap<String, HashMap<String, String>>(data);

								city1.setText((CharSequence) sortedData.keySet().toArray()[0]);
								city2.setText((CharSequence) sortedData.keySet().toArray()[1]);
								city3.setText((CharSequence) sortedData.keySet().toArray()[2]);
								city4.setText((CharSequence) sortedData.keySet().toArray()[3]);
								city5.setText((CharSequence) sortedData.keySet().toArray()[4]);
								city6.setText((CharSequence) sortedData.keySet().toArray()[5]);
								
								pCity1.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[0]).get("pm25").split(" ")[0])));
								pCity2.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[1]).get("pm25").split(" ")[0])));
								pCity3.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[2]).get("pm25").split(" ")[0])));
								pCity4.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[3]).get("pm25").split(" ")[0])));
								pCity5.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[4]).get("pm25").split(" ")[0])));
								pCity6.setProgress(getPercentage(Integer.parseInt(sortedData.get(
										sortedData.keySet().toArray()[5]).get("pm25").split(" ")[0])));
								
								
								
							} catch (ParseException e) {
								// TODO Auto-generated catch block
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
	    		//getActionBar().setSubtitle("");
	    	}
		}
    
    
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
							// TODO Auto-generated catch block
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
    		//getActionBar().setSubtitle("");
    	}
    }
    

}
