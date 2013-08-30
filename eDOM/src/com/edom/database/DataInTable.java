package com.edom.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataInTable {
	// Database table
	  public static final String TABLE_DATA_IN = "DataIn";
	  public static final String COLUMN_ID = "_id";
	  public static final String COLUMN_OBJECT_NAME 	= "object_name";
	  public static final String COLUMN_OBJECT_TYPE 	= "object_type";
	  public static final String COLUMN_OBJECT_PLACE 	= "object_place";
	  public static final String COLUMN_OBJECT_FLOOR 	= "object_floor";
	  public static final String COLUMN_SIGNAL_ST_A 	= "signal_st_a";
	  public static final String COLUMN_SIGNAL_ST_B 	= "signal_st_b";
	  public static final String COLUMN_SIGNAL_ST_C 	= "signal_st_c";
	  public static final String COLUMN_SIGNAL_ST_D 	= "signal_st_d";
	  public static final String COLUMN_VALUE_ST_A 		= "value_st_a";
	  public static final String COLUMN_VALUE_ST_B 		= "value_st_b";
	  public static final String COLUMN_VALUE_ST_C 		= "value_st_c";
	  public static final String COLUMN_VALUE_ST_D 		= "value_st_d";
	  public static final String COLUMN_UNIT_OFF 		= "unit_off";
	  public static final String COLUMN_UNIT_VALUE_A 	= "unit_val_a";
	  public static final String COLUMN_UNIT_VALUE_B 	= "unit_val_b";
	  public static final String COLUMN_UNIT_VALUE_C 	= "unit_val_c";
	  public static final String COLUMN_UNIT_VALUE_D 	= "unit_val_d";
	  public static final String COLUMN_ACTION_A 		= "action_a";
	  public static final String COLUMN_ACTION_B 		= "action_b";
	  public static final String COLUMN_TAGS_OBJECT 	= "tags_object";
	  public static final String COLUMN_TAGS_PLACE 		= "tags_place";
	  public static final String COLUMN_TAGS_DETAILS 	= "tags_position";
	  public static final String COLUMN_TAGS_COMMAND_A 	= "tags_com_a";
	  public static final String COLUMN_TAGS_COMMAND_B 	= "tags_com_b";
	  

	  // Database creation SQL statement
	  private static final String DATABASE_CREATE = "create table " 
	      + TABLE_DATA_IN
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_OBJECT_NAME + " text, " 
	      + COLUMN_OBJECT_TYPE + " text, " 
	      + COLUMN_OBJECT_PLACE + " text, " 
	      + COLUMN_OBJECT_FLOOR + " text, " 
	      + COLUMN_SIGNAL_ST_A + " text, " 
	      + COLUMN_SIGNAL_ST_B + " text, " 
	      + COLUMN_SIGNAL_ST_C + " text, " 
	      + COLUMN_SIGNAL_ST_D + " text, " 
	      + COLUMN_VALUE_ST_A + " text, " 
	      + COLUMN_VALUE_ST_B + " text, " 
	      + COLUMN_VALUE_ST_C + " text, " 
	      + COLUMN_VALUE_ST_D + " text, " 
	      + COLUMN_UNIT_OFF + " text, " 
	      + COLUMN_UNIT_VALUE_A + " text, " 
	      + COLUMN_UNIT_VALUE_B + " text, " 
	      + COLUMN_UNIT_VALUE_C + " text, " 
	      + COLUMN_UNIT_VALUE_D + " text, " 
	      + COLUMN_ACTION_A + " text, " 
	      + COLUMN_ACTION_B + " text, " 
	      + COLUMN_TAGS_OBJECT + " text, " 
	      + COLUMN_TAGS_PLACE + " text, " 
	      + COLUMN_TAGS_DETAILS + " text, " 
	      + COLUMN_TAGS_COMMAND_A + " text, " 
	      + COLUMN_TAGS_COMMAND_B + " text " 
	      + ");";

	  public static void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }

	  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
	      int newVersion) {
	    Log.w(DataInTable.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + ", which will destroy all old data");
	    database.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA_IN);
	    onCreate(database);
	  }
}
