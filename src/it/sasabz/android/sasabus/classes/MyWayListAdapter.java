/**
 * 
 *
 * MyListAdapter.java
 * 
 * Created: 17.12.2011 15:10:39
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
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
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.SASAbus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class MyWayListAdapter extends BaseAdapter {
	private final Context context;
	private final Vector<Passaggio> list;
	private final int layoutId;
	private final int[] whereIdList;

	
	/**
	 * This constructor creates an object with the following parameters
	 * @param context is the context to work with
	 * @param whereId is the resource id where to place the string
	 * @param layoutId is the layout id of the list_view
	 * @param list is the list of dbobject's which are to putting in the list_view
	 */
	public MyWayListAdapter(Context context, int[] whereIdList, int layoutId, Vector<Passaggio> list) {
		this.context = context;
		this.list = list;
		this.layoutId = layoutId;
		this.whereIdList = whereIdList;
	}

	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) 
		{
              LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              v = vi.inflate(layoutId, null);
		}
		if (list != null)
		{
			TextView textViewPalina = (TextView) v.findViewById(whereIdList[0]);
			TextView textViewOrario = (TextView) v.findViewById(whereIdList[1]);
			Time currentTime = new Time();
			currentTime.setToNow();
			Time sasaTime = list.get(position).getOrario();
			textViewOrario.setText(list.get(position).getOrario().format("%H:%M"));
			textViewPalina.setText(PalinaList.getById(list.get(position).getIdPalina()).toString());
			if (sasaTime.after(currentTime))
			{
				v.setBackgroundColor(Color.rgb(0, 70, 0));
			}
			else if (sasaTime.before(currentTime))
			{
				v.setBackgroundColor(Color.rgb(70, 0, 0));
			}
			else
			{
				v.setBackgroundColor(Color.rgb(255, 125, 33));
			}
		}
		
		return v;
	}

	@Override
	public int getCount() {
		if(list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		if(list == null)
			return null;
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (list == null)
			return -1;
		return list.get(position).getId();
	}

}
