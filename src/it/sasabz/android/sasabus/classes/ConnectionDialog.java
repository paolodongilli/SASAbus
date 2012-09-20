/**
 *
 * About.java
 * 
 * Created: Jun 21, 2011 12:56:09 AM
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
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.string;
import it.sasabz.android.sasabus.hafas.XMLConnection;

import java.util.Calendar;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
	
	private void fillData()
	{
		MyXMLConnectionAdapter adapter = new MyXMLConnectionAdapter(list);
		ListView listv = (ListView)findViewById(android.R.id.list);
		listv.setAdapter(adapter);
	}
	
	

	

}
