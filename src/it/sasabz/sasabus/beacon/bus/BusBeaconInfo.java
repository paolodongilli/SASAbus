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

import java.util.Date;

import android.location.Location;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult.Feature;

public class BusBeaconInfo {

	private Integer nearestStartStation;
	private String uuid;
	private int minor;
	private int major;
	private Date startDate;
	private long seconds;
	private Location location;
	private Integer tripId;
	private String lineName;
	private Integer lineId;
	private Date lastSeen;
	private int startBusstationId;
	private int stopBusstationId;

	public BusBeaconInfo(String uuid, int major, int minor, long time, Integer nearestStartStation) {
		this(uuid, major, minor, null, null, time, null, null, null, nearestStartStation);
	}

	public BusBeaconInfo(String uuid, int major, int minor, Double longitude, Double latitude, long time, Integer tripId,
			String lineName, Integer lineId, Integer nearestStartStation) {
		this.uuid = uuid;
		this.minor = minor;
		this.major = major;
		this.seconds = 0;
		this.startDate = new Date();
		this.location = new Location("BusApi");
		if (longitude != null && latitude != null) {
			this.location.setLongitude(longitude);
			this.location.setLongitude(latitude);
		}
		this.location.setTime(time);
		this.tripId = tripId;
		this.lineName = lineName;
		this.lineId = lineId;
		this.setNearestStartStation(nearestStartStation);
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
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setLongitude(double longitude) {
		this.location.setLongitude(longitude);
	}

	public void setLatitude(double latitude) {
		this.location.setLatitude(latitude);
	}

	public void seen() {
		Date now = new Date();
		seconds = (now.getTime() - startDate.getTime()) / 1000;
		lastSeen = now;
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
		return this.lastSeen;
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

	public void setStopBusstationId(int stopBusstationId) {
		this.stopBusstationId = stopBusstationId;
	}
	
	public int getStopBusstationId() {
		return this.stopBusstationId;
	}

	public void setStartBusstationId(int stopBusstationId) {
		this.startBusstationId = stopBusstationId;
	}

	public Date getStartDate(){
		return startDate;
	}

	public int getStartBusstationId() {
		return this.startBusstationId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public Integer getNearestStartStation() {
		return nearestStartStation;
	}

	public void setNearestStartStation(Integer nearestStartStation) {
		this.nearestStartStation = nearestStartStation;
	}
}
