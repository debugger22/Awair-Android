package com.pirhoalpha.ubiplug_oaq;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.about_fragment, container, false);
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	//Setting typefaces of the labels
    	Typeface tf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),"fonts/roboto-regular.ttf");
        TextView appname = (TextView)getActivity().findViewById(R.id.lbl_app_name);
        TextView version = (TextView)getActivity().findViewById(R.id.lbl_version);
        TextView company = (TextView)getActivity().findViewById(R.id.lbl_company_name);
        appname.setTypeface(tf);
        version.setTypeface(tf);
        company.setTypeface(tf);
        
    	
    }
    
    
}