/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyApiTask.java
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
package it.sasabz.sasabus.gson.survey.service;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Base64;
import it.sasabz.sasabus.gson.AbstractApiTask;
import it.sasabz.sasabus.gson.IApiCallback;

public class SurveyApiTask<T> extends AbstractApiTask<T> {

	private String apiUser;
	private String apiPassword;

	public SurveyApiTask(String apiUrl, String apiUser, String apiPassword, List<BasicNameValuePair> params,
			IApiCallback<T> callback, Class<T> gsonClass) {
		this.apiUrl = apiUrl;
		this.apiUser = apiUser;
		this.apiPassword = apiPassword;
		this.params = params;
		this.callback = callback;
		this.gsonClass = gsonClass;
		
	}

	@Override
	protected String doInBackground(String... params) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpRequest = new HttpPost(this.apiUrl);
		String json = null;
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(this.params));
			httpRequest.addHeader("Authorization", "Basic "
					+ Base64.encodeToString((this.apiUser + ":" + this.apiPassword).getBytes("UTF-8"), Base64.NO_WRAP));
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			json = EntityUtils.toString(httpResponse.getEntity());
		} catch (Exception e) {
		}
		return json;
	}
}
