package com.edom.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class eDomDatabaseHelper extends SQLiteOpenHelper {

	//private static final String DATABASE_NAME = "ceuron1.db";
	 private static final String DATABASE_NAME = null;
	private static final int DATABASE_VERSION = 1;

	public eDomDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		DataInTable.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		DataInTable.onUpgrade(database, oldVersion, newVersion);
	}

}
