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
package it.sasabz.sasabus.beacon.bus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.altbeacon.beacon.Beacon;

import android.content.Intent;
import android.location.Location;
import android.util.Log;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.BeaconScannerService;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.beacon.survey.IBeaconSuitableCallback;
import it.sasabz.sasabus.beacon.survey.ISurveyAction;
import it.sasabz.sasabus.beacon.survey.ISurveyLocationCallback;
import it.sasabz.sasabus.beacon.survey.SurveyLocationHandler;
import it.sasabz.sasabus.beacon.bus.trip.TripNotificationAction;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.service.BusApiService;
import it.sasabz.sasabus.logic.TripThread;
import it.sasabz.sasabus.opendata.client.model.BusLine;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;

public class BusBeaconHandler implements IBeaconHandler {

	private SasaApplication mApplication;
	private SharedPreferenceManager mSharedPreferenceManager;
	private ISurveyAction mSurveyAction;
	public static TripNotificationAction mTripNotificationAction;
	private boolean isAlive = false;
	public static HashMap<String, BusBeaconInfo> mBusBeaconMap;
	private boolean created = false;

	public BusBeaconHandler(SasaApplication beaconApplication, ISurveyAction surveyAction,
			TripNotificationAction tripNotificationAction) {
		mApplication = beaconApplication;
		mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
		mBusBeaconMap = mSharedPreferenceManager.getBusBeaconMap();
		mSurveyAction = surveyAction;
		mTripNotificationAction = tripNotificationAction;
	}

	@Override
	public void beaconInRange(String uuid, int major, int minor) {
		if(major == 385)
			major = 410;
		final String key = uuid + "_" + major;
		final BusBeaconInfo beaconInfo;
		if (mBusBeaconMap.keySet().contains(key)) {
			beaconInfo = mBusBeaconMap.get(key);
			beaconInfo.seen();
			mBusBeaconMap.put(key, beaconInfo);
			Log.d(SasaApplication.TAG, "reputkey: " + key);
			beaconInfo.seen();
			Log.d(SasaApplication.TAG, "Beacon has been seen for " + beaconInfo.getSeenSeconds());
			if (beaconInfo.getTripId() == null && this.mApplication.isOnline() || beaconInfo.getStartBusstationId() == -1)
				getBusInformation(major, beaconInfo);
		} else {
			beaconInfo = new BusBeaconInfo(uuid, major, minor, Calendar.getInstance().getTimeInMillis(),
					mSharedPreferenceManager.getCurrentBusStop());
			mBusBeaconMap.put(key, beaconInfo);
			Log.d(SasaApplication.TAG, "putkey: " + key);
			if (this.mApplication.isOnline())
				getBusInformation(major, beaconInfo);
		}
	}

	private void getBusInformation(final int major, final BusBeaconInfo beaconInfo) {
		BusApiService.getInstance(mApplication).getBusInformation(major, new IApiCallback<BusInformationResult>() {

			@Override
			public void onSuccess(BusInformationResult result) {
				if (BeaconScannerService.isAlive)
					if (result.hasFeatures()) {
						Feature busInformation = result.getFirstFeature();
						beaconInfo.setBusInformation(busInformation);

						Log.d("beaconInfoSeen", "" + beaconInfo.getSeenSeconds());
						if (busInformation != null && busInformation.getProperties() != null
								&& beaconInfo.getSeenSeconds() > 20) {
							beaconInfo.setStartBusstationId(result.getLastFeature().getProperties().getNextStopNumber());
						}
						Log.d(SasaApplication.TAG, "Got location from bus information: "
								+ beaconInfo.getLocation().getLongitude() + " / " + beaconInfo.getLocation().getLatitude());
					} else {
						handleNoBusInformation();
					}
			}

			@Override
			public void onFailure(Exception e) {
				handleNoBusInformation();
			}

			private void handleNoBusInformation() {
				SurveyLocationHandler handler = new SurveyLocationHandler(mApplication, new ISurveyLocationCallback() {

					@Override
					public void onSuccess(Location location) {
						Log.d(SasaApplication.TAG, "Got location from system: " + location.getLongitude() + " / "
								+ location.getLatitude());
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
		mSharedPreferenceManager.setBusBeaconMap(mBusBeaconMap);
	}

	@Override
	public void inspectBeacons() {
		if (this.mApplication.isOnline())
			if (checkLastSurveyTime()) {
				Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();

				while (iterator.hasNext()) {
					Entry<String, BusBeaconInfo> pair = (Entry<String, BusBeaconInfo>) iterator.next();
					final BusBeaconInfo beaconInfo = pair.getValue();
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
			}
		if(created) {
			beaconsInRange(new ArrayList<Beacon>());
			new Thread() {
				public void run() {
					synchronized (this) {
						try {
							this.wait(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					beaconsInRange(new ArrayList<Beacon>());
				}

			}.start();
		}

	}

	private void deleteUnvisibleBeacons() {
		synchronized (mBusBeaconMap) {
			Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, BusBeaconInfo> pair = (Entry<String, BusBeaconInfo>) iterator.next();
				final BusBeaconInfo beaconInfo = pair.getValue();
				if (beaconInfo.getLastSeen().getTime()
						+ mApplication.getConfigManager().getValue("beacon_lastSeenTresholdd", 10000) < Calendar.getInstance()
						.getTimeInMillis()) {
					mBusBeaconMap.remove(pair.getKey());
					if (mSharedPreferenceManager.hasCurrentTrip() &&
							mSharedPreferenceManager.getCurrentTrip().getBusId() == pair.getValue().getMajor())
						mSharedPreferenceManager.setCurrentTrip(null);
				}
			}
		}
	}

	/**
	 * Checks if the given beacon is suitable for a survey
	 * 
	 * @param beaconInfo
	 * @param callback
	 */
	private void isBeaconSuitableForSurvey(final BusBeaconInfo beaconInfo, final IBeaconSuitableCallback callback) {
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

								if (busInformation != null && busInformation.getProperties() != null) {
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

	/**
	 * Checks if the given beacon is suitable for a survey
	 * 
	 * @param beaconInfo
	 * @param callback
	 */
	private void isBeaconCurrentTrip(final BusBeaconInfo beaconInfo) {
		if (beaconInfo.getStartBusstationId() != -1
				|| beaconInfo.getNearestStartStation() != null
				&& (mSharedPreferenceManager.getCurrentBusStop() == null
				|| !(getBusStopName(mSharedPreferenceManager.getCurrentBusStop())
				.equals(getBusStopName(beaconInfo.getNearestStartStation())))))
			BusApiService.getInstance(mApplication).getBusInformation(beaconInfo.getMajor(),
					new IApiCallback<BusInformationResult>() {

						@Override
						public void onSuccess(BusInformationResult result) {
							if (BeaconScannerService.isAlive && result.hasFeatures()) {
								Feature busInformation = result.getLastFeature();
								Location busLocation = new Location("BusInfo");

								if (busInformation != null && busInformation.getProperties() != null) {
									if(beaconInfo.getTripId() != busInformation.getProperties().getFrtFid()) {
										if(mSharedPreferenceManager.getCurrentTrip() == null)
											beaconInfo.setStartBusstationId(busInformation.getProperties().getNextStopNumber());
										beaconInfo.setLineId(busInformation.getProperties().getLineNumber());
										beaconInfo.setLineName(busInformation.getProperties().getLineName());
										beaconInfo.setTripId(busInformation.getProperties().getFrtFid());
									}
								}
								busLocation.setLongitude(busInformation.getGeometry().getCoordinates().get(0));
								busLocation.setLatitude(busInformation.getGeometry().getCoordinates().get(1));
								Log.d(SasaApplication.TAG, "Got location from bus information: "
										+ busLocation.getLongitude() + " / " + busLocation.getLatitude());
								if (busInformation != null && busInformation.getProperties() != null) {
									if (busInformation.getProperties().getNextStopNumber() != beaconInfo.getStartBusstationId()) {
										SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
										SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
										Date date = new Date();
										final String day = yyyyMMdd.format(date);
										int seconds = (date.getHours() * 60 + date.getMinutes()) * 60;
										new Thread(new TripThread(beaconInfo.getLineId(), getBeaconLine(beaconInfo.getLineId()),
												beaconInfo.getTripId(), beaconInfo.getMajor(), day, seconds, mApplication, busInformation)).start();
									}
								}

							}
						}

						@Override
						public void onFailure(Exception e) {

						}
					});
	}

	@Override
	public void beaconsInRange(Collection<Beacon> beacons) {
		try {
			String versionDate = mApplication.getOpenDataStorage().getVersionDateIfExists();

			if (versionDate != null) {
				created = true;
				int i = 0;
				synchronized (mBusBeaconMap) {
					for (Beacon beacon : beacons) {
						String uuid = beacon.getId1().toString();
						int major = beacon.getId2().toInt();
						int minor = beacon.getId3().toInt();
						this.beaconInRange(uuid, major, minor);
						Log.d(SasaApplication.TAG, "Beacon [" + i + "] " + uuid + " | " + major + " | " + minor + " |  :  "
								+ beacon.getDistance() + "m ");
						i++;
					}
				}
				deleteUnvisibleBeacons();
				try {
					BusBeaconInfo firstSeenBusBeaconInfo = null;
					Iterator<Entry<String, BusBeaconInfo>> busIterator = mBusBeaconMap.entrySet().iterator();
					int in = 0;
					while (busIterator.hasNext()) {
						in++;
						BusBeaconInfo beaconInfo = busIterator.next().getValue();
						if (firstSeenBusBeaconInfo == null
								|| beaconInfo.getStartDate().before(firstSeenBusBeaconInfo.getStartDate()))
							firstSeenBusBeaconInfo = beaconInfo;
					}
					Log.d(SasaApplication.TAG, "beaconssize: " + in);
					Log.d(SasaApplication.TAG,
							firstSeenBusBeaconInfo == null ? "beacon: null" : "beacon: " + firstSeenBusBeaconInfo.getMajor());
					if (firstSeenBusBeaconInfo != null) {
						if (this.mApplication.isOnline())
							isBeaconCurrentTrip(firstSeenBusBeaconInfo);
						else if (mSharedPreferenceManager.hasCurrentTrip()) {
							Integer busstop = mSharedPreferenceManager.getCurrentBusStop();
							if (busstop != null)
								mSharedPreferenceManager.getCurrentTrip().calculateDelay(busstop, mApplication);
							SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
							Date date = new Date();
							final String day = yyyyMMdd.format(date);
							int seconds = (date.getHours() * 60 + date.getMinutes()) * 60;
							new Thread(new TripThread(firstSeenBusBeaconInfo.getLineId(), getBeaconLine(firstSeenBusBeaconInfo.getLineId()),
									firstSeenBusBeaconInfo.getTripId(), firstSeenBusBeaconInfo.getMajor(), day, seconds, mApplication, mSharedPreferenceManager.getCurrentTrip().getVirtualFeature())).start();
						}

					} else if (mSharedPreferenceManager.hasCurrentTrip())
						mSharedPreferenceManager.setCurrentTrip(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				mSharedPreferenceManager.setBusBeaconMap(mBusBeaconMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getBeaconLine(int liNr) {
		try {
			BusLine[] busLines;
			busLines = this.mApplication.getOpenDataStorage().getBusLines().getList();
			for (BusLine busLine : busLines) {
				if (busLine.getLI_NR() == liNr)
					return busLine.getShortName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public boolean isHandlerEnabled() {
		return true;
	}

	private String getBusStopName(int busStopId){
		try {
				return mApplication.getOpenDataStorage().getBusStations().findBusStop(busStopId).getBusStation().findName_it();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
