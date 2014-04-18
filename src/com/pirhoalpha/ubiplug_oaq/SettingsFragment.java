package com.pirhoalpha.ubiplug_oaq;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/*
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
*/
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsFragment extends Fragment {
    
	//private UserDataManager userDataManagerReader;
	private Button cmdConnectWithFB;
	private EditText txtName;
	private EditText txtEmail;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.settings_fragment, container, false);
        //LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);
    	//authButton.setFragment(this);
    	//authButton.setPublishPermissions(Arrays.asList("publish_stream"));
    	//authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
    	Typeface tf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(),"fonts/roboto-regular.ttf");
    	txtName = (EditText)getActivity().findViewById(R.id.txtName);
    	txtEmail = (EditText)getActivity().findViewById(R.id.txtEmail);
    	
    	//userDataManagerReader = new UserDataManager(getActivity(),0); //Opened UserDataManager in Read Mode
    	//txtName.setText(userDataManagerReader.getName());
    	//TextView pm25info = (TextView)getActivity().findViewById(R.id.lbl_gaseous_info);
        //pm25info.setTypeface(tf);
    	/*
    	Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {

    	    // callback when session changes state
			@SuppressWarnings("deprecation")
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				Log.v("Facebook", session.toString()+" "+state.toString());
				
				if (session.isOpened()) {
			          // make request to the /me API  
					
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

			            // callback after Graph API response with user object
			            @Override
			            public void onCompleted(GraphUser user, Response response) {
			              if (user != null) {
			                //TextView welcome = (TextView) findViewById(R.id.welcome);
			                //welcome.setText("Hello " + user.getName() + "!");
			            	  txtName.setText(user.getName());
			            	  txtName.setEnabled(false);
			            	  //UserDataManager userDataManagerWriter = new UserDataManager(getActivity(),1); //Opened UserDataManager in Write Mode
			            	  //userDataManagerWriter.setName(user.getName());
			            	  //userDataManagerWriter.setFBConnected();
			            	  
			              }
			            }
			          });
			        }
				
			}
    	  });
    	*/
    	
    }
    
}