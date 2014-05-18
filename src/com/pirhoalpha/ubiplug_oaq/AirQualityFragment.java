/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This Fragment class is for the text shown on KnowMore Activity.
 * 
 */
public class AirQualityFragment extends Fragment {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Typeface tf = Typeface.createFromAsset(getActivity()
				.getApplicationContext().getAssets(),
				"fonts/roboto-regular.ttf");
		TextView lbl_aq_text = (TextView) getActivity().findViewById(
				R.id.lbl_aq_text);
		// Changing typefaces of TextViews
		lbl_aq_text.setTypeface(tf);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.air_quality_fragment,
				container, false);
		return rootView;
	}

}