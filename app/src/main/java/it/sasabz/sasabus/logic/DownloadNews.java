/*
 * SASAbus - Android app for SASA bus open data
 *
 * DownloadNews.java
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

package it.sasabz.sasabus.logic;

import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.data.orm.NewsList;
import it.sasabz.sasabus.ui.news.NewsFragment.NewsCallback;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;

public class DownloadNews
{

   public static int BOTH = 0, MERANO = 1, BOLZANO = 2;

   public static void downloadInfos(final SherlockFragment fragment, final NewsCallback callback)
   {

      NewsList info = new NewsList(callback, fragment.getActivity());
      info.execute();
   }

   public static List<News> getInfosForArea(List<News> infos, int area)
   {
      List<News> filteredInfos = new ArrayList<News>();

      for (News information : infos)
      {
         if (information.getCity() == 0 || information.getCity() == 1 && area == MERANO)
         {
            filteredInfos.add(information);
         }
         else if (information.getCity() == 0 || information.getCity() == 2 && area == BOLZANO)
         {
            filteredInfos.add(information);
         }
      }

      return filteredInfos;
   }

}