/*
 * SASAbus - Android app for SASA bus open data
 *
 * DateButton.java
 *
 * Created: Feb 11, 2014 10:55:00 AM
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

import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.routing.TimePicker.TimeHasBeenSetListener;

import java.util.Date;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TimeButton
{
   public static void init(final Button button)
   {
      Date now = new Date();
      button.setText(TimePicker.simpleDateFormat.format(now));
      button.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            openTimePickerDialog(button);
         }
      });
   }

   /**
    * actually opens the TimePicker
    * @param view
    *          is the total view of the fragment
    */
   public static void openTimePickerDialog(final Button button)
   {
      String timeButtonText = button.getText().toString();

      TimePicker timePicker = new TimePicker();
      timePicker.setTimeAlreadySetString(timeButtonText);
      timePicker.setCallback(new TimeHasBeenSetListener()
      {
         @Override
         public void timeHasBeenSet(String time)
         {
            button.setText(time);
            Log.i("testtime", time);
         }
      });
      MainActivity mainActivity = (MainActivity) button.getContext();
      timePicker.show(mainActivity.getSupportFragmentManager(), "Time Picker");
   }
}
