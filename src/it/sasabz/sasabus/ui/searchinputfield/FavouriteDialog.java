/*
 * SASAbus - Android app for SASA bus open data
 *
 * FavouriteDialog.java
 *
 * Created: Feb 24, 2014 11:00:00 AM
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

package it.sasabz.sasabus.ui.searchinputfield;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.Favourite;
import it.sasabz.sasabus.opendata.client.model.FavouriteList;
import it.sasabz.sasabus.ui.MainActivity;
import java.io.IOException;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class FavouriteDialog extends Dialog
{

   public FavouriteDialog(final MainActivity mainActivity, final BusStationAdvancedInputText searchInputField)
                                                                                                              throws IOException
   {
      super(mainActivity);
      final BusStation current = searchInputField.getSelectedBusStation();

      this.setTitle("Favourites");
      this.setContentView(R.layout.favourite_dialog);
      this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

      Button add = (Button) this.findViewById(R.id.add_favourite_button);
      ListView listView = (ListView) this.findViewById(R.id.listView_favourites);

      final AndroidOpenDataLocalStorage openDataStorage = mainActivity.getOpenDataStorage();

      final Favourite[] favourites = openDataStorage.getFavouriteList().getList();

      if (current != null)
      {
         String name = mainActivity.getBusStationNameUsingAppLanguage(current);
         add.setEnabled(true);
         add.setText(String.format(this.getContext().getString(R.string.FavouriteDialog_add_favourite), name));
         add.setOnClickListener(new View.OnClickListener()
         {
            @Override
            public void onClick(View v)
            {
               try
               {
                  Favourite[] newFavourites = new Favourite[favourites.length + 1]; // Arrays.copy requires Api 9
                  for (int i = 0; i < favourites.length; i++)
                  {
                     newFavourites[i] = favourites[i];
                  }
                  newFavourites[favourites.length] = new Favourite(current.getORT_NAME());
                  openDataStorage.setFavouriteList(new FavouriteList(newFavourites));
                  FavouriteDialog.this.dismiss();
               }
               catch (Exception e)
               {
                  mainActivity.handleApplicationException(e);
               }
            }
         });
      }
      else
      {
         add.setEnabled(false);
         add.setOnClickListener(null);
      }

      final ArrayAdapter<String> favouriteNames = new ArrayAdapter<String>(mainActivity,
                                                                           android.R.layout.simple_list_item_1);

      for (int i = 0; i < favourites.length; i++)
      {
         String name = favourites[i].getName();
         for (BusStation busStation : openDataStorage.getBusStations().getList())
         {
            if (busStation.getORT_NAME().equals(name))
            {
               favouriteNames.add(mainActivity.getBusStationNameUsingAppLanguage(busStation));
            }
         }
      }

      listView.setAdapter(favouriteNames);

      listView.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
         {
            searchInputField.setInputTextFireChange(favouriteNames.getItem(position));
            FavouriteDialog.this.dismiss();
         }
      });

   }
}
