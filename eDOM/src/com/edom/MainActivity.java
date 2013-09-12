//Test 5
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
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edom.contentprovider.DataInContentProvider;
import com.edom.database.DataInTable;




public class MainActivity extends Activity {

	public TextView tw;
	public ImageView MicrophonView;
	public ImageView MicrophonGlameView;
	
	public int licznik=0;
	public static final ArrayList<String> DataIn = new ArrayList<String>();  
	
	  private SoundPool soundPool;
	  private int soundID;
	  boolean loaded = false;
	
	public static String currentViewDiscriptionType="";
	public static String currentViewDiscriptionPlace="";

	int LastRms[]= new int[10];
	  
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
	private Intent intentVoice;
	
	
	SQLiteDatabase baza = null;
	HttpURLConnection connection;
    OutputStreamWriter request = null;
    
    boolean TMP=false;
    private Timer timer;
    private TimerTask timerTask;
    Intent intent;

    boolean voiceServerBusy=false;
    char 	endOfSpeachWait;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		
		//IntentVoice------------------------------------------------------------------------------------
	        intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
	    	intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    	intentVoice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

	    	intentVoice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
	   //---------------------------------------------------------------------------------------------
		
		
		
		
		intent = new Intent(this, InputList.class);
		


		CharSequence uri = "http://www.ceuron.pl/Test/_db3131385.csv"; 
		String st=urlGet(String.valueOf(uri));		
		String STAB[]=st.split("\n");
		
		ContentValues entry = new ContentValues();
		
		

		
		//ImageButton btTmp = (ImageButton)findViewById(R.id.button_all_home);
		//btTmp.setAlpha(100);
		
		MicrophonView  = (ImageView)findViewById(R.id.microphone);	
		MicrophonGlameView  = (ImageView)findViewById(R.id.mikrophone_tlo);	
		MicrophonGlameView.setAlpha(2);
		
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
	   
	 
		
		

        //startService(new Intent(MainActivity.this,myService.class));

              
        
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
  		    	 
  		    	 if (endOfSpeachWait==1)
  		    	 {
  		    		Log.d("Voice analize",  " listenVoiceStart forse");
  		    		listenVoiceStart();  
  		    	 }
  		    	 if (endOfSpeachWait>0)endOfSpeachWait--;
  		    	 
  		    	
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
            	 	  endOfSpeachWait=0;
            	 	  listenActive=true;
                      Log.d("Voice analize", "onReadyForSpeech");
             }
             public void onBeginningOfSpeech()
             {
            	      listenActive=true;
                      Log.d("Voice analize", "onBeginningOfSpeech");
                      MicrophonView.setVisibility(View.VISIBLE);
             }
             public void onRmsChanged(float rmsdB)
             {
            	 int Val=(int)(rmsdB*3);
            	 endOfSpeachWait=0;
            	 MicrophonView.setAlpha(0xFF);
            	 for(char i=5; i>1; i--)
            	 {
            		 Val+=LastRms[i]/(i/2);
            		 LastRms[i]=LastRms[i-1];
            	 }
            	 Val+=LastRms[0];
            	 Val+=40;
            	 
            	 
            	 if (Val>60) Val=60;

            	 MicrophonGlameView.setAlpha(Val); 

              
             }
             
             public void onBufferReceived(byte[] buffer)
             {
                      //Log.d(TAG, "onBufferReceived");
             }
             public void onEndOfSpeech()
             {
            	 Log.d("Voice analize", "onEndofSpeech");
            	      listenActive=false;
            	      
            	      //listenVoiceStart(); 
            	      MicrophonView.setAlpha(80);
            	      MicrophonGlameView.setAlpha(0); 
            	      endOfSpeachWait=2;
                      
             }
             public void onError(int error)
             {
            	 
            	 listenActive=false;
            	 //voiceServerBusy=true;
            	 MicrophonView.setAlpha(80);
       	         MicrophonGlameView.setAlpha(0); 
       		     

                      Log.d("Voice analize",  "error " +  error);
                     // mText.setText("error " + error);
                    if (error==sr.ERROR_RECOGNIZER_BUSY) 
                    	  {
                    	 Log.d("Voice analize",  " ERROR_RECOGNIZER_BUSY -------------------------------");
                    		 sr.cancel();
                    	  	  
                    	  }else{
                    		  
                    	  }
                    
                    listenVoiceStart();  
                     
             }
             public void onResults(Bundle results)                   
             {
            	 endOfSpeachWait=0;
            	 if (listenActive==false) listenVoiceStart(); 
            	 
            	 
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
    		//sr.destroy();

 	             sr.startListening(intentVoice);
 	             

 	             Log.i("Voice analize","lisenVoiceStart");
 	             
 	            MicrophonView.setVisibility(View.VISIBLE);  
 	            MicrophonGlameView.setVisibility(View.VISIBLE); 
 	           
 	         
 	           
 	             
 	    }
    
    
    

        
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
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("wejdŸ")==-1) voiceAnlizeAPIOpenOrdersList.append("wejdŸ"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("prezentuj")==-1) voiceAnlizeAPIOpenOrdersList.append("prezentuj"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("demonstruj")==-1) voiceAnlizeAPIOpenOrdersList.append("demonstruj"+"\n");
	    		

	    		if (voiceAnlizeAPICloseOrdersList.indexOf("cofnij")==-1) voiceAnlizeAPICloseOrdersList.append("cofnij"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wróæ")==-1) voiceAnlizeAPICloseOrdersList.append("wróæ"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wstacz")==-1) voiceAnlizeAPICloseOrdersList.append("wstacz"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wyjdŸ")==-1) voiceAnlizeAPICloseOrdersList.append("wyjdŸ"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("powrót")==-1) voiceAnlizeAPICloseOrdersList.append("powrót"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("opuœæ")==-1) voiceAnlizeAPICloseOrdersList.append("opuœæ"+"\n");

	    		
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
	     

	    
	    
	    
	    
	    
	    
	    
	    


	  

	

	


