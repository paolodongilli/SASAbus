package it.sasabz.android.sasabus;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabHostActivity extends TabActivity{
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tab_host);
	 
	        TabHost tabHost = getTabHost();
	 
	        // Tab for Home/Online
	        TabSpec homespec = tabHost.newTabSpec("Online");
	        // setting Title and Icon for the Tab
	        homespec.setIndicator("Online", getResources().getDrawable(R.drawable.online));
	        Intent homeIntent = new Intent(this, HomeActivity.class);
	        homespec.setContent(homeIntent);
	 
	        // Tab for Offline Search
	        TabSpec offlineSpec = tabHost.newTabSpec("Offline");
	        offlineSpec.setIndicator("Offline", getResources().getDrawable(R.drawable.offline));
	        Intent offlineIntent = new Intent(this, SelectBacinoActivity.class);
	        offlineSpec.setContent(offlineIntent);
	 
	        // Tab for Infos/News
	        TabSpec newsSpec = tabHost.newTabSpec("Info");
	        newsSpec.setIndicator("Info", getResources().getDrawable(R.drawable.news));
	        Intent infoIntent = new Intent(this, InfoActivity.class);
	        newsSpec.setContent(infoIntent);
	 
	        // Adding all TabSpec to TabHost
	        tabHost.addTab(homespec); // Adding online tab
	        tabHost.addTab(offlineSpec); // Adding offline tab
	        tabHost.addTab(newsSpec); // Adding info tab
	    }
}
