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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.altbeacon.beacon.Beacon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.BeaconScannerService;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.beacon.bus.trip.TripNotificationAction;
import it.sasabz.sasabus.beacon.survey.IBeaconSuitableCallback;
import it.sasabz.sasabus.beacon.survey.ISurveyAction;
import it.sasabz.sasabus.beacon.survey.ISurveyLocationCallback;
import it.sasabz.sasabus.beacon.survey.SurveyLocationHandler;
import it.sasabz.sasabus.data.trips.FinishedTrip;
import it.sasabz.sasabus.data.trips.TripsSQLiteOpenHelper;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.service.BusApiService;
import it.sasabz.sasabus.logic.TripThread;
import it.sasabz.sasabus.opendata.client.model.BusLine;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesDepartureAdapter;

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

			if (beaconInfo.getLastSeen().getTime()
					+ 10000 < Calendar.getInstance()
					.getTimeInMillis())
				mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
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
						final Feature busInformation = result.getFirstFeature();
						beaconInfo.setBusInformation(busInformation);

						Log.d("beaconInfoSeen", "" + beaconInfo.getSeenSeconds());
						if (busInformation != null && busInformation.getProperties() != null
								&& beaconInfo.getSeenSeconds() > 20) {
							beaconInfo.setStartBusstationId(result.getLastFeature().getProperties().getNextStopNumber());
						}else{
							SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");
							Date date = new Date();
							final String day = yyyyMMdd.format(date);
							int seconds = (date.getHours() * 60 + date.getMinutes()) * 60;
							final TripThread tripThread = new TripThread(beaconInfo.getLineId(), getBeaconLine(beaconInfo.getLineId()),
									beaconInfo.getTripId(), beaconInfo.getMajor(), day, seconds, mApplication, busInformation);
							tripThread.setPostExecute(new Runnable(){
								public void run(){
									if(beaconInfo.getBusDepartureItem() == null) {
										beaconInfo.setBusDepartureItem(tripThread.getBusDepartureItem());
										mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
									}
								}
							});
							new Thread(tripThread).start();
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
					synchronized (this) {
						try {
							this.wait(30000);
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
		try {
			synchronized (mBusBeaconMap) {
				Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<String, BusBeaconInfo> pair = (Map.Entry<String, BusBeaconInfo>) iterator.next();
					final BusBeaconInfo beaconInfo = pair.getValue();
					if (beaconInfo.getLastSeen().getTime()
							+ mApplication.getConfigManager().getValue("beacon_buslastSeenTreshold", 30000) < Calendar.getInstance()
							.getTimeInMillis()) {
						mBusBeaconMap.remove(pair.getKey());
						mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
						if (mSharedPreferenceManager.hasCurrentTrip() &&
								mSharedPreferenceManager.getCurrentTrip().getBusId() == pair.getValue().getMajor()) {
							if (beaconInfo.getSeenSeconds() > mApplication.getConfigManager()
									.getValue("beacon_secondsInBus", 120)) {
								TripsSQLiteOpenHelper.getInstance(mApplication).addTrip(new FinishedTrip(beaconInfo.getStartBusstationId(), beaconInfo.getStopBusstationId(),
										beaconInfo.getLineId(), beaconInfo.getTripId(), mSharedPreferenceManager.getCurrentTrip().getTagesart_nr(), beaconInfo.getStartDate(),
										beaconInfo.getLastSeen()));
							}
							mSharedPreferenceManager.setCurrentTrip(null);
						}
					} else if (beaconInfo.getLastSeen().getTime()
							+ 10000 < Calendar.getInstance()
							.getTimeInMillis()) {
						mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
						if(mSharedPreferenceManager.hasCurrentTrip() &&
								mSharedPreferenceManager.getCurrentTrip().getBusId() == pair.getValue().getMajor()) {
							NotificationManager notificationManager = (NotificationManager) mApplication.getSystemService(Context.NOTIFICATION_SERVICE);
							notificationManager.cancel(2);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
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

								beaconInfo.setStopBusstationId(result.getFirstFeature().getProperties().getNextStopNumber());
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
										final Feature.Properties properties = busInformation.getProperties();
										final int color = 0xff000000 | properties.getLiColorRed() << 16 | properties.getLiColorGreen() << 8 | properties.getLineColorBlue();
										final TripThread tripThread = new TripThread(beaconInfo.getLineId(), getBeaconLine(beaconInfo.getLineId()),
												beaconInfo.getTripId(), beaconInfo.getMajor(), day, seconds, mApplication, busInformation);
										tripThread.setPostExecute(new Runnable(){
											public void run(){
												CurentTrip curentTrip = new CurentTrip(tripThread.getBusDepartureItem(),
														color, properties.getFrtFid(), beaconInfo.getMajor(), tripThread.getDayType());
												mApplication.getSharedPreferenceManager().setCurrentTrip(curentTrip);
											}
										});
										new Thread(tripThread).start();
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
			Iterator<Map.Entry<String, BusBeaconInfo>> busIterator = mBusBeaconMap.entrySet().iterator();
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
				if((firstSeenBusBeaconInfo.getLastSeen().getTime()
						+ 10000 > Calendar.getInstance()
						.getTimeInMillis())) {
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
						Feature feature = mSharedPreferenceManager.getCurrentTrip().getVirtualFeature();
						final Feature.Properties properties = feature.getProperties();
						final int color = 0xff000000 | properties.getLiColorRed() << 16 | properties.getLiColorGreen() << 8 | properties.getLineColorBlue();
						final TripThread tripThread = new TripThread(firstSeenBusBeaconInfo.getLineId(), getBeaconLine(firstSeenBusBeaconInfo.getLineId()),
								firstSeenBusBeaconInfo.getTripId(), firstSeenBusBeaconInfo.getMajor(), day, seconds, mApplication, feature);
						final BusBeaconInfo beaconInfo = firstSeenBusBeaconInfo;
						tripThread.setPostExecute(new Runnable(){
							public void run(){
								CurentTrip curentTrip = new CurentTrip(tripThread.getBusDepartureItem(),
										color, properties.getFrtFid(), beaconInfo.getMajor(), tripThread.getDayType());
								mApplication.getSharedPreferenceManager().setCurrentTrip(curentTrip);
							}
						});
						new Thread(tripThread).start();
					}
				}
			} /*else if (mSharedPreferenceManager.hasCurrentTrip())
				mSharedPreferenceManager.setCurrentTrip(null);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		mSharedPreferenceManager.setBusBeaconMap(mBusBeaconMap);
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

	public static ArrayAdapter<? extends Object> getDepartureAdapter(Context context){
		if(mBusBeaconMap != null){
			ArrayList<BusDepartureItem> departureItems = new ArrayList<BusDepartureItem>();
			Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, BusBeaconInfo> pair = (Entry<String, BusBeaconInfo>) iterator.next();
				final BusBeaconInfo beaconInfo = pair.getValue();
				if(beaconInfo.getBusDepartureItem() != null && beaconInfo.getLastSeen().getTime()
						+ 10000 > Calendar.getInstance().getTimeInMillis())
					departureItems.add(beaconInfo.getBusDepartureItem());
			}
			if(!departureItems.isEmpty())
				return new BusSchedulesDepartureAdapter(context, departureItems);
		}
		return new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
	}
}
