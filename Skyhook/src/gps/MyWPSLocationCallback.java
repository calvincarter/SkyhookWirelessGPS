package gps;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.example.skyhook.MainActivity;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSPeriodicLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;

public class MyWPSLocationCallback implements WPSLocationCallback, WPSPeriodicLocationCallback {
    int count = 0;
    int retry = 3;	// during GPS retrieve if problem occurs. 
    				// Retry connecting this amount of times
    				// until quitting 
    
    MainActivity context;
    
	public MyWPSLocationCallback(MainActivity c) {
		this.context = c;
	}
	
	// What the application should do after it's done
    public void done()
    {
    	retry = 0;
    	
    	// restart GPS
    	context.startGPS();
    }

    // What the application should do if an error occurs
    public WPSContinuation handleError(WPSReturnCode error)
    {
    	switch (error) { 
	    	case WPS_ERROR: Log.d("skyhooktest","error Some other error occurred.");  break;
	    	case WPS_ERROR_LOCATION_CANNOT_BE_DETERMINED:  Log.d("skyhooktest","error: A location couldn't be determined.");  break;
	    	case WPS_ERROR_NO_WIFI_IN_RANGE:  Log.d("skyhooktest","error: No Wifi reference points in range.");  break;
	    	case WPS_ERROR_SERVER_UNAVAILABLE: Log.d("skyhooktest","error: The server is unavailable."); break;
	    	case WPS_ERROR_UNAUTHORIZED:  Log.d("skyhooktest","error:User authentication failed.");  break;
	    	case WPS_ERROR_WIFI_NOT_AVAILABLE:  Log.d("skyhooktest","error: No Wifi adapter was detected.");  break;
	    	case WPS_OK:  Log.d("skyhooktest","error: ok");  break;
    	}
    	
        // To retry the location call on error use WPS_CONTINUE,
        // otherwise return WPS_STOP	
    	if(retry-- > 0)
    		return WPSContinuation.WPS_CONTINUE;
    	else
    		return WPSContinuation.WPS_STOP;
    }

    // Implements the actions using the location object
    // Use this function if you only need GPS location for 1 time use
    public void handleWPSLocation(WPSLocation location) {
		count++;
		
		final double lat = location.getLatitude();
		final double lon = location.getLongitude();
		
		// Bundle the latitude and longitude into a message
		// Pass the information to handler
		// handler will display it on UI textbox
		// This technique is used so we dont freeze the UI main thread
		Message msg = context.handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putDouble("lat", lat);
		bundle.putDouble("lon", lon);
		bundle.putInt("count", count);
        msg.setData(bundle);
        context.handler.sendMessage(msg);
    }

    // Use this function if you need GPS location for multiple time use
    @Override
	public WPSContinuation handleWPSPeriodicLocation(WPSLocation location) {
		count++;
		
		final double lat = location.getLatitude();
		final double lon = location.getLongitude();
		
		// Bundle the latitude and longitude into a message
		// Pass the information to handler
		// handler will display it on UI textbox
		// This technique is used so we dont freeze the UI main thread
		Message msg = context.handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putDouble("lat", lat);
		bundle.putDouble("lon", lon);
		bundle.putInt("count", count);
        msg.setData(bundle);
        context.handler.sendMessage(msg);
        
        /*
		Other cool information we can get
        
        if (location.hasHPE())
    		Log.e("skykooktest","accuracy in meters: "+ location.getHPE());
    	else
    		Log.e("skykooktest","unknown accuracy");
    
    	Log.e("skykooktest","altitude="+ location.getAltitude()+" number of cell towers used="+ location.getNCell()+" number access pts used: "+ location.getNAP());
    	Log.e("skykooktest","speed: "+   location.getSpeed()+" bearing (degrees): "+ location.getBearing());
        
        */

        return WPSContinuation.WPS_CONTINUE;
		//return WPSContinuation.WPS_STOP;  // if we wanted to stop
	}
    
}
