/**
 *
 * SelectDestinazioneActivity.java
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

package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.Area;
import it.sasabz.sasabus.data.models.BusLine;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.orm.BusStopList;
import it.sasabz.sasabus.ui.SASAbus;
import it.sasabz.sasabus.ui.adapter.MyListAdapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class DepartureFragment extends Fragment implements OnItemClickListener{

    
    private ArrayList<DBObject> list = null;
    
    private BusLine linea;
    
    private Area bacino = null;
    
	private DepartureFragment() {
    	
    }
    
    public DepartureFragment(Area bacino, BusLine linea)
    {
    	this();
    	this.linea = linea;
    	this.bacino = bacino;
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View result = inflater.inflate(R.layout.palina_listview_layout, container, false);
    	TextView titel = (TextView)result.findViewById(R.id.untertitel);
        titel.setText(R.string.select_palina);
        
        Resources res = getResources();
        
        TextView lineat = (TextView)result.findViewById(R.id.line);
        TextView from = (TextView)result.findViewById(R.id.from);
        TextView to = (TextView)result.findViewById(R.id.to);
        
        lineat.setText(res.getString(R.string.line_txt) + " " + linea.toString());
        from.setText("");
        to.setText("");
        
        fillData(result);
    	return result;
    }


    /**
     * this method gets a list of palinas and fills the list_view with the palinas
     */
    private void fillData(View result) {
    	list = BusStopList.getBusLineList(linea.getId(), bacino.getTable_prefix());
    	MyListAdapter destinazioni = new MyListAdapter(SASAbus.getContext(), R.id.text, R.layout.departure_row, list);
    	ListView listview = (ListView)result.findViewById(android.R.id.list);
    	listview.setAdapter(destinazioni);
    	listview.setOnItemClickListener(this);
    }
    
   
	@Override
	public void onItemClick(AdapterView<?> av, View v, int position, long id) {
    	BusStop departure = (BusStop)list.get(position);
    	
    	/*Intent selDest = new Intent(this, SelectArrivalActivity.class);
    	selDest.putExtra("arrival", arrival.getName_de());
    	selDest.putExtra("bacino", bacino.getId());
    	selDest.putExtra("linea", linea);
    	startActivity(selDest);*/
    	FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		
		Fragment fragment = fragmentManager.findFragmentById(R.id.onlinefragment);
		if(fragment != null)
		{
			ft.remove(fragment);
		}
		fragment = new ArrivalFragment(bacino, linea, departure);
		ft.add(R.id.onlinefragment, fragment);
		ft.addToBackStack(null);
		ft.commit();
		fragmentManager.executePendingTransactions();
	    	
	}
}
