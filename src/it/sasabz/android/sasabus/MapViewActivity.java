/**
 *
 * SelectPalinaActivity.java
 * 
 * Created: Jan 16, 2011 11:41:06 AM
 * 
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.impl.conn.DefaultClientConnection;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Linea;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.MyItemizedOverlay;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewActivity extends MapActivity {

	//saves the partenza globally for this object
    private int partenza;
    
    //saves the destination globally for this object
    private int destinazione;
    
    private int line;

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    
    public MapViewActivity() {
    	super();
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
		partenza = 0;
		destinazione = 0;
		line = 0;
		if (extras != null) {
			partenza = extras.getInt("partenza");
			destinazione = extras.getInt("destinazione");
			line = extras.getInt("line");
		}
		
		Palina destination = PalinaList.getById(destinazione);
		Palina part = PalinaList.getById(partenza);
		Linea linea = LineaList.getById(line);
		if(destination == null || part == null || linea == null)
		{
			Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG);
			super.finish();
		}
		else
		{
	        setContentView(R.layout.standard_mapview_layout);
	        
			TextView titel = (TextView)findViewById(R.id.titel);
			titel.setText(R.string.show_way);
			
			Resources res = getResources();
			
			TextView lineat = (TextView)findViewById(R.id.line);
	        TextView from = (TextView)findViewById(R.id.from);
	        TextView to = (TextView)findViewById(R.id.to);
	        
	        MapView map = (MapView)findViewById(R.id.mapview);
	        
	        List<Overlay> mapOverlays = map.getOverlays();
	        Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
	        MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable,this);
	    	
	        GeoPoint point = new GeoPoint((int)(destination.getLatitude() * 1000000),(int)(destination.getLongitude() * 1000000));
	        OverlayItem overlayitem = new OverlayItem(point, res.getString(R.string.to), destination.toString());
	
	        GeoPoint point2 = new GeoPoint((int)(part.getLatitude() * 1000000),(int)(part.getLongitude() * 1000000));
	        OverlayItem overlayitem2 = new OverlayItem(point2, res.getString(R.string.from), part.toString());
	
	        itemizedoverlay.addOverlay(overlayitem);
	        itemizedoverlay.addOverlay(overlayitem2);
	
	        lineat.setText(res.getString(R.string.line) + " " + linea.toString());
	        from.setText(res.getString(R.string.from) + " " + part.toString());
	        to.setText(res.getString(R.string.to) + " " + destination.toString());
	        
	        mapOverlays.add(itemizedoverlay);
	        //map.setBuiltInZoomControls(true);
		}
    }

    
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.optionmenu, menu);
         return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
			{
				new About(this).show();
				return true;
			}
			case R.id.menu_credits:
			{
				new Credits(this).show();
				return true;
			}	
			case R.id.menu_settings:
			{
				Intent settings = new Intent(this, SetSettingsActivity.class);
				startActivity(settings);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
