/*
 * This source code is a property of PiRhoAlpha Research Pvt. Ltd.
 * Copyright 2014
 * 
 * Author Sudhanshu Mishra
 */

package com.pirhoalpha.ubiplug_oaq;

public class NavDrawerItem {

	private String count = "0";
	private int icon;
	// boolean to set visibility of the counter
	private boolean isCounterVisible = false;
	private String title;

	public NavDrawerItem() {
	}

	public NavDrawerItem(String title, int icon) {
		this.title = title;
		this.icon = icon;
	}

	public NavDrawerItem(String title, int icon, boolean isCounterVisible,
			String count) {
		this.title = title;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}

	/**
	 * This method returns index of the menu
	 * 
	 * @return position
	 */
	public String getCount() {
		return this.count;
	}

	/**
	 * This method returns if counter is visible or not
	 * 
	 * @return
	 */
	public boolean getCounterVisibility() {
		return this.isCounterVisible;
	}

	/**
	 * This method returns icon of the menu item
	 * 
	 * @return icon
	 */
	public int getIcon() {
		return this.icon;
	}

	/**
	 * This method returns title of the menu tem
	 * 
	 * @return title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * This method sets the value of the counter on the menu item.
	 * 
	 * @param count
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * This method sets the visiblity of the counter on the menu item.
	 * 
	 * @param isCounterVisible
	 */
	public void setCounterVisibility(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	/**
	 * This method sets icon of the action bar according to the selected menu
	 * item
	 * 
	 * @param icon
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}

	/**
	 * This method sets title of the action bar according to the menu item
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}