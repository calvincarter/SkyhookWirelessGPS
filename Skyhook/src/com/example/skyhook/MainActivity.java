package com.example.skyhook;

import gps.MyWPSLocationCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.skyhookwireless.wps.*;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	// Use XPS to use Wireless and GPS sensors
    XPS _xps;
    
    Button bt_startGPS;
    TextView tv_location;
    
    // My project API Key
    private String _key = "eJwz5DQ0AANDM2POaldzCxdTQ1MTXTNDM1NdE3MnY10LAyMLXSdjRwtXM2cTS2MT01oACvsK2w";

    // Use handler to update UI textbox. There is a thread that passes lon and lat to handler
    public Handler handler = new Handler() {
		  @Override
		  public void handleMessage(Message msg) { 
			// Retrieve the information that's passed from MYWPSLocationCallback class
			Bundle bundle = msg.getData();
			Double lat = bundle.getDouble("lat");
			Double lon = bundle.getDouble("lon");
			Integer count = bundle.getInt("count");
	
			tv_location.setText(count + " Latitude: " + lat + ", Longitude: " + lon);
		  }
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        
        
        tv_location = (TextView) findViewById(R.id.textView1);
        bt_startGPS = (Button) findViewById(R.id.button1);
        bt_startGPS.setOnClickListener(handlerButton);
        
    }
    
    // Initialize XPS class and set project API key
    public void setup() {
    	_xps = new XPS(this);
    	_xps.setKey(_key);	
    }
    
    // When user clicks on button, start to get GPS locations
    View.OnClickListener handlerButton = new View.OnClickListener()    {    
    	public void onClick(View v) {
    		startGPS();
    	}
    };
    
    @Override
    public void onPause()
    {
        super.onPause();
        // make sure WPS is stopped
        _xps.abort();
    }
    
    public void startGPS() {
    	final MyWPSLocationCallback callback = new MyWPSLocationCallback(this);    		
        Runnable runnable = new Runnable() {

        	public void run() {
        			/* 
        			Use "_xps.getLocation" if you only want GPS location 1 time
        			there is a function called: handleWPSLocation(WPSLocation location)
        			that gets GPS location coordinates
        			
        			
        			Recommend this function only when your experiencing issues with GPS updates
        			Note: be sure to comment out "_xps.getPeriodicLocation"
        			*/
        		
            		//_xps.getLocation(null,
                    //               WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
                    //               callback);
					
        		
    	            /*
    	             * Use "_xps.getPeriodicLocation" function below for multiple GPS requests 
    	            1. getPeriodicLocation parameters
    	            2. authentification - use null if your using api key
    	            3. streetAddressLookup - request street address lookup in addition to latitude/longitude lookup.
    	            4. period - maximum time in milliseconds between location reports. Note: has to be 5000 or more in order to work. WPS may report location updates more often than the specified period if a new or better location becomes available within a given period.
    	            5. iterations - number of time a location is to be reported. A value of zero indicates an unlimited number of iterations.
    	            6. callback - a WPSPeriodicLocationCallback
    	            
    	            */
            		
    	             _xps.getPeriodicLocation(null,
                           WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
                           5000, 0,callback); 
            	}
        };
        
        Thread mythread = new Thread(runnable);
      	mythread.start();

    }
   
}
