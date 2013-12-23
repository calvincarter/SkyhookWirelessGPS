package com.example.skyhook;

import java.io.File;

import gps.MyWPSLocationCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.skyhookwireless.wps.*;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	// Use XPS to use Wireless and GPS sensors
    XPS _xps;
    
    Button bt_startGPS;
    TextView tv_location;
    public ToggleButton tog_bt_accuracy;
    
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
        
        // Set up GPS and sign-in
        setupAuthentification();
        
        // Saves GPS map to phone, so app doesn't
        // continuously contact server for GPS map coordinates
        // This is a huge help!!!
        setupTiling();
               
        tv_location = (TextView) findViewById(R.id.textView1);
        tog_bt_accuracy = (ToggleButton) findViewById(R.id.toggleButton1);
        bt_startGPS = (Button) findViewById(R.id.button1);
        bt_startGPS.setOnClickListener(handlerButton);
        
    }
    
    // Initialize XPS class and set project API key
    private void setupAuthentification() {
    	_xps = new XPS(this);
    	_xps.setKey(_key);	
    }
    
    // The tiling mode is a mix of the network and device-centric mode. It downloads, 
    // from the server, a small portion of the database so the device can automonously 
    // determine its location, without further need to contact the server.
    private void setupTiling() {
	     // make directory to save skyhook location maps. In theory, you can save enough of them so that you do not need network access and can still determine location. Android localization always requires network access, even GPS requires network access
	     // make dir
	     File externalStorage = Environment.getExternalStorageDirectory();  // get path to sdcard
	     File skyhookCacheDirectory = new File(externalStorage.getAbsolutePath()+"/skyhook/");
	     skyhookCacheDirectory.mkdir();
		  
	     /*
	     Setup tiling so location determination can be performed locally. 
	     Tiles are typically less then 50KB in size, so to download an area of 3x3 tiles 
	     for each session you would set maxDataSizePerSession to 450KB, i.e. 460,800. 
	     It is recommended that maxDataSizePerSession be a factor of 2 - 10 smaller than 
	     maxDataSizeTotal, so that tiles from several areas can be cached.

		 Parameters:
		 dirpath - the path to a directory to store tiles.
		 maxDataSizePerSession - the maximum size of data downloaded per session, in bytes. A value of 0 disables any further tile downloads.
		 maxDataSizeTotal - the maximum size of all stored tiles, in bytes. A value of 0 causes all stored tiles to be deleted.
		 listener - the callback function to control the tile download. By default, all tiles are downloaded.
	     */

	     // set up tiling
	     int _maxDataSizePerSession = 100000000;  // a lot of data, so we should get everything we need. 
	     int _maxDataSizeTotal = _maxDataSizePerSession;
	     
	     _xps.setTiling(skyhookCacheDirectory.getAbsolutePath(), _maxDataSizePerSession, _maxDataSizeTotal,  null); 
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
        		
        			if(tog_bt_accuracy.isChecked()) {
        				_xps.getLocation(null,
                                   WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
                                   callback);
        			}
        			else {
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
            	}
        };
        
        Thread mythread = new Thread(runnable);
      	mythread.start();

    }
   
}
