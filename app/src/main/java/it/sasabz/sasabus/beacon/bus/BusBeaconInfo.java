/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyBeaconInfo.java
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

import android.location.Location;

import java.io.Serializable;
import java.util.Date;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStop;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;
import it.sasabz.sasabus.logic.TripThread;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class BusBeaconInfo implements Serializable {

	private TripBusStop startRealtimeApiTrackStation;
	private BusDepartureItem busDepartureItem = null;
    private Feature lastFeature;
	private String uuid;
	private int minor;
	private int major;
	private long startDate;
	private long seconds;
	private Double latitude;
	private Double longitude;
	private long locationTime;
	private Integer tripId;
	private String lineName;
	private Integer lineId;
	private long lastSeen;
	private TripBusStop startBusstation = null;
	private TripBusStop stopBusstation;

	public BusBeaconInfo(String uuid, int major, int minor, long time) {
		this(uuid, major, minor, null, null, time, null, null, null, null);
	}

	public BusBeaconInfo(String uuid, int major, int minor, Double longitude, Double latitude, long time, Integer tripId,
						 String lineName, Integer lineId, TripBusStop startRealtimeApiTrackStation) {
		this.uuid = uuid;
		this.minor = minor;
		this.major = major;
		this.seconds = 0;
		this.startDate = new Date().getTime();
		this.latitude = latitude;
		this.longitude = longitude;
		this.locationTime = time;
		this.tripId = tripId;
		this.lineName = lineName;
		this.lineId = lineId;
		this.startRealtimeApiTrackStation = startRealtimeApiTrackStation;
		seen();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public long getSeenSeconds() {
		return seconds;
	}

	public Location getLocation() {
		Location location = new Location("BusApi");
		if (longitude != null && latitude != null) {
			location.setLongitude(longitude);
			location.setLongitude(latitude);
		}
		location.setTime(locationTime);
		return location;
	}

	public void setLocation(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.locationTime = location.getTime();
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void seen() {
		Date now = new Date();
		seconds = (now.getTime() - getStartDate().getTime()) / 1000;
		lastSeen = now.getTime();
	}

	public Integer getTripId() {
		return this.tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getLineName() {
		return this.lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public Date getLastSeen() {
		return new Date(this.lastSeen);
	}

	/**
	 * Sets longitude, latitude, ... from bus information
	 * @param busInformation
	 */
	public void setBusInformation(Feature busInformation) {
		setLongitude(busInformation.getGeometry().getCoordinates().get(0));
		setLatitude(busInformation.getGeometry().getCoordinates().get(1));
		setTripId(busInformation.getProperties().getFrtFid());
		setLineName(busInformation.getProperties().getLineName());
		setLineId(busInformation.getProperties().getLineNumber());
	}

	public void setStopBusstation(TripBusStop stopBusstation) {
		this.stopBusstation = stopBusstation;
	}

	public TripBusStop getStopBusstation() {
		return this.stopBusstation;
	}

	public void setStartBusstation(TripBusStop startBusstation) {
		this.startBusstation = startBusstation;
	}

	public Date getStartDate(){
		return new Date(startDate);
	}

	public TripBusStop getStartBusstation() {
		return startBusstation;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public TripBusStop getStartRealtimeApiTrackStation() {
		return startRealtimeApiTrackStation;
	}

	public BusDepartureItem getBusDepartureItem() {
		return busDepartureItem;
	}

	public void setBusDepartureItem(BusDepartureItem busDepartureItem) {
		this.busDepartureItem = busDepartureItem;
	}

	public void setLastFeature(Feature lastFeature, SasaApplication mApplication) {
		this.lastFeature = lastFeature;
		final TripThread tripThread = new TripThread(this, mApplication, lastFeature);
		this.busDepartureItem = tripThread.getBusDepartureItem();
	}

    public Feature getLastFeature(){
        return lastFeature;
    }

	public void setStartRealtimeApiTrackStation(TripBusStop startRealtimeApiTrackStation) {
		this.startRealtimeApiTrackStation = startRealtimeApiTrackStation;
	}
}