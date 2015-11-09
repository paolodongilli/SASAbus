/*
 * SASAbus - Android app for SASA bus open data
 *
 * NewsFragment.java
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
import it.sasabz.sasabus.logic.DownloadNews;
import it.sasabz.sasabus.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;

/**
 * Display general information about changes of routes ecc.
 */
public class NewsFragment extends SherlockFragment
{

   private ViewPager        mViewPager;
   private NewsPagerAdapter mPagerAdapter;

   private MenuItem         mOptionsMenuitemRefresh;

   private String[]         mCities;

   private boolean          loaded = false, loading = false;

   MainActivity             mainActivity;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
   {

      this.mainActivity = (MainActivity) this.getActivity();

      View view = inflater.inflate(R.layout.fragment_news, container, false);

      this.getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      this.initializeViews(view);

      this.addTabs(view);

      this.setHasOptionsMenu(true);
      
      SasaApplication application = (SasaApplication) this.getActivity().getApplication();
      application.getTracker().track("News");

      return view;
   }

   private void initializeViews(View view)
   {
      this.mViewPager = (ViewPager) view.findViewById(R.id.pager);

      this.mCities = this.getResources().getStringArray(R.array.cities);
   }

   private void addTabs(View view)
   {
      // Instantiate the PagerAdapter
      this.mPagerAdapter = new NewsPagerAdapter(this.mViewPager,
                                                this.getChildFragmentManager(),
                                                this.getSherlockActivity());

      // Add Tabs to the Adapter
      this.mPagerAdapter.addTab(null, this.mCities[0], null);
      this.mPagerAdapter.addTab(null, this.mCities[1], null);

      this.mViewPager.setAdapter(this.mPagerAdapter);

      // Bind the widget to the adapter
      PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
      tabs.setViewPager(this.mViewPager);

      //Set the color for the Tab strips
      tabs.setIndicatorColorResource(R.color.orange_pressed);
      tabs.setBackgroundResource(R.drawable.ab_solid_sasabus);
      tabs.setTextColorResource(android.R.color.white);
      tabs.setTabBackground(R.drawable.background_tab);
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu)
   {
      MainActivity parentActivity = (MainActivity) this.getSherlockActivity();
      boolean drawerIsOpen = parentActivity.isDrawerOpen();
      //If the drawer is closed, show the menu related to the content
      if (!drawerIsOpen)
      {
         menu.clear();
         parentActivity.getSupportMenuInflater().inflate(R.menu.news_fragment, menu);
         this.mOptionsMenuitemRefresh = menu.findItem(R.id.menu_refresh);
         this.setRefreshActionButtonState();
         if (!this.loaded)
         {
            this.loadInfos();
         }
      }
      super.onPrepareOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
         case R.id.menu_refresh:
            this.loadInfos();
            break;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   private void setRefreshActionButtonState()
   {
      if (this.loading)
      {
         this.mOptionsMenuitemRefresh.setActionView(R.layout.actionbar_indeterminate_progress);
      }
      else
      {
         this.mOptionsMenuitemRefresh.setActionView(null);
      }
   }

   public interface NewsCallback
   {
      void newsDownloaded(List<News> infos);
   }

   private void loadInfos()
   {
      this.setLoading(true);
      DownloadNews.downloadInfos(this, new NewsCallback()
      {
         @Override
         public void newsDownloaded(List<News> infos)
         {
            NewsFragment.this.setLoading(false);
            Log.i("infos", "" + infos);
            if (NewsFragment.this.isAdded())
            {
               if (infos != null)
               {
                  NewsFragment.this.addAdapterToListViews(infos);
               }
               else
               {
                  NewsFragment.this.mainActivity.handleApplicationException(new IOException("News network error!"));
               }
            }

         }
      });
   }

   private void addAdapterToListViews(List<News> infos)
   {
      List<List<News>> cityInfos = new ArrayList<List<News>>();
      cityInfos.add(DownloadNews.getInfosForArea(infos, DownloadNews.BOLZANO));
      cityInfos.add(DownloadNews.getInfosForArea(infos, DownloadNews.MERANO));
      this.mPagerAdapter.setData(cityInfos);
   }

   private void setLoading(boolean loading)
   {
      this.loading = loading;
      if (!loading)
      {
         this.loaded = true;
      }
      this.setRefreshActionButtonState();
   }

}