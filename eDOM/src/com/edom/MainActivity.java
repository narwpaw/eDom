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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.edom.contentprovider.DataInContentProvider;
import com.edom.database.DataInTable;




public class MainActivity extends Activity {

	   String[] WagArrStr ={
			    "825496873984762923",
			    "653095872408856462",
			    "937562103412474890",
			    "934586721095672413",
			    "279104753724762524",
			    "745146978245600281",
			    "385756027697634183",
			    "834557291157491278",
			    "037561364893679159",
			    "305712587254629341"};
	
    static byte[][] WagArr= new byte[10][18];
	
	public TextView tw;
	public ImageView MicrophonView;
	public ImageView MicrophonGlameView;
	
	public int licznik=0;
	public static final ArrayList<String> DataIn = new ArrayList<String>();  
	public static  int 	equalizer=0;
	protected AudioManager mAudioManager; 
	
	  private SoundPool soundPool;
	  private int soundID;
	  boolean loaded = false;
	  
	public static  boolean 	manualListenActive=false;  
	
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
    boolean listenActive=false;
    private SharedPreferences preferences;
    boolean app_speaker;
    boolean listen_const;
    boolean applicationActive=false;
    boolean firstLoop=true;
    public static List<String> ObjestsList=new ArrayList<String>();
    
    private String websiteOld;
    private String controller_idOld;
    private String activeCodeOld;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		
		//IntentVoice------------------------------------------------------------------------------------
	        intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
	    	intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    	intentVoice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

	    	intentVoice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
	   //---------------------------------------------------------------------------------------------
		
		
		
		
		intent = new Intent(this, InputList.class);
		
		
		
		MicrophonView  = (ImageView)findViewById(R.id.microphone);	
		MicrophonGlameView  = (ImageView)findViewById(R.id.mikrophone_tlo);	
		MicrophonGlameView.setAlpha(2);
		
		
		preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
		String website=preferences.getString("website", "");
		String controller_id=preferences.getString("controller_id", "");
		app_speaker=preferences.getBoolean("speaker", true);    
        listen_const=preferences.getBoolean("listen_const", false);  
        

        
        
        

        
		

			

			
	   
	   
			makeVoiceAnalizeList();
	   
	 
		
		

      

              
        
    
        sr = SpeechRecognizer.createSpeechRecognizer(this);       
        sr.setRecognitionListener(new listener());
        //ImageButton speakButton = (ImageButton) findViewById(R.id.buttonVoice);
        
        
        
        
        
        
		 timer = new Timer();
		 timerTask = new TimerTask(){
						
			public void run(){
				mHandler.obtainMessage(1).sendToTarget();
			}
			
		};
		
		
		
		timer.schedule(timerTask, 100, 300);
        
		
		if (listen_const) {
			listenVoiceStart(); 
			manualListenActive=true;
		}
		
		
		

		MicrophonView.setOnClickListener(new OnClickListener() 
	    {
	        @Override
	        public void onClick(View v) 
	        {
	        	if (manualListenActive)
	        	{
	        		
	        		listenVoiceStop();
	        	}else{
	        		manualListenActive=true;
	        		listenVoiceStart(); 
	        	}
	        }
	    });
		
		
		
		for (char x=0; x<18; x++)
		{
			for (char y=0; y<10; y++)
			{
				WagArr[y][x]=(byte) ((byte) WagArrStr[y].charAt(x) -48);
			}
		}
		
		
		

	}
	
	
	
	
	
	
	
	
	
	 
	

	
	@Override
	protected void onResume(){
	    super.onResume();
	    
	    Log.d("eDom","onResume");
	    
		preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
		String website=preferences.getString("website", "");
		String controller_id=preferences.getString("controller_id", "");
		app_speaker=preferences.getBoolean("speaker", true);    
        listen_const=preferences.getBoolean("listen_const", false);  
        
        String activeCode=preferences.getString("activeCode", "");
        int startTrialDay =preferences.getInt("startDay", 0);

        SharedPreferences.Editor editor = preferences.edit();
        
        
        String ID_Read=DecodeActivationCode(activeCode);
        //String ID_Read=DecodeActivationCode("056526081378850544");
        
        
        
        
        if ( ((website.equals(websiteOld))==false) ||  ((controller_id.equals(controller_idOld))==false) || ((activeCode.equals(activeCodeOld))==false)  )
        {
        	 Log.d("eDom","Rozpoczęcie pobierania listy obiektów");
        	
        	
        	websiteOld=website;
   	 		controller_idOld=controller_id;
   	 		activeCodeOld=activeCode;
        
        
		        Log.d("list filetr","Zdekodowane ID "+ID_Read );
		
		       
		        
		        
		        
		        
		        if (ID_Read.equals( controller_id)==false)
		        {
		    		Date now = new Date();
		    		long t = now.getTime();
		    		int curTime=(int) (t/1000/60/60/24);
		        	
		        	if(startTrialDay==0)
		        	{
		        		startTrialDay=(int) curTime;
		        		editor.putInt("startDay", startTrialDay);
		        		editor.commit();
		        	}
		        	
		        	startTrialDay =preferences.getInt("startDay", 0);
		            
		            int TimeLife=curTime-startTrialDay;
		            if (TimeLife>40)
		            {
		            	applicationActive=false;
		            	
		            	if (firstLoop) Toast.makeText(getApplicationContext(), "Okres testowania minął", Toast.LENGTH_LONG).show();
		            	
		            }else{
		            	applicationActive=true;
		            	if (firstLoop) Toast.makeText(getApplicationContext(), "Aktywuj aplikację.\n Pozostało "+Integer.toString(TimeLife=40-TimeLife)+" dni.", Toast.LENGTH_LONG).show();
		            }
		
		        }else{
		        	applicationActive=true;
		        }
		        
		        
		        if (applicationActive)
		        {
		        	Uri uri=Uri.parse(website+"/_db"+controller_id+".csv");
		        }
		        
		        
		        
		        
		        String st=null;
		        if (applicationActive)
		        {
					Uri uri=Uri.parse(website+"/_db"+controller_id+".csv");
					st=urlGet(String.valueOf(uri));	
		        }
		        
				if ((st==null) || (website==null) || (controller_id==null) || (applicationActive==false))
				{
					if (firstLoop)
					{
						Intent intent = new Intent(MainActivity.this, MyPreferences.class);
		            	startActivity(intent);
					}else{
						stopService(new Intent(MainActivity.this, myService.class));
			        	this.finish();
					}
				}else{
				
					String STAB[]=st.split("\n");
				
				    ContentValues entry = new ContentValues();		
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
						
						
						
						
						
						
						
						
						
						//pobranie listy obiektów ------------------------------------------------------------------------------------------
						
						
						
		
				        Cursor cursor = getContentResolver().query(DataInContentProvider.CONTENT_URI, new String[] {DataInTable.COLUMN_OBJECT_NAME, DataInTable.COLUMN_OBJECT_TYPE, DataInTable.COLUMN_OBJECT_PLACE,DataInTable.COLUMN_OBJECT_FLOOR}, " 1=1 ) GROUP BY ("+DataInTable.COLUMN_OBJECT_TYPE,null,null);		
				        
						
				        List<String> spinnerListFiltrTMP=new ArrayList<String>();
				        List<String> PlacesForSpiner=new ArrayList<String>();
				        int SpinerItem=0;
				        int idx=0;
				        
				        
				        ObjestsList.clear();
				        if (cursor.getCount()>0)
				        {
		
				        	idx++;
						    	if(cursor.moveToFirst()){ //Metoda zwraca FALSE jesli cursor jest pusty
						    		
						    		Log.d("list filetr","C" );
		
				  	  	    	       do{
				  	  	    	    	  String place=cursor.getString(cursor.getColumnIndex(DataInTable.COLUMN_OBJECT_TYPE));
				  	  	    	    	
				  	  	    	    	  for (String Plc: PlacesForSpiner)
				  	  	    	    	  {
				  	  	    	    		if (place.contains(Plc))
					  	  	    	    	  {
				  	  	    	    		
					  	  	    	    		//spinnerListFiltrTMP.add(place.toUpperCase());
					  	  	    	    	    spinnerListFiltrTMP.add(place);
					  	  	    	    		SpinerItem=idx;
					  	  	    	    		break;
					  	  	    	    	  } 
				  	  	    	    	  }
				  	  	    	    	  
				  	  	    	    	  
				  	  	    	    	idx++; 
				  	  	 
				  	  	    	    //ObjestsList.add(place.toUpperCase());
				  	  	    	    	
				  	  	    	    ObjestsList.add(place);
				  	  	    	    	
				  	  	    	       }while(cursor.moveToNext());
						    	}
				        }
				        
				  
				        if (spinnerListFiltrTMP.size()>1)
				        {
				        	if (spinnerListFiltrTMP.size()<ObjestsList.size()-1)
				        	{
				        		ObjestsList.add(0, spinnerListFiltrTMP.toString().replace("[", "").replace("]", ""));
				        	}
				        
				        	SpinerItem=0;
				        }
				        
				       
				        
				       
				   
				        
				        
				        ImageButton ButtonObject;
				        ButtonObject  = (ImageButton)findViewById(R.id.Button1);
				        
				        
				        //wyłączenie widoczności
				        for (int BtNr=ObjestsList.size(); BtNr<=6; BtNr++)
				        {
				        	
				        	
				        	switch (BtNr) {
					  	      case 1:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button1);	
					  	    	break;
					  	      case 2:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button2);	
					  	    	break;
					  	      case 3:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button3);	
					  	    	break;
					  	      case 4:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button4);	
				  	    		break;
					  	      case 5:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button5);	
				  	    		break;
					  	      case 6:
					  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button6);	
				  	    		break;
				        	}
				        	ButtonObject.setVisibility(View.INVISIBLE);
				        }
				        
				        
				        
				        char BtNr=0;
				        for (String ObjectType : ObjestsList)
				        {
				        	BtNr++;
				        	DrowButton(BtNr,ObjectType);
				        }
				        
				        if (BtNr<6)
				        {
				        	BtNr++;
				        	DrowButton(BtNr,"www");
				        }
				        
			
						
				} 
				
				Log.d("eDom","Zakończenie pobierania listy obiektów");
		        
        }//koniec if ( ((website.equals(websiteOld))==false) ||  ((controller_id.equals(controller_idOld))==false) || ((activeCode.equals(activeCodeOld))==false)  )

        
        
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (listen_const)
        {
        	mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }else{

            mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);	
        }

        
        
        if(firstLoop) 
        	{
        	  Log.d("eDom","Pruba uruchomienia serwisu");
              if (isMyServiceRunning()==false)
              {
            	  startService(new Intent(MainActivity.this,myService.class));
              }else{
            	  Log.d("eDom","Serwis już jest uruchomiony");
              }
        	   
        	}
        
        firstLoop=false;
        
        Log.d("eDom","Koniec onResume");
	}
	

	
	

	 private boolean isMyServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if (myService.class.getName().equals(service.service.getClassName())) {
		            return true;
		        }
		    }
		    return false;
		}
	
	
	
	public void DrowButton(char BtNr, String ObjectType)
	{

		ImageButton ButtonObject;
        ButtonObject  = (ImageButton)findViewById(R.id.Button1);
		

        	
       
    	    switch (BtNr) {
	  	      case 1:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button1);	
	  	    	break;
	  	      case 2:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button2);	
	  	    	break;
	  	      case 3:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button3);	
	  	    	break;
	  	      case 4:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button4);	
  	    		break;
	  	      case 5:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button5);	
  	    		break;
	  	      case 6:
	  	    	ButtonObject  = (ImageButton)findViewById(R.id.Button6);	
  	    		break;
  	    		
  	    		
    	    }
        	

    	    if (ObjectType=="www")
        	{
        		ButtonObject.setImageResource(R.drawable.www);
        	}
        	else if (ObjectType=="light")
        	{
        		ButtonObject.setImageResource(R.drawable.lights);
        	}
        	else if (ObjectType=="blinds")
        	{
        		ButtonObject.setImageResource(R.drawable.blinds);
        	}else if (ObjectType=="temperature")
        	{
        		ButtonObject.setImageResource(R.drawable.themperature);
        	}
        	
        	ButtonObject.setVisibility(View.VISIBLE);	
       
		
	}
	
	
	
	static public   String DecodeActivationCode(String Reg_Code)
	{
		String ID_Txt="";
		byte KodTmp[]=new byte[18], WagaTyp;
		//char ID[]=new char[7];
		Integer sum, ID_Period;
		String StrTmp;
		boolean SumKontrol;
		

		if (Reg_Code.length()!=18) return "";		    
		
		
					  for (char i=0; i<=17; i++) 
						  {
		
						    KodTmp[i]=(byte) ((byte) Reg_Code.charAt(i) -48);
						  }

				      

				      //dekodowanie
				      WagaTyp=KodTmp[17];
				      sum=0;
				      for (char i=0; i<=16; i++)
				       {
				             if (  (KodTmp[i]<(WagArr[WagaTyp][i]))) 
				             {
				                KodTmp[i]= (byte) (10+ KodTmp[i]-(WagArr[WagaTyp][i]));
				             } else {                
				                KodTmp[i]=(byte) (KodTmp[i]-(WagArr[WagaTyp][i]));
				             }
				             if (i<15) sum=sum+KodTmp[i];
				       }
				       
				       
				       ID_Period=KodTmp[12]*10 +  KodTmp[14];
				       
				       SumKontrol=false;
				       if ((KodTmp[15]==(sum / 10)) && (KodTmp[16]==sum % 10))  SumKontrol=true;

				       
				       if (SumKontrol)
				       {
					       ID_Txt=Integer.toString(KodTmp[8]);
					       ID_Txt+=Integer.toString(KodTmp[2]);
					       ID_Txt+=Integer.toString(KodTmp[10]);
					       ID_Txt+=Integer.toString(KodTmp[6]);
					       ID_Txt+=Integer.toString(KodTmp[0]);
					       ID_Txt+=Integer.toString(KodTmp[4]);
					       ID_Txt+=Integer.toString(KodTmp[13]);
				       }

		
		return ID_Txt;
	}
	
	
	
	
    public Handler mHandler = new Handler() {
      	 
    	  
    	//private final String DB_NAME = "ceuron.db";	   
    	boolean valueChanged =false;
    	
  	  
  		    public void handleMessage(Message msg) {
  		    	
  		    	//licznik++;
  		    	//TextView TextCounter = (TextView) findViewById(R.id.textlicz);     
  		    	// TextCounter.setText("nr."+licznik);
  		    	 
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
		

		
	  //int ObjNr=ObjestsList.size();
	  int ButtonNr=0;
	  
		
	    switch (v.getId()) {

	      case R.id.Button1:
	    	  ButtonNr=1;
//	    	  intent.putExtra("filterObjectType", "light");
	      break;
	      case R.id.Button2:
	    	  ButtonNr=2;
//	    	  intent.putExtra("filterObjectType", "blinds");
		    break; 
	      case R.id.Button3:
	    	  ButtonNr=3;
//	    	  intent.putExtra("filterObjectType", "temperature");
		    break; 
	      case R.id.Button4:
	    	  ButtonNr=4;
//	    	  intent.putExtra("filterObjectType", "temperature");
		    break; 
	      case R.id.Button5:
	    	  ButtonNr=5;
//	    	  intent.putExtra("filterObjectType", "temperature");
		    break; 
	      case R.id.Button6:
	    	  ButtonNr=6;
//	    	  intent = new Intent(this, wwwActivity.class);
//	    	  intent.putExtra("WebSite", website);
		  break;   
		  

		  
		  
		    
		    
	      case R.id.button_all_home:
	    	  intent.putExtra("filterObjectType", "");
	      break; 
		    
	        
	      }
	    
	    
	    
		  if (ButtonNr>0)
		  {
			  
			 if (ButtonNr>ObjestsList.size()) //jeżeli wybrano wszystkie kategorie
			 {
			     preferences = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
			  	 String website=preferences.getString("website", "");
				 intent = new Intent(this, wwwActivity.class);
		    	 intent.putExtra("WebSite", website); 
			 }else{ //jeżeli wybrano jedną kategorię obiektów
				 intent.putExtra("filterObjectType", ObjestsList.get(ButtonNr-1) ); 
			 }
	
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
         
            Intent intent = new Intent(MainActivity.this, MyPreferences.class);
            startActivity(intent);
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
            	 Val+=100;
            	 
            	 
            	 if (Val>200) Val=200;

            	 
            	 //intent.putExtra("equalizer", Val);
            	 //equalizer=Val;
            	 
            	 //InputList.handleToClose.
            	 
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
            	      
            	      
            	      if (listen_const) {
            	    	  
                 	      MicrophonView.setAlpha(80);
                	      MicrophonGlameView.setAlpha(0); 
                	      endOfSpeachWait=2;
            	    	  
                        }else{
                        	
                        	MicrophonView.setAlpha(0xFF);	
                      	  MicrophonGlameView.setVisibility(View.INVISIBLE);  
                      	  manualListenActive=false; 
            		      }   
            	      
                      
             }
             public void onError(int error)
             {
            	 
            	 listenActive=false;
            	 //voiceServerBusy=true;
            	 
            	 if (listen_const) {
            	 MicrophonView.setAlpha(80);
       	         MicrophonGlameView.setAlpha(0); 
            	 }else{
            		 MicrophonView.setAlpha(0xFF); 
            	 }
       		     

                      Log.d("Voice analize",  "error " +  error);
                     // mText.setText("error " + error);
                    if (error==sr.ERROR_RECOGNIZER_BUSY) 
                    	  {
                    	 Log.d("Voice analize",  " ERROR_RECOGNIZER_BUSY -------------------------------");
                    		 sr.cancel();
                    	  	  
                    	  }else{
                    		  
                    	  }
                    
                    //listenVoiceStart();  
                    
                    if (listen_const) {
                    	listenVoiceStart();  
                      }else{
                    	  MicrophonGlameView.setVisibility(View.INVISIBLE);  
                    	  manualListenActive=false; 
          		      }
                 
                     
             }
             public void onResults(Bundle results)                   
             {
            	 endOfSpeachWait=0;
            	 //if (listenActive==false) listenVoiceStart(); 
            	 
            	 
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
                      

                      if (action==true)
                      {
                  		
                    	  if (app_speaker==true)
                          {
	                		Context appContext = getApplicationContext();
	                        MediaPlayer mp = MediaPlayer.create(appContext , R.raw.wykonalem_polecenie);
	                        mp.start();
                          }
                    	  
                    	  Toast.makeText(getApplicationContext(), "Wykonuje komendę głosową: \n"+ExeCommand, Toast.LENGTH_SHORT).show();
                    	  if ((listenActive==false) && (listen_const)) 
                    		  {
                    		  	listenVoiceStart();   
                    		  
                    		  }else{
                    			  MicrophonGlameView.setVisibility(View.INVISIBLE);   
                    			  manualListenActive=false; 
                    		  }
                      }else{

                    	  Toast.makeText(getApplicationContext(), "Zrozumiałem coś w rodzaju:\n\n"+WhatHear, Toast.LENGTH_LONG).show();
                    	  if (app_speaker==true)
                          {
	                  		Context appContext = getApplicationContext();
	                        MediaPlayer mp = MediaPlayer.create(appContext , R.raw.niezrozumialem_powtorz);
	                        mp.start();
                          }
                    	  if (listenActive==false ) listenVoiceStart();   
                  		
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

     			
     			
     			if (listen_const) {
     				if (listenActive==false) listenVoiceStart();  
                  }else{
                	  MicrophonGlameView.setVisibility(View.INVISIBLE);  
                	  manualListenActive=false; 
      		      }
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
    
    private void listenVoiceStop()
	    {
		//sr.destroy();

	             sr.stopListening();//(intentVoice);

	             Log.i("Voice analize","stopListening");
	             
	            //MicrophonView.setVisibility(View.INVISIBLE);  
	            MicrophonGlameView.setVisibility(View.INVISIBLE);   
	            manualListenActive=false; 
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
        	
            
         // TODO Sprawdzić poprawność wysterowania wielu węzłów
        	
        	
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
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("pokaż")==-1) voiceAnlizeAPIOpenOrdersList.append("pokaż"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("wyświetl")==-1) voiceAnlizeAPIOpenOrdersList.append("wyświetl"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("daj")==-1) voiceAnlizeAPIOpenOrdersList.append("daj"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("widok")==-1) voiceAnlizeAPIOpenOrdersList.append("widok"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("wejdź")==-1) voiceAnlizeAPIOpenOrdersList.append("wejdź"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("prezentuj")==-1) voiceAnlizeAPIOpenOrdersList.append("prezentuj"+"\n");
	    		if (voiceAnlizeAPIOpenOrdersList.indexOf("demonstruj")==-1) voiceAnlizeAPIOpenOrdersList.append("demonstruj"+"\n");
	    		

	    		if (voiceAnlizeAPICloseOrdersList.indexOf("cofnij")==-1) voiceAnlizeAPICloseOrdersList.append("cofnij"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wróć")==-1) voiceAnlizeAPICloseOrdersList.append("wróć"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wstacz")==-1) voiceAnlizeAPICloseOrdersList.append("wstacz"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("wyjdź")==-1) voiceAnlizeAPICloseOrdersList.append("wyjdź"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("powrót")==-1) voiceAnlizeAPICloseOrdersList.append("powrót"+"\n");
	    		if (voiceAnlizeAPICloseOrdersList.indexOf("opuść")==-1) voiceAnlizeAPICloseOrdersList.append("opuść"+"\n");

	    		
	    	  }while(cursor.moveToNext());
		}
    }
    
	
	
	
}
 
	    




    
    	    
	    
    
 void   SendAction(String Signal, String Set) 
	{
	 Log.d("Voice analize", " -		Send "+Set+" to signal: "+Signal);
	

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
 
 



	@Override
	 public void onDestroy() {
	  super.onDestroy();
	  //baza.close();	
	  //timer.cancel();
	  //timerTask.cancel();
	  stopService(new Intent(MainActivity.this, myService.class));

	  Toast.makeText(this, "Zamykam eDom", Toast.LENGTH_SHORT).show();

	 }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	}
	     

	    
	    
	    
	    
	    
	    
	    
	    


	  

	

	


