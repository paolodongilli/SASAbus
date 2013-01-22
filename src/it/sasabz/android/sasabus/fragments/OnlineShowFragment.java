/**
 *
 * OnlineShowConnectionActivity.java
 *
 *
 * Copyright (C) 2012 Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package it.sasabz.android.sasabus.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import it.sasabz.android.sasabus.InfoActivity;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.id;
import it.sasabz.android.sasabus.R.layout;
import it.sasabz.android.sasabus.R.menu;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.android.sasabus.classes.adapter.MyXMLConnectionRequestAdapter;
import it.sasabz.android.sasabus.classes.dbobjects.Palina;
import it.sasabz.android.sasabus.classes.dialogs.About;
import it.sasabz.android.sasabus.classes.dialogs.ConnectionDialog;
import it.sasabz.android.sasabus.classes.dialogs.Credits;
import it.sasabz.android.sasabus.classes.hafas.XMLAttributVariante;
import it.sasabz.android.sasabus.classes.hafas.XMLConnection;
import it.sasabz.android.sasabus.classes.hafas.XMLConnectionRequest;
import it.sasabz.android.sasabus.classes.hafas.XMLJourney;
import it.sasabz.android.sasabus.classes.hafas.XMLRequest;
import it.sasabz.android.sasabus.classes.hafas.XMLStation;
import it.sasabz.android.sasabus.classes.hafas.XMLWalk;
import it.sasabz.android.sasabus.classes.hafas.services.XMLBackwardScroll;
import it.sasabz.android.sasabus.classes.hafas.services.XMLConnectionRequestList;
import it.sasabz.android.sasabus.classes.hafas.services.XMLForwardScroll;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineShowFragment extends Fragment implements OnItemClickListener{

	private XMLStation from = null;
	private XMLStation to = null;
	private Date datetime = null;
	
	private View result = null;
	private ListView listview = null;
	
	public static final int XML_FAILURE = 0;
	public static final int NO_DATA = 1;

	
	private ProgressDialog progress = null;
	
	private Vector<XMLConnectionRequest> list = null;
	
	private XMLConnectionRequestList request = null;

	private OnlineShowFragment()
	{
		
	}
	
	public OnlineShowFragment(XMLStation from, XMLStation to, String datetime) {
		this();
		this.from = from;
		this.to = to;
		SimpleDateFormat simple = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		try
		{
			this.datetime = simple.parse(datetime);
		}
		catch(Exception e)
		{
			Log.v("Datumsfehler", "Das Datum hat eine falsche Formatierung angenommen!!!");
			Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
			getFragmentManager().popBackStack();
		}
	}

	private Context getContext() {
		return this.getActivity();
	}

	
	public void onResume()
	{
		super.onResume();
		if(list != null)
			fillData(list);
	}
	
	/** Called with the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!XMLRequest.haveNetworkConnection()) {
			Toast.makeText(getContext(), R.string.no_network_connection,
					Toast.LENGTH_LONG).show();
			getFragmentManager().popBackStack();
			return null;
		}
		result = inflater.inflate(R.layout.connection_listview_layout, container, false);

		if(list == null && request == null)
		{
			progress = new ProgressDialog(getContext());
		    progress.setMessage(getResources().getText(R.string.waiting));
		    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progress.setCancelable(false);
		    progress.show();
		    
			request = new XMLConnectionRequestList(from, to, datetime, this);
			request.execute();
		}
		else
		{
			progress = null;
			fillData(list);
		}
		listview = (ListView)result.findViewById(android.R.id.list);
		listview.setOnItemClickListener(this);
		ImageButton later = (ImageButton)result.findViewById(R.id.later);
		ImageButton earlier = (ImageButton)result.findViewById(R.id.earlier);
		
		later.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progress = new ProgressDialog(getContext());
			    progress.setMessage(getResources().getText(R.string.waiting));
			    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    progress.setCancelable(false);
			    progress.show();
				XMLForwardScroll forward = new XMLForwardScroll(list, getThis());
				forward.execute();
				
			}
		});
		
		earlier.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progress = new ProgressDialog(getContext());
			    progress.setMessage(getResources().getText(R.string.waiting));
			    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    progress.setCancelable(false);
			    progress.show();
				XMLBackwardScroll backward = new XMLBackwardScroll(list, getThis());
				backward.execute();
				
			}
		});
		return result;
	}

	public OnlineShowFragment getThis()
	{
		return this;
	}
	

	public void fillData(Vector <XMLConnectionRequest> list) {
		this.list = list;

		MyXMLConnectionRequestAdapter adapter = new MyXMLConnectionRequestAdapter(list);

		listview.setAdapter(adapter);
		
		if(progress != null && progress.isShowing())
			progress.dismiss();
	}

	
	 public AlertDialog getErrorDialog(String message)
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setCancelable(false);
			builder.setMessage(message);
			builder.setTitle(R.string.error);
			builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					getFragmentManager().popBackStack();
				}
			});
			return builder.create();
	    }
	    
	    public void myShowDialog(int res)
	    {
	    	if(progress != null)
	    		progress.dismiss();
	    	switch(res)
	    	{
	    		case XML_FAILURE:
	    			getErrorDialog(getResources().getString(R.string.error_station)).show();
	    		break;
	    		case NO_DATA:
	    			getErrorDialog(getResources().getString(R.string.error_connection)).show();
	    		break;
	    	}
	    }
	

	@Override
	public void onItemClick(AdapterView <?> parent, View v, int position, long id) {
		XMLConnectionRequest conreq = list.get(position);
		if (conreq.getConnectionlist() == null) {
			Log.v("XML-LOGGER", "Die Liste der Verbindungsdetails ist null!!!!");
		}
		ConnectionDialog dial = new ConnectionDialog(this, conreq.getConnectionlist());
		dial.show();
	}
}