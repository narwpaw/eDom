package com.edom;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.RingtonePreference;
import android.view.Menu;
import android.view.MenuItem;



public class MyPreferences extends PreferenceActivity {
    private SharedPreferences preferences;
    public final static String MY_PREFS = "myPrefs";
    
    
    EditTextPreference website;
    EditTextPreference controller_id;
    EditTextPreference activate;
    
    CheckBoxPreference listen_const;
    CheckBoxPreference speaker;
    int startTrialDay;

   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        //Odpowiednik setContentView();
        
        addPreferencesFromResource(R.xml.preferences);
        
        

        
        //Pobierz SharedPreferences
        preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
    
        //Pobierz referencje do pól preferencji z pliku xml (po wartoœci key)
        
        website = (EditTextPreference)findPreference("website");
        controller_id= (EditTextPreference)findPreference("controller_id"); 
        
        listen_const = (CheckBoxPreference)findPreference("listen_const");
        speaker = (CheckBoxPreference)findPreference("speaker");

        activate= (EditTextPreference)findPreference("activeCode");  
        


        
        
        
        
        //final Preference chimeMaster = findPreference("ChimeMaster");
        website.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
                final String value = (String) newVal;
                website.setSummary(value);
                return true;
            }
        });
            
            

        controller_id.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newVal) {
                    final String value = (String) newVal;
                    controller_id.setSummary(value);
                    return true;
                }   
        });     
        

        activate.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newVal) {
            	final String value = (String) newVal;
            	activate.setText(value);
            	setPrefereces();
  
                ActivateInfo();
                return true;
            }   
        });    
        
        
        //Pobierz wartoœci preferencji
        getPreferences();
        
        
        

    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_zamknij, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        String ktoryElement = "";
 
        switch (item.getItemId()) {
 
        case R.id.menu_zamknij:
        	this.finish();
            break;
        }

        return true;
    }
	
    

    @Override
    protected void onPause() {
        //Zapisz preferencje
        setPrefereces();
        super.onStop();
    }
    
    

    
    private void getPreferences() {
        
    	preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
        website.setText(preferences.getString("website", ""));
        website.setSummary(preferences.getString("website", ""));
        controller_id.setText(preferences.getString("controller_id", ""));
        
        String HideId="";
        for (char i=0; i<preferences.getString("controller_id", "").length(); i++)
        	{
        	 HideId=HideId+"*";
        	}
        	
        controller_id.setSummary(HideId);
        
        listen_const.setChecked(preferences.getBoolean("listen_const", false));
        speaker.setChecked(preferences.getBoolean("speaker", true));
        
        
        
        ActivateInfo();
        

    }
    
    
    
    private void ActivateInfo()
    {
    	preferences = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
    	
        String ActiveCode=preferences.getString("activeCode", "");
        String ID_Read="";
        
        if (ActiveCode.length()==18) 
        	{
        	 ID_Read=MainActivity.DecodeActivationCode(ActiveCode);//preferences.getString("activeCode", ""));
        	}
        String ID_Controller=preferences.getString("controller_id", "");
        
        
        //activate.setSummary(ActiveCode+"  d³:"+ActiveCode.length()+" Id"+ ID_Read);
        
        
        if ( (ID_Controller.length()!=7) || (ID_Read.equals(ID_Controller)==false))
        {
	        //wyliczenie iloœci dni do koñca aktywacji
	        Date now = new Date();
			long t = now.getTime();
			int curTime=(int) (t/1000/60/60/24);
	        
	        
	        startTrialDay =preferences.getInt("startDay", 0);
	        
	        int TimeLeft=curTime-startTrialDay;
	        if (TimeLeft>40)
	        {
	        	TimeLeft=0;
	        }else{
	        	TimeLeft=40-TimeLeft;
	        }
	        
	        activate.setTitle("Wpisz kod aktywacyjny");
	        activate.setSummary("Pozosta³o "+Integer.toString(TimeLeft) +" dni");
        }else{
        	activate.setTitle("Aktywny");
        	activate.setSummary("eDom zosta³ aktywowany.");
        	
        	activate.setEnabled(false);
        }
        //------------------------------------------------------------------------

    }
    
    
    
    
    private void setPrefereces() {
        SharedPreferences.Editor editor = preferences.edit();
        
        
        if (website.getText().contains("http://"))
        {
        	editor.putString("website", website.getText());
        }else{
        	editor.putString("website","http://"+website.getText());
        }
        
        editor.putString("controller_id", controller_id.getText());
        

        editor.putString("activeCode", activate.getText());
        
        
        
        editor.putBoolean("listen_const", listen_const.isChecked());
        editor.putBoolean("speaker", speaker.isChecked());
        
        
        
        //editor.putString("list", list.getValue());
        editor.commit();
        
        //getPreferences();
    }
}
