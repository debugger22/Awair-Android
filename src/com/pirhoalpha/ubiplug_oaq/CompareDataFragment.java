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
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Toast;


public class CompareDataFragment extends Fragment {
    
	private Spinner citiesSpinner;
	private Map<String, String> sortedData;
	private ArrayList<String> cities;
	private GridView comparisionGrid;
	private LinearLayout compContainer;
	private String particleData;
	private String chemicalData;
	private String gaseousData;
	private String overallData;
	private String otherParticleData;
	private String otherChemicalData;
	private String otherGaseousData;
	private String otherOverallData;
	
	private String otherCityName;
	private HashMap<String, String> pollutionData; 
	
	
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
    	citiesSpinner = (Spinner)getActivity().findViewById(R.id.citiesSpinner);
    	cities = new ArrayList<String>();
    	comparisionGrid = (GridView)getActivity().findViewById(R.id.comparisionGrid);
    	compContainer = (LinearLayout)getActivity().findViewById(R.id.comparisonDataConatainer);
    	getCitiesData(getActivity());
    	citiesSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Double lat = Double.valueOf(sortedData.get(cities.get(position)).split(" ")[0]);
				Double lng = Double.valueOf(sortedData.get(cities.get(position)).split(" ")[1]);
				otherCityName = cities.get(position);
				getData(getActivity(),lat,lng);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getActivity(), "Please select a city to compare", Toast.LENGTH_SHORT).show();
				
			}
    		
    	});
    	particleData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[0];
    	chemicalData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[1];
    	gaseousData = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[2];
    	overallData = String.valueOf((int) (100-(Math.min(Double.valueOf(particleData), 480.0)/5.0)));
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
						    	HashMap<String, String> data = (HashMap<String, String>)parser.parse(response, containerFactory);
								sortedData = new TreeMap<String, String>(data);
								data.clear();
								Log.v("data",sortedData.toString());
								for(String city:sortedData.keySet()){
									cities.add(city);
								}
								ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,
										android.R.layout.simple_spinner_item, cities);
								citiesSpinner.setAdapter(dataAdapter);
								dataAdapter.notifyDataSetChanged();
								
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
					    	pollutionData = (HashMap<String, String>)parser.parse(response, containerFactory);
							otherParticleData  = pollutionData.get(DatabaseReader.AirData.COLUMN_NAME_PM25).split(" ")[0];
							otherGaseousData  = pollutionData.get(DatabaseReader.AirData.COLUMN_NAME_O3).split(" ")[0];
							otherOverallData = String.valueOf((int) (100-(Math.min(Double.valueOf(otherParticleData), 480.0)/5.0)));
							int co=0;
							int no2=0;
							try{
								co  = Integer.parseInt(pollutionData.get(DatabaseReader.AirData.COLUMN_NAME_CO).split(" ")[0]);	
							}catch(Exception e){
								Log.v("ValueError", e.toString());
								
							}
							try{	
								no2  = Integer.parseInt(pollutionData.get(DatabaseReader.AirData.COLUMN_NAME_NO2).split(" ")[0]);
						    }catch(Exception e){
								Log.v("ValueError", e.toString());
								
							}
							if(co!=0 && no2!=0){
								otherChemicalData = String.valueOf((co+no2)/2);
							}else if(co==0 || no2==0){
								if(co!=0)otherChemicalData = String.valueOf(co);
								if(no2!=0)otherChemicalData = String.valueOf(no2);
							}
							String city_name = ((String[])getActivity().getIntent().getSerializableExtra("pollutants_data"))[3];
							String[] finalData = new String[]{
									city_name, otherCityName,
									overallData+" %",otherOverallData+" %",
						    		particleData,otherParticleData,
									chemicalData,otherChemicalData,
									gaseousData,otherGaseousData
						    };
							CustomArrayAdapter<String> adapter = new CustomArrayAdapter<String>(ctx, R.layout.custom_text_view, finalData);
						    
						    comparisionGrid.setAdapter(adapter);
						    comparisionGrid.setClickable(false);
						    comparisionGrid.setSmoothScrollbarEnabled(true);
							compContainer.setVisibility(View.VISIBLE);
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


class CustomArrayAdapter<String> extends ArrayAdapter {

    private Context mContext;
    private int id;
    private String[] items ;
    private Typeface tf;

    public CustomArrayAdapter(Context context, int textViewResourceId , String[] list ) 
    {
        super(context, textViewResourceId, list);           
        mContext = context;
        tf = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto-regular.ttf");
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.custom_text_view);

        
        if(items[position] != null )
        {
        	if(position<=1){
        		text.setTextColor(Color.WHITE);
        		text.setBackgroundColor(Color.BLACK);
        	}else if(position>1 && position<=3){
        		text.setTextColor(Color.BLACK);
        		text.setBackgroundColor(mContext.getResources().getColor(R.color.silver));
        	}else if(position>3 && position<=5){
        		text.setTextColor(Color.WHITE);
        		text.setBackgroundColor(mContext.getResources().getColor(R.color.good));
        	}else if(position>5 && position<=7){
        		text.setTextColor(Color.WHITE);
        		text.setBackgroundColor(mContext.getResources().getColor(R.color.pink));
        	}else if(position>7 && position<=9){
        		text.setTextColor(Color.WHITE);
        		text.setBackgroundColor(mContext.getResources().getColor(R.color.peterblue));
        	}
        	text.setTypeface(tf);
            text.setText((CharSequence) items[position]);     
        }
        

        return mView;
    }

}
