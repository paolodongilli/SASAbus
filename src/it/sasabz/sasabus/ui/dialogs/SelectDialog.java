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
package it.sasabz.sasabus.ui.dialogs;

import it.sasabz.android.sasabus.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.sasabus.data.hafas.XMLConnection;
import it.sasabz.sasabus.data.hafas.XMLJourney;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.ui.adapter.MyXMLConnectionAdapter;
import it.sasabz.sasabus.ui.fragments.OrarioFragment;
import it.sasabz.sasabus.ui.fragments.WayFragment;
import it.sasabz.sasabus.ui.map.MapSelectActivity;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class SelectDialog extends Dialog{

	private MapSelectActivity activity = null;
	
	private BusStop palina = null;
	
	public SelectDialog(MapSelectActivity activity, BusStop palina) {
		super(activity);
		this.activity = activity;
		this.palina = palina;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.select_dialog_layout);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		setTitle(R.string.select_from_to);
		TextView busstop = (TextView)findViewById(R.id.busstop);
		busstop.setText(palina.toString());
		
		Button from_select = (Button)findViewById(R.id.select_from);
		from_select.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.setFrom(palina.toString());
				dismiss();
			}
		});
		
		Button to_select = (Button)findViewById(R.id.select_to);
		to_select.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.setTo(palina.toString());
				dismiss();
			}
		});
	}
	
	public Context getThis()
	{
		return this.getContext();
	}

}
