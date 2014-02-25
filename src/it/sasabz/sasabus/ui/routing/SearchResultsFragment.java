/*
 * SASAbus - Android app for SASA bus open data
 *
 * SearchResultsFragment.java
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

package it.sasabz.sasabus.ui.routing;

import it.bz.tis.sasabus.backend.shared.travelplanner.ConRes;
import it.bz.tis.sasabus.backend.shared.travelplanner.Connection;
import it.sasabz.sasabus.R;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SearchResultsFragment extends SherlockFragment
{

   private TextView           textviewDeparture;
   private TextView           textviewArrival;
   private ExpandableListView expandablelistviewResults;

   ConRes[]                   data;

   BusStation                 startBusName;
   BusStation                 endBusName;
   String                     startDate;
   String                     startTime;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      View view = inflater.inflate(R.layout.fragment_search_results, container, false);

      this.textviewDeparture = (TextView) view.findViewById(R.id.textview_departure);
      this.textviewArrival = (TextView) view.findViewById(R.id.textview_arrival);

      this.textviewDeparture.setText("");
      this.textviewArrival.setText("");

      this.expandablelistviewResults = (ExpandableListView) view.findViewById(R.id.expandablelistview_results);

      Connection[] connections = new Connection[this.data.length];
      for (int i = 0; i < connections.length; i++)
      {
         connections[i] = this.data[i].getConnectionList().getConnections()[0];
      }

      this.expandablelistviewResults.setAdapter(new RoutingResultAdapter(connections, this.getActivity()));

      this.expandablelistviewResults.setVisibility(View.VISIBLE);

      this.textviewDeparture.setText(this.startBusName.getORT_NAME());
      this.textviewArrival.setText(this.endBusName.getORT_NAME());

      TextView date = (TextView) view.findViewById(R.id.textview_date);
      date.setText(this.startDate);

      TextView time = (TextView) view.findViewById(R.id.textview_time);
      time.setText(this.startTime);

      return view;
   }

   public void setData(ConRes[] data,
                       BusStation startBusStationNameItDe,
                       BusStation endBusStationNameItDe,
                       String startDate,
                       String startTime)
   {
      this.data = data;
      this.startBusName = startBusStationNameItDe;
      this.endBusName = endBusStationNameItDe;
      this.startDate = startDate;
      this.startTime = startTime;
   }

}