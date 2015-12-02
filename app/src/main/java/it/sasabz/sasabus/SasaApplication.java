/*
 * SASAbus - Android app for SASA bus open data
 *
 * SasaApplication.java
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
package it.sasabz.sasabus;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;

import it.sasabz.sasabus.beacon.BluetoothStateChangeReceiver;
import it.sasabz.sasabus.config.ConfigManager;
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.data.trips.TripsSQLiteOpenHelper;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.tracker.ITracker;
import it.sasabz.sasabus.tracker.googleanalytics.GoogleTracker;
import it.sasabz.sasabus.ui.AbstractSasaActivity;

public class SasaApplication extends Application {

	public static final String TAG = "SASA";
	private ITracker mTracker;
	private SharedPreferenceManager mPreferenceManager;
	private ConfigManager mConfigManager;
	private AndroidOpenDataLocalStorage opendataStorage;
//	private static HashMap<String, BusBeaconInfo> mBusBeaconMap;
	
	private AbstractSasaActivity mActivity = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("all trips", TripsSQLiteOpenHelper.getInstance(this).getFinishedTrips().toString());
		try {
			this.opendataStorage = new AndroidOpenDataLocalStorage(this.getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPreferenceManager = new SharedPreferenceManager(this.getApplicationContext());
		mConfigManager = ConfigManager.getInstance(this.getApplicationContext());
		mTracker = new GoogleTracker(this);
		sendBroadcast(new Intent(this, BluetoothStateChangeReceiver.class));
	}

	/**
	 * Gets the shared preference manager
	 * @return
	 */
	public SharedPreferenceManager getSharedPreferenceManager() {
		return mPreferenceManager;
	}

	/**
	 * Gets the config manager
	 * @return
	 */
	public ConfigManager getConfigManager() {
		return this.mConfigManager;
	}

	/**
	 * Sets the activity
	 * @param activity
	 */
	public void setActivity(AbstractSasaActivity activity) {
		this.mActivity = activity;
	}

	/**
	 * Gets the activity
	 * @return
	 */
	public AbstractSasaActivity getActivity() {
		return this.mActivity;
	}

	/**
	 * Getst the tracker
	 * @return
	 */
	public ITracker getTracker() {
		return this.mTracker;
	}

	/**
	 * Gets the android id
	 */
	public String getAndroidId() {
		return getAndroidId(this.getApplicationContext());
	}
	
	/**
	 * Checks if the system is online
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	/**
	 * Gets the android id
	 * @param context
	 */
	public static String getAndroidId(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

/*	public static HashMap<String, BusBeaconInfo> getBusBeaconMap() {
		return mBusBeaconMap;
	}

	public static void setBusBeaconMap(HashMap<String, BusBeaconInfo> mBusBeaconMap) {
		SasaApplication.mBusBeaconMap = mBusBeaconMap;
	}*/

	public AndroidOpenDataLocalStorage getOpenDataStorage() {
		return opendataStorage;
	}

}
