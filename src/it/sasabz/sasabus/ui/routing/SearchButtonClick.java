/*
 * SASAbus - Android app for SASA bus open data
 *
 * SearchButtonClick.java
 *
 * Created: Feb 10, 2014 10:55:00 AM
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

import it.bz.tis.sasabus.backend.server.sasabusdb.SASAbusDBServerImpl;
import it.bz.tis.sasabus.backend.shared.SASAbusDBDataReady;
import it.bz.tis.sasabus.backend.shared.travelplanner.ConRes;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import java.text.ParseException;
import java.util.Calendar;
import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import com.actionbarsherlock.app.SherlockFragment;

public class SearchButtonClick implements OnClickListener
{
   SearchFragment searchFragment;

   BusStation     startBusStationNameItDe;
   BusStation     endBusStationNameItDe;

   public SearchButtonClick(SearchFragment searchFragment)
   {
      super();
      this.searchFragment = searchFragment;
   }

   @Override
   public void onClick(final View v)
   {

      this.startBusStationNameItDe = SearchButtonClick.this.searchFragment.autocompletetextviewDeparture.getSelectedBusStation();
      this.endBusStationNameItDe = SearchButtonClick.this.searchFragment.autoCompleteTextViewArrival.getSelectedBusStation();

      if (this.startBusStationNameItDe != null && this.endBusStationNameItDe != null)
      {
         this.searchFragment.buttonSearch.setText(v.getContext().getString(R.string.searching_connection));

         new Thread(new Runnable()
         {
            @Override
            public void run()
            {
               try
               {
                  String dep = SearchButtonClick.this.startBusStationNameItDe.getRoutingName();
                  String arr = SearchButtonClick.this.endBusStationNameItDe.getRoutingName();
                  SASAbusDBServerImpl.calcRouting(dep,
                                                  arr,
                                                  SearchButtonClick.this.readDateTimeForRoutingService(),
                                                  new SASAbusDBDataReady<ConRes>()
                                                  {
                                                     @Override
                                                     public void ready(final ConRes data0)
                                                     {
                                                        SearchButtonClick.this.onFirstRoute(data0);
                                                     }
                                                  });
               }
               catch (Exception exxx)
               {
                  SearchButtonClick.this.searchFragment.mainActivity.handleApplicationException(exxx);
               }
            }
         }).start();
      }
      else
      {
         new AlertDialog.Builder(v.getContext()).setMessage(this.searchFragment.getActivity().getString(R.string.route_missing_bus_stops)).setPositiveButton("Ok",
                                                                                                                                                             null).create().show();
      }

   }

   private void onFirstRoute(final ConRes data0)
   {
      try
      {
         SASAbusDBServerImpl.nextRouteImpl(data0.getConResCtxt()[0], new SASAbusDBDataReady<ConRes>()
         {

            @Override
            public void ready(final ConRes data1)
            {
               SearchButtonClick.this.onSecondRoute(data0, data1);

            }
         });
      }
      catch (Exception e)
      {
         this.searchFragment.mainActivity.handleApplicationException(e);
      }

   }

   private void onSecondRoute(final ConRes data0, final ConRes data1)
   {
      try
      {
         SASAbusDBServerImpl.nextRouteImpl(data1.getConResCtxt()[0], new SASAbusDBDataReady<ConRes>()
         {
            @Override
            public void ready(final ConRes data2)
            {
               SearchButtonClick.this.onThirdRoute(data0, data1, data2);
            }
         });
      }
      catch (Exception e)
      {
         this.searchFragment.mainActivity.handleApplicationException(e);
      }
   }

   private void onThirdRoute(final ConRes data0, final ConRes data1, final ConRes data2)
   {
      try
      {

         SearchResultsFragment fragmentToShow = (SearchResultsFragment) SherlockFragment.instantiate(this.searchFragment.mainActivity,
                                                                                                     SearchResultsFragment.class.getName());

         fragmentToShow.setData(new ConRes[] { data0, data1, data2 },
                                this.startBusStationNameItDe,
                                this.endBusStationNameItDe,
                                this.searchFragment.dateButton.getText().toString(),
                                this.searchFragment.timeButton.getText().toString());
         FragmentManager fragmentManager = this.searchFragment.mainActivity.getSupportFragmentManager();
         fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(null).commit();

         this.searchFragment.getActivity().runOnUiThread(new Runnable()
         {
            @Override
            public void run()
            {
               SearchButtonClick.this.searchFragment.buttonSearch.setText(SearchButtonClick.this.searchFragment.getString(R.string.search_connection));
            }
         });

      }
      catch (Exception e)
      {
         this.searchFragment.mainActivity.handleApplicationException(e);
      }

   }

   private long readDateTimeForRoutingService() throws ParseException
   {
      Calendar cal = Calendar.getInstance();
      cal.setTime(DatePicker.simpleDateFormat.parse(this.searchFragment.dateButton.getText().toString()));

      long ret = (cal.get(Calendar.YEAR) * 100L + cal.get(Calendar.MONTH) + 1)
                 * 100L
                 + cal.get(Calendar.DAY_OF_MONTH);

      cal.setTime(TimePicker.simpleDateFormat.parse(this.searchFragment.timeButton.getText().toString()));

      ret = (ret * 100L + cal.get(Calendar.HOUR_OF_DAY)) * 100L + cal.get(Calendar.MINUTE);

      return ret;
   }
}
