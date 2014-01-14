package com.edom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.edom.contentprovider.DataInContentProvider;
import com.edom.database.DataInTable;

public class myService extends Service  {
	@Override
	 public IBinder onBind(Intent arg0) {
	  // TODO Auto-generated method stub
	  return null;
	 }
	 
	SQLiteDatabase baza = null;
	CharSequence uri;// = "http://www.ceuron.pl/Test/di3131385.csv"; 
	//CharSequence uri="http://www.ceuron.pl/Test/di3131385.csv"; 
	private final String DB_NAME = "ceuron.db";	
	ArrayList<String> lastValue = new ArrayList<String>();
	boolean FirstLope=true;
	private Timer timer;
	private TimerTask timerTask;
	private SharedPreferences preferences;
	
	 @Override
	 public void onCreate() {
	  super.onCreate();
	  
	  
	  Log.d("eDom","Start service Create");
	  
	  
	  if (!FirstLope) Toast.makeText(this, "Us³uga Ceuron uruchomiona!", Toast.LENGTH_LONG).show();  
	  
	  baza = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
	  okresowePowiadomienie();
	  
	  
	  
	  
	  
		preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
		String website=preferences.getString("website", "");
		String controller_id=preferences.getString("controller_id", "");
		uri=website+"/di"+controller_id+".csv";

	  
	  
	  
	  //tw.setText("ssss");
	  //Toast.makeText(getApplicationContext(),"Service wystartowal", Toast.LENGTH_LONG).show();
	 }
	  
	 private void okresowePowiadomienie(){
	   
	   timer = new Timer();
	   timerTask = new TimerTask(){
	   


	   public void run() {
	     


	    mHandler.obtainMessage(1).sendToTarget();
	   
	    
	   }}; 
	    
	  timer.scheduleAtFixedRate(timerTask , 200, 1000);  
	 }
	 
	 

	 
	  
	 protected TextView findViewById(int textview1) {
		// TODO Auto-generated method stub
		return null;
	}
	 
	 
	 
	 

	 
	 


	public Handler mHandler = new Handler() {
		 
		     
		 
  		    public void handleMessage(Message msg) {
		    	   

  		    	Log.d("my service", "Service start");
				
		    	

  				preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
  				String website=preferences.getString("website", "");
  				String controller_id=preferences.getString("controller_id", "");
  				uri=website+"/di"+controller_id+".csv";
  		    	
					
					String st=urlGet(String.valueOf(uri));

					
		    		if ((st==null) || (st.length()==0)) return;
		    		
		    		Log.d("my service", "Service start row split");	
		    		String STAB[]=st.split("\n");
		    		Log.d("my service", "Service end row split");	
		    		//Log.d("my service", "Service uRL rows:"+STAB.length);
	
		    		short i=1;
		    		boolean valueChanged=false;	
			
					
					    
					String Where = null;

					ContentValues values = new ContentValues();
					
					Log.d("eDom","Pobrano: "+STAB[15]);
					
					
					if (STAB.length>1)
					do{
						
				
						
						
					    valueChanged=false;	
				
					    
					    
					    
					    //Log.d("my service", "Service start col split:"+STAB[i]);

					    
						String STAB1[]=STAB[i].split(";");	
						//Log.d("my service", "Service end col split");
						
						if (FirstLope)
	  	  	    	       {
	  	  	    	    	   valueChanged=true;
	  	  	    	    	   lastValue.add(STAB1[1]);
	  	  	    	    	   Log.d("my service", "First service lope");
	  	  	    	        
	  	  	    	       }else if ((!lastValue.get(i-1).equalsIgnoreCase(STAB1[1])) || (STAB1[0].equalsIgnoreCase("")))
	  	  	    	       {
	  	  	    	    			valueChanged=true;
	  	  	    	    			lastValue.set(i-1, STAB1[1]);
	  	  	    	       }						
						
							if (valueChanged)
							{
								
								Log.d("my service", "DB write");
								values.clear();
								values.put(DataInTable.COLUMN_VALUE_ST_A, STAB1[1]);
								Where=DataInTable.COLUMN_SIGNAL_ST_A+"='"+STAB1[0]+"'";
								    
								getContentResolver().update(DataInContentProvider.CONTENT_URI, values, Where, null);
							
								values.clear();
								values.put(DataInTable.COLUMN_VALUE_ST_B, STAB1[1]);
								Where=DataInTable.COLUMN_SIGNAL_ST_B+"='"+STAB1[0]+"'";
								    
								getContentResolver().update(DataInContentProvider.CONTENT_URI, values, Where, null);
							
								values.clear();
								values.put(DataInTable.COLUMN_VALUE_ST_C, STAB1[1]);
								Where=DataInTable.COLUMN_SIGNAL_ST_C+"='"+STAB1[0]+"'";
								    
								getContentResolver().update(DataInContentProvider.CONTENT_URI, values, Where, null);
							
								values.clear();
								values.put(DataInTable.COLUMN_VALUE_ST_D, STAB1[1]);
								Where=DataInTable.COLUMN_SIGNAL_ST_D+"='"+STAB1[0]+"'";
								    
								getContentResolver().update(DataInContentProvider.CONTENT_URI, values, Where, null);
						
							
							}
						 
						
						
						i++;
					}while(i<STAB.length);
					FirstLope=false;
					

					Log.d("my service", "Service stop");
		    };
		};	 
	 
	 
	 
	 
		public String urlGet(String urlString){
		      
			
			Log.d("my service", "urlGet start");
		     URLConnection urlConnection = null;
		        URL url = null;
		        String string = null;
		         
		        try {
		   url = new URL(urlString);
		   urlConnection = url.openConnection();
		    
		   InputStream inputStream = urlConnection.getInputStream();
		   InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		   BufferedReader reader = new BufferedReader(inputStreamReader, 8192);
		    
		   StringBuffer stringBuffer = new StringBuffer();
		    
		   while((string = reader.readLine()) != null){
		    stringBuffer.append(string + "\n");
		   }
		   inputStream.close(); 
		    
		   string = stringBuffer.toString();
		    
		 
		   
		   reader=null;
		   stringBuffer=null;
		   url=null;
		   
		   
		  } catch (MalformedURLException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }  
		  Log.d("my service", "urlGet end");
			

		  return string;
}
	
		
		

	       
	 
	 

	@Override
	 public void onDestroy() {
	  super.onDestroy();
	  //baza.close();	
	  timer.cancel();
	  timerTask.cancel();

	  Toast.makeText(this, "Us³uga Ceuron zosta³a zamkniêta!", Toast.LENGTH_SHORT).show();

	 }
}
