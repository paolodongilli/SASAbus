/*
 * SASAbus - Android app for SASA bus open data
 *
 * CustomDialog.java
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

package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;

import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;

public class CustomDialog extends SherlockDialogFragment
{

   Dialog                                    dialog;

   static String                             TITLE    = "title";
   static String                             MESSAGE  = "message";
   static String                             POSITIVE = "positive";
   static String                             NEGATIVE = "negative";

   protected String                          title;
   protected String                          message;
   protected String                          positiveText;
   protected String                          negativeText;
   protected List<?>                         list;

   protected TextView                        textviewTitle;
   protected ScrollView                      scrollviewMessage;
   protected TextView                        textviewMessage;
   protected ListView                        listviewList;
   protected Button                          buttonNegative;
   protected Button                          buttonPositive;

   protected Adapter                         adapter;
   protected DialogInterface.OnClickListener positiveListener;
   protected DialogInterface.OnClickListener negativeListener;

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState)
   {

      this.dialog = new Dialog(this.getSherlockActivity());

      this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

      this.dialog.setContentView(R.layout.custom_dialog);

      WindowManager.LayoutParams wmlp = this.dialog.getWindow().getAttributes();
      wmlp.width = WindowManager.LayoutParams.FILL_PARENT;

      this.initializeViews();

      this.addTextToViewsAndRemoveUnnecessary();

      return this.dialog;
   }

   private void initializeViews()
   {
      this.textviewTitle = (TextView) this.dialog.findViewById(R.id.textview_title);
      this.scrollviewMessage = (ScrollView) this.dialog.findViewById(R.id.scrollview_message);
      this.textviewMessage = (TextView) this.dialog.findViewById(R.id.textview_message);
      this.listviewList = (ListView) this.dialog.findViewById(R.id.listview_list);
      this.buttonNegative = (Button) this.dialog.findViewById(R.id.button_negative);
      this.buttonPositive = (Button) this.dialog.findViewById(R.id.button_positive);
   }

   private void addTextToViewsAndRemoveUnnecessary()
   {

      if (this.title != null)
      {
         this.textviewTitle.setText(this.title);
      }

      if (this.message != null)
      {
         this.textviewMessage.setText(Html.fromHtml(this.message));
      }
      else
      {
         this.scrollviewMessage.setVisibility(View.GONE);
      }

      if (this.list != null)
      {
         this.listviewList.setAdapter((ListAdapter) this.adapter);
      }
      else
      {
         this.listviewList.setVisibility(View.GONE);
      }

      if (this.list != null)
      {

      }
      else
      {

      }

      if (this.negativeText != null)
      {
         this.buttonNegative.setText(this.negativeText);
         this.buttonNegative.setOnClickListener(new OnClickListener()
         {
            @Override
            public void onClick(View v)
            {
               CustomDialog.this.negativeListener.onClick(CustomDialog.this.getDialog(), 0);
            }
         });
      }
      else
      {
         this.buttonNegative.setVisibility(View.GONE);
      }

      if (this.positiveText != null)
      {
         this.buttonPositive.setText(this.positiveText);
         this.buttonPositive.setOnClickListener(new OnClickListener()
         {
            @Override
            public void onClick(View v)
            {
               CustomDialog.this.positiveListener.onClick(CustomDialog.this.getDialog(), 0);
            }
         });
      }
      else
      {
         this.buttonPositive.setVisibility(View.GONE);
      }

   }

   public static class Builder
   {

      CustomDialog     customDialog;
      SherlockFragment fragment;

      public Builder(SherlockFragment fragment)
      {
         this.customDialog = new CustomDialog();
         this.fragment = fragment;
      }

      public void setTitle(String title)
      {
         this.customDialog.title = title;
      }

      public void setMessage(String message)
      {
         this.customDialog.message = message;
      }

      public void setList(List<?> list, Adapter adapter)
      {
         this.customDialog.list = list;
         this.customDialog.adapter = adapter;
      }

      public void setPositiveButton(String text, DialogInterface.OnClickListener listener)
      {
         this.customDialog.positiveText = text;
         this.customDialog.positiveListener = listener;
      }

      public void setNegativeButton(String text, DialogInterface.OnClickListener listener)
      {
         this.customDialog.negativeText = text;
         this.customDialog.negativeListener = listener;
      }

      public void show()
      {
         this.customDialog.show(this.fragment.getSherlockActivity().getSupportFragmentManager(),
                                "info_dialog");
      }

   }

}