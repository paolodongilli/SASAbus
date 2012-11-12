/**
 *
 * InfoActivity.java
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
package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Information;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyListAdapter;
import it.sasabz.android.sasabus.classes.services.InformationList;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class InfoActivity extends ListActivity {

	private Vector<DBObject> list = null;
	private ProgressDialog progdial = null;

	public InfoActivity() {
		// TODO Auto-generated constructor stub
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.listview_infos_layout);

		TextView titel = (TextView) findViewById(R.id.titel);
		titel.setText(R.string.menu_infos);

		fillData();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton(android.R.string.ok,
				new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
		Information information = (Information) list.get(position);
		builder.setTitle(information.getTitel());
		builder.setMessage(information.getNachricht());
		builder.create().show();
	}

	/**
	 * fills the list_view with the modes which are offered to the user
	 */
	public void fillData() {
		SharedPreferences shared = PreferenceManager
				.getDefaultSharedPreferences(this);
		int infocity = Integer.parseInt(shared.getString("infos", "0"));
		
		progdial = new ProgressDialog(this);
		
		progdial.setMessage(getResources().getText(R.string.waiting));
		progdial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progdial.show();
		
		InformationList info = new InformationList(this);
		info.execute(Integer.valueOf(infocity));
	}

	public void fillList(Vector<DBObject> list)
	{
		this.list = list;
		MyListAdapter infos = new MyListAdapter(SASAbus.getContext(),
				R.id.text, R.layout.infos_row, list);
		setListAdapter(infos);
		progdial.dismiss();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.removeItem(R.id.menu_infos);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about: {
			new About(this).show();
			return true;
		}
		case R.id.menu_credits: {
			new Credits(this).show();
			return true;
		}
		case R.id.menu_settings: {
			Intent settings = new Intent(this, SetSettingsActivity.class);
			startActivity(settings);
			return true;
		}
		}
		return false;
	}
}