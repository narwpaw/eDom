package com.edom;

import java.util.ArrayList;

import com.edom.R;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MyArrayAdapter1 extends ArrayAdapter<ContentValues> {
	  private final Context context;
	  private final ArrayList<ContentValues> values;

	  public MyArrayAdapter1(Context context, ArrayList<ContentValues> carL) {
	    super(context, R.layout.rowlayout, carL);
	    this.context = context;
	    this.values = carL;
	    
	  }

	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    TextView textstateView = (TextView) rowView.findViewById(R.id.statelabel);
	    TextView textViewStan = (TextView) rowView.findViewById(R.id.textViewVal);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    ToggleButton toggleButton1 = (ToggleButton) rowView.findViewById(R.id.toggleButton1);
	    ImageButton buttonDown = (ImageButton) rowView.findViewById(R.id.buttonD);
	    ImageButton buttonUp = (ImageButton) rowView.findViewById(R.id.buttonU);
	    
	    String opis = "";
	    String stan = "";
	    String nazwa = "";
	    String wartosc = "";
	    String typ = "";
	    String akcja = "";


	   nazwa=(String) values.get(position).get("discription");
	   opis=(String) values.get(position).get("signal");
	   wartosc=(String) values.get(position).get("value");
	   stan=(String) values.get(position).get("state");
	   typ=(String) values.get(position).get("type");
	   akcja=(String) values.get(position).get("action_a");
	   
	    textView.setText(nazwa);
	    textstateView.setText(stan);

	    
	    if (opis.startsWith("r") || opis.startsWith("c") || opis.startsWith("d"))
	    {
	    	toggleButton1.setVisibility(View.INVISIBLE);  
	    	textViewStan.setText(wartosc+stan);
	    	textViewStan.setVisibility(View.VISIBLE);
	    	textstateView.setVisibility(View.INVISIBLE);
	    	buttonDown.setVisibility(View.INVISIBLE);
    		buttonUp.setVisibility(View.INVISIBLE);
	    }else{
	    	textViewStan.setText(null);
	    	//toggleButton1.setVisibility(0);  
	    	toggleButton1.setVisibility(View.VISIBLE);  
	    	if (opis.startsWith("q") || opis.startsWith("b")) {
			      //imageView.setImageResource(R.drawable.bulb_1a);
			      toggleButton1.setEnabled(true);
			    } else {
			      //imageView.setImageResource(R.drawable.ic_launcher);
			      toggleButton1.setEnabled(false);
			    }
	    	
	    	if ((typ.startsWith("rolet")) || (typ.startsWith("blind")))
	    	{
	    		
	    		buttonDown.setVisibility(View.VISIBLE);
	    		buttonUp.setVisibility(View.VISIBLE);
	    	
	    		toggleButton1.setVisibility(View.INVISIBLE);
	    		
	    		
	    		imageView.setImageResource(R.drawable.blind_half_close);
	    	}else if ((typ.startsWith("oswietlenie")) || (typ.startsWith("light")))
	    	{
	    		imageView.setImageResource(R.drawable.bulb_1a);
	    		
	    		buttonDown.setVisibility(View.INVISIBLE);
	    		buttonUp.setVisibility(View.INVISIBLE);
	    		
	    		toggleButton1.setVisibility(View.VISIBLE);
	    		
	    	}
	    	else{
	    		imageView.setImageResource(R.drawable.ic_launcher);
	    		buttonDown.setVisibility(View.INVISIBLE);
	    		buttonUp.setVisibility(View.INVISIBLE);
	    		if (akcja!="") toggleButton1.setVisibility(View.VISIBLE);
	    	}
	    	
	    	
				
	    	if (wartosc.startsWith("0"))
			    {
			    	toggleButton1.setChecked(false);
			    	//textstateView.setText("W³¹czony");
			    }else{
			    	toggleButton1.setChecked(true);
			    	//textstateView.setText("Wy³¹czony");
			    }
			   
	    }
	    

	    return rowView;
	  }
	} 


