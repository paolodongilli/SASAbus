/*
 * SASAbus - Android app for SASA bus open data
 *
 * NavigationDrawerAdapter.java
 *
 * Created: Jan 27, 2014 10:55:00 AM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity.DrawerItem;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavigationDrawerAdapter extends ArrayAdapter<DrawerItem> {

   Context mContext;
   int mResource;

   public NavigationDrawerAdapter(Context context, int resource,
         List<DrawerItem> objects) {
      super(context, resource, objects);
      this.mContext = context;
      this.mResource = resource;
   }

   static class ViewHolder {
      final TextView textviewNavigationTitle;

      public ViewHolder(TextView textviewNavigationTitle) {
         this.textviewNavigationTitle = textviewNavigationTitle;
      }
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {

      TextView textviewNavigationTitle;

      if (convertView == null) {
         convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
         textviewNavigationTitle = (TextView) convertView.findViewById(R.id.textview_title);
         convertView.setTag(new ViewHolder(textviewNavigationTitle));
      } else {
         ViewHolder viewHolder = (ViewHolder) convertView.getTag();
         textviewNavigationTitle = viewHolder.textviewNavigationTitle;
      }

      DrawerItem drawerItem = getItem(position);
      textviewNavigationTitle.setText(drawerItem.navigationTitle);
      textviewNavigationTitle.setCompoundDrawablesWithIntrinsicBounds(drawerItem.navigationIcon, null, null, null);
      return convertView;
   }
}
