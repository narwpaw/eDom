//Test 4
package com.edom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edom.contentprovider.DataInContentProvider;
import com.edom.database.DataInTable;




public class MainActivity extends Activity {

	public TextView tw;

	public int licznik=0;
	public static final ArrayList<String> DataIn = new ArrayList<String>();  
	
	  private SoundPool soundPool;
	  private int soundID;
	  boolean loaded = false;
	
	public static String currentViewDiscriptionType="";
	public static String currentViewDiscriptionPlace="";
	
	  
	StringBuffer voiceAnlizeObjectList= new StringBuffer(); 
	StringBuffer voiceAnlizePlaceList= new StringBuffer();  
	StringBuffer voiceAnlizeDetalisList= new StringBuffer(); 
	StringBuffer voiceAnlizeOrdersList= new StringBuffer(); 
	StringBuffer voiceAnlizeAPIOpenOrdersList= new StringBuffer(); 
	StringBuffer voiceAnlizeAPICloseOrdersList= new StringBuffer(); 
	
	private Uri DataInUri;
	
    //RecognitionListener *******************
    private TextView mText;
    private SpeechRecognizer sr;
    private static final String TAG = "MainActivity";
	
	
	
	SQLiteDatabase baza = null;
	HttpURLConnection connection;
    OutputStreamWriter request = null;
    
    boolean TMP=false;
    private Timer timer;
    private TimerTask timerTask;
    Intent intent;
    ProgressBar VoiceBar;
    boolean voiceServerBusy=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		intent = new Intent(this, InputList.class);
		


		CharSequence uri = "http://www.ceuron.pl/Test/_db3131385.csv"; 
		String st=urlGet(String.valueOf(uri));		
		String STAB[]=st.split("\n");
		
		ContentValues entry = new ContentValues();
		
		
		VoiceBar = (ProgressBar)findViewById(R.id.progressBar1); 
		
		
		//ImageButton btTmp = (ImageButton)findViewById(R.id.button_all_home);
		//btTmp.setAlpha(100);
		

		
		Uri.encode("<?xml version='1.0' encoding='utf-8'?>");
		getContentResolver().delete(DataInContentProvider.CONTENT_URI, null, null);
		entry.clear();
		short i=1;
		int lnd=0;
			do{
				String STAB1_TMP[]=STAB[i].split(";");		
				
				String[] STAB1 = new String[20];
				
				lnd=STAB1_TMP.length;
				for (char k=0; k<20; k++)
				{
					if (k<lnd)
					{
						STAB1[k]=STAB1_TMP[k];
					}
					else{
						STAB1[k]=" ";
					}
		
					
				}
				
				
				entry.put(DataInTable.COLUMN_OBJECT_NAME, 	STAB1[0]);
				entry.put(DataInTable.COLUMN_OBJECT_TYPE, 	STAB1[1]);
				entry.put(DataInTable.COLUMN_OBJECT_PLACE, 	STAB1[2]);
				entry.put(DataInTable.COLUMN_OBJECT_FLOOR, 	STAB1[3]);
				entry.put(DataInTable.COLUMN_SIGNAL_ST_A, 	STAB1[4]);
				entry.put(DataInTable.COLUMN_SIGNAL_ST_B, 	STAB1[5]);
				entry.put(DataInTable.COLUMN_SIGNAL_ST_C, 	STAB1[6]);
				entry.put(DataInTable.COLUMN_SIGNAL_ST_D, 	STAB1[7]);
				entry.put(DataInTable.COLUMN_UNIT_OFF, 		STAB1[8]);
				entry.put(DataInTable.COLUMN_UNIT_VALUE_A, 	STAB1[9]);
				entry.put(DataInTable.COLUMN_UNIT_VALUE_B, 	STAB1[10]);
				entry.put(DataInTable.COLUMN_UNIT_VALUE_C, 	STAB1[11]);
				entry.put(DataInTable.COLUMN_UNIT_VALUE_D, 	STAB1[12]);
				entry.put(DataInTable.COLUMN_ACTION_A, 		STAB1[13]);
				entry.put(DataInTable.COLUMN_ACTION_B, 		STAB1[14]);
				entry.put(DataInTable.COLUMN_TAGS_OBJECT, 	STAB1[15]);
				entry.put(DataInTable.COLUMN_TAGS_PLACE, 	STAB1[16]);
				entry.put(DataInTable.COLUMN_TAGS_DETAILS, STAB1[17]);
				entry.put(DataInTable.COLUMN_TAGS_COMMAND_A,STAB1[18]);
				entry.put(DataInTable.COLUMN_TAGS_COMMAND_B,STAB1[19]);
				
				entry.put(DataInTable.COLUMN_VALUE_ST_A, 	"0");
				entry.put(DataInTable.COLUMN_VALUE_ST_B, 	"0");
				entry.put(DataInTable.COLUMN_VALUE_ST_C, 	"0");
				entry.put(DataInTable.COLUMN_VALUE_ST_D, 	"0");
				
				
				
				Uri.encode("<?xml version='1.0' encoding='utf-8'?>");
				DataInUri = getContentResolver().insert(DataInContentProvider.CONTENT_URI, entry);

			      
				i++;
			}while(i<STAB.length);
	   
			
		
			entry=null;
			st=null;
			STAB=null;
			uri=null;
			
	   
	   
			makeVoiceAnalizeList();
	   
	 
		
		

        startService(new Intent(MainActivity.this,myService.class));

              
        
        mText = (TextView) findViewById(R.id.textView1);     
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener());
        //ImageButton speakButton = (ImageButton) findViewById(R.id.buttonVoice);
        
        
        
        
		 timer = new Timer();
		 timerTask = new TimerTask(){
						
			public void run(){
				mHandler.obtainMessage(1).sendToTarget();
			}
			
		};
		
		
		
		timer.schedule(timerTask, 100, 1000);
        
		
		listenVoiceStart(); 

	}
	
	

	
	
	
    public Handler mHandler = new Handler() {
      	 
    	  
    	//private final String DB_NAME = "ceuron.db";	   
    	boolean valueChanged =false;
    	
  	  
  		    public void handleMessage(Message msg) {
  		    	
  		    	licznik++;
  		    	TextView TextCounter = (TextView) findViewById(R.id.textlicz);     
  		    	 TextCounter.setText("nr."+licznik);
  		    	
  		    };
    };
	
	
	
	
	

	private OnClickListener aaa = new OnClickListener(){
		public void onClick(View v){
			

			
			//EditText edittext =(EditText)findViewById(R.id.edit_message);
			//String message = edittext.getText().toString();
			//intent.putExtra(EXTRA_MESSAGE, message);
			//startActivity(intent);
			
			
		}
		
	};
	
	
	public void ButtonOnClick(View v) {
	    switch (v.getId()) {
	      case R.id.button_all_home:
	    	  intent.putExtra("filterObjectType", "");
	        break;
	      case R.id.Button1:
	    	  intent.putExtra("filterObjectType", "light");
	        break;
	      case R.id.Button2:
	    	  intent.putExtra("filterObjectType", "blinds");
		    break; 
	      case R.id.Button3:
	    	  intent.putExtra("filterObjectType", "temperature");
		    break; 
	      case R.id.Button6:
	    	  
	    	  intent = new Intent(this, wwwActivity.class);
	    	  intent.putExtra("WebSite", "http://www.ceuron.pl");
//gggg
		    break;   
		    
	        
	      }
	    startActivity(intent);
	    intent = new Intent(this, InputList.class);
	    //intent.removeCategory("filterPlace");
	    intent.putExtra("filterPlace", "");
	    intent.putExtra("filterObjectType", "");

	}
	
	
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        String ktoryElement = "";
 
        switch (item.getItemId()) {
 
        case R.id.menu_settings:
            ktoryElement = "pierwszy";
            
            break;
        case R.id.menu_zamknij:
        	stopService(new Intent(MainActivity.this, myService.class));
        	this.finish();
            break;
 
        }

 
        return true;
    }
	


	public String urlGet(String urlString){
	      
	     URLConnection urlConnection = null;
	        URL url = null;
	        String string = null;
	         
	        try {
	   url = new URL(urlString);
	   urlConnection = url.openConnection();
	    
	   InputStream inputStream = urlConnection.getInputStream();
	   InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	   BufferedReader reader = new BufferedReader(inputStreamReader);
	    
	       
	   StringBuffer stringBuffer = new StringBuffer();
	    
	   while((string = reader.readLine()) != null){
	    stringBuffer.append(string + "\n");
	   }
	   inputStream.close(); 
	    
	   string = stringBuffer.toString();
	    
	   reader=null;
	   stringBuffer=null;
	    
	  } catch (MalformedURLException e) {
	   e.printStackTrace();
	  } catch (IOException e) {
	   e.printStackTrace();
	  }  
	   
	        
	        
	    
	  return string;
	    }
	
	private CharSequence Parse(String myString){      
	 CharSequence title = myString.subSequence(myString.indexOf("<title>")+7,myString.indexOf("</title>"));
	 return title;
	} 

	    
	    
	    


    //speach
    public class listener implements RecognitionListener          
    {
    		 private boolean listenActive=false;
    	
             public void onReadyForSpeech(Bundle params)
             {
            	 	  listenActive=true;
                      Log.d("Voice analize", "onReadyForSpeech");
             }
             public void onBeginningOfSpeech()
             {
            	      listenActive=true;
                      Log.d("Voice analize", "onBeginningOfSpeech");
                      VoiceBar.setVisibility(View.VISIBLE);
             }
             public void onRmsChanged(float rmsdB)
             {
                      //Log.d(TAG, "onRmsChanged");
            	 	  listenActive=true;
            	      if ((int)rmsdB>VoiceBar.getMax()){
            	    	  VoiceBar.setMax((int)rmsdB);
            	      }
                      VoiceBar.setProgress((int) rmsdB);
                      //mText.setText("S³yszê: "+rmsdB); 
             }
             
             public void onBufferReceived(byte[] buffer)
             {
                      //Log.d(TAG, "onBufferReceived");
             }
             public void onEndOfSpeech()
             {
            	      listenActive=false;
            	      listenVoiceStart(); 
                      Log.d("Voice analize", "onEndofSpeech");
             }
             public void onError(int error)
             {
            	 
            	 listenActive=false;
            	 
                      Log.d("Voice analize",  "error " +  error);
                     // mText.setText("error " + error);
                      if (error!=8) 
                    	  {
                    	  	  listenVoiceStart();  
                    	  }else{
                    		  voiceServerBusy=true;
                    		  VoiceBar.setVisibility(View.INVISIBLE);
                    		  
                    			
               
/*                    			 timer = new Timer();
                    			 timerTask = new TimerTask(){
                    							
                    				public void run(){
                    					ForseStartListen();
                    					timer.cancel();
                    				}
                    				
                    			};
                    			timer.schedule(timerTask, 100, 1000000);
                    			*/
                    		  
                    		  
                    	  }
                     
             }
             public void onResults(Bundle results)                   
             {
                      String str = new String();
                      listenActive=false;
                      
                      
                      Log.d("Voice analize", "onResults " + results);
                      

                      
                      ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

     
                      String ExeCommand=null;
                      boolean action=false;

                      
                      Log.d("Voice analize", "results: " + data.size());
                      
                      
                      String WhatHear="";
                      for (String frase: data)
                      {
                    	if (FindPhrase(frase, true))
                      	{
                            
                      		ExeCommand=frase;
                      		action=true;
                      		break;
                      	}
                      		WhatHear=WhatHear+frase+"\n";
                      }
                      
                      
                      


                      //SoundPool soundID = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                      if (action==true)
                      {
                  		

/*                		int iTmp = soundID.load(getApplicationContext(), R.raw.wykonalem_polecenie, 1); // in 2nd param u have to pass your desire ringtone
                		soundID.play(iTmp, 1, 1, 0, 0, 1);
                		MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.wykonalem_polecenie); // in 2nd param u have to pass your desire ringtone
                		mPlayer.start();
                	
                		mText.setText(""); */
                    	  
                    	  
                    	  Toast.makeText(getApplicationContext(), "Wykonuje komendê g³osow¹: \n"+ExeCommand, Toast.LENGTH_SHORT).show();
                      }else{
/*                    	int iTmp = soundID.load(getApplicationContext(), R.raw.niezrozumialem_powtorz, 1); // in 2nd param u have to pass your desire ringtone
                  		soundID.play(iTmp, 1, 1, 0, 0, 1);
                  		MediaPlayer mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.niezrozumialem_powtorz); // in 2nd param u have to pass your desire ringtone
                  		mPlayer.start(); */
                  		
                  		mText.setText("S³ucham"); 
                    	  
                  		
                  		
                  
                    	 
                  		Toast.makeText(getApplicationContext(), "Zrozumia³em coœ w rodzaju:\n\n"+WhatHear, Toast.LENGTH_LONG).show();
                      }
                      
                      
                      
                     if (listenActive==false) listenVoiceStart(); 
                      
                      
             }
             public void onPartialResults(Bundle partialResults)
             {
                      Log.d("Voice analize", "onPartialResults");
                      listenActive=false;
             }
             public void onEvent(int eventType, Bundle params)
             {
                      Log.d("Voice analize", "onEvent " + eventType);
                      listenActive=false;
             }
             
             
     		private void ForseStartListen()
    		{
     			Log.d("Voice analize",  "Forse lisenVoiceStart ");
     			if (listenActive==false) listenVoiceStart(); 
    		}
     		
    
     		

   
    }
    

    private void listenVoiceStart()
 	    {
 	    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
 	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
 	        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

 	        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
 	             sr.startListening(intent);
 	             Log.i("Voice analize","lisenVoiceStart");
 	             
 	             VoiceBar.setVisibility(View.VISIBLE);    
 	             
 	    }
    
    
    
    /*ImageButton  bt_voice = 	(ImageButton )findViewById(R.id.buttonVoice);
	
    bt_voice.setOnClickListener(new OnClickListener(){
	        public void onClick(View view){
                 Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
                 intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                 intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

                 intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
                      sr.startListening(intent);
                      Log.i("111111","11111111");
             
	        }
	}); */
    
    
  //Button lisen voice 
/*    public void onClick(View v) {
        if (v.getId() == R.id.buttonVoice) 
        {
        	lisenVoiceStart();
        	mText.setText("S³ucham"); 
        }
    }*/
        
        
 /*       public void onClick(View v) {
            if (v.getId() == R.id.buttonVoice) 
            {
            	FindPhrase("zamknij roletê w salonie");
            }
        }*/
        
        
    	private enum Objectyp{
			NONE,ALL,LIGHT,BLINDS 
		}
    
    

    	
    	
private boolean FindPhrase(String str, boolean adjust) {
        	
        	if ((str==null) || (str.length()<5)) return false;
        	Log.d("Voice analize", "Analize fraze: "+str+"  adjust:"+adjust);
        	
        	
		 	str.replace("'t", " not");
		 	str.replace("'", " ");

        	List<String> wordsTmp = Arrays.asList(str.split(" "));
        	List<String> words = new ArrayList<String>();
        	
        	Log.d("Voice analize", "Analize fraze: "+str+"  adjust:"+adjust+ ", Words to processing: "+words.size());
        	

            
            for(String singleWord: wordsTmp)
            {  
            	if (singleWord.length()>2) 	
            		{
            			if ((adjust==false) || (singleWord.length()<6))
       		     		{
            				words.add(singleWord);
       		     		}else{//cut ends of words

       		     			words.add(singleWord.substring(0,singleWord.length()-2));
       		     		}
            		}
            } 
            wordsTmp=null;
        	
            
		     
        	
        	
        	Log.d("Voice analize", "Real words to processing: "+words.size()+"   :"+words);
        	
        	
        	if (words.size()==0) {
        		words=null;
        		return false;
        	}
        	
        	ArrayList<String> objectList= new ArrayList<String>();
        	ArrayList<String>  placeList= new ArrayList<String>(); 
        	ArrayList<String>  detalisList= new ArrayList<String>(); 
        	ArrayList<String>  orderList= new ArrayList<String>(); 
        	
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

        	//prepare finding
        	int  orderType=0;
        	
        	
        	Log.d("Voice analize", " -voiceAnlizeObjectList: "+voiceAnlizeObjectList);
        	
        	//Collect the words---------------------------------------------------------------------------------------
        	for (String singleWords: words)
        	{
        		Log.d("Voice analize", " -Analizing word: "+singleWords);
        		if (voiceAnlizeObjectList.indexOf(singleWords)!=-1)
        		{
        			objectList.add(singleWords);
        		}
        		else if (voiceAnlizePlaceList.indexOf(singleWords)!=-1)
        		{
        			placeList.add(singleWords);
        		}
        		else if (voiceAnlizeDetalisList.indexOf(singleWords)!=-1)
        		{
        			detalisList.add(singleWords);
        		}
        		else if (voiceAnlizeOrdersList.indexOf(singleWords)!=-1)
        		{
        			orderList.add(singleWords);
        			orderType=1;
        		}
        		else if (voiceAnlizeAPIOpenOrdersList.indexOf(singleWords)!=-1)
    			{
    				orderType=2;
    			}
        		else if (voiceAnlizeAPICloseOrdersList.indexOf(singleWords)!=-1)
        		{
    				orderType=3;
        		}
        	}
        	
        	
  
        	
        	
        	// Building SQL request--------------------------------------------------------------------------------
        	String whereSQL="";
		 	String filterObjectType="";
		 	String filterObject="";
		 	String filterPlace="";
		 	String filterDetails="";
		 	boolean foundAction=false;
        	if (orderType>0)
        	{      	

        		
        		
		        	String sep="";
		        	
		        	
		        	
		        	//checking object
		        	if (objectList.size()>0)
		        	{
		        		whereSQL=" ( ";
		        		for (String St:objectList)
		        		{
		        			whereSQL=whereSQL +sep+ DataInTable.COLUMN_TAGS_OBJECT+" like '%"+St+"%'";
		        			sep=" ) or ( ";
		        			filterObject=filterObject+St+",";
		        		}
		        		whereSQL=whereSQL+" ) ";
		        	}

		        	//checking place
		        	if (((whereSQL!="") ||  (orderType>1))&& (placeList.size()>0))
		        	{
		        		sep=" and (";
		        		for (String St:placeList)
		        		{
		        			whereSQL=whereSQL +sep+ DataInTable.COLUMN_TAGS_PLACE+" like '%"+St+"%'";
		        			sep=" ) or ( ";
		        			filterPlace=filterPlace+St+",";
		        		}
		        		whereSQL=whereSQL+" ) ";
		        	}
		        	
		        	//checking details
		        	if (((whereSQL!="") ||  (orderType>1)) && (detalisList.size()>0))
		        	{
		        		sep=" and (";
		        		for (String St:detalisList)
		        		{
		        			whereSQL=whereSQL +sep+ DataInTable.COLUMN_TAGS_DETAILS+" like '%"+St+"%'";
		        			sep=" ) or ( ";
		        			filterDetails=filterDetails+St+",";
		        		}
		        		
		        		whereSQL=whereSQL+" ) ";
		        	}
		        	
		        	sep=null;
        	
        	
        	

		        	Log.d("Voice analize", " -SQL request condition: "+whereSQL+"  fraze length: "+whereSQL.length() + "   Order type: "+orderType);

		        	//Execute order------------------------------------------------------------------------------------
		        	
		        	if ((whereSQL.length()>3) || (orderType==3))
		        	{
		        		
		        		Log.d("Voice analize", " -SQL request condition length>3");
				        	if (orderType==2)
				        	{        	
				        		if ((filterObject!=null) && (filterObject.length()>2)) intent.putExtra("filterObject", filterObject);
				        		if ((filterPlace!=null) && (filterPlace.length()>2)) intent.putExtra("filterPlace", filterPlace);
				        		if ((filterDetails!=null) && (filterDetails.length()>2)) intent.putExtra("filterDetails", filterDetails);
				        		Log.d("Voice analize", "Open new window");
				        		startActivity(intent);
				        		intent.putExtra("filterPlace", "");
							    intent.putExtra("filterObjectType", "");
							    intent.putExtra("filterObject", "");
							    intent.putExtra("filterDetails", "");
						    	
				        		foundAction=true;
				        	}else if (orderType==3)
				        	{
				        		Log.d("Voice analize", "Close window");
		
				        		
							    InputList.handleToClose.finish();
							    foundAction=true;
				        		
				        	}if (orderType==1)
				        	{
				        		Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,whereSQL,null,null);
		
				        		char action=0;
				        		if (cursor.getCount()>0)
					   		     {    
					   				 Log.d("Voice analize", " -Ready to action with "+cursor.getCount()+" objects");
					   				 
					   				    	if(cursor.moveToFirst())
					   				    	{ //Metoda zwraca FALSE jesli cursor jest pusty
					   				    		
					   				    		String command_A="";
					   				    		String command_B="";
					   				    		
					   				    		
					   				    		do{
					   						    		command_A=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_A));
					   				    	    		command_B=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_B));
					   				    	    		
					   				    	    		Log.d("Voice analize", " -		Analize Command A: "+command_A);
					   				    	    		
					   				    	    		
					   				    	    		action=0;
					   				    	    		
					   				    	    		for (String singleWord: orderList)
					   					 		        {
		
					   					 		        		if (command_A.contains(singleWord)) 
					   					 		        		{
						   					 		        		action=1;
						   					 		        		Log.d("Voice analize", " -		Action 1");
						   					 		        		break;
					   					 		        		}
			
					   					 		        }
					   						    	
					   				    	    		
					   									if (action==0)
					   									{
					   										for (String singleWord: orderList)
					   					  	 		        {
				   					  	 		        		if (command_B.contains(singleWord))
				   					  	 		        		{
				   					  	 		        			Log.d("Voice analize", " -		Action 2");
				   					  	 		        			action=2;
				   					  	 		        			break;
				   					  	 		        		}
			
					   					  	 		        }
					   									
					   									}
					   									
					   									
					   									Log.d("Voice analize", " 	-Ation type: "+command_A+"  on object: "+cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_NAME)));
					   		
					   									String typ=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_TYPE));
					   									String signal=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_A));
					   									if (action==1)
					   									{
					   										foundAction=true;
					   								  		
					   								  		if ((typ.startsWith("rolet")) || (typ.startsWith("blind")))
					   								    	{
					   								  			SendAction(signal, "2");
					   								    	}else{
			
					   								    		SendAction(signal, "1");
					   								    	}
					   								  	}
					   									else if (action==2)
					   									{
					   										foundAction=true;
					   										
					   										if ((typ.startsWith("rolet")) || (typ.startsWith("blind")))
					   								    	{
					   										    signal=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_B));
					   					
					   										    SendAction(signal, "2");
					   								    	}else{
					   								    		
					   								    		SendAction(signal, "0");
					   								    	}
					   								  	}
					   				    		}while(cursor.moveToNext());
					   				    		command_A=null;
					   				    		command_B=null;
			
					   				    	}//END cursor.moveToFirst())
					   		 	}
		
				        		
				        		
				        	}
		        	}
        	}
        	
        	Log.d("Voice analize", " -Finish");
        	
        	
        	//-----------------------------------------------------------------------------------------------
        	
        	objectList=null;
        	placeList=null;
        	detalisList=null;
        	whereSQL=null;
        	return foundAction;
        	
        	
        	
        	
        }
    	
    	
    	
    	/*
        private boolean FindPhrase(String str, boolean adjust) {
			// TODO Auto-generated method stub
		 	String WhereSeparator="";
		 	String Where="";
		 	String Where2="";
		 	int  action=0; 
		 	
		 	//Log.d("Voice analize", "FindPhrase");
		 	
		 	
		 	if ((str==null) || (str.length()<5)) return false;
		 	
		 	str.replace("'t", " not");
		 	str.replace("'", " ");
		 	
		 	Log.d("Voice analize", "Analize fraze: "+str+"  adjust:"+adjust);
		 	
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
		 	
		 	

		     WhereSeparator= "";
		     

		     String WordsTMP[] =str.split(" ");

		     int countWords=0;
		     if  (WordsTMP.length==0)
		     {
		    	 //Log.d("Voice analize", " -Can't split. Escape ");
		    	 return false;
		     }else{
		    	 
		    	 for (String singleWord: WordsTMP)
			     {
			     	if (singleWord.length()>3)
			     	{
			     		WordsTMP[countWords]=singleWord;
			     		countWords++;
			     	}
			     } 
		     }
		     
		     
		     if (countWords==0){
		
		    	 return false;
		     }
		     

		     String[] Words= new String[countWords];
		     System.arraycopy(WordsTMP, 0, Words, 0, countWords);
		     
		     
		     //cut ends of words
		     if (adjust)
		     {
		    	 int k=0;
		    	 for (String singleWord: Words)
			     {
		    		 if (singleWord.length()>5) Words[k]=singleWord.substring(0,singleWord.length()-2);
		    		 k++;
			     } 
		     }
		     
		     

		     String TMP_Words="";
		     for (String singleWord: Words)
		     {
		    	 TMP_Words+=singleWord+", ";
		     } 
		     
		     Log.d("Voice analize", " -Analize words: "+TMP_Words);
		     
		     
		     
		     
		     //find object -----------------------------------------------------------------------------------------------
		     WhereSeparator = " (";
		     for (String singleWord: Words)
		     {
		     		Where+=WhereSeparator + DataInTable.COLUMN_TAGS_OBJECT+" like '%"+singleWord+"%'";
			        	WhereSeparator= " or ";
		     }
		     Where+=")";
		     
		     Log.d("Voice analize", " -SQL Where "+Where);
		     
		     Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,Where,null,null);
		     
		     
	         		

		     
		     //if object not found
		     if (cursor.getCount()==0) 
		    	 {
			    	 	Log.d("Voice analize", " -No objects found. Return");
			    	 	Log.d("Voice analize", " -currentViewDiscriptionType: "+currentViewDiscriptionType); 
			    	 	
			    	 	if ((str.contains("cofnij")) || (str.contains("wróæ")) || (str.contains("wstacz"))  || (str.contains("wyjdŸ"))  || (str.contains("powrót"))  || (str.contains("zamknij okno"))   )
						{
							Log.i("Voice analize","Powrót");
							
							
							if (InputList.active)
			    			{
			    				InputList.handleToClose.finish();
			    			}
							return true;
						}
			    	 	else if (currentViewDiscriptionType.length()>3)
						{
				   				Where=Where+" and "+currentViewDiscriptionType;
				   			 cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,Where,null,null);
				   		     
						}else{
								 return false; 
						}
						
		    	 }else{
		    		 Log.d("Voice analize", " -Objects found: "+cursor.getCount()); 
		    	 }
		     
		     
		     
		     
		     
		     Log.d("Voice analize", " -Words: "+Words.length); 
		     
		     //if words more then 2 (object name and order) then check place or other condition
		     Cursor cursor2 = null;
		     if (Words.length>2)
		     {
		    	
		    	 	Where2=Where;
					if (cursor.getCount()>1)
					{
						 //Log.d("Voice analize", " Words>2");  
						
				        WhereSeparator = " and (";
				        for (String singleWord: Words)
				        {
	
				        		Where+=WhereSeparator + DataInTable.COLUMN_TAGS_PLACE+" like '%"+singleWord+"%'";
					        	WhereSeparator= " or ";
				        }
				        Where+=")";
				        cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,Where,null,null);
				        Log.d("Voice analize", " -Places found: "+cursor.getCount());
					}
					
					
					if ((cursor.getCount()>1) || (cursor.getCount()==0))
				     {
							if (cursor.getCount()==0)  Where=Where2;
						
					        WhereSeparator = " and (";
					        for (String singleWord: Words)
					        {

					        		Where+=WhereSeparator + DataInTable.COLUMN_TAGS_DETAILS+" like '%"+singleWord+"%'";
						        	WhereSeparator= " or ";

					        }
					        Where+=")";
					        cursor2 = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,Where,null,null); 
					        Log.d("Voice analize", " -Positions found: "+cursor.getCount());
				     }
		     }        
				
			      

		     //finding order
		     boolean foundAction=false;
		     

		     if (cursor.getCount()>0)
		     {    
				 Log.d("Voice analize", " -Ready to action with "+cursor.getCount()+" objects");
				 
				    	if(cursor.moveToFirst())
				    	{ //Metoda zwraca FALSE jesli cursor jest pusty
				    		
				    		String command_A="";
				    		String command_B="";
				    		
				    		
				    		do{
						    		command_A=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_A));
				    	    		command_B=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_B));
				    	    		
				    	    		Log.d("Voice analize", " -		Analize Command A: "+command_A);
				    	    		
				    	    		
				    	    		action=0;
				    	    		for (String singleWord: Words)
					 		        {

				    	    			    Log.d("Voice analize", " -		singleWord: "+singleWord);
					 		        		if (command_A.contains(singleWord)) {
					 		        		action=1;
					 		        		Log.d("Voice analize", " -		Action 1");
					 		        			break;
					 		        			}

					 		        }
						    	
				    	    		
									if (action==0)
									{
										Log.d("Voice analize", " -		Analize Command B: "+command_B);
						    		for (String singleWord: Words)
					  	 		        {
						    					Log.d("Voice analize", " -		singleWord: "+singleWord);

					  	 		        		if (command_B.contains(singleWord)) {
					  	 		        			Log.d("Voice analize", " -		Action 2");
					  	 		        			action=2;
					  	 		        			break;
					  	 		        			}

					  	 		        }
									
									}
									
									
									Log.d("Voice analize", " 	-Ation type: "+command_A+"  on object: "+cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_NAME)));
		
									String typ=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_TYPE));
									String signal=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_A));
									if (action==1)
									{
										foundAction=true;
								  		
								  		if ((typ.startsWith("rolet")) || (typ.startsWith("blind")))
								    	{
								  			SendAction(signal, "2");
								    	}else{

								    		SendAction(signal, "1");
								    	}
								  	}
									else if (action==2)
									{
										foundAction=true;
										
										if ((typ.startsWith("rolet")) || (typ.startsWith("blind")))
								    	{
										    signal=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_ACTION_B));
					
										    SendAction(signal, "2");
								    	}else{
								    		
								    		SendAction(signal, "0");
								    	}
								  	}
				    		}while(cursor.moveToNext());

				    	}//END cursor.moveToFirst())
		 	}
			 
			 
			 
			 
			 
			 
		//Action inside application
			String FilterObject="";
			String FilterObjectSeparator="";
			String FilterPlace="";
			String FilterPlaceSeparator="";
			
			if (!foundAction) 
		 	{
				if ((cursor.getCount()>0) &&  ((str.contains("poka¿")) || (str.contains("wyœwietl")) ||  (str.contains("daj")) ||  (str.contains("otwórz okno"))   || (str.contains("widok"))  ))
			     {
					if(cursor.moveToFirst())
			    	{ //Metoda zwraca FALSE jesli cursor jest pusty
			    		
			    		String get_str="";

		    		do{
		    		
			    			get_str=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_TYPE));
			    			if ((!FilterObject.contains(get_str)) && (get_str.length()>2)) 
			    				{
			    					FilterObject=FilterObject+FilterObjectSeparator+get_str;
			    					FilterObjectSeparator=", ";
			    				}
			    			
			    			
			    			if (Words.length>2)
			    			{
			    			get_str=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_PLACE));
			    			if ((!FilterPlace.contains(get_str)) && (get_str.length()>2))
			    				{
			    					FilterPlace=FilterPlace+FilterPlaceSeparator+get_str;
			    					FilterPlaceSeparator=", ";
			    				}
			    			}
	
			    		}while(cursor.moveToNext());

			    	}//END cursor.moveToFirst())
					
					if (InputList.active)
	    			{
	    				InputList.handleToClose.finish();
	    			}
					if ((FilterObject!=null) && (FilterObject.length()>2)) intent.putExtra("filterObject", FilterObject);
					if ((FilterPlace!=null) && (FilterPlace.length()>2)) intent.putExtra("filterPlace", FilterPlace);
			    	startActivity(intent);
			    	intent.putExtra("filterPlace", "");
				    intent.putExtra("filterObject", "");
				    return true;
					
			    }
		
		 	}
			 
			 
			 
			 
				
			 Log.d("Voice analize", " 	-Analise finish. Return: "+action); 

			 
			if (foundAction)
			{
				
				return true;
			}else{
				return false;
			}
			    		   
		 } 
		       
            */
       
    	
    	
	    
void makeVoiceAnalizeList()
{    
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
    
    Log.d("Voice analize", "makeVoiceAnalizeList ");
    
    
    Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, projection,null,null,null);
    
    if (cursor.getCount()>0)
    {
    	if(cursor.moveToFirst())
    	{
	    	do{
	    		
	    		//object list
	    		if (voiceAnlizeObjectList.indexOf(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_OBJECT)))==-1){
	    			voiceAnlizeObjectList.append(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_OBJECT))+"\n");
	    		};
	    		
	    		//place list
	    		if (voiceAnlizePlaceList.indexOf(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_PLACE)))==-1){
	    			voiceAnlizePlaceList.append(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_PLACE))+"\n");
	    		};
	    		
	    		//Details list
	    		if (voiceAnlizeDetalisList.indexOf(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_DETAILS)))==-1){
	    			voiceAnlizeDetalisList.append(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_DETAILS))+"\n");
	    		};
	    		
	    		//Orders list
	    		if (voiceAnlizeOrdersList.indexOf(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_A)))==-1){
	    			voiceAnlizeOrdersList.append(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_A))+"\n");
	    		};
	    		if (voiceAnlizeOrdersList.indexOf(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_B)))==-1){
	    			voiceAnlizeOrdersList.append(cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_TAGS_COMMAND_B))+"\n");
	    		};

	    		//API orders list
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("poka¿")==-1) voiceAnlizeAPIOpenOrdersList.append("poka¿"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("wyœwietl")==-1) voiceAnlizeAPIOpenOrdersList.append("wyœwietl"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("daj")==-1) voiceAnlizeAPIOpenOrdersList.append("daj"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("widok")==-1) voiceAnlizeAPIOpenOrdersList.append("widok"+"\n");

	    		if (voiceAnlizeAPICloseOrdersList.indexOf("cofnij")==-1) voiceAnlizeAPICloseOrdersList.append("cofnij"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wróæ")==-1) voiceAnlizeAPICloseOrdersList.append("wróæ"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wstacz")==-1) voiceAnlizeAPICloseOrdersList.append("wstacz"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wyjdŸ")==-1) voiceAnlizeAPICloseOrdersList.append("wyjdŸ"+"\n");

	    		
	    	  }while(cursor.moveToNext());
		}
    }
    
	
	
	
}
 
	    




    
    	    
	    
    
 void   SendAction(String Signal, String Set) 
	{
	 Log.d("Voice analize", " -		Send "+Set+" to signal: "+Signal);
	
 /*
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
	    }*/
    
}
 
 



	@Override
	 public void onDestroy() {
	  super.onDestroy();
	  //baza.close();	
	  timer.cancel();
	  timerTask.cancel();
	  Toast.makeText(this, "Us³uga Ceuron zosta³a zamkniêta!", Toast.LENGTH_LONG).show();
	 }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	}
	     

	    
	    
	    
	    
	    
	    
	    
	    


	  

	

	


