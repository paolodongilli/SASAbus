package it.sasabz.sasabus.beacon.bus.trip;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.BeaconScannerService;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature.Properties;
import it.sasabz.sasabus.gson.bus.service.BusApiService;
import it.sasabz.sasabus.logic.TripThread;
import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class CurentTrip implements Serializable {

	private BusBeaconInfo beaconInfo;
	private Feature lastFeature;
	private int tagesart_nr;
	private boolean gcmRegisterd = false;
	private Properties beaconDelay;
	private BusDepartureItem oldDepartureItem = null;
	private boolean notificationShown = false;
	private boolean survayTriggered = false;

	public CurentTrip(BusBeaconInfo beaconInfo, SasaApplication mApplication) {
		this.beaconInfo = beaconInfo;
		this.lastFeature = beaconInfo.getLastFeature();
		try {
			BusDayType calendarDay = mApplication.getOpenDataStorage().getBusDayTypeList().findBusDayTypeByDay(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			if (calendarDay != null)
				this.tagesart_nr = calendarDay.getDayTypeId();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public BusBeaconInfo getBeaconInfo() {
		return beaconInfo;
	}

	public void setBeaconInfo(BusBeaconInfo beaconInfo) {
		this.beaconInfo = beaconInfo;
	}

	public int getColor() {
		Feature.Properties properties = beaconInfo.getLastFeature().properties;
		return 0xff000000 | properties.getLiColorRed() << 16 | properties.getLiColorGreen() << 8 | properties.getLineColorBlue();
	}

	public boolean checkUpdate() {
		BusDepartureItem newDepartureItem = this.beaconInfo.getBusDepartureItem();
		return oldDepartureItem == null || oldDepartureItem.getStopTimes()[0].getSeconds() != newDepartureItem.getStopTimes()[0].getSeconds() ||
				oldDepartureItem.getDelayNumber() != newDepartureItem.getDelayNumber() ||
				oldDepartureItem.getSelectedIndex() != newDepartureItem.getSelectedIndex();
	}

	public Feature getVirtualFeature() {
		Properties properties = beaconInfo.getLastFeature().getProperties();
		if(beaconDelay != null && beaconDelay.getGpsDate().getTime() > properties.getGpsDate().getTime())
			properties = beaconDelay;
		Feature ret = new Feature();
		ret.properties = properties;
		return ret;
	}

	public boolean calculateDelay(int busstop, SasaApplication mSasaApplication) {
		BusStop[] busstops = new BusStop[0];
		try {
			busstops = mSasaApplication.getOpenDataStorage().getBusStations().findBusStop(busstop).getBusStation()
					.getBusStops();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BusDepartureItem busDepartureItem = beaconInfo.getBusDepartureItem();//TODO;
		Properties preProperties = beaconInfo.getLastFeature().getProperties();
		for (int i = 0; i < busDepartureItem.getStopTimes().length; i++) {
			for (BusStop bs : busstops)
				if (busDepartureItem.getStopTimes()[i].getBusStop() == bs.getORT_NR()) {
					Date now = new Date();
					int delay = now.getHours() * 3600 + now.getMinutes() * 60 + now.getSeconds()
							- busDepartureItem.getStopTimes()[i].getSeconds();
					int nextDepartureIndex = busDepartureItem.getStopTimes().length - 1 == i ? i : i + 1;
					if (beaconDelay == null || beaconDelay.getNextStopNumber() != nextDepartureIndex)
						beaconDelay = new Properties(preProperties.getFrtFid(), delay, preProperties.getLineNumber(),
								preProperties.getLineName(), getColor(), busDepartureItem.getStopTimes()[
								nextDepartureIndex].getBusStop());
					beaconDelay.nextStopNumber = i + 1 < busDepartureItem.getStopTimes().length?
							busDepartureItem.getStopTimes()[i + 1].getBusStop():
							busDepartureItem.getStopTimes()[i].getBusStop();
					return true;
				}
		}
		return false;
	}
	public void setOldDepartureItem(BusDepartureItem oldDepartureItem) {
		this.oldDepartureItem = oldDepartureItem;
	}

	public int getTagesart_nr() {
		return tagesart_nr;
	}

	public int getBusId() {
		return beaconInfo.getMajor();
	}

	public boolean isGcmRegisterd() {
		return gcmRegisterd;
	}

	public void setGcmRegisterd(boolean gcmRegisterd) {
		this.gcmRegisterd = gcmRegisterd;
	}

	public boolean isNotificationShown() {
		return notificationShown;
	}

	public void setNotificationShown(boolean notificationShown) {
		this.notificationShown = notificationShown;
	}

	public void setLastFeatures(Feature lastFeature, SasaApplication mSasaApplication){
		SharedPreferenceManager mSharedPreferenceManager = mSasaApplication.getSharedPreferenceManager();
		Properties lastProperties = lastFeature.getProperties();
		if (beaconInfo.getTripId() != lastProperties.getFrtFid()) {
			if (mSharedPreferenceManager.getCurrentTrip() == null &&
					beaconInfo.getStartBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API)
				beaconInfo.setStartBusstation(new TripBusStop(TripBusStop.TripBusStopType.REALTIME_API,
						lastProperties.getNextStopNumber()));
			beaconInfo.setLineId(lastProperties.getLineNumber());
			beaconInfo.setLineName(lastProperties.getLineName());
			beaconInfo.setTripId(lastProperties.getFrtFid());
		}
		Log.d("realtimeAPITrack", beaconInfo.getStartRealtimeApiTrackStation().getBusStopId()+" != "+lastProperties.getNextStopNumber()
				+ " " + beaconInfo.getStartBusstation().getTripBusStopType() + " " + beaconInfo.getStartBusstation().getBusStopId());
		beaconInfo.setLastFeature(lastFeature, mSasaApplication);
		mSharedPreferenceManager.setCurrentTrip(this);
	}

	public boolean isSurvayTriggered() {
		return survayTriggered;
	}

	public void setSurvayTriggered(boolean survayTriggered) {
		this.survayTriggered = survayTriggered;
	}



	public void calculateFeaterByBeaconBusStop(SasaApplication mApplication){
		SharedPreferenceManager mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
		CurentTrip curentTrip = mSharedPreferenceManager.getCurrentTrip();
		if (curentTrip != null &&
				curentTrip.getBeaconInfo().getTripId() == beaconInfo.getTripId()) {
			Integer busstop = mSharedPreferenceManager.getCurrentBusStop();
			if (busstop != null)
				if(curentTrip.calculateDelay(busstop, mApplication)) {
					Feature feature = curentTrip.getVirtualFeature();
					beaconInfo.setLastFeature(feature, mApplication);
					mApplication.getSharedPreferenceManager().setCurrentTrip(new CurentTrip(beaconInfo, mApplication));
					return;
				}
			TripThread tripThread = new TripThread(beaconInfo, mApplication, beaconInfo.getLastFeature());
			beaconInfo.setBusDepartureItem(tripThread.getBusDepartureItem());
		}
	}

	public void findRealtimePosition(final SasaApplication mApplication) {
		BusApiService.getInstance(mApplication).getBusInformation(beaconInfo.getMajor(),
				new IApiCallback<BusInformationResult>() {

					@Override
					public void onSuccess(BusInformationResult result) {
						if (BeaconScannerService.isAlive && result.hasFeatures()) {
							Feature busInformation = result.getLastFeature();

							if (busInformation != null && busInformation.getProperties() != null) {
								setLastFeatures(busInformation, mApplication);
							}

						}
					}

					@Override
					public void onFailure(Exception e) {

					}
				});
	}
}
