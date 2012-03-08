/**
 * 
 *
 * SelectPalinaLocationActivity.java
 * 
 * Created: 14.12.2011 19:04:53
 * 
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
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

import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.SharedMenu;
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
import android.widget.Toast;


public class SelectPalinaLocationActivity extends ListActivity{

    //this variabled are to manage the GPS-GPSListener
	private LocationManager mlocManager = null;
	private LocationListener mlocListener = null;
	
	//saves the list of busstops for this object
	private Vector <DBObject> list = null;
	

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creating the listener for the GPS
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }
    
    /**
     * this class provides simply simply the GPS-location update.
     * when the GPS performs an update, this listener is being removed
     * and the list_view where filled with the busstops which were into the 
     * given radius
     * @author Markus Windegger (markus@mowiso.com)
     *
     */
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
			Toast.makeText(SASAbus.getContext(), R.string.gps_disabled, Toast.LENGTH_LONG).show();
			gpsDisabled();
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

	}
    
    /**
     * if the GPS is disabled, then this method starts a new activity automatically
     * in the normal mode
     */
    public void gpsDisabled()
    {
    	mlocManager.removeUpdates(mlocListener);
    	Intent selBac = new Intent(SASAbus.getContext(), SelectBacinoActivity.class);
    	startActivity(selBac);
    }
    
    /**
     * this method is called when the GPS has recieved an update
     * @param loc is the location recieved with the GPS update
     */
    public void onLocationRecieve(Location loc) {
    	mlocManager.removeUpdates(mlocListener);
        setContentView(R.layout.select_palina_layout);
        fillData(loc);
    }

    /**
     * this method returns a pointer to the object itself. it is used
     * by the internal class MyLocationListener
     * @return this Activity
     */
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
    	Palina partenza = (Palina)list.get(position);
    	Intent selDest = new Intent(this, SelectDestinazioneLocationActivity.class);
    	selDest.putExtra("partenza", partenza.getName_de());
    	startActivity(selDest);
    }

    /**
     * this method fills the list_view with the possible departures when recieving the
     * location from the GPS
     * @param loc is the location with the newest position
     */
    private void fillData(Location loc) {
    	 list = PalinaList.getListGPS(loc);
         MyListAdapter paline = new MyListAdapter(SASAbus.getContext(), R.id.palina, R.layout.paline_row, list);
         mlocManager.removeUpdates(mlocListener);
         setListAdapter(paline);
     }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(...);  // specific to this activity
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case SharedMenu.MENU_ABOUT:
			{
				new About(this).show();
				return true;
			}
			case SharedMenu.MENU_CREDITS:
			{
				new Credits(this).show();
				return true;
			}	
		}
		return false;
	}
}
