/*
 * SASAbus - Android app for SASA bus open data
 *
 * SearchInputField.java
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

package it.sasabz.sasabus.ui.searchinputfield;

import it.sasabz.sasabus.R;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.ui.MainActivity;

import java.io.IOException;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class BusStationAdvancedInputText extends RelativeLayout
{

   private ViewGroup            viewgroupInputfield;
   private AutoCompleteTextView autocompleteTextView;

   private ViewGroup            viewgroupMore;
   private ImageButton          imageButtonMore;

   private ViewGroup            viewgroupButtons;
   private ImageButton          imagebuttonNearby;
   private ImageButton          imagebuttonMap;
   private ImageButton          imagebuttonFavorites;

   MainActivity                 mainActivity;

   BusStation[]                 busStations;

   Runnable                     listener;

   boolean                      toolButtonsOpen;

   public BusStationAdvancedInputText(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      this.mainActivity = (MainActivity) context;
      inflate(context, R.layout.search_inputfield, this);

      this.initializeViews();
      this.init(context, attrs);

   }

   private void init(Context context, AttributeSet attrs)
   {
      TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                                                                   R.styleable.BusStationAdvancedInputText,
                                                                   0,
                                                                   0);
      try
      {
         this.autocompleteTextView.setHint(array.getString(R.styleable.BusStationAdvancedInputText_android_hint));
      }
      finally
      {
         array.recycle();
      }
   }

   private void initializeViews()
   {

      this.viewgroupInputfield = (ViewGroup) this.findViewById(R.id.linearlayout_autocompletetextview);
      this.autocompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.autocompletetextview_busstop);

      this.viewgroupMore = (ViewGroup) this.findViewById(R.id.linearlayout_more);
      this.imageButtonMore = (ImageButton) this.viewgroupMore.findViewById(R.id.imagebutton_more);

      this.imageButtonMore.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            if (BusStationAdvancedInputText.this.toolButtonsOpen)
            {
               BusStationAdvancedInputText.this.closeSearchButtons();
            }
            else
            {
               BusStationAdvancedInputText.this.openSearchButtons();
            }
         }
      });

      this.viewgroupButtons = (ViewGroup) this.findViewById(R.id.linearlayout_buttons);
      this.imagebuttonNearby = (ImageButton) this.viewgroupButtons.findViewById(R.id.imagebutton_nearby);
      this.imagebuttonMap = (ImageButton) this.viewgroupButtons.findViewById(R.id.imagebutton_map);
      this.imagebuttonFavorites = (ImageButton) this.viewgroupButtons.findViewById(R.id.imagebutton_favorites);

      this.imagebuttonNearby.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            new NearbyDialog(BusStationAdvancedInputText.this.mainActivity, BusStationAdvancedInputText.this).show();
         }
      });
      this.imagebuttonMap.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            try
            {
               new SelectFromMap(BusStationAdvancedInputText.this,
                                 BusStationAdvancedInputText.this.mainActivity).show();
            }
            catch (InterruptedException e)
            {
               BusStationAdvancedInputText.this.mainActivity.handleApplicationException(e);
            }
         }
      });
      this.imagebuttonFavorites.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View arg0)
         {
            try
            {
               new FavouriteDialog(BusStationAdvancedInputText.this.mainActivity,
                                   BusStationAdvancedInputText.this).show();
            }
            catch (IOException e)
            {
               BusStationAdvancedInputText.this.mainActivity.handleApplicationException(e);
            }
         }
      });

      this.autocompleteTextView.setOnFocusChangeListener(new OnFocusChangeListener()
      {
         @Override
         public void onFocusChange(View v, boolean hasFocus)
         {
            if (hasFocus && BusStationAdvancedInputText.this.toolButtonsOpen)
            {
               BusStationAdvancedInputText.this.closeSearchButtons();
            }
         }
      });

      this.openSearchButtons();
   }

   private void closeSearchButtons()
   {
      LayoutParams paramsButtons = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      LayoutParams paramsInputfield = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

      paramsButtons.addRule(RelativeLayout.RIGHT_OF, this.viewgroupMore.getId());
      this.viewgroupButtons.setLayoutParams(paramsButtons);

      paramsInputfield.addRule(RelativeLayout.LEFT_OF, this.viewgroupMore.getId());
      this.viewgroupInputfield.setLayoutParams(paramsInputfield);

      this.imageButtonMore.setSelected(false);

      this.toolButtonsOpen = false;
   }

   private void openSearchButtons()
   {
      LayoutParams paramsButtons = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      LayoutParams paramsInputfield = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

      paramsButtons.addRule(RelativeLayout.LEFT_OF, this.viewgroupMore.getId());
      this.viewgroupButtons.setLayoutParams(paramsButtons);

      paramsInputfield.addRule(RelativeLayout.LEFT_OF, this.viewgroupButtons.getId());
      this.viewgroupInputfield.setLayoutParams(paramsInputfield);

      this.imageButtonMore.setSelected(true);

      this.toolButtonsOpen = true;
   }

   public void setBusStations(BusStation[] busStations)
   {
      this.busStations = busStations;

      String[] stationNames = new String[busStations.length];
      for (int i = 0; i < busStations.length; i++)
      {
         stationNames[i] = this.mainActivity.getBusStationNameUsingAppLanguage(busStations[i]);
      }

      final ArrayAdapter<String> adapter;

      adapter = new ArrayAdapter<String>(this.mainActivity,
                                         android.R.layout.simple_dropdown_item_1line,
                                         stationNames);

      this.autocompleteTextView.setAdapter(adapter);
   }

   public void setInputTextFireChange(String text)
   {
      this.autocompleteTextView.setText(text);
      if (text.length() > 0)
      {
         if (this.listener != null)
         {
            this.listener.run();
         }
         if (this.toolButtonsOpen)
         {
            this.closeSearchButtons();
         }
         this.autocompleteTextView.dismissDropDown();
      }
   }

   public void swapText(BusStationAdvancedInputText inputField2)
   {
      String tmp1 = this.autocompleteTextView.getText().toString();
      String tmp2 = inputField2.autocompleteTextView.getText().toString();

      this.autocompleteTextView.setText(tmp2);
      inputField2.autocompleteTextView.setText(tmp1);

   }

   public BusStation getSelectedBusStation()
   {
      String text = this.autocompleteTextView.getText().toString().trim().toUpperCase();
      BusStation found = null;
      if (text.length() > 0)
      {
         for (BusStation busStation : this.busStations)
         {
            if (busStation.findName_it().toUpperCase().startsWith(text) ||
                busStation.findName_de().toUpperCase().startsWith(text))
            {
               if (found != null)
               {
                  return null;
               }
               found = busStation;
            }
         }
      }
      if (found != null)
      {
         this.autocompleteTextView.setText(this.mainActivity.getBusStationNameUsingAppLanguage(found));
         this.autocompleteTextView.dismissDropDown();
      }

      return found;
   }

   public void setOnChangeListener(final Runnable listener)
   {
      this.listener = listener;
      this.autocompleteTextView.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
         {
            listener.run();
         }
      });
   }
}