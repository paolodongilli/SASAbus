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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.BeaconScannerService;
import it.sasabz.sasabus.beacon.IBeaconHandler;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStop;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStopLocationCallback;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStopLocationHandler;
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
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesDepartureAdapter;

public class BusBeaconHandler implements IBeaconHandler {

    private SasaApplication mApplication;
    private SharedPreferenceManager mSharedPreferenceManager;
    private ISurveyAction mSurveyAction;
    public static TripNotificationAction mTripNotificationAction;
    public static HashMap<String, BusBeaconInfo> mBusBeaconMap;
    private BusStation gpsBusStop;
    private long gpsTime;

    public BusBeaconHandler(SasaApplication beaconApplication, ISurveyAction surveyAction,
                            TripNotificationAction tripNotificationAction) {
        mApplication = beaconApplication;
        mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
        mBusBeaconMap = mSharedPreferenceManager.getBusBeaconMap();
        mSurveyAction = surveyAction;
        mTripNotificationAction = tripNotificationAction;
        inspectBeacons();
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("execute", "runnable");
                        inspectBeacons();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 180000);
    }

    @Override
    public void beaconInRange(String uuid, int major, int minor) {
        final String key = uuid + "_" + major;
        final BusBeaconInfo beaconInfo;
        if (mBusBeaconMap.keySet().contains(key)) {
            beaconInfo = mBusBeaconMap.get(key);

            if (beaconInfo.getLastSeen().getTime()
                    + 10000 < Calendar.getInstance()
                    .getTimeInMillis()) {
                mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
            }
            beaconInfo.seen();
            mBusBeaconMap.put(key, beaconInfo);
            Log.d(SasaApplication.TAG, "reputkey: " + key);
            beaconInfo.seen();
            Log.d(SasaApplication.TAG, "Beacon has been seen for " + beaconInfo.getSeenSeconds());
            if (beaconInfo.getStartRealtimeApiTrackStation() == null && this.mApplication.isOnline())
                getBusInformation(major, beaconInfo);
        } else {
            Integer currentBusstop = mSharedPreferenceManager.getCurrentBusStop();
            beaconInfo = new BusBeaconInfo(uuid, major, minor, Calendar.getInstance().getTimeInMillis());
            beaconInfo.setStartBusstation(currentBusstop == null ? null : new TripBusStop(TripBusStop.TripBusStopType.BEACON, currentBusstop));
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
                        try {
                            final Feature busInformation = result.getLastFeature();
                            if (beaconInfo.getTripId() == null) {
                                beaconInfo.setBusInformation(busInformation);
                                mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
                            }
                            beaconInfo.setLastFeature(busInformation, mApplication);
                            if (System.currentTimeMillis() - gpsTime > 60000) {
                                TripBusStopLocationHandler tripBusStopLocationHandler = new TripBusStopLocationHandler(mApplication, new TripBusStopLocationCallback() {
                                    @Override
                                    public void onSuccess(BusStation busStation) {
                                        gpsBusStop = busStation;
                                        gpsTime = System.currentTimeMillis();
                                        if (beaconInfo.getStartBusstation() == null ||
                                                beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API) {
                                            BusStop busStop = getTripBusStop(busStation, beaconInfo.getBusDepartureItem());
                                            if (busStop != null)
                                                beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.GPS,
                                                        busStop.getORT_NR()));
                                        }
                                        synchronized (this){
                                            this.notifyAll();
                                        }
                                    }

                                    @Override
                                    public void onFailure() {
                                        synchronized (this){
                                            this.notifyAll();
                                        }
                                    }
                                });
                                tripBusStopLocationHandler.locate();
                                synchronized (tripBusStopLocationHandler){
                                    tripBusStopLocationHandler.wait(3000);
                                }
                            }
                            else {
                                if (beaconInfo.getStartBusstation() == null ||
                                        beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API) {
                                    BusStop busStop = getTripBusStop(gpsBusStop, beaconInfo.getBusDepartureItem());
                                    if (busStop != null)
                                        beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.GPS,
                                                busStop.getORT_NR()));
                                }

                            }

                            if (mSharedPreferenceManager.getCurrentBusStop() != null &&
                                    (beaconInfo.getStartBusstation() == null ||
                                            beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API))
                                beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.BEACON,
                                        mSharedPreferenceManager.getCurrentBusStop()));
                            if(busInformation.getProperties() != null) {
                                if (beaconInfo.getStartBusstation() == null)
                                    beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
                                            result.getLastFeature().getProperties().getNextStopNumber()));
                                if (beaconInfo.getSeenSeconds() > 20){
                                    if (beaconInfo.getStartRealtimeApiTrackStation() == null)
                                        beaconInfo.setStartRealtimeApiTrackStation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
                                                result.getLastFeature().getProperties().getNextStopNumber()));
                                    if (beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API) {
                                        BusDepartureItem departureItem = beaconInfo.getBusDepartureItem();
                                        beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
                                                departureItem.getStopTimes()[departureItem.getSelectedIndex()].getBusStop()));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
        if (System.currentTimeMillis() - gpsTime > 60000)
            if (mSharedPreferenceManager.hasCurrentTripWitoutTimeout()) {
                new TripBusStopLocationHandler(mApplication, new TripBusStopLocationCallback() {
                    @Override
                    public void onSuccess(BusStation busStation) {
                        gpsBusStop = busStation;
                        gpsTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onFailure() {

                    }
                }).locate();
            }
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
        beaconsInRange(new ArrayList<Beacon>());

    }

    private void deleteUnvisibleBeacons() {
        try {
            synchronized (mBusBeaconMap) {
                Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();
                CurentTrip curentTrip = mSharedPreferenceManager.getCurrentTrip();

                while (iterator.hasNext()) {
                    Map.Entry<String, BusBeaconInfo> pair = (Map.Entry<String, BusBeaconInfo>) iterator.next();
                    final BusBeaconInfo beaconInfo = pair.getValue();
                    if (beaconInfo.getLastSeen().getTime()
                            + mApplication.getConfigManager().getValue("beacon_buslastSeenTreshold", 300000) < Calendar.getInstance()
                            .getTimeInMillis()) {
                        mBusBeaconMap.remove(pair.getKey());
                        mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
                        if (mSharedPreferenceManager.hasCurrentTripWitoutTimeout() &&
                                curentTrip.getBusId() == pair.getValue().getMajor()) {
                            if (beaconInfo.getSeenSeconds() > mApplication.getConfigManager()
                                    .getValue("beacon_secondsInBus", 120)) {
                                TripsSQLiteOpenHelper.getInstance(mApplication).addTrip(new FinishedTrip(beaconInfo.getStartBusstation().getBusStopId(), beaconInfo.getStopBusstation().getBusStopId(),
                                        beaconInfo.getLineId(), beaconInfo.getTripId(), curentTrip.getTagesart_nr(), beaconInfo.getStartDate(),
                                        beaconInfo.getLastSeen()));
                            }
                            mSharedPreferenceManager.setCurrentTrip(null);
                        }
                    } else if (beaconInfo.getLastSeen().getTime()
                            + 10000 < Calendar.getInstance()
                            .getTimeInMillis()) {
                        mApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
                        if (mSharedPreferenceManager.hasCurrentTripWitoutTimeout() &&
                                curentTrip.getBusId() == pair.getValue().getMajor()) {
                            curentTrip.setNotificationShown(false);
                            NotificationManager notificationManager = (NotificationManager) mApplication.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(2);
                            if (beaconInfo.getStopBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API && beaconInfo.getLastSeen().getTime()
                                    + 30000 < Calendar.getInstance().getTimeInMillis())
                                if (mSharedPreferenceManager.getCurrentBusStop() != null)
                                    beaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.BEACON,
                                            mSharedPreferenceManager.getCurrentBusStop()));
                                else if (System.currentTimeMillis() - gpsTime < 60000) {
                                    BusStop busStop = getTripBusStop(gpsBusStop, curentTrip
                                            .getBeaconInfo().getBusDepartureItem());
                                    if (busStop != null)
                                        beaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.GPS,
                                                busStop.getORT_NR()));
                                }
                            curentTrip.setBeaconInfo(beaconInfo);
                            if(!curentTrip.isSurvayTriggered()) {
                                isBeaconSuitableForSurvey(curentTrip);
                            }
                            mSharedPreferenceManager.setCurrentTrip(curentTrip);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the given beacon is suitable for a survey
     *
     * @param beaconInfo
     * @param callback
     */
/*    private void isBeaconSuitableForSurvey(final BusBeaconInfo beaconInfo, final IBeaconSuitableCallback callback) {
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
                                    beaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
                                            busInformation.getProperties().getNextStopNumber()));
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
    }*/

    private void isBeaconSuitableForSurvey(CurentTrip curentTrip) {
        Log.wtf("isBeaconSuitableForSurvey", mApplication.isOnline()+" && "+checkLastSurveyTime()+" && "+(curentTrip.getBeaconInfo().getSeenSeconds() > mApplication.getConfigManager()
                .getValue("beacon_secondsInBus", 120)));
        if(curentTrip.getBeaconInfo().getStartBusstation() != null && mApplication.isOnline() &&
                checkLastSurveyTime() && curentTrip.getBeaconInfo().getSeenSeconds() > mApplication.getConfigManager()
                .getValue("beacon_secondsInBus", 120)) {
            mSurveyAction.triggerSurvey(curentTrip.getBeaconInfo());
            curentTrip.setSurvayTriggered(true);
        }
    }

    /**
     * Checks if the given beacon is suitable for a survey
     *
     * @param beaconInfo
     */
    private void isBeaconCurrentTrip(final BusBeaconInfo beaconInfo) {
        Log.d("isBeaconCurrentTrip", "" + beaconInfo.getStartBusstation() + " " +
                beaconInfo.getStartRealtimeApiTrackStation() + " " +
                (beaconInfo.getLastFeature() != null? beaconInfo.getLastFeature().getProperties().getNextStopNumber() : ""));
        if (beaconInfo.getStartBusstation() != null || beaconInfo.getStartRealtimeApiTrackStation() != null)
            BusApiService.getInstance(mApplication).getBusInformation(beaconInfo.getMajor(),
                    new IApiCallback<BusInformationResult>() {

                        @Override
                        public void onSuccess(BusInformationResult result) {
                            if (BeaconScannerService.isAlive && result.hasFeatures()) {
                                Feature busInformation = result.getLastFeature();

                                if (busInformation != null && busInformation.getProperties() != null) {
                                    Feature.Properties lastProperties = beaconInfo.getLastFeature().getProperties();
                                    beaconInfo.setLastFeature(busInformation, mApplication);
                                    if((beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.BEACON ||
                                            beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.GPS)
                                            && mSharedPreferenceManager.getCurrentBusStop() != null
                                            && !getBusStopName(beaconInfo.getStartBusstation().getBusStopId()).equals(
                                            getBusStopName(mSharedPreferenceManager.getCurrentBusStop()))
                                            || beaconInfo.getStartRealtimeApiTrackStation() != null && beaconInfo.getStartRealtimeApiTrackStation().getBusStopId()
                                            != lastProperties.getNextStopNumber()) {
                                        CurentTrip curentTrip = new CurentTrip(beaconInfo, mApplication);
                                        curentTrip.setLastFeatures(busInformation, mApplication);
                                        if(mSharedPreferenceManager.hasCurrentTripWitoutTimeout() && mSharedPreferenceManager.getCurrentTrip().getBeaconInfo().getMajor() != beaconInfo.getMajor()){
                                            BusBeaconInfo preBeaconInfo =  mSharedPreferenceManager.getCurrentTrip().getBeaconInfo();
                                            if (preBeaconInfo.getSeenSeconds() > mApplication.getConfigManager()
                                                    .getValue("beacon_secondsInBus", 120)) {
                                                TripsSQLiteOpenHelper.getInstance(mApplication).addTrip(new FinishedTrip(preBeaconInfo.getStartBusstation().getBusStopId(), preBeaconInfo.getStopBusstation().getBusStopId(),
                                                        preBeaconInfo.getLineId(), preBeaconInfo.getTripId(), mSharedPreferenceManager.getCurrentTrip().getTagesart_nr(), preBeaconInfo.getStartDate(),
                                                        preBeaconInfo.getLastSeen()));
                                            }
                                        }
                                        mSharedPreferenceManager.setCurrentTrip(curentTrip);
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
                if ((firstSeenBusBeaconInfo == null
                        || beaconInfo.getStartDate().before(firstSeenBusBeaconInfo.getStartDate()))
                        && beaconInfo.getLastSeen().getTime() + 30000 > System.currentTimeMillis())
                    firstSeenBusBeaconInfo = beaconInfo;
            }
            Log.d(SasaApplication.TAG, "beaconssize: " + in);
            Log.d(SasaApplication.TAG,
                    firstSeenBusBeaconInfo == null ? "beacon: null" : "beacon: " + firstSeenBusBeaconInfo.getMajor());
            if (firstSeenBusBeaconInfo != null) {
                if (mSharedPreferenceManager.hasCurrentTripWitoutTimeout() && mSharedPreferenceManager.getCurrentTrip().getBeaconInfo().getMajor() == firstSeenBusBeaconInfo.getMajor()) {
                    if ((firstSeenBusBeaconInfo.getLastSeen().getTime()
                            + 10000 < Calendar.getInstance()
                            .getTimeInMillis())) {
                        Integer busstop = mSharedPreferenceManager.getCurrentBusStop();
                        if (busstop != null) {
                            if (mSharedPreferenceManager.getCurrentTrip().calculateDelay(busstop, mApplication)) {
                                firstSeenBusBeaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.BEACON,
                                        busstop));
                                Feature feature = mSharedPreferenceManager.getCurrentTrip().getVirtualFeature();
                                firstSeenBusBeaconInfo.setLastFeature(feature, mApplication);
                            }
                        } else if (firstSeenBusBeaconInfo.getLastFeature() != null) {
                            BusDepartureItem departureItem = firstSeenBusBeaconInfo.getBusDepartureItem();
                            firstSeenBusBeaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
                                    departureItem.getStopTimes()[departureItem.getSelectedIndex()].getBusStop()));
                        }
                    } else {
                    Log.d("seen", (firstSeenBusBeaconInfo.getLastSeen().getTime()
                            + 10000-Calendar.getInstance()
                            .getTimeInMillis())+"");
                        CurentTrip curentTrip = mSharedPreferenceManager.getCurrentTrip();
                        curentTrip.setBeaconInfo(firstSeenBusBeaconInfo);
                        if (!curentTrip.isNotificationShown()) {
                            curentTrip.setNotificationShown(true);
                            BusBeaconHandler.mTripNotificationAction.showNotification();
                        }
                        if (this.mApplication.isOnline()) {
                            if (!curentTrip.isGcmRegisterd()) {
                                Log.d("gcmRegId",""+mSharedPreferenceManager.getGcmRegId());
                                if (mApplication.checkGooglePlayServicesAvailable() && mSharedPreferenceManager.getGcmRegId() != null)
                                    registerGcmTrackService(firstSeenBusBeaconInfo.getMajor());
                                curentTrip.findRealtimePosition(mApplication);
                            }
                        } else
                            curentTrip.calculateFeaterByBeaconBusStop(mApplication);
                        mSharedPreferenceManager.setCurrentTrip(curentTrip);
                    }
                } else {
                    isBeaconCurrentTrip(firstSeenBusBeaconInfo);
                }
            } /*else if (mSharedPreferenceManager.hasCurrentTrip())
                mSharedPreferenceManager.setCurrentTrip(null);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSharedPreferenceManager.setBusBeaconMap(mBusBeaconMap);
    }

    private void registerGcmTrackService(final int vehiclcode) {
        new Thread(){
            public void run(){
                BufferedReader br = null;
                try {
                    URL url = new URL(mApplication.getConfigManager().getValue("gcm_service_registration_url","http://gcmtest.opensasa.info/registration.php") + "?gcmregid=" + URLEncoder.encode(mSharedPreferenceManager.getGcmRegId()) + "&vehiclecode=" + vehiclcode);
                    br = new BufferedReader(new InputStreamReader(url.openStream()));
                    if(br.readLine().equals("OK")){
                        CurentTrip curentTrip = mSharedPreferenceManager.getCurrentTrip();
                        if(curentTrip.getBeaconInfo().getMajor() == vehiclcode) {
                            curentTrip.setGcmRegisterd(true);
                            mSharedPreferenceManager.setCurrentTrip(curentTrip);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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

    public static ArrayAdapter<? extends Object> getDepartureAdapter(SasaApplication mApplication) {
        if (mBusBeaconMap != null) {
            ArrayList<BusDepartureItem> departureItems = new ArrayList<BusDepartureItem>();
            Iterator<Entry<String, BusBeaconInfo>> iterator = mBusBeaconMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, BusBeaconInfo> pair = (Entry<String, BusBeaconInfo>) iterator.next();
                final BusBeaconInfo beaconInfo = pair.getValue();
                if (beaconInfo.getBusDepartureItem() != null && beaconInfo.getLastSeen().getTime()
                        + 10000 > Calendar.getInstance().getTimeInMillis())
                    departureItems.add(beaconInfo.getBusDepartureItem());
            }
            if (!departureItems.isEmpty())
                return new BusSchedulesDepartureAdapter(mApplication, departureItems);
        }
        return new ArrayAdapter<String>(mApplication, android.R.layout.simple_list_item_1);
    }

    public BusStop getTripBusStop(BusStation busStation, BusDepartureItem departureItem) {
        try {
            for (int i = 0; i < departureItem.getStopTimes().length; i++)
                for (BusStop stop : busStation.getBusStops())
                    if (departureItem.getStopTimes()[i].getBusStop() == stop.getORT_NR())
                        return stop;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }



    private String getBusStopName(int busStopId) {
        try {
            return mApplication.getOpenDataStorage().getBusStations().findBusStop(busStopId).getBusStation().findName_it();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}