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
import java.util.Date;

import org.altbeacon.beacon.Beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.MainActivity;

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
		if(mSharedPreferenceManager.isBusStopDetectionEnabled() && !mSharedPreferenceManager.hasCurrentTrip()
		&& !mSharedPreferenceManager.itsCurrentBusStopSeen() && mSharedPreferenceManager.getCurrentBusStopDetectStart()
				+ 90000 < new Date().getTime()){
			mSharedPreferenceManager.setCurrentBusStopSeen();
			Intent intent = new Intent(mApplication, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(mApplication, 0, intent, Intent.FILL_IN_DATA);
			Notification notification = new NotificationCompat.Builder(mApplication)
					.setContentTitle(getBeaconBusStop()).setContentText(mApplication.getString(R.string.show_next_departures))
					.setTicker(getBeaconBusStop()).setSmallIcon(R.drawable.icon)
					.setAutoCancel(true).setContentIntent(pendingIntent)
					.build();

			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.ledARGB = Color.argb(255, 255, 166, 0);
			notification.ledOnMS = 200;
			notification.ledOffMS = 200;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			NotificationManager notificationManager = (NotificationManager) mApplication
					.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(3, notification);
		}
		Intent intent = new Intent(mApplication.getApplicationContext().getString(R.string.station_beacon_uid));
		mApplication.getApplicationContext().sendBroadcast(intent);

	}

	@Override
	public void clearBeacons() {

	}

	public void removeBeacon(String key) {
	}

	@Override
	public void inspectBeacons() {
		new Thread(){
			public void run(){
				synchronized (this){
					try {
						this.wait(31000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(mSharedPreferenceManager.getCurrentBusStop() == null){
					NotificationManager notificationManager = (NotificationManager) mApplication
							.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel(3);
				}
			}
		}.start();
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

	private String getBeaconBusStop(){
		try {
			if (mApplication.getSharedPreferenceManager().isBusStopDetectionEnabled()) {
				Integer busStopId = mApplication.getSharedPreferenceManager().getCurrentBusStop();
				if (busStopId != null) {
					mSharedPreferenceManager.setCurrentBusStopSeen();
					return getBusStationNameUsingAppLanguage(mApplication.getOpenDataStorage().getBusStations().findBusStop(busStopId).getBusStation());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public String getBusStationNameUsingAppLanguage(BusStation busStation) {
		if (mApplication.getApplicationContext().getString(R.string.bus_station_name_language).equals("de")) {
			return busStation.findName_de();
		} else {
			return busStation.findName_it();
		}
	}


}