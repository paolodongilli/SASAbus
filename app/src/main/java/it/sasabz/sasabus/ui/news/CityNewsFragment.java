/*
 * SASAbus - Android app for SASA bus open data
 *
 * CityNewsFragment.java
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

package it.sasabz.sasabus.ui.news;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.ui.CustomDialog;

import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class CityNewsFragment extends SherlockFragment
{

   private static final String ARG_Position = "position";

   private int                 mPosition;
   private ListView            mListView;
   private ListAdapter         mListAdapter;
   private List<News>          mNews;

   public static CityNewsFragment newInstance(int position)
   {
      CityNewsFragment cityFragment = new CityNewsFragment();

      Bundle args = new Bundle();
      args.putInt(ARG_Position, position);
      cityFragment.setArguments(args);

      return cityFragment;

   }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      //		if (savedInstanceState!= null) {
      //			mPosition = savedInstanceState.getInt(ARG_Position);
      //		}

      this.mPosition = this.getArguments().getInt(ARG_Position);

   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {
      View view = inflater.inflate(R.layout.fragment_city_news, null);
      this.mListView = (ListView) view.findViewById(R.id.listview_news);

      if (this.mListAdapter != null)
      {
         this.addAdapterToList();
      }
      
      SasaApplication application = (SasaApplication) this.getActivity().getApplication();
      application.getTracker().track("CityNews");

      return view;
   }

   public void setListAdapter(List<News> infos, SherlockFragmentActivity activity)
   {
      this.mListAdapter = new NewsAdapter(activity, R.layout.listview_item_news, R.id.textview_busline, infos);
      this.mNews = infos;
   }

   public void addAdapterToList()
   {

      this.mListView.setAdapter(this.mListAdapter);

      //Add onClickListener for list
      this.mListView.setOnItemClickListener(new OnItemClickListener()
      {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id)
         {

            //Open new Dialog for an Info
            CustomDialog.Builder infoDialogBuilder = new CustomDialog.Builder(CityNewsFragment.this);
            infoDialogBuilder.setTitle(CityNewsFragment.this.mNews.get(position).getTitle());
            infoDialogBuilder.setMessage(CityNewsFragment.this.mNews.get(position).getMessage());
            infoDialogBuilder.setNegativeButton(CityNewsFragment.this.getResources().getString(android.R.string.ok),
                                                new DialogInterface.OnClickListener()
                                                {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which)
                                                   {
                                                      dialog.dismiss();
                                                   }
                                                });
            infoDialogBuilder.show();
         }
      });

   }

}
