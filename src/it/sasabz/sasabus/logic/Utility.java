package it.sasabz.sasabus.logic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class that contains some useful methods, which could be used everywhere 
 */
public class Utility {

	/**
	 * checks if a network connection is available or not
	 * 
	 * @return boolean if the network is reachable or not
	 */
	public static boolean hasNetworkConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (context
				.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo)
		{
			// testing WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			// testing GPRS/EDGE/UMTS/HDSPA/HUSPA/LTE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
		
}