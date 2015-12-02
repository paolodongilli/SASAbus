package it.sasabz.sasabus.beacon.bus.trip;

import java.util.Date;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature.Properties;
import it.sasabz.sasabus.opendata.client.model.BusStop;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class CurentTrip {

	private BusDepartureItem busDepartureItem;
	private int color = 0;
	private int tripId = 0;
	private int busId = 0;
	private int tagesart_nr;

	public CurentTrip(BusDepartureItem busDepartureItem, int color, int tripId, int busId, int tagesart_nr) {
		this.busDepartureItem = busDepartureItem;
		this.color = color;
		this.tripId = tripId;
		this.busId = busId;
		this.tagesart_nr = tagesart_nr;
	}

	public BusDepartureItem getBusDepartureItem() {
		return busDepartureItem;
	}

	public void setBusDepartureItem(BusDepartureItem busDepartureItem) {
		this.busDepartureItem = busDepartureItem;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean checkUpdate(CurentTrip trip) {
		BusDepartureItem busDepartureItem = trip.getBusDepartureItem();
		return tripId != trip.tripId || busDepartureItem.getDelayNumber() != this.busDepartureItem.getDelayNumber()
				|| busDepartureItem.getDeparture_index() != this.busDepartureItem.getDeparture_index();
	}

	public Feature getVirtualFeature() {
		Feature ret = new Feature();
		Properties properties = new Properties();
		Date gpsDate = new Date();
		gpsDate.setTime(gpsDate.getTime() - 3600000);
		properties.gpsDate = gpsDate;
		properties.delay = busDepartureItem.getDelayNumber() * 60;
		properties.liColorRed = (color & 0x00ff0000) >> 16;
		properties.liColorGreen = (color & 0x0000ff00) >> 8;
		properties.lineColorBlue = color & 0x000000ff;
		ret.properties = properties;
		return ret;
	}

	public void calculateDelay(int busstop, SasaApplication mSasaApplication) {
		BusStop[] busstops = new BusStop[0];
		try {
			busstops = mSasaApplication.getOpenDataStorage().getBusStations().findBusStop(busstop).getBusStation()
					.getBusStops();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < busDepartureItem.getStopTimes().length; i++) {
			for (BusStop bs : busstops)
				if (busDepartureItem.getStopTimes()[i].getBusStop() == bs.getORT_NR()) {
					Date now = new Date();
					busDepartureItem.setDelay((now.getHours() * 3600 + now.getMinutes() * 60 + now.getSeconds()
					- busDepartureItem.getStopTimes()[i].getSeconds()) / 60);
					return;
				}
		}
	}

	public int getTagesart_nr() {
		return tagesart_nr;
	}

	public int getBusId() {
		return busId;
	}
}
