package com.pirhoalpha.ubiplug_oaq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class UserDataManager {
	
	/**
	 * ------------Data Format-------------
	 * UserName::UserEmail::NotificationSetting::DidYouKnowSetting::isFBConnected::isRated
	 * 
	 * */
	public static final String FILENAME = "config.txt";
	private OutputStreamWriter outputStreamWriter;
	private InputStreamReader inputStreamReader;
	private InputStream inputStream;
	private BufferedReader bufferedReader;
	private String fileData;
	private Context ctx;
	
	UserDataManager(Context ctx, int mode){
		this.ctx = ctx;
		try {
	        inputStream = ctx.openFileInput(UserDataManager.FILENAME);

	        if ( inputStream != null ) {
	            inputStreamReader = new InputStreamReader(inputStream);
	            bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();
	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }
	            inputStream.close();
	            inputStreamReader.close();
	            fileData = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("UserDataManager", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("UserDataManager", "Can not read file: " + e.toString());
	    }
	}
	
	private void readFile(){
		try {
			inputStream = ctx.openFileInput(UserDataManager.FILENAME);
	        if ( inputStream != null ) {
	            inputStreamReader = new InputStreamReader(inputStream);
	            bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();
	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }
	            inputStream.close();
	            inputStreamReader.close();
	            fileData = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("UserDataManager", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("UserDataManager", "Can not read file: " + e.toString());
	    }
	}
	
	public void setName(String name){
		String data = name+"::"+getEmail()
				+"::"+String.valueOf(getNotificationSetting())
				+"::"+String.valueOf(getDidYouKnowSetting())
				+"::"+String.valueOf(isFBConnected())
				+"::"+String.valueOf(isRated());
		writeFile(data);
	}
	
	public void setEmail(String email){
		String data = getName()+"::"+email
				+"::"+String.valueOf(getNotificationSetting())
				+"::"+String.valueOf(getDidYouKnowSetting())
				+"::"+String.valueOf(isFBConnected())
				+"::"+String.valueOf(isRated());
		writeFile(data);
	}
	
	public void setNotificationSetting(int toWhat){
		String data = getName()+"::"+getEmail()
				+"::"+String.valueOf(toWhat)
				+"::"+String.valueOf(getDidYouKnowSetting())
				+"::"+String.valueOf(isFBConnected())
				+"::"+String.valueOf(isRated());
		writeFile(data);
	}
	
	public void setDidYouKnowSetting(int toWhat){
		String data = getName()+"::"+getEmail()
				+"::"+String.valueOf(getNotificationSetting())
				+"::"+String.valueOf(toWhat)
				+"::"+String.valueOf(isFBConnected())
				+"::"+String.valueOf(isRated());
		writeFile(data);
	}
	
	public void setFBConnected(){
		String data = getName()+"::"+getEmail()
				+"::"+String.valueOf(getNotificationSetting())
				+"::"+String.valueOf(getDidYouKnowSetting())
				+"::"+String.valueOf(1)
				+"::"+String.valueOf(isRated());
		writeFile(data);
	}
	
	public void setRated(){
		String data = getName()+"::"+getEmail()
				+"::"+String.valueOf(getNotificationSetting())
				+"::"+String.valueOf(getDidYouKnowSetting())
				+"::"+String.valueOf(isFBConnected())
				+"::"+String.valueOf(1);
		writeFile(data);
	}
	
	public int getNotificationSetting(){
		readFile();
		return Integer.valueOf(fileData.split("::")[2]);
	}
	
	public int getDidYouKnowSetting(){
		readFile();
		return Integer.valueOf(fileData.split("::")[3]);
		
	}
	
	public int isFBConnected(){
		readFile();
		return Integer.valueOf(fileData.split("::")[4]);
	}
	
	public int isRated(){
		readFile();
		return Integer.valueOf(fileData.split("::")[5]);
	}
	
	public String getName(){
		readFile();
		return fileData.split("::")[0];
	}
	
	public String getEmail(){
		readFile();
		return fileData.split("::")[1];
	}
	
	private void writeFile(String data){
		try {
			outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(UserDataManager.FILENAME, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("UserDataManager", "File write failed: " + e.toString());
			
		}
	}
}
