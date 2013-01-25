package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.classes.hafas.services.XMLConnectionRequestList;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class TabHostActivity extends TabActivity implements OnTabChangeListener{
	 
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        Resources res = getResources();
        setContentView(R.layout.tab_host);
 
        TabHost tabHost = getTabHost();
 
        // Tab for Home/Online
        TabSpec homespec = tabHost.newTabSpec(res.getString(R.string.online));
        // setting Title and Icon for the Tab
        homespec.setIndicator(res.getString(R.string.online), getResources().getDrawable(R.drawable.online));
        Intent homeIntent = new Intent(this, OnlineActivity.class);
        homespec.setContent(homeIntent);
 
        // Tab for Offline Search
        TabSpec offlineSpec = tabHost.newTabSpec(res.getString(R.string.offline));
        offlineSpec.setIndicator(res.getString(R.string.offline), getResources().getDrawable(R.drawable.offline));
        Intent offlineIntent = new Intent(this, OfflineActivity.class);
        offlineSpec.setContent(offlineIntent);
 
        // Tab for Infos/News
        TabSpec newsSpec = tabHost.newTabSpec(res.getString(R.string.info));
        newsSpec.setIndicator(res.getString(R.string.info), getResources().getDrawable(R.drawable.news));
        Intent infoIntent = new Intent(this, InfoActivity.class);
        newsSpec.setContent(infoIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(homespec); // Adding online tab
        tabHost.addTab(offlineSpec); // Adding offline tab
        tabHost.addTab(newsSpec); // Adding info tab
        tabHost.setOnTabChangedListener(this);
        
        if(haveNetworkConnection())
        	tabHost.setCurrentTabByTag(res.getString(R.string.online));
        else
        	tabHost.setCurrentTabByTag(res.getString(R.string.offline));
    }
	 
	/**
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (this.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			//testing WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			//testing GPRS/EDGE/UMTS/HDSPA/HUSPA/LTE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	@Override
	public void onTabChanged(String tabId) {
		if(tabId.equals(getResources().getString(R.string.online)) && !haveNetworkConnection())
		{
			getTabHost().setCurrentTabByTag(getResources().getString(R.string.offline));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(true);
			builder.setMessage(R.string.no_network_connection);
			builder.setTitle(R.string.error_title);
			builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		
	}
}
