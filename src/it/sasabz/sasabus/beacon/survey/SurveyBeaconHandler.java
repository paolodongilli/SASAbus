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
package it.sasabz.sasabus.beacon.survey;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.altbeacon.beacon.Beacon;

import android.location.Location;
import android.util.Log;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.BeaconObserver;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.service.BusApiService;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;

public class SurveyBeaconHandler implements IBeaconHandler {

	private HashMap<String, SurveyBeaconInfo> mBeaconMap;
	private SasaApplication mApplication;
	private SharedPreferenceManager mSharedPreferenceManager;
	private ISurveyAction mSurveyAction;

	public SurveyBeaconHandler(SasaApplication beaconApplication, ISurveyAction surveyAction) {
		mBeaconMap = new HashMap<String, SurveyBeaconInfo>();
		mApplication = beaconApplication;
		mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
		mSurveyAction = surveyAction;
	}

	@Override
	public void beaconInRange(String uuid, int major, int minor) {
		final String key = uuid + "_" + major;
		if (checkLastSurveyTime() == true) {
			final SurveyBeaconInfo beaconInfo;
			if (mBeaconMap.keySet().contains(key)) {
				beaconInfo = mBeaconMap.get(key);
				beaconInfo.seen();
				mBeaconMap.put(key, beaconInfo);
				Log.d(SasaApplication.TAG, "Beacon has been seen for " + beaconInfo.getSeenSeconds());
			} else {
				beaconInfo = new SurveyBeaconInfo(uuid, major, minor, Calendar.getInstance().getTimeInMillis());
				mBeaconMap.put(key, beaconInfo);
				BusApiService.getInstance(mApplication).getBusInformation(major,
						new IApiCallback<BusInformationResult>() {

							@Override
							public void onSuccess(BusInformationResult result) {
								if (result.hasFeatures()) {
									Feature busInformation = result.getFirstFeature();
									beaconInfo.setBusInformation(busInformation);
									
									if (busInformation != null &&
										busInformation.getProperties() != null) {
											beaconInfo.setStartBusstationId(busInformation.getProperties().getNextStopNumber());
									}
									Log.d(SasaApplication.TAG,
											"Got location from bus information: "
													+ beaconInfo.getLocation().getLongitude() + " / "
													+ beaconInfo.getLocation().getLatitude());
								} else {
									handleNoBusInformation();
								}
							}

							@Override
							public void onFailure(Exception e) {
								handleNoBusInformation();
							}

							private void handleNoBusInformation() {
								SurveyLocationHandler handler = new SurveyLocationHandler(mApplication,
										new ISurveyLocationCallback() {

									@Override
									public void onSuccess(Location location) {
										Log.d(SasaApplication.TAG, "Got location from system: "
												+ location.getLongitude() + " / " + location.getLatitude());
										beaconInfo.setLocation(location);
									}

									@Override
									public void onFailure() {
									}
								});
								handler.locate();
							}
						});
			}
		}
	}

	private boolean checkLastSurveyTime() {
		boolean result = true;
		Date lastSurveyDate = mSharedPreferenceManager.getSurveyLastOccurence();
		if (lastSurveyDate != null) {
			long secondsBetweenLastSurvey = (Calendar.getInstance().getTimeInMillis() - lastSurveyDate.getTime())
					/ 1000;
			int prefSurveyRecurring = mSharedPreferenceManager.getSurveyRecurring();
			Log.d(SasaApplication.TAG, "Last survey: " + secondsBetweenLastSurvey + "s ago");
			Log.d(SasaApplication.TAG, "Survey cycle: " + prefSurveyRecurring + "s");
			result = secondsBetweenLastSurvey > prefSurveyRecurring;
		}
		return result;
	}

	@Override
	public void clearBeacons() {
		mBeaconMap.clear();
	}

	public void removeBeacon(String key) {
		mBeaconMap.remove(key);
	}

	@Override
	public void inspectBeacons() {
		Iterator<Entry<String, SurveyBeaconInfo>> iterator = mBeaconMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, SurveyBeaconInfo> pair = (Map.Entry<String, SurveyBeaconInfo>) iterator.next();
			final SurveyBeaconInfo beaconInfo = pair.getValue();
			this.isBeaconSuitableForSurvey(beaconInfo, new IBeaconSuitableCallback() {
				@Override
				public void onSuccess() {
					Log.d(SasaApplication.TAG, "Triggering survey now.");
					mSurveyAction.triggerSurvey(beaconInfo);
				}

				@Override
				public void onFailure() {
				}
			});
		}
		clearBeacons();
	}

	/**
	 * Checks if the given beacon is suitable for a survey
	 * 
	 * @param beaconInfo
	 * @param callback
	 */
	private void isBeaconSuitableForSurvey(final SurveyBeaconInfo beaconInfo, final IBeaconSuitableCallback callback) {
		if (beaconInfo.getLastSeen().getTime()
				+ mApplication.getConfigManager().getValue("beacon_lastSeenTreshold", 10) < Calendar.getInstance()
						.getTimeInMillis()) {
			BusApiService.getInstance(mApplication).getBusInformation(beaconInfo.getMajor(),
					new IApiCallback<BusInformationResult>() {

						@Override
						public void onSuccess(BusInformationResult result) {
							if (result.hasFeatures()) {
								Feature busInformation = result.getFirstFeature();
								Location busLocation = new Location("BusInfo");
								
								if (busInformation != null &&
									busInformation.getProperties() != null) {
									beaconInfo.setStopBusstationId(busInformation.getProperties().getNextStopNumber());
								}
								busLocation.setLongitude(busInformation.getGeometry().getCoordinates().get(0));
								busLocation.setLatitude(busInformation.getGeometry().getCoordinates().get(1));
								Log.d(SasaApplication.TAG, "Got location from bus information: "
										+ busLocation.getLongitude() + " / " + busLocation.getLatitude());
								checkTrip(busLocation);
							} else {
								handleNoBusInformation();
							}
						}

						@Override
						public void onFailure(Exception e) {
							handleNoBusInformation();
						}

						private void handleNoBusInformation() {
							SurveyLocationHandler handler = new SurveyLocationHandler(mApplication,
									new ISurveyLocationCallback() {

								@Override
								public void onSuccess(Location location) {
									Log.d(SasaApplication.TAG, "Got location from system: " + location.getLongitude()
											+ " / " + location.getLatitude());
									checkTrip(location);
								}

								@Override
								public void onFailure() {
									callback.onFailure();
								}
							});
							handler.locate();
						}

						private void checkTrip(Location busLocation) {
							float tripDistance = beaconInfo.getLocation().distanceTo(busLocation);
							Log.d(SasaApplication.TAG, "Trip distance: " + tripDistance + "m");
							if (tripDistance > mApplication.getConfigManager().getValue("beacon_minTripDistance", 400)
									&& beaconInfo.getSeenSeconds() > mApplication.getConfigManager()
											.getValue("beacon_secondsInBus", 120)) {
								callback.onSuccess();
							} else {
								callback.onFailure();
							}
						}
					});
		}
	}

	@Override
	public void beaconsInRange(Collection<Beacon> beacons) {
		if (this.mApplication.isOnline()) {
			int i = 0;
			for (Beacon beacon : beacons) {
				String uuid = beacon.getId1().toString();
				int major = beacon.getId2().toInt();
				int minor = beacon.getId3().toInt();
				this.beaconInRange(uuid, major, minor);
				Log.d(SasaApplication.TAG, "Beacon [" + i + "] " + uuid + " | " + major + " | " + minor
						+ " |  :  " + beacon.getDistance() + "m ");
				i++;
			}
		}
	}

	@Override
	public String getUUid() {
		return mApplication.getConfigManager().getValue("beacon_uid", "");
	}

	@Override
	public String getIdentifier() {
		return mApplication.getConfigManager().getValue("beacon_region", "");
	}

	@Override
	public boolean isHandlerEnabled() {
		return true;
	}
}
