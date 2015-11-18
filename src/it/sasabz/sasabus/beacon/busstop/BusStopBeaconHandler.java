/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyBeaconHandler.java
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
package it.sasabz.sasabus.beacon.busstop;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;

import android.content.Intent;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;

public class BusStopBeaconHandler implements IBeaconHandler {

	private SasaApplication mApplication;
	private SharedPreferenceManager mSharedPreferenceManager;

	public BusStopBeaconHandler(SasaApplication beaconApplication) {
		mApplication = beaconApplication;
		mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
	}

	@Override
	public void beaconInRange(String uuid, int major, int minor) {
		mSharedPreferenceManager.setCurrentBusStop(major);
		Intent intent = new Intent(mApplication.getApplicationContext().getString(R.string.station_beacon_uid));
		mApplication.getApplicationContext().sendBroadcast(intent);

	}

	@Override
	public void clearBeacons() {
		mSharedPreferenceManager.setCurrentBusStop(null);
	}

	public void removeBeacon(String key) {
	}

	@Override
	public void inspectBeacons() {
		clearBeacons();
	}

	@Override
	public String getUUid() {
		return mApplication.getConfigManager().getValue("station_beacon_uid", "");
	}

	@Override
	public String getIdentifier() {
		return mApplication.getConfigManager().getValue("station_beacon_region", "");
	}

	@Override
	public boolean isHandlerEnabled() {
		return mSharedPreferenceManager.isBusStopDetectionEnabled();
	}

	@Override
	public void beaconsInRange(Collection<Beacon> beacons) {
		Beacon nearestBeacon = null;
		for (Beacon beacon : beacons) {
			if (nearestBeacon == null
					|| (beacon.getDistance() > 0 && beacon.getDistance() < nearestBeacon.getDistance())) {
				nearestBeacon = beacon;
			}
		}
		if (nearestBeacon != null) {
			String uuid = nearestBeacon.getId1().toString();
			int major = nearestBeacon.getId2().toInt();
			int minor = nearestBeacon.getId3().toInt();
			this.beaconInRange(uuid, major, minor);

		}
	}

}