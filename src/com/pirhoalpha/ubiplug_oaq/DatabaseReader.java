package com.pirhoalpha.ubiplug_oaq;

import android.provider.BaseColumns;

public final class DatabaseReader {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseReader() {}

    /* Inner class that defines the table contents */
    public static abstract class AirData implements BaseColumns {
        public static final String TABLE_NAME = "airdata";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CITY_NAME = "city_name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_PM25 = "pm25";
        public static final String COLUMN_NAME_O3 = "o3";
        public static final String COLUMN_NAME_NO2 = "no2";
        public static final String COLUMN_NAME_SO2 = "so2";
        public static final String COLUMN_NAME_CO = "co";
        public static final String COLUMN_NAME_DEW = "dew";
        public static final String COLUMN_NAME_WIND = "wind";
        public static final String COLUMN_NAME_TEMPERATURE = "temperature";
        public static final String COLUMN_NAME_PRESSURE = "pressure";
        public static final String COLUMN_NAME_HUMIDITY = "humidity";
        
        public static final String BASE_URL = "http://www.ubiplug.com:8080/ubiair/getdata/";
        public static final String ERROR_REPORT_URL = "http://www.ubiplug.com:8080/ubiair/automaticerrorreport/";
        
    }
    

	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	public static final String SQL_CREATE =
	    "CREATE TABLE " + AirData.TABLE_NAME + " (" +
	    AirData.COLUMN_NAME_ID + " INT(1) PRIMARY KEY," +
	    AirData.COLUMN_NAME_CITY_NAME + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_PM25 + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_O3 + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_NO2 + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_SO2 + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_CO + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_DEW + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_WIND + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_TEMPERATURE + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_PRESSURE + TEXT_TYPE + COMMA_SEP +
	    AirData.COLUMN_NAME_HUMIDITY + TEXT_TYPE +
	     // Any other options for the CREATE command
	    " )";
	
	public static final String SQL_DELETE =
		    "DROP TABLE IF EXISTS " + AirData.TABLE_NAME;

}