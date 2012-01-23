/**
 * 
 *
 * SelectPalinaLocationActivity.java
 * 
 * Created: 14.12.2011 19:04:53
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.android.sasabus;

import java.util.Vector;

import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class SelectPalinaLocationActivity extends ListActivity{

    
	private LocationManager mlocManager = null;
	private LocationListener mlocListener = null;
	
	private Vector <DBObject> list = null;
	
	private boolean ausgesucht = false;

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }
    
    public class MyLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location loc)
		{
			onLocationRecieve(loc);	
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			gpsDisabled();
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			if(status == LocationProvider.TEMPORARILY_UNAVAILABLE || status == LocationProvider.OUT_OF_SERVICE)
			{
				gpsDisabled();
			}
		}

	}
    
    public void gpsDisabled()
    {
    	mlocManager.removeUpdates(mlocListener);
    	if(!ausgesucht)
    	{
    		new GPSDisabled(getMe()).show();
    		Intent selBac = new Intent(getMe(), SelectBacinoActivity.class);
    		startActivity(selBac);
    	}
    }
    
    public void onLocationRecieve(Location loc) {
        setContentView(R.layout.select_palina_layout);
        fillData(loc);
    }

    public Activity getMe()
    {
    	return this;
    }
    
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	ausgesucht = true;
    	Palina partenza = (Palina)list.get(position);
    	Intent selDest = new Intent(this, SelectDestinazioneLocationActivity.class);
    	selDest.putExtra("partenza", partenza.getName_de());
    	mlocManager.removeUpdates(mlocListener);
    	startActivity(selDest);
    }

    
    private void fillData(Location loc) {
    	 list = PalinaList.getListGPS(loc);
         MyListAdapter paline = new MyListAdapter(SASAbus.getContext(), R.id.palina, R.layout.paline_row, list);
         setListAdapter(paline);
     }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
    
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
		}
		return false;
	}
}
