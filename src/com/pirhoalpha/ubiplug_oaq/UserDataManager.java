/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

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
	 * UserName::UserEmail::NotificationSetting
	 * ::DidYouKnowSetting::isFBConnected::isRated
	 * 
	 * */
	public static final String FILENAME = "config.txt";
	private BufferedReader bufferedReader;
	private final Context ctx;
	private String fileData;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private OutputStreamWriter outputStreamWriter;

	UserDataManager(Context ctx, int mode) {
		this.ctx = ctx;
		try {
			this.inputStream = ctx.openFileInput(UserDataManager.FILENAME);

			if (this.inputStream != null) {
				this.inputStreamReader = new InputStreamReader(this.inputStream);
				this.bufferedReader = new BufferedReader(this.inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = this.bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}
				this.inputStream.close();
				this.inputStreamReader.close();
				this.fileData = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("UserDataManager", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("UserDataManager", "Can not read file: " + e.toString());
		}
	}

	public int getDidYouKnowSetting() {
		readFile();
		return Integer.valueOf(this.fileData.split("::")[3]);

	}

	public String getEmail() {
		readFile();
		return this.fileData.split("::")[1];
	}

	public String getName() {
		readFile();
		return this.fileData.split("::")[0];
	}

	public int getNotificationSetting() {
		readFile();
		return Integer.valueOf(this.fileData.split("::")[2]);
	}

	public int isFBConnected() {
		readFile();
		return Integer.valueOf(this.fileData.split("::")[4]);
	}

	public int isRated() {
		readFile();
		return Integer.valueOf(this.fileData.split("::")[5]);
	}

	private void readFile() {
		try {
			this.inputStream = this.ctx.openFileInput(UserDataManager.FILENAME);
			if (this.inputStream != null) {
				this.inputStreamReader = new InputStreamReader(this.inputStream);
				this.bufferedReader = new BufferedReader(this.inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ((receiveString = this.bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}
				this.inputStream.close();
				this.inputStreamReader.close();
				this.fileData = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("UserDataManager", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("UserDataManager", "Can not read file: " + e.toString());
		}
	}

	public void setDidYouKnowSetting(int toWhat) {
		String data = getName() + "::" + getEmail() + "::"
				+ String.valueOf(getNotificationSetting()) + "::"
				+ String.valueOf(toWhat) + "::"
				+ String.valueOf(isFBConnected()) + "::"
				+ String.valueOf(isRated());
		writeFile(data);
	}

	public void setEmail(String email) {
		String data = getName() + "::" + email + "::"
				+ String.valueOf(getNotificationSetting()) + "::"
				+ String.valueOf(getDidYouKnowSetting()) + "::"
				+ String.valueOf(isFBConnected()) + "::"
				+ String.valueOf(isRated());
		writeFile(data);
	}

	public void setFBConnected() {
		String data = getName() + "::" + getEmail() + "::"
				+ String.valueOf(getNotificationSetting()) + "::"
				+ String.valueOf(getDidYouKnowSetting()) + "::"
				+ String.valueOf(1) + "::" + String.valueOf(isRated());
		writeFile(data);
	}

	public void setName(String name) {
		String data = name + "::" + getEmail() + "::"
				+ String.valueOf(getNotificationSetting()) + "::"
				+ String.valueOf(getDidYouKnowSetting()) + "::"
				+ String.valueOf(isFBConnected()) + "::"
				+ String.valueOf(isRated());
		writeFile(data);
	}

	public void setNotificationSetting(int toWhat) {
		String data = getName() + "::" + getEmail() + "::"
				+ String.valueOf(toWhat) + "::"
				+ String.valueOf(getDidYouKnowSetting()) + "::"
				+ String.valueOf(isFBConnected()) + "::"
				+ String.valueOf(isRated());
		writeFile(data);
	}

	public void setRated() {
		String data = getName() + "::" + getEmail() + "::"
				+ String.valueOf(getNotificationSetting()) + "::"
				+ String.valueOf(getDidYouKnowSetting()) + "::"
				+ String.valueOf(isFBConnected()) + "::" + String.valueOf(1);
		writeFile(data);
	}

	private void writeFile(String data) {
		try {
			this.outputStreamWriter = new OutputStreamWriter(
					this.ctx.openFileOutput(UserDataManager.FILENAME,
							Context.MODE_PRIVATE));
			this.outputStreamWriter.write(data);
			this.outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("UserDataManager", "File write failed: " + e.toString());

		}
	}
}
