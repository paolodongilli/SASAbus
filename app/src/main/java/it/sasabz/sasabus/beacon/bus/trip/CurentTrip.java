package it.sasabz.sasabus.beacon.bus.trip;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature.Properties;
import it.sasabz.sasabus.opendata.client.model.BusDayType;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class CurentTrip implements Serializable {

	private BusBeaconInfo beaconInfo;
	private int tagesart_nr;
	private Properties beaconDelay;

	public CurentTrip(BusBeaconInfo beaconInfo, SasaApplication mApplication) {
		this.beaconInfo = beaconInfo;
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

	public boolean checkUpdate(CurentTrip trip) {
		Feature.Properties oldProperties = trip.beaconInfo.getLastFeature().properties;
		Feature.Properties newProperties = this.beaconInfo.getLastFeature().properties;
		return oldProperties.getFrtFid() != newProperties.getFrtFid() || oldProperties.getDelay() / 60 != newProperties.getDelay() / 60
				|| oldProperties.getNextStopNumber() != newProperties.getNextStopNumber();
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

	public int getTagesart_nr() {
		return tagesart_nr;
	}

	public int getBusId() {
		return beaconInfo.getMajor();
	}
}
