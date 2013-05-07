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
package it.sasabz.android.sasabus.classes.dialogs;

import it.sasabz.android.sasabus.MapSelectActivity;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.dbobjects.Palina;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SelectDialog extends Dialog{

	private MapSelectActivity activity = null;
	
	private Palina palina = null;
	
	public SelectDialog(MapSelectActivity activity, Palina palina) {
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
