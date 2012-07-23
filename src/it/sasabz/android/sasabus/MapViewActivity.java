/**
 *
 * ShowWayActivity.java
 * 
 * Created: Mar 15, 2012 22:40:06 PM
 * 
 * Copyright (C) 2012 Paolo Dongilli and Markus Windegger
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



import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.Linea;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.Palina;
import it.sasabz.android.sasabus.classes.PalinaList;
import it.sasabz.android.sasabus.classes.Passaggio;
import it.sasabz.android.sasabus.classes.PassaggioList;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewActivity extends MapActivity {


	
	//provides the linea for this object
	private int partenza = -1;

	//provides the destination for this object
	private int destinazione = -1;
	
	//provides the lineaid for this object
	private int linea = -1;
	
	//provides the orarioId for this object
	private int orarioId = -1;
	
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		partenza = 0;
		destinazione = 0;
		if (extras != null) {
			partenza = extras.getInt("partenza");
			destinazione = extras.getInt("destinazione");
			linea = extras.getInt("line");
			orarioId = extras.getInt("orarioId");
		}

		Palina part = PalinaList.getById(partenza);
		part.setId(partenza);
		Palina dest = PalinaList.getById(destinazione);
		dest.setId(destinazione);
		
		Linea line = LineaList.getById(linea);
		
		Resources res = getResources();
		
		Passaggio pas = PassaggioList.getById(orarioId);
		
		if (part == null || dest == null || line == null || pas == null)
		{
			Toast.makeText(this, res.getString(R.string.error_application), Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		setContentView(R.layout.standard_mapview_layout);
		TextView titel = (TextView)findViewById(R.id.titel);
		titel.setText(R.string.map);
		
		
		TextView lineat = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        if(lineat == null || from == null || to == null)
        {
        	Toast.makeText(this, R.string.error_application, Toast.LENGTH_LONG).show();
        	finish();
        	return;
        }
        
        lineat.setText(res.getString(R.string.line) + " " + line.toString());
        from.setText(res.getString(R.string.from) + " " + part.toString());
        to.setText(res.getString(R.string.to) + " " + dest.toString());

        
        MapView mapView = (MapView)findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMapFile(new File(Environment.getExternalStorageDirectory() , res.getString(R.string.db_dir) + "/" + res.getString(R.string.app_name_osm) + ".map"));

		GeoPoint partPoint = new GeoPoint(part.getLatitude(), part.getLongitude());
		GeoPoint destPoint = new GeoPoint(dest.getLatitude(), dest.getLongitude());
		
		
		
		Drawable bus = getResources().getDrawable(R.drawable.busstop);
		
		OverlayItem partOverlay = new OverlayItem(partPoint,res.getString(R.string.from), part.toString(), bus);
		OverlayItem destOverlay = new OverlayItem(destPoint,res.getString(R.string.to), dest.toString(), bus);
		
		ArrayItemizedOverlay arr = new ArrayItemizedOverlay(bus);
		
		arr.addItem(partOverlay);
		arr.addItem(destOverlay);
		
		mapView.getOverlays().add(arr);
		

		Vector<Passaggio> paslist = PassaggioList.getVectorWay(orarioId, dest.getName_de());
		
		Iterator<Passaggio> iter = paslist.iterator();
		
		Drawable inter = getResources().getDrawable(R.drawable.intermediate_stop);
		ArrayItemizedOverlay intermediate = new ArrayItemizedOverlay(inter);
		
		
		while(iter.hasNext())
		{
			Passaggio passa = iter.next();
			Palina pal = PalinaList.getById(passa.getIdPalina());
			pal.setId(passa.getIdPalina());
			if(pal.getId() != dest.getId() && pal.getId() != part.getId())
			{
				GeoPoint point = new GeoPoint(pal.getLatitude(), pal.getLongitude());
				OverlayItem overlay = new OverlayItem(point,res.getString(R.string.intermediate), pal.toString(), inter);
				intermediate.addItem(overlay);
			}
		}
		
		mapView.getOverlays().add(intermediate);

		mapView.setCenter(partPoint);
		
		mapView.getController().setZoom(14);
	
	
		
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
	
	
}
