/**
 *
 * XMLBasicStop.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.sasabus.data.hafas;

import java.util.Date;

public class XMLBasicStop{
	
	private XMLStation station = null;
	
	private Date arrtime = null;
	
	private int index = 0;

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the station
	 */
	public XMLStation getStation() {
		return station;
	}

	/**
	 * @param station the station to set
	 */
	public void setStation(XMLStation station) {
		this.station = station;
	}

	/**
	 * @return the arrtime
	 */
	public Date getArrtime() {
		return arrtime;
	}

	/**
	 * @param arrtime the arrtime to set
	 */
	public void setArrtime(Date arrtime) {
		this.arrtime = arrtime;
	}
	
	
}
