package com.pirhoalpha.ubiplug_oaq;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChemicalFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.chemical_fragment, container, false);
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	Typeface tf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),"fonts/roboto-regular.ttf");
        TextView pm25info = (TextView)getActivity().findViewById(R.id.lbl_chemical_info);
        pm25info.setTypeface(tf);
    	
    }
    
    
}