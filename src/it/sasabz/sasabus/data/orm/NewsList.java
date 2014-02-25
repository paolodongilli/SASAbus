/*
 * SASAbus - Android app for SASA bus open data
 *
 * NewsList.java
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

package it.sasabz.sasabus.data.orm;

import it.sasabz.sasabus.R;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.data.network.SasabusHTTP;
import it.sasabz.sasabus.ui.news.NewsFragment.NewsCallback;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/** Downlaods latest News about service variations */
public class NewsList extends AsyncTask<Void, Void, ArrayList<News>>
{

   private final NewsCallback callback;
   Context                    context;

   public NewsList(NewsCallback callback, Context context)
   {
      super();
      this.callback = callback;
      this.context = context;
   }

   @Override
   protected ArrayList<News> doInBackground(Void... params)
   {

      ArrayList<News> listNews = new ArrayList<News>();

      try
      {

         String newsserver = this.context.getString(R.string.newsserverjson);
         SasabusHTTP http = new SasabusHTTP(newsserver);

         String json = http.postData();

         if (json == null)
         {
            throw new IOException("JSON request string is NULL");
         }

         JSONArray newsArray = new JSONArray(json);

         for (int i = 0; i < newsArray.length(); i++)
         {
            JSONObject newsObject = newsArray.getJSONObject(i);

            int id = Integer.parseInt(newsObject.getString("id"));
            String title_de = newsObject.getString("titel_de");
            String title_it = newsObject.getString("titel_it");
            String message_de = newsObject.getString("nachricht_de");
            String message_it = newsObject.getString("nachricht_it");
            int city = Integer.parseInt(newsObject.getString("gebiet"));

            JSONArray linesAffectedArray = newsObject.getJSONArray("linienliste");
            int[] linesAffected = new int[linesAffectedArray.length()];
            for (int j = 0; j < linesAffectedArray.length(); j++)
            {
               linesAffected[j] = Integer.parseInt(linesAffectedArray.getString(j));
            }

            News news = new News(id, title_de, title_it, message_de, message_it, city, linesAffected);
            listNews.add(news);
         }

      }
      catch (Exception e)
      {
         Log.v("INFORMATION LIST", "FAILURE", e);
      }
      return listNews;
   }

   @Override
   protected void onPostExecute(ArrayList<News> result)
   {
      super.onPostExecute(result);
      this.callback.newsDownloaded(result);
   }

}
