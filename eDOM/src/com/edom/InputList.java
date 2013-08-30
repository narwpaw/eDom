package com.edom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.edom.contentprovider.DataInContentProvider;
import com.edom.database.DataInTable;


public class InputList extends Activity {

	SQLiteDatabase baza = null;
	boolean FirstLope= true;
	//Cursor cursor;
	
	
	private ArrayAdapter<ContentValues> adapter ; 
	//private ArrayAdapter<Map<String, String>> adapter ;  
	private ListView list ;
	private String filterObjectType;
	private String filterObject;
	private String filterPlace;
	private String filterDetails;
    private Timer timer;
    private TimerTask timerTask;
    public static Activity handleToClose;
    public static boolean active = false;
    //private String Where="";
	
    String whereSQL="";
    String whereSQL_Object="";
    String whereSQL_Place="";
	int licz=0;
	Cursor cursor;
	
	OutputStreamWriter request = null;
	HttpURLConnection connection;
	
	//ArrayList<Map<String, String>> carL = new ArrayList<Map<String, String>>();  
	
	ArrayList<ContentValues> carL = new ArrayList<ContentValues>();  
	
	ArrayList<String> lastValueA = new ArrayList<String>();	
	ArrayList<String> lastValueB = new ArrayList<String>();	
	ArrayList<String> lastValueC = new ArrayList<String>();	
	ArrayList<String> lastValueD = new ArrayList<String>();	
	
	
  	String[] projection = { 
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
  			};
  	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_list);
        
        list =  (ListView) findViewById(R.id.listView1);

         
        
        active=true;
        handleToClose = this;
        
        StringBuffer Sss = new StringBuffer();

        adapter = new MyArrayAdapter1(this, carL); //android.R.layout.simple_list_item_1, carL);



        
 
        
        
        
        
        Intent intent = getIntent();
        filterObjectType=null;
        filterPlace=null;
        filterObjectType = intent.getStringExtra("filterObjectType");
        filterObject = intent.getStringExtra("filterObject");
        filterPlace = intent.getStringExtra("filterPlace");
        filterDetails = intent.getStringExtra("filterDetails");
        intent.removeCategory(filterObjectType);
        intent.removeCategory(filterPlace);
        
		 timer = new Timer();
		 timerTask = new TimerTask(){
						
			public void run(){
				mHandler.obtainMessage(1).sendToTarget();
			}
			
		};
		
		
		
		

	    	String WhereOrSepar="";
	    	String WhereAndSepar="";
	    	List<String> PlacesForSpiner=new ArrayList<String>();


	    	if ((filterObjectType!=null) && (!filterObjectType.equals("")))
	    	{
	    		Log.d("list filetr", "Filter type exist ");
	    		whereSQL_Object=whereSQL_Object+"(";
		    		String fliterTab[]=filterObjectType.split(",");
		    		
		    		for (String FOT: fliterTab)
		    		{
		    			whereSQL_Object=whereSQL_Object+WhereOrSepar+DataInTable.COLUMN_OBJECT_TYPE+"='"+FOT+"'";
		    			WhereOrSepar=" or ";
		
		    		}
		    		whereSQL_Object=whereSQL_Object+")";
		    	WhereAndSepar=" and ";
	    	}

	    	
	    	Log.d("list filetr", "Filter: "+whereSQL_Object);


	    	
	    	if ((filterObject!=null) && (filterObject.length()>0))
	    	{
	    		Log.d("list filetr", "Filter object exist ");
	    		WhereOrSepar="";
	    		whereSQL_Object=whereSQL_Object+WhereAndSepar+"(";
		    		String fliterTab2[]=filterObject.split(",");
		    		
		    		for (String FOT: fliterTab2)
		    		{
		    			whereSQL_Object=whereSQL_Object+WhereOrSepar+DataInTable.COLUMN_TAGS_OBJECT+" like '%"+FOT+"%'";
		    			WhereOrSepar=" or ";
		    			MainActivity.currentViewDiscriptionPlace=MainActivity.currentViewDiscriptionPlace+FOT+" ";

		    		}
		    		whereSQL_Object=whereSQL_Object+")";
		    	WhereAndSepar=" and ";
	    	}
	    	
	    	
	    	
	    	
	    	

	    	
	    	
	    	if ((filterDetails!=null) && (filterDetails.length()>0))
	    	{
	    		Log.d("list filetr", "Filter Details exist ");
	    		WhereOrSepar="";
	    		whereSQL_Object=whereSQL_Object+WhereAndSepar+"(";
		    		String fliterTab2[]=filterDetails.split(",");
		    		
		    		for (String FOT: fliterTab2)
		    		{
		    			whereSQL_Object=whereSQL_Object+WhereOrSepar+DataInTable.COLUMN_TAGS_DETAILS+" like '%"+FOT+"%'";
		    			WhereOrSepar=" or ";
		    			MainActivity.currentViewDiscriptionPlace=MainActivity.currentViewDiscriptionPlace+FOT+" ";

		    		}
		    		whereSQL_Object=whereSQL_Object+")";
		    	WhereAndSepar=" and ";
	    	}

	    	Log.d("list filetr", "Filter: "+whereSQL_Object);
		
		
	    	
	    	if ((filterPlace!=null) && (filterPlace.length()>0))
	    	{
	    		Log.d("list filetr", "Filter place exist ");
	    		WhereOrSepar="";
	    		whereSQL_Place=whereSQL_Place+"(";
		    		String fliterTab2[]=filterPlace.split(",");
		    		
		    		for (String FOT: fliterTab2)
		    		{
		    			whereSQL_Place=whereSQL_Place+WhereOrSepar+DataInTable.COLUMN_TAGS_PLACE+" like '%"+FOT+"%'";
		    			WhereOrSepar=" or ";
		    			MainActivity.currentViewDiscriptionPlace=MainActivity.currentViewDiscriptionPlace+FOT+" ";
		    			PlacesForSpiner.add(FOT);
		    		}
		    		whereSQL_Place=whereSQL_Place+")";
		    	//WhereAndSepar=" and ";
	    	}
	    	
	    	if (whereSQL_Place=="")
      		{
      			whereSQL=whereSQL_Object;
      		}else{
      			whereSQL=whereSQL_Object+" and "+whereSQL_Place;
      		}
	    	
	    	
	    	
	    	
			//----SPINNER----------------------------------------------------------------------------------------------------
			
	        Spinner spinner= (Spinner)findViewById(R.id.spinner1);

	        List<String> lables=new ArrayList<String>();

	        Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, new String[] {DataInTable.COLUMN_OBJECT_NAME, DataInTable.COLUMN_OBJECT_TYPE, DataInTable.COLUMN_OBJECT_PLACE,DataInTable.COLUMN_OBJECT_FLOOR}, " 1=1 ) GROUP BY ("+DataInTable.COLUMN_OBJECT_PLACE,null,null);		
	        
	
		    	   
	        //List<String> spinnerListTMP=new ArrayList<String>();
	        List<String> spinnerListFiltrTMP=new ArrayList<String>();
	        int SpinerItem=0;
	        int idx=0;
	        
	        Log.d("list filetr","PlacesForSpiner: "+PlacesForSpiner );
	        
	        if (cursor.getCount()>0)
	        {
	        	lables.add(getString(R.string.Caly_dom).toUpperCase());
	        	idx++;
			    	if(cursor.moveToFirst()){ //Metoda zwraca FALSE jesli cursor jest pusty
			    		
			    		Log.d("list filetr","C" );

	  	  	    	       do{
	  	  	    	    	  String place=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_PLACE));
	  	  	    	    	
	  	  	    	    	  for (String Plc: PlacesForSpiner)
	  	  	    	    	  {
	  	  	    	    		if (place.contains(Plc))
		  	  	    	    	  {
	  	  	    	    		
		  	  	    	    		spinnerListFiltrTMP.add(place.toUpperCase());
		  	  	    	    		SpinerItem=idx;
		  	  	    	    		break;
		  	  	    	    	  } 
	  	  	    	    	  }
	  	  	    	    	  
	  	  	    	    	  
	  	  	    	    	idx++; 
	  	  	 
	  	  	    	    	lables.add(place.toUpperCase());
	  	  	    	       }while(cursor.moveToNext());
			    	}
	        }
	        
	  
	        if (spinnerListFiltrTMP.size()>1)
	        {
	        	if (spinnerListFiltrTMP.size()<lables.size()-1)
	        	{
	        		lables.add(0, spinnerListFiltrTMP.toString().replace("[", "").replace("]", ""));
	        	}
	        
	        	SpinerItem=0;
	        	
	        	
	        }
	        
	        
	        
	        // Creating adapter for spinner
	        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
	        // Drop down layout style - list view with radio button
	        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        // attaching data adapter to spinner
	        spinner.setAdapter(dataAdapter);
	        
	        
	        spinner.setSelection(SpinerItem);
	        
	        
	        
	        
	        

	        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
		        @Override
		        public void onItemSelected(AdapterView dataAdapter, View v, int i, long lng) {
		        	
		        	ContentValues myEntry;
		      		//myEntry=(ContentValues)list.getItemAtPosition(i);
		      		String filterName=dataAdapter.getItemAtPosition(i).toString().toLowerCase();
		      		

		      		
		      		whereSQL_Place="";
			    	if ((filterName!=null) && (filterName.length()>0) && (!filterName.contains(getString(R.string.Caly_dom).toLowerCase())))
			    	{
			    		String WhereOrSepar="";
			    		whereSQL_Place="(";
			    		
			    		Log.d("list filetr","filterName: "+filterName);
			    		filterName.replace(" ", "");
				    		String filterNameTab[]=filterName.split(",");
				    		
				    		

					    		Log.d("list filetr","filterNameTab: "+filterNameTab);
					    		for (String FOT: filterNameTab)
					    		{
					    			whereSQL_Place=whereSQL_Place+WhereOrSepar+DataInTable.COLUMN_TAGS_PLACE+" like '%"+FOT+"%'";
					    			WhereOrSepar=" or ";
	
					    		}
			
				    		Log.d("list filetr","filterNameTab end");
				    		whereSQL_Place=whereSQL_Place+")";
				    	//WhereAndSepar=" and ";
			    	}
		      		
		      		
		      		if (whereSQL_Place=="")
		      		{
		      			whereSQL=whereSQL_Object;
		      		}
		      		else if ((whereSQL_Object=="") && (whereSQL_Place!=""))
		      		{
		      			whereSQL=whereSQL_Place;
	
		      		}else{
		      			whereSQL=whereSQL_Object+" and "+whereSQL_Place;
		      		}
		      		Log.d("list filetr","whereSQL: "+whereSQL);
		        }
		         
		        @Override
		        public void onNothingSelected(AdapterView arg0) {
		        //do something else
		        }
	        });
			
			
			
			//----------------------------------------------------------------------------------------------------------------
		 	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
		
		timer.schedule(timerTask, 100, 1000);

        
    }



 

    
    
    
 //Periodic get data from SQLite end show on view list.  
    public Handler mHandler = new Handler() {
   	 
  	  
    	//private final String DB_NAME = "ceuron.db";	   
    	boolean valueChanged =false;
    	
  	  
  		    public void handleMessage(Message msg) {

  		    
  		    	valueChanged =false;
  		    	
  		    	
  		    	
  		    	

  		    	

  		   
  		    	
  		 
  		    	
  		    	
  		    	
  		    	
  		    	
  		    	
  		    	
  		    	Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection, whereSQL, null,null);
  		    	//Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection, Where, null,DataInTable.COLUMN_OBJECT_PLACE+" DESC");
  		    	int idx;
  		

  		    	
  		    	String SIGNAL_ST_A=null;
  		    	String SIGNAL_ST_B=null;
  		    	String SIGNAL_ST_C=null;
  		    	String SIGNAL_ST_D=null;
  		    	
  		    	if (cursor.getCount()>0)
  		    	if(cursor.moveToFirst()){ //Metoda zwraca FALSE jesli cursor jest pusty
  		    		
  		    		Log.d("list filetr", "Obiects list exist");
  		    		
	  	  	    	carL.clear();
	  	  	        		
	  	  	    			//Map<String, String> DInfo = new HashMap<String, String>();
	  	  	    		   
	  	  	    	       do{
	  	  	    	    	ContentValues DInfo = new ContentValues(); 
	  	  	    	    
	  	  	    	        //DInfo.put(cursor.getString(cursor.getColumnIndex("signal")),ValueTmp);     
	  	  	    	       

	  	  	    	    	DInfo.put("signal", 	cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_SIGNAL_ST_A)));
	  	  	    	    	
	  	  	    	    	String syg=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_SIGNAL_ST_A));
	  	  	    	    	//je¿eli sygna³ dwustanowy
	  	  	    	    	if ((!syg.startsWith("r")) && (!syg.startsWith("c")) && (!syg.startsWith("d")))
	  	  	    	    	{
	  	  	    	    		 SIGNAL_ST_A=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_VALUE_ST_A));
	  	  	    	    		 SIGNAL_ST_B=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_VALUE_ST_B));
	  	  	    	    		 SIGNAL_ST_C=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_VALUE_ST_C));
	  	  	    	    		 SIGNAL_ST_D=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_VALUE_ST_D));
	  	  	    	    		
	  	  	    	    	DInfo.put("state",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_UNIT_OFF)));
	  	  	    	    	
	  	  	    	    
	  	  	    	    /*if (syg.startsWith("i03"))
	  	  	    	    {
	  	  	    	    licz++;
	  	  	    	    
	  	  	    	    Toast.makeText(getApplicationContext() ,
	  	  	    	    		"nr: "+licz+
	  	  	    	    		"\ndolna krancowka: "+SIGNAL_ST_A+
	  	  	    	    		"\ngorna krancowka: "+SIGNAL_ST_B+
	  	  	    	    		"\nruch w dol: "+SIGNAL_ST_C+
	  	  	    	    		"\nruch w gore: "+SIGNAL_ST_D
	  	  	    	    		, 0).show(); 
	  	  	    	    }*/
	  	  	    	    	
	  	  	    	    		if (SIGNAL_ST_A.startsWith("1"))
	  	  	    	    		{
	  	  	    	    		//if (syg.startsWith("i03"))Toast.makeText(getApplicationContext() ,"dolna krancowka", 0).show(); 
	  	  	    	    			DInfo.put("state",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_UNIT_VALUE_A)));
	  	  	    	    		} if (SIGNAL_ST_B.startsWith("1"))
	  	  	    	    		{
	  	  	    	    		//if (syg.startsWith("i03"))Toast.makeText(getApplicationContext() ,"gorna krancowka", 0).show(); 
	  	  	    	    			DInfo.put("state",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_UNIT_VALUE_B)));
	  	  	    	    		} if (SIGNAL_ST_C.startsWith("1"))
	  	  	    	    		{
	  	  	    	    		//if (syg.startsWith("i03"))Toast.makeText(getApplicationContext() ,"ruch w dol", 0).show(); 
	  	  	    	    			DInfo.put("state",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_UNIT_VALUE_C)));
	  	  	    	    		} if (SIGNAL_ST_D.startsWith("1"))
	  	  	    	    		{
	  	  	    	    		//if (syg.startsWith("i03")) Toast.makeText(getApplicationContext() ,"ruch w gore", 0).show(); 
	  	  	    	    			DInfo.put("state",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_UNIT_VALUE_D)));
	  	  	    	    		}else 
	  	  	    	    		{
	  	  	    	    			
	  	  	    	    		}
	  	  	    	    		
	  	  	    	    		
	  	  	    	    		
	  	  	    	    		
	  	  	    	    		
	  	  	    	    	
	  	  	    	    	}else{
	  	  	    	    		DInfo.put("state",	"");
	  	  	    	    	}
	  	  	    	    	
	  	  	    	    	DInfo.put("value",		cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_VALUE_ST_A)));
	  	  	    	    	DInfo.put("discription", cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_NAME)));	   
	  	  	    	        DInfo.put("type", cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_TYPE)));	
	  	  	    	        DInfo.put("action_a", cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_A)));	
	  	  	    	        DInfo.put("action_b", cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_B)));	
	  	  	    	        carL.add(DInfo);   
	  	  	    	    	   
	  	  	    	       idx = cursor.getPosition();
	  	  	    	       if (FirstLope)
	  	  	    	       {
	  	  	    	    	   valueChanged=true;
	  	  	    	    	   lastValueA.add(SIGNAL_ST_A);
	  	  	    	    	   lastValueB.add(SIGNAL_ST_B);
	  	  	    	    	   lastValueC.add(SIGNAL_ST_C);
	  	  	    	    	   lastValueD.add(SIGNAL_ST_D);
	  	  	    	        
	  	  	    	       }else{
	  	  	    	    	   
	  	  	   
	  	  	    	    	if ((!lastValueA.equals(SIGNAL_ST_A)) || (!lastValueB.equals(SIGNAL_ST_B)) || (!lastValueC.equals(SIGNAL_ST_C)) ||(!lastValueD.equals(SIGNAL_ST_D)))
	  	  	    	    	{
	  	  	    	    			valueChanged=true;
	  	  	    	    			lastValueA.set(idx, SIGNAL_ST_A);
	  	  	    	    			lastValueB.set(idx, SIGNAL_ST_B);
	  	  	    	    			lastValueC.set(idx, SIGNAL_ST_C);
	  	  	    	    			lastValueD.set(idx, SIGNAL_ST_D);
	  	  	    	    	}
	  	  	    	       }
	  	  	  
	  	  	    	       }while(cursor.moveToNext()); //Metoda zwraca FALSE wówczas gdy cursor przejdzie ostatni wpis
	  	  	    	      }
  		    	
  		    	cursor.close();
  		    	
					  if (FirstLope==true)
		  	  	    	{
		  	  	    		FirstLope=false;
		  	  	    		list.setAdapter(adapter);

		  	  	    	}else{
		  	  	    		if (valueChanged)
		  	  	    		{
		  	  	    			adapter.notifyDataSetChanged();
		  	  	    		}
		  	  	    	}
				  
		  		       
  		    	
  		    	
  	    	
  	    };
  	};
  	
  		
  	@Override
	 public void onDestroy() {
	  super.onDestroy();
	    timer.cancel();
	    timerTask.cancel();
	    MainActivity.currentViewDiscriptionType="";
	    MainActivity.currentViewDiscriptionPlace="";
	    active=false;
	  
	 }
  	

  	
  	

  	
  	
  	
  	
  	
  	
  	public void myClickHandler(View view){
 
  		int position =  list.getPositionForView(view);
  		
  		ContentValues myEntry;
  		
  		myEntry=(ContentValues)list.getItemAtPosition(position);
  		
  		String signal=myEntry.getAsString("signal");
  		String state=myEntry.getAsString("value");
  		
/*  		Toast.makeText(getApplicationContext(),
			      "Click ListItem Number "+position+" "+signal, Toast.LENGTH_LONG)
			      .show();*/

  		if (state.startsWith("0"))
  		{
  			Swiatlo(signal, "1");
  		}else{
  			Swiatlo(signal, "0");
  		}
    }

  	public void myClickDownHandler(View view){
  		 
  		int position =  list.getPositionForView(view);
  		ContentValues myEntry;
  		myEntry=(ContentValues)list.getItemAtPosition(position);
  		String signal=myEntry.getAsString("action_a");
  		Swiatlo(signal, "2");

    }
  	
  	public void myClickUpHandler(View view){
 		 
  		int position =  list.getPositionForView(view);
  		ContentValues myEntry;
  		myEntry=(ContentValues)list.getItemAtPosition(position);
  		String signal=myEntry.getAsString("action_b");
  		Swiatlo(signal, "2");

    }
 	
 	
  	void   Swiatlo(String Signal, String Set) 
  	{
  	    URL url = null;   
  	    String response = null;         
  	    //String parameters = "username="+mUsername+"&password="+mPassword;   

  	    try
  	    {
  	        url = new URL("http://www.ceuron.pl/Test/putgate.php");
  	        connection = (HttpURLConnection) url.openConnection();
  	        connection.setDoOutput(true);
  	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
  	        connection.setRequestMethod("POST");    

  	        request = new OutputStreamWriter(connection.getOutputStream());

  	        
  	  
  	      	request.write("NodeID=3131385&Order=2&Content="+Signal+"="+Set);
  	  
  	        
  	        request.flush();
  	        request.close();            
  	        String line =""; 

  	        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
  	        BufferedReader reader = new BufferedReader(isr);
  	        StringBuilder sb = new StringBuilder();
  	        while ((line = reader.readLine()) != null)
  	        {
  	            sb.append(line + "\n");
  	        }
  	        // Response from server after login process will be stored in response variable.                
  	        response = sb.toString();
  	        // You can perform UI operations here
  	        //Toast.makeText(this,"Message from Server: \n"+ response, 0).show();             
  	       

  	        
  	        isr.close();
  	        reader.close();


  	    }
  	    catch(IOException e)
  	    {
  	        // Error
  	    }
  	
  	}




















}






