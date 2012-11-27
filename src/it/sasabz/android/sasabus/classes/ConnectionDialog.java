/**
 *
 * ConnectionDialog.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.android.sasabus.ShowWayActivity;
import it.sasabz.android.sasabus.hafas.XMLConnection;
import it.sasabz.android.sasabus.hafas.XMLJourney;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class ConnectionDialog extends Dialog{

	private Vector<XMLConnection> list = null;
	
	public ConnectionDialog(Context context, Vector<XMLConnection> list) {
		super(context);
		this.list = list;
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.transfer_listview_layout);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		setTitle(R.string.connection_details);
		fillData();
	}
	
	public Context getThis()
	{
		return this.getContext();
	}
	
	private void fillData()
	{
		MyXMLConnectionAdapter adapter = new MyXMLConnectionAdapter(list);
		ListView listv = (ListView)findViewById(android.R.id.list);
		listv.setAdapter(adapter);
		listv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				XMLConnection conn = (XMLConnection)list.get(position);
				if(conn instanceof XMLJourney)
				{
					SimpleDateFormat simple = new SimpleDateFormat("HH:mm");
					Intent intent = new Intent(getThis(), ShowWayActivity.class);
					intent.putExtra("line", ((XMLJourney)conn).getAttribut("NUMBER"));
					String fromtext = "(" + conn.getDeparture().getStation().getHaltestelle().replace(" -", ")");
					String totext = "(" + conn.getArrival().getStation().getHaltestelle().replace(" -", ")");
					intent.putExtra("partenza", fromtext);
					intent.putExtra("destinazione", totext);
					intent.putExtra("orario_des", simple.format(conn.getDeparture().getArrtime()));
					intent.putExtra("orario_arr", simple.format(conn.getArrival().getArrtime()));
					getThis().startActivity(intent);
				}
			}
		});
	}
	
	

	

}
