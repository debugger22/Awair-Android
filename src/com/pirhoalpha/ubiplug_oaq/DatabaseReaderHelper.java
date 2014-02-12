package com.pirhoalpha.ubiplug_oaq;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class DatabaseReaderHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ubiairdatabase.db";

    public DatabaseReaderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseReader.SQL_CREATE);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseReader.SQL_DELETE);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public int addData(Map<String, String> newData){
    	SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DatabaseReader.AirData.COLUMN_NAME_ID, 1);
		values.put(DatabaseReader.AirData.COLUMN_NAME_CITY_NAME, (String)newData.get("city_name"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_DATE, (String)newData.get("date"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_PM25,(String)newData.get("pm25"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_O3, (String)newData.get("o3"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_NO2, (String)newData.get("no2"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_SO2, (String)newData.get("so2"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_CO, (String)newData.get("co"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_DEW, (String)newData.get("dew"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_WIND, (String)newData.get("wind"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_TEMPERATURE, (String)newData.get("temperature"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_PRESSURE, (String) newData.get("pressure"));
		values.put(DatabaseReader.AirData.COLUMN_NAME_HUMIDITY, (String) newData.get("humidity"));
		int newRowId;
		newRowId = (int) db.insert(DatabaseReader.AirData.TABLE_NAME,null,values);
		db.close();
    	return newRowId;
    }
    
    public Map<String,String> getData(){
    	int id = 1;
    	try{
	    	SQLiteDatabase db = this.getReadableDatabase();
	    	Cursor cursor = db.query(DatabaseReader.AirData.TABLE_NAME, new String[] { 
	    			DatabaseReader.AirData.COLUMN_NAME_CITY_NAME,
	    			DatabaseReader.AirData.COLUMN_NAME_DATE,
	    			DatabaseReader.AirData.COLUMN_NAME_PM25,
	    			DatabaseReader.AirData.COLUMN_NAME_O3,
	    			DatabaseReader.AirData.COLUMN_NAME_NO2,
	    			DatabaseReader.AirData.COLUMN_NAME_SO2,
	    			DatabaseReader.AirData.COLUMN_NAME_CO,
	    			DatabaseReader.AirData.COLUMN_NAME_DEW,
	    			DatabaseReader.AirData.COLUMN_NAME_WIND,
	    			DatabaseReader.AirData.COLUMN_NAME_TEMPERATURE,
	    			DatabaseReader.AirData.COLUMN_NAME_PRESSURE,
	    			DatabaseReader.AirData.COLUMN_NAME_HUMIDITY
	    			}, DatabaseReader.AirData.COLUMN_NAME_ID + "=?",
	                new String[] { "1" }, null, null, null, null);
	        if (cursor != null)
	            cursor.moveToFirst();
	        Map<String, String> data = new HashMap<String, String>();
	        data.put(DatabaseReader.AirData.COLUMN_NAME_CITY_NAME, cursor.getString(0));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_DATE, cursor.getString(1));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_PM25, cursor.getString(2));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_O3, cursor.getString(3));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_NO2, cursor.getString(4));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_SO2, cursor.getString(5));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_CO, cursor.getString(6));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_DEW, cursor.getString(7));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_WIND, cursor.getString(8));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_TEMPERATURE, cursor.getString(9));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_PRESSURE, cursor.getString(10));
	        data.put(DatabaseReader.AirData.COLUMN_NAME_HUMIDITY, cursor.getString(11));
	        db.close();
	        return data;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public int flushData(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	try{
    		db.execSQL(DatabaseReader.SQL_DELETE);
    		db.execSQL(DatabaseReader.SQL_CREATE);
    		db.close();
    		return 1;
    	}catch(Exception e){
    		return 0;
    	}
    }
    
    
}