/*
 * SASAbus - Android app for SASA bus open data
 *
 * AbstractApiTask.java
 *
 * Created: Sep 02, 2015 08:24:00 PM
 *
 * Copyright (C) 2011-2015 Raiffeisen Online GmbH (Norman Marmsoler, Jürgen Sprenger, Aaron Falk) <info@raiffeisen.it>
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
package it.sasabz.sasabus.gson;

import java.util.Date;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import it.sasabz.sasabus.gson.serializer.DateSerializer;

public abstract class AbstractApiTask<T> extends AsyncTask<String, Void, String> {
	
	protected String apiUrl;
	protected IApiCallback<T> callback;
	protected List<BasicNameValuePair> params;
	protected Class<T> gsonClass;
	
	@Override
	protected void onPostExecute(String json) {
		if (callback != null) {
			if (json == null) {
				callback.onFailure(new Exception());
			} else {
				try {
					Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create();
					callback.onSuccess(gson.fromJson(json, this.gsonClass));
				} catch (Exception e) {
					callback.onFailure(e);
				}
			}
		}
	}

}
