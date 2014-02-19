package com.pirhoalpha.ubiplug_oaq;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GaseousFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.gaseous_fragment, container, false);
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	Typeface tf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),"fonts/roboto-regular.ttf");
        TextView pm25info = (TextView)getActivity().findViewById(R.id.lbl_gaseous_info);
        pm25info.setTypeface(tf);
    	
    }
    
    
}