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
package it.sasabz.sasabus.beacon.busstop;

import java.util.Date;

public class BusStopBeaconInfo {

	private String uuid;
	private int major;
	private Date startDate;
	private long seconds;
	private Date lastSeen;

	public BusStopBeaconInfo(String uuid, int major, long time) {
		this.uuid = uuid;
		this.major = major;
		this.seconds = 0;
		this.startDate = new Date();
		seen();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public void seen() {
		Date now = new Date();
		seconds = (now.getTime() - startDate.getTime()) / 1000;
		lastSeen = now;
	}
	
	public Date getLastSeen() {
		return this.lastSeen;
	}

	public Date getStartDate() {
		return startDate;
	}
}
