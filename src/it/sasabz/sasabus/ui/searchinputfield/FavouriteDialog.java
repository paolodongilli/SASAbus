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
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

public class FavouriteDialog extends Dialog
{
   Favourite[] favourites;

   public FavouriteDialog(final MainActivity mainActivity, final BusStationAdvancedInputText searchInputField)
                                                                                                              throws IOException
   {
      super(mainActivity);
      final BusStation current = searchInputField.getSelectedBusStation();

      this.setTitle(mainActivity.getString(R.string.FavouriteDialog_title));
      this.setContentView(R.layout.favourite_dialog);
      this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

      Button add = (Button) this.findViewById(R.id.add_favourite_button);
      final ListView listView = (ListView) this.findViewById(R.id.listView_favourites);

      final AndroidOpenDataLocalStorage openDataStorage = mainActivity.getOpenDataStorage();

      this.favourites = openDataStorage.getFavouriteList().getList();

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
                  Favourite[] newFavourites = new Favourite[FavouriteDialog.this.favourites.length + 1]; // Arrays.copy requires Api 9
                  for (int i = 0; i < FavouriteDialog.this.favourites.length; i++)
                  {
                     newFavourites[i] = FavouriteDialog.this.favourites[i];
                  }
                  newFavourites[FavouriteDialog.this.favourites.length] = new Favourite(current.getRoutingName());
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

      final ArrayList<String> favouriteNames = new ArrayList<String>();

      for (int i = 0; i < this.favourites.length; i++)
      {
         String name = this.favourites[i].getName();
         BusStation[] busStations = openDataStorage.getBusStations().getList();
         // Find first an exact match (new way)
         boolean found = false;
         for (BusStation busStation : busStations)
         {
            if (busStation.getRoutingName().equals(name))
            {
               favouriteNames.add(mainActivity.getBusStationNameUsingAppLanguage(busStation));
               found = true;
               break;
            }
         }
         if (!found)
         {
            // If now found search with the "old" way for "old" favourites
            for (BusStation busStation : busStations)
            {
               if (busStation.getORT_NAME().equals(name))
               {
                  favouriteNames.add(mainActivity.getBusStationNameUsingAppLanguage(busStation));
                  found = true;
                  break; // Don't add more than one!
               }
            }
         }
         if (!found) // bus station renamed?
         {
            // Add name, because otherwise the favourites length does not match list names
            favouriteNames.add(name);
         }
      }

      listView.setAdapter(new ArrayAdapter<String>(mainActivity,
                                                   android.R.layout.simple_list_item_1,
                                                   favouriteNames));

      listView.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
         {
            searchInputField.setInputTextFireChange(favouriteNames.get(position));
            FavouriteDialog.this.dismiss();
         }
      });

      listView.setOnItemLongClickListener(new OnItemLongClickListener()
      {
         @Override
         public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long arg3)
         {
            AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
            builder.setTitle(mainActivity.getString(R.string.FavouriteDialog_delete_confirm_title));
            builder.setMessage(String.format(mainActivity.getString(R.string.FavouriteDialog_delete_confirm_message),
                                             favouriteNames.get(position)));

            final AlertDialog[] alertDialog = new AlertDialog[1];

            builder.setPositiveButton(mainActivity.getString(R.string.FavouriteDialog_delete_favourite_yes),
                                      new OnClickListener()
                                      {
                                         @Override
                                         public void onClick(DialogInterface arg0, int arg1)
                                         {
                                            favouriteNames.remove(position);
                                            try
                                            {
                                               Favourite[] newFavourites = new Favourite[FavouriteDialog.this.favourites.length - 1]; // Arrays.copy requires Api 9
                                               for (int i = 0; i < newFavourites.length; i++)
                                               {
                                                  if (i < position)
                                                  {
                                                     newFavourites[i] = FavouriteDialog.this.favourites[i];
                                                  }
                                                  else
                                                  {
                                                     newFavourites[i] = FavouriteDialog.this.favourites[i + 1];
                                                  }
                                               }
                                               openDataStorage.setFavouriteList(new FavouriteList(newFavourites));
                                               FavouriteDialog.this.favourites = newFavourites;

                                               listView.setAdapter(new ArrayAdapter<String>(mainActivity,
                                                                                            android.R.layout.simple_list_item_1,
                                                                                            favouriteNames));
                                            }
                                            catch (Exception e)
                                            {
                                               mainActivity.handleApplicationException(e);
                                            }
                                            alertDialog[0].dismiss();
                                         }
                                      });
            builder.setNegativeButton(mainActivity.getString(R.string.FavouriteDialog_delete_favourite_cancel),
                                      new OnClickListener()
                                      {
                                         @Override
                                         public void onClick(DialogInterface arg0, int arg1)
                                         {
                                            alertDialog[0].dismiss();
                                         }
                                      });
            alertDialog[0] = builder.create();
            alertDialog[0].show();
            return true;
         }
      });

      if (this.favourites.length > 0)
      {
         Toast.makeText(mainActivity,
                        mainActivity.getString(R.string.FavouriteDialog_delete_toast),
                        Toast.LENGTH_LONG).show();
      }

   }
}
