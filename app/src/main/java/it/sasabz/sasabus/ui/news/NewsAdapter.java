/*
 * SASAbus - Android app for SASA bus open data
 *
 * NewsAdapter.java
 *
 * Created: Jan 3, 2014 11:29:26 AM
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

package it.sasabz.sasabus.ui.news;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.News;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<News>
{

   private Context context;
   private int     resource;

   public NewsAdapter(Context context, int resource, int textViewResourceId, List<News> objects)
   {
      super(context, resource, textViewResourceId, objects);
      this.context = context;
      this.resource = resource;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {

      View view;

      if (convertView == null)
      {
         view = LayoutInflater.from(this.context).inflate(this.resource, null);
      }
      else
      {
         view = convertView;
      }

      TextView textviewBusline = (TextView) view.findViewById(R.id.textview_busline);

      String buslines = "";
      String linesAffected = this.getItem(position).getLinesAffectedAsString();
      if (linesAffected != "")
      {
         buslines = this.context.getResources().getString(R.string.lines) + ": " + linesAffected;
      }
      else
      {
         textviewBusline.setVisibility(View.GONE);
      }

      textviewBusline.setText(buslines);

      TextView textviewNewsTitle = (TextView) view.findViewById(R.id.textview_title);
      String newsTitle = this.getItem(position).getTitle();
      textviewNewsTitle.setText(newsTitle);

      return view;

   }
}