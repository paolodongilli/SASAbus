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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class PointingLocationActivity extends Activity{

    //this variabled are to manage the GPS-GPSListener
	private LocationManager mlocManager = null;
	private LocationListener mlocListener = null;
	
	private SensorManager sensorService = null;
	
	private Palina palina = null;
	
	private Location location = null;
	

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointer);
        Bundle extras = getIntent().getExtras();
        int palinanr = -1;
		if (extras != null) {
			palinanr = extras.getInt("palina");
		}
		if(palinanr == -1)
		{
			System.exit(-1);
		}
		palina = PalinaList.getById(palinanr);
        //creating the listener for the GPS
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (sensor != null) {
			sensorService.registerListener(mySensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_NORMAL);
			Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");

		} else {
			Log.e("Compass MainActivity", "Registerered for ORIENTATION Sensor");
			Toast.makeText(this, "ORIENTATION Sensor not found",
					Toast.LENGTH_LONG).show();
			finish();
		}
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
    	updateDist(loc);
    }

    /**
     * This method draws the pointer needle with the right angle
     * @param loc is the location recieved from the location manager on an update
     */
    private void draw_pointer(float angle_sensor)
    {
    	if(location != null)
    	{
	    	ImageView image = (ImageView)findViewById(R.id.pointing_needle);
	        image.setImageResource(R.drawable.pfeil);
	        
	        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.bus);
	        int bmpWidth = bitmap.getWidth();
	        int bmpHeight = bitmap.getHeight();
	        
	    	Matrix matrix = new Matrix();
	    	
	    	float angle = (float)getAngle(angle_sensor);
	
	        matrix.postRotate(angle);
	         
	        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	        image.setImageBitmap(resizedBitmap);
	        
    	}
    }
    
    
    private void updateDist(Location loc)
    {
    	location = loc;
    	double distance = getDistance(loc);
    	distance = 40045d * distance / 360d;
    	TextView text = (TextView)findViewById(R.id.description);
        String dist = palina.toString() + ": " + (int)(distance * 1000) + "m";
        text.setText(dist);
    }
    
    public double getDistance(Location loc)
    {
    	double latitude = Math.abs(palina.getLatitude() - loc.getLatitude());
    	double longitude = Math.abs(palina.getLongitude() - loc.getLongitude());
    	
    	double ret = Math.sqrt(latitude * latitude + longitude * longitude);
    	
    	return ret;
    }
    
    
    public double getAngle(float angle_sensor)
    {
    	double anka = palina.getLatitude() - location.getLatitude();
    	
    	double hypo = getDistance(location);
    	
    	double angle_rad = Math.sin(anka / hypo);
    	
    	double angle_deg = angle_rad * 180d / Math.PI;
    	
    	return angle_sensor + angle_deg;
    }
    
    private SensorEventListener mySensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			draw_pointer(event.values[0]);
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		
		}
	};
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
    public void onDestroy()
    {
    	mlocManager.removeUpdates(mlocListener);
    	sensorService.unregisterListener(mySensorEventListener);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
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
			case SharedMenu.MENU_SETTINGS:
			{
				Intent settings = new Intent(this, SetSettingsActivity.class);
				startActivity(settings);
				return true;
			}
		}
		return false;
	}
}
