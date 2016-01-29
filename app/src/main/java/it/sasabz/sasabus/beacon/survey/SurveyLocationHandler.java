/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyLocationHandler.java
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
package it.sasabz.sasabus.beacon.survey;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import it.sasabz.sasabus.SasaApplication;

public class SurveyLocationHandler implements LocationListener {

	private LocationManager locationManager;
	private String provider;
	private ISurveyLocationCallback callback;
	private int timeout;
	private int lastFixTreshold;
	private int accuracyTreshold;
	private boolean locationFound = false;

	public SurveyLocationHandler(SasaApplication application, ISurveyLocationCallback callback) {
		this.locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
		this.provider = LocationManager.NETWORK_PROVIDER;
		this.callback = callback;
		this.timeout = application.getConfigManager().getValue("beacon_locatingTimeout", 40000);
		this.lastFixTreshold = application.getConfigManager().getValue("beacon_lastFixTreshold", 10);
		this.accuracyTreshold = application.getConfigManager().getValue("beacon_accuracyTreshold", 40);
	}

	/**
	 * Starts the locating mechanism. Uses the last known location if it's new
	 * and accurate enough. Otherwise tries for the in the configuration
	 * configured time to get the location info using the network provider.
	 */
	public void locate() {
		if (locationManager.isProviderEnabled(provider)) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null && location.getTime() < Calendar.getInstance().getTimeInMillis() - lastFixTreshold
					&& location.getAccuracy() < accuracyTreshold) {
				callback.onSuccess(location);
			} else {
				locationManager.requestLocationUpdates(this.provider, 500, 1, this);
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (locationFound == false) {
							locationManager.removeUpdates(SurveyLocationHandler.this);
							callback.onFailure();
						}
					}
				}, this.timeout);
			}
		} else {
			callback.onFailure();
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(SasaApplication.TAG, "Location changed: " + location.getLongitude() + " / " + location.getLatitude()
				+ "(Accuracy: " + location.getAccuracy() + "m)");
		Log.d(SasaApplication.TAG,
				"Got location from system: " + location.getLongitude() + " / " + location.getLatitude());
		if (location.getAccuracy() < accuracyTreshold) {
			locationManager.removeUpdates(this);
			locationFound = true;
			callback.onSuccess(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.d(SasaApplication.TAG, "Location provider disabled: " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.d(SasaApplication.TAG, "Location provider enabled: " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
