/*
 * SASAbus - Android app for SASA bus open data
 *
 * DatePicker.java
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * Picker for the date
 */
public class DatePicker extends SherlockDialogFragment implements OnDateSetListener
{

   public static interface DateHasBeenSetListener
   {
      void dateHasBeenSet(String date);
   }

   /** The format that the date has */
   public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);

   /** The date that has already been set before
    * and gets used for the initial date */
   private String                       dateAlreadySetString;
   private DateHasBeenSetListener       callback;

   public void setDateAlreadySetString(String dateAlreadySetString)
   {
      this.dateAlreadySetString = dateAlreadySetString;
   }

   public void setCallback(DateHasBeenSetListener callback)
   {
      this.callback = callback;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState)
   {

      //Convert the date in String format to Date format and set it to the new picker

      if (dateAlreadySetString == null)
      {
         dateAlreadySetString = simpleDateFormat.format(new Date());
      }
      Date dateAlreadySet = null;
      try
      {
         dateAlreadySet = simpleDateFormat.parse(dateAlreadySetString);
      }
      catch (ParseException e)
      {
         Log.e("error", "could not parse date");
         e.printStackTrace();
      }

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateAlreadySet);

      int year = calendar.get(Calendar.YEAR);
      int monthOfYear = calendar.get(Calendar.MONTH);
      int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

      return new DatePickerDialog(getSherlockActivity(), this, year, monthOfYear, dayOfMonth);
   }

   @Override
   public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth)
   {

      //In Android the month starts with 0,
      //therefore we have to add 1
      monthOfYear++;

      //Add a 0 if the month has only 1 digit
      String actualMonth = Integer.valueOf(monthOfYear).toString();
      if (actualMonth.length() < 2)
      {
         actualMonth = "0" + actualMonth;
      }

      //Add a 0 if the day has only 1 digit
      String actualDayOfMonth = Integer.valueOf(dayOfMonth).toString();
      if (actualDayOfMonth.length() < 2)
      {
         actualDayOfMonth = "0" + actualDayOfMonth;
      }

      String dateText = actualDayOfMonth + "." + actualMonth + "." + year;

      if (callback != null)
      {
         callback.dateHasBeenSet(dateText);
      }
   }

}