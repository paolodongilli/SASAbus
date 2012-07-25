/**
 *
 * SasabusHTTP.java
 * 
 * Created: May 1, 2012 18:20:40 PM
 * 
 * Copyright (C) 2012 Markus Windegger
 * 
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


package it.sasabz.android.sasabus.classes;






import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;




public class SasabusHTTP {
    
	private String hostname;
	
	
	public SasabusHTTP(String hostname)
	{
		this.hostname = hostname;
	}
	
	
	/**
	 * Requesting data via get-request using the apache http-classes
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String postData()
			throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost(hostname);
		
		Log.v("HOSTNAME", hostname);
		
		HttpResponse rp = httpclient.execute(post);

		String responseBody = null;
		
		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			responseBody = EntityUtils.toString(rp.getEntity());
		}
		return responseBody;
	}
	
	/**
	 * Requesting data via get-request using the apache http-classes
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String postData(List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost post = new HttpPost(hostname);
		
		Log.v("HOSTNAME", hostname);
		
		post.setEntity(new UrlEncodedFormEntity(params));
		
		HttpResponse rp = httpclient.execute(post);

		String responseBody = null;
		
		if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			responseBody = EntityUtils.toString(rp.getEntity());
		}
		return responseBody;
	}

    
}