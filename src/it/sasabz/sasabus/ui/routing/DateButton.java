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
import it.sasabz.sasabus.ui.routing.DatePicker.DateHasBeenSetListener;

import java.util.Date;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DateButton
{

   public static void init(final Button button)
   {
      Date now = new Date();
      button.setText(DatePicker.simpleDateFormat.format(now));
      button.setOnClickListener(new OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
            openDatePickerDialog(button);
         }
      });
   }

   /**
    * Actually open the DatePicker
    */
   private static void openDatePickerDialog(final Button button)
   {
      String dateButtonText = button.getText().toString();

      DatePicker datePicker = new DatePicker();
      datePicker.setDateAlreadySetString(dateButtonText);
      datePicker.setCallback(new DateHasBeenSetListener()
      {
         @Override
         public void dateHasBeenSet(String date)
         {
            button.setText(date);
         }
      });
      MainActivity mainActivity = (MainActivity) button.getContext();
      datePicker.show(mainActivity.getSupportFragmentManager(), "Date Picker");
   }
}
