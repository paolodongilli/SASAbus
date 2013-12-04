/**
 *
 * InformationList.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.sasabus.data.orm;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.data.network.SasabusHTTP;
import it.sasabz.sasabus.ui.SASAbus;
import it.sasabz.sasabus.ui.news.NewsFragment.NewsCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/** Downlaods latest News about service variations */
public class NewsList extends
		AsyncTask<Void, Void, ArrayList<News>> {

	private final NewsCallback callback;

	public NewsList(NewsCallback callback) {
		super();
		this.callback = callback;
	}

	@Override
	protected ArrayList<News> doInBackground(Void... params) {
		
		ArrayList<News> listNews = new ArrayList<News>();
		
		try {
			String newsserver = SASAbus.getContext().getString(
					R.string.newsserverjson);
			SasabusHTTP http = new SasabusHTTP(newsserver);

			String json = http.postData();
			
			if (json == null) {
				throw new IOException("JSON request string is NULL");
			}
			
			JSONArray newsArray = new JSONArray(json);
			
			for (int i = 0; i < newsArray.length(); i++) {
				JSONObject newsObject = newsArray.getJSONObject(i);
				
				int id = Integer.parseInt(newsObject.getString("id"));
				String title_de = newsObject.getString("titel_de");
				String title_it = newsObject.getString("titel_it");
				String message_de = newsObject.getString("nachricht_de");
				String message_it = newsObject.getString("nachricht_it");
				int city = Integer.parseInt(newsObject.getString("gebiet"));
				
				JSONArray linesAffectedArray = newsObject.getJSONArray("linienliste");
				int[] linesAffected = new int[linesAffectedArray.length()];
				for (int j = 0; j < linesAffectedArray.length(); j++) {
					linesAffected[j] = Integer.parseInt(linesAffectedArray.getString(j));
				}
				
				News news = new News(id, title_de, title_it, message_de, message_it, city, linesAffected);
				listNews.add(news);
			}
			
		} catch (Exception e) {
			Log.v("INFORMATION LIST", "FAILURE", e);
		}
		return listNews;
	}

	@Override
	protected void onPostExecute(ArrayList<News> result) {
		super.onPostExecute(result);
		callback.newsDownloaded(result);
	}

}
