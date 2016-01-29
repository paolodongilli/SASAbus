/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusApiService.java
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

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class BusApiService extends AbstractBusApiService {

	private static final String PATH_POSITIONS = "positions";
	private static BusApiService instance;
	
	private BusApiService(Context context) {
		this.apiUrl = context.getResources().getString(R.string.bus_api_url);
	}
	
	public static BusApiService getInstance(Context context) {
		if (instance == null) {
			instance = new BusApiService(context);
		}
		return instance;
	}

	@Override
	public void getBusInformation(int vehicleCode, IApiCallback<BusInformationResult> callback) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		/**
		 * TODO remove BZ from vehicleCode
		 */
		params.add(new BasicNameValuePair("vehiclecode", ""+vehicleCode/* + " BZ"*/));
		BusApiTask<BusInformationResult> task = new BusApiTask<BusInformationResult>(this.apiUrl + PATH_POSITIONS,
				params, callback, BusInformationResult.class);
		callApi(task);
	}

}
