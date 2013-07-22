/**
 *
 * MyListAdapter.java
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
package it.sasabz.sasabus.ui.adapter;

import it.sasabz.sasabus.data.models.DBObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 *Adapter for the autocomplete input fields in the search tab 
 *
 */
public class MyAutocompleteAdapter extends BaseAdapter implements Filterable {
	private final Context context;
	private final ArrayList<DBObject> arrayListOriginalList;
	private ArrayList<DBObject> arrayListDataList = new ArrayList<DBObject>();
	private final int layoutId;

	/**
	 * Creates a new MyAutocompleteAdapter
	 * 
	 * @param context
	 *            is the context to work with
	 * @param whereId
	 *            is the resource id where to place the string
	 * @param layoutId
	 *            is the layout id of the list_view
	 * @param list
	 *            is the list of dbobject's which are to putting in the
	 *            list_view
	 */
	public MyAutocompleteAdapter(Context context, int layoutId,
			ArrayList<DBObject> list) {
		this.context = context;
		this.arrayListOriginalList = list;
		this.layoutId = layoutId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(layoutId, null);
		}
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
//		textView.setTextColor(context.getResources().getColor(android.R.color.black));
		if (arrayListDataList != null) {
			DBObject listItem = arrayListDataList.get(position);
			if (listItem != null) {
				textView.setText(arrayListDataList.get(position).toString());
			}
		}
		return view;
	}
	
	@Override
	public int getCount() {
		if (arrayListDataList == null){
			return 0;
		}
		return arrayListDataList.size();
	}

	@Override
	public Object getItem(int position) {
		if (arrayListDataList == null || position >= arrayListDataList.size()) {
			return null;
		}
		return arrayListDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (arrayListDataList == null) {
			return -1;
		}
		return arrayListDataList.get(position).getId();
	}

	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				ArrayList<DBObject> temporary_datalist = new ArrayList<DBObject>();
				if (constraint != null) {
					Iterator<DBObject> iter = arrayListOriginalList.iterator();
					String[] constraints = constraint.toString().split(" ");
					while (iter.hasNext()) {
						DBObject object = iter.next();
						String objectString = object.toString();
						boolean match = false;
						for (int i = 0; i < constraints.length && (match || i == 0); ++i){
							match = objectString
								.toLowerCase(Locale.getDefault())
								.contains(constraints[i]
											.toString()
											.toLowerCase(Locale.getDefault()));
						}
						if (match){
							temporary_datalist.add(object);
						}
					}
				}
				filterResults.values = temporary_datalist;
				filterResults.count = temporary_datalist.size();
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint, FilterResults results) {
				try{
					if (results.values instanceof ArrayList<?>) {
						arrayListDataList = (ArrayList<DBObject>) results.values;
					}
					notifyDataSetChanged();
				} catch (Exception e) {
					Log.e("MyAutocompleteAdapter", "Error", e);
				}
			}
		};
		return myFilter;
	}
}