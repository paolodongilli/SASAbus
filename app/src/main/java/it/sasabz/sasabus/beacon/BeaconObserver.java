/*
 * SASAbus - Android app for SASA bus open data
 *
 * BeaconObserver.java
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
package it.sasabz.sasabus.beacon;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.config.ConfigManager;

public class BeaconObserver implements BeaconConsumer, BootstrapNotifier {

	private SasaApplication mApplication;
	private BeaconManager mBeaconManager;
	private Region mRegionSurvey;
	private Region mRegionBusStop;
	private BusBeaconHandler mBeaconHandlerBus;
	private IBeaconHandler mBeaconHandlerBusStop;
	private ConfigManager mConfigManager;

	@SuppressWarnings("unused") // need the reference
	private RegionBootstrap mRegionBootstrap;
	@SuppressWarnings("unused") // need the reference
	private RegionBootstrap mRegionBusstopBootstrap;

	public BeaconObserver(SasaApplication application, BusBeaconHandler beaconHandlerBus, IBeaconHandler beaconHandlerBusStop) {
		mApplication = application;
		mBeaconHandlerBus = beaconHandlerBus;
		mBeaconHandlerBusStop = beaconHandlerBusStop;
		mConfigManager = mApplication.getConfigManager();
	}

	/**
	 * Starts listening for available beacons
	 */
	public void startListening() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			mBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(mApplication);
			mBeaconManager.getBeaconParsers()
					.add(new BeaconParser().setBeaconLayout(mConfigManager.getValue("beacon_layout", "")));
			mBeaconManager.setBackgroundScanPeriod(mConfigManager.getValue("beacon_backgroundScanPeriod", 3000));
			mBeaconManager.setForegroundScanPeriod(mConfigManager.getValue("beacon_foregroundScanPeriod", 3000));
			mBeaconManager.setBackgroundBetweenScanPeriod(
					mConfigManager.getValue("beacon_backgroundBetweenScanPeriod", 0));
			mBeaconManager.setForegroundBetweenScanPeriod(
					mConfigManager.getValue("beacon_backgroundBetweenScanPeriod", 0));

			mRegionSurvey = new Region(mBeaconHandlerBus.getIdentifier(),
					Identifier.parse(mBeaconHandlerBus.getUUid()), null, null);
			mRegionBootstrap = new RegionBootstrap(this, mRegionSurvey);
			

			mRegionBusStop = new Region(mBeaconHandlerBusStop.getIdentifier(),
					Identifier.parse(mBeaconHandlerBusStop.getUUid()), null, null);
			mRegionBusstopBootstrap = new RegionBootstrap(this, mRegionBusStop);
			mBeaconManager.bind(this);
		}
	}

	@Override
	public void onBeaconServiceConnect() {
		mBeaconManager.setRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
				Log.d(SasaApplication.TAG, "Beaconsize " + beacons.size()+" region"+region.getUniqueId());
				if (region.getUniqueId().equals(mBeaconHandlerBus.getIdentifier())) {
					mBeaconHandlerBus.beaconsInRange(beacons);
				}
				
				if (region.getUniqueId().equals(mBeaconHandlerBusStop.getIdentifier())) {
					mBeaconHandlerBusStop.beaconsInRange(beacons);
				}
			}
		});
	}

	/**
	 * Starts getting detailed beacon information
	 */
	private void startRangingBeacon(Region region) {
		try {
			Log.d("NORM","startRanging");
			if (region.getUniqueId().equals(mBeaconHandlerBus.getIdentifier())) {
				mBeaconManager.startRangingBeaconsInRegion(mRegionSurvey);
			}
			
			if (region.getUniqueId().equals(mBeaconHandlerBusStop.getIdentifier())) {
				mBeaconHandlerBusStop.clearBeacons();
				mBeaconManager.startRangingBeaconsInRegion(mRegionBusStop);
			}
			
		} catch (RemoteException e) {
		}
	}


	/**
	 * Stops getting detailed beacon information
	 */
	private void stopRangingBeacon(Region region) {
		try {
			Log.d("NORM","stopRanging");
			if (region.getUniqueId().equals(mBeaconHandlerBus.getIdentifier())) {
				mBeaconHandlerBus.inspectBeacons();
				mBeaconManager.stopRangingBeaconsInRegion(mRegionSurvey);
			}
			
			if (region.getUniqueId().equals(mBeaconHandlerBusStop.getIdentifier())) {
				mBeaconHandlerBusStop.inspectBeacons();
				mBeaconManager.stopRangingBeaconsInRegion(mRegionBusStop);
			}
		} catch (RemoteException e) {
		}
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region region) {
	}

	@Override
	public void didEnterRegion(Region region) {
		Log.d(SasaApplication.TAG, "Entered region: " + region.getId1());
		startRangingBeacon(region);
	}

	@Override
	public void didExitRegion(Region region) {
		Log.d(SasaApplication.TAG, "Exited region: " + region.getId1());
		stopRangingBeacon(region);
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return mApplication.bindService(service, conn, flags);
	}

	@Override
	public Context getApplicationContext() {
		return mApplication.getApplicationContext();
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		this.mApplication.unbindService(conn);
	}

	public void stopListening() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			mBeaconManager.unbind(this);
			mBeaconHandlerBusStop.clearBeacons();
			mBeaconHandlerBus.clearBeacons();
		}
		
	}
}
