/*
 * SASAbus - Android app for SASA bus open data
 *
 * NewsPagerAdapter.java
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

import it.sasabz.sasabus.data.models.News;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * This is a helper class that implements the management of tabs and all
 * details of connecting a ViewPager with associated {@link PagerSlidingTabStrip}.
 */
public class NewsPagerAdapter extends FragmentStatePagerAdapter
{
   private final SherlockFragmentActivity mActivity;
   private final ViewPager                mViewPager;
   private final FragmentManager          mFragmentManager;
   private final List<TabInfo>            mTabs = new ArrayList<TabInfo>();

   static final class TabInfo
   {
      private final Bundle args;
      private final String title;
      private List<News>   news;

      public TabInfo(Bundle args, String title, List<News> news)
      {
         this.args = args;
         this.title = title;
         this.news = news;
      }
   }

   public NewsPagerAdapter(ViewPager viewPager,
                           FragmentManager fragmentManager,
                           SherlockFragmentActivity activity)
   {
      super(fragmentManager);
      this.mViewPager = viewPager;
      this.mFragmentManager = fragmentManager;
      this.mActivity = activity;
   }

   public void addTab(Bundle args, String title, List<News> news)
   {
      TabInfo info = new TabInfo(args, title, news);
      this.mTabs.add(info);
   }

   @Override
   public CharSequence getPageTitle(int position)
   {
      TabInfo info = this.mTabs.get(position);
      return info.title;
   }

   @Override
   public Fragment getItem(int position)
   {
      TabInfo info = this.mTabs.get(position);
      CityNewsFragment newCityNewsFragment = CityNewsFragment.newInstance(position);
      if (info.news != null)
      {
         newCityNewsFragment.setListAdapter(info.news, this.mActivity);
      }

      return newCityNewsFragment;
   }

   @Override
   public int getItemPosition(Object object)
   {
      return POSITION_NONE;
   }

   @Override
   public int getCount()
   {
      return this.mTabs.size();
   }

   public void setData(List<List<News>> news)
   {
      for (int i = 0; i < this.mTabs.size(); i++)
      {
         TabInfo info = this.mTabs.get(i);
         info.news = news.get(i);
      }
      this.notifyDataSetChanged();
   }

}