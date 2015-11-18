/*
 * SASAbus - Android app for SASA bus open data
 *
 * IBeaconHandler.java
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
package it.sasabz.sasabus.beacon;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;

public interface IBeaconHandler {
	public void beaconInRange(String uuid, int major, int minor);
	public void beaconsInRange(Collection<Beacon> beacons);
	public void clearBeacons();
	public void inspectBeacons();
	public String getUUid();
	public String getIdentifier();
	public boolean isHandlerEnabled();
	
}
