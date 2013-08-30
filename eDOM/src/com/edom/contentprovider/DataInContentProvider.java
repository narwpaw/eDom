package com.edom.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.edom.database.DataInTable;
import com.edom.database.eDomDatabaseHelper;

public class DataInContentProvider extends ContentProvider {

	  // database
	  private eDomDatabaseHelper database;

	  // Used for the UriMacher
	  private static final int DATAINS = 10;
	  private static final int DATAIN_ID = 20;

	  private static final String AUTHORITY = "com.edom.contentprovider";

	  private static final String BASE_PATH = "edom";
	  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
	      + "/" + BASE_PATH);

	  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	      + "/todos";
	  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/todo";

	  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	  static {
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH, DATAINS);
	    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DATAIN_ID);
	  }

	  @Override
	  public boolean onCreate() {
	    database = new eDomDatabaseHelper(getContext());
	    return false;
	  }

	  @Override
	  public Cursor query(Uri uri, String[] projection, String selection,
	      String[] selectionArgs, String sortOrder) {

	    // Uisng SQLiteQueryBuilder instead of query() method
	    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

	    // Check if the caller has requested a column which does not exists
	    checkColumns(projection);

	    // Set the table
	    queryBuilder.setTables(DataInTable.TABLE_DATA_IN);

	    int uriType = sURIMatcher.match(uri);
	    switch (uriType) {
	    case DATAINS:
	      break;
	    case DATAIN_ID:
	      // Adding the ID to the original query
	      queryBuilder.appendWhere(DataInTable.COLUMN_ID + "="
	          + uri.getLastPathSegment());
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }

	    SQLiteDatabase db = database.getWritableDatabase();
	    Cursor cursor = queryBuilder.query(db, projection, selection,
	        selectionArgs, null, null, sortOrder);
	    // Make sure that potential listeners are getting notified
	    cursor.setNotificationUri(getContext().getContentResolver(), uri);

	    return cursor;
	  }

	  @Override
	  public String getType(Uri uri) {
	    return null;
	  }

	  @Override
	  public Uri insert(Uri uri, ContentValues values) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    long id = 0;
	    switch (uriType) {
	    case DATAINS:
	      id = sqlDB.insert(DataInTable.TABLE_DATA_IN, null, values);
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return Uri.parse(BASE_PATH + "/" + id);
	  }

	  @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case DATAINS:
	      rowsDeleted = sqlDB.delete(DataInTable.TABLE_DATA_IN, selection,
	          selectionArgs);
	      break;
	    case DATAIN_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(DataInTable.TABLE_DATA_IN,
	        		DataInTable.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = sqlDB.delete(DataInTable.TABLE_DATA_IN,
	        		DataInTable.COLUMN_ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	  }

	  @Override
	  public int update(Uri uri, ContentValues values, String selection,
	      String[] selectionArgs) {

	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case DATAINS:
	      rowsUpdated = sqlDB.update(DataInTable.TABLE_DATA_IN, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case DATAIN_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(DataInTable.TABLE_DATA_IN, 
	            values,
	            DataInTable.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(DataInTable.TABLE_DATA_IN, 
	            values,
	            DataInTable.COLUMN_ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	  }

	  private void checkColumns(String[] projection) {
	    String[] available = { 
	    		DataInTable.COLUMN_OBJECT_NAME,
	    		DataInTable.COLUMN_OBJECT_TYPE,
	    		DataInTable.COLUMN_OBJECT_PLACE,
	    		DataInTable.COLUMN_OBJECT_FLOOR,
	    		DataInTable.COLUMN_SIGNAL_ST_A,
	    		DataInTable.COLUMN_SIGNAL_ST_B, 
	    		DataInTable.COLUMN_SIGNAL_ST_C,
	    		DataInTable.COLUMN_SIGNAL_ST_D,
	    		DataInTable.COLUMN_VALUE_ST_A,
	    		DataInTable.COLUMN_VALUE_ST_B,
	    		DataInTable.COLUMN_VALUE_ST_C,
	    		DataInTable.COLUMN_VALUE_ST_D,
	    		DataInTable.COLUMN_UNIT_OFF,
	    		DataInTable.COLUMN_UNIT_VALUE_A,
	    		DataInTable.COLUMN_UNIT_VALUE_B,
	    		DataInTable.COLUMN_UNIT_VALUE_C,
	    		DataInTable.COLUMN_UNIT_VALUE_D,
	    		DataInTable.COLUMN_ACTION_A,
	    		DataInTable.COLUMN_ACTION_B,
	    		DataInTable.COLUMN_TAGS_OBJECT,
	    		DataInTable.COLUMN_TAGS_PLACE,
	    		DataInTable.COLUMN_TAGS_DETAILS,
	    		DataInTable.COLUMN_TAGS_COMMAND_A,
	    		DataInTable.COLUMN_TAGS_COMMAND_B,
	    		DataInTable.COLUMN_ID };
	    if (projection != null) {
	      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	      // Check if all columns which are requested are available
	      if (!availableColumns.containsAll(requestedColumns)) {
	        throw new IllegalArgumentException("Unknown columns in projection");
	      }
	    }
	  }

	} 
