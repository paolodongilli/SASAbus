/*
 * SASAbus - Android app for SASA bus open data
 *
 * ParkingsFragment.java
 *
 * Created: May 14, 2014 19:24:00 PM
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
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;

public class ParkingsFragment extends SherlockFragment
{

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      final MainActivity mainActivity = (MainActivity) this.getActivity();
      LinearLayout ll = new LinearLayout(mainActivity);
      ll.setBackgroundColor(Color.WHITE);

      final ListView listView = new ListView(mainActivity);
      final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity,
                                                                    android.R.layout.simple_list_item_1);

      adapter.add(mainActivity.getString(R.string.searching_connection));
      listView.setAdapter(adapter);
      ll.addView(listView);

      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               String ids = IOUtils.toString(new URL("http://ipchannels.integreen-life.bz.it/parkingFrontEnd/rest/getParkingIds"));
               JSONArray jsonArray = new JSONArray(ids);
               int len = jsonArray.length();

               final ParkingAdapter adapter = new ParkingAdapter(mainActivity);

               adapter.clear();

               for (int i = 0; i < len; i++)
               {

                  final ParkingData parkingData = new ParkingData();

                  int pid = jsonArray.getInt(i);

                  String infos = IOUtils.toString(new URL("http://ipchannels.integreen-life.bz.it/parkingFrontEnd/rest/getParkingStation?identifier="
                                                          + pid));

                  JSONObject jsonObject = new JSONObject(infos);

                  parkingData.name = jsonObject.getString("name");
                  //String parkingAddress = jsonObject.getString("address");
                  parkingData.tot = Integer.parseInt(jsonObject.getString("slots"));

                  parkingData.free = Integer.parseInt(IOUtils.toString(new URL("http://ipchannels.integreen-life.bz.it/parkingFrontEnd/rest/getNumberOfFreeSlots?identifier="
                                                                               + pid)));

                  adapter.add(parkingData);

               }

               mainActivity.runOnUiThread(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     listView.setAdapter(adapter);
                  }
               });
            }
            catch (Exception e)
            {
               mainActivity.handleApplicationException(e);
            }

         }
      }).start();

      return ll;

   }
}
