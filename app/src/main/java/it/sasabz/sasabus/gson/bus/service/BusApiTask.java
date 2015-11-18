/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusApiTask.java
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
package it.sasabz.sasabus.gson.bus.service;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import it.sasabz.sasabus.gson.AbstractApiTask;
import it.sasabz.sasabus.gson.IApiCallback;

public class BusApiTask<T> extends AbstractApiTask<T> {

	public BusApiTask(String apiUrl, List<BasicNameValuePair> params, IApiCallback<T> callback, Class<T> gsonClass) {
		this.apiUrl = apiUrl;
		this.params = params;
		this.callback = callback;
		this.gsonClass = gsonClass;
	}
	
	private String generateUrl()
	{
		String url = this.apiUrl;
		if(!url.endsWith("?")) {
	        url += "?";
		}
		return url + URLEncodedUtils.format(this.params, "UTF-8");
	}

	@Override
	protected String doInBackground(String... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(generateUrl());
		String json = null;
		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			json = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
		}
		return json;
	}
}
