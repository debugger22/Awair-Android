package com.pirhoalpha.ubiplug_oaq;

import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class KnowMore extends Activity{
	
	private ListView lstComponents;
	
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_know_more);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(android.os.Build.VERSION.SDK_INT>=15)getActionBar().setDisplayHomeAsUpEnabled(true);
	    if(android.os.Build.VERSION.SDK_INT>=15)getActionBar().setHomeButtonEnabled(true);
	    if(android.os.Build.VERSION.SDK_INT>=15)getActionBar().setIcon(getResources().getDrawable(R.drawable.hand));
		lstComponents = (ListView)findViewById(R.id.lstAirComponents);
		String [] data = new String[]{"Ozone (O3)", "Carbon Monoxide (CO)","Nitrogen Dioxide (NO2)"};
		CustomArrayAdapterKnowMore<String> adapter = new CustomArrayAdapterKnowMore<String>(this, R.layout.custom_text_view, data);
		lstComponents.setAdapter(adapter);
		lstComponents.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
				if(position==0){
					Intent i = new Intent(KnowMore.this, GasDetail.class);
					i.putExtra("data", new String[]{(java.lang.String) ((TextView)view).getText(), getResources().getString(R.string.ozone_intro),getResources().getString(R.string.ozone_effects)});
					startActivity(i);
					overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
				}
				if(position==1){
					Intent i = new Intent(KnowMore.this, GasDetail.class);
					i.putExtra("data", new String[]{(java.lang.String) ((TextView)view).getText(), getResources().getString(R.string.co_intro),getResources().getString(R.string.co_effects)});
					startActivity(i);
					overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
				}
				if(position==2){
					Intent i = new Intent(KnowMore.this, GasDetail.class);
					i.putExtra("data", new String[]{(java.lang.String) ((TextView)view).getText(), getResources().getString(R.string.no2_intro),getResources().getString(R.string.no2_effects)});
					startActivity(i);
					overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
				}
			}
		});
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed in the action bar.
	            // Create a simple intent that starts the hierarchical parent activity and
	            // use NavUtils in the Support Package to ensure proper handling of Up.
	            finish();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	
}


class CustomArrayAdapterKnowMore<String> extends ArrayAdapter {

    private Context mContext;
    private int id;
    private String[] items ;
    private Typeface tf;
    private Typeface tfbold;

    public CustomArrayAdapterKnowMore(Context context, int textViewResourceId , String[] list ) 
    {
        super(context, textViewResourceId, list);           
        mContext = context;
        tf = Typeface.createFromAsset(mContext.getAssets(),"fonts/roboto-regular.ttf");
        id = textViewResourceId;
        items = list;
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

        		text.setTextColor(Color.BLACK);
        		text.setTypeface(tf);
        		text.setText((CharSequence) items[position]);
        		text.setHeight(80);
            
        

        return mView;
    }

}