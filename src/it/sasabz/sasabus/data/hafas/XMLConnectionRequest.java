/**
 *
 * XMLConnectionRequest.java
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
import java.util.Vector;

public class XMLConnectionRequest {
	
	private String context = null;
	
	private XMLBasicStop departure = null;
	
	private XMLBasicStop arrival = null;
	
	/**
	 * This is connectionlist.lenght - 1
	 */
	private int transfers = 0;
	
	private Date duration = null;
	
	private Vector<XMLConnection> connectionlist = null;

	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * @return the departure
	 */
	public XMLBasicStop getDeparture() {
		return departure;
	}

	/**
	 * @param departure the departure to set
	 */
	public void setDeparture(XMLBasicStop departure) {
		this.departure = departure;
	}

	/**
	 * @return the arrival
	 */
	public XMLBasicStop getArrival() {
		return arrival;
	}

	/**
	 * @param arrival the arrival to set
	 */
	public void setArrival(XMLBasicStop arrival) {
		this.arrival = arrival;
	}

	/**
	 * @return the transfers
	 */
	public int getTransfers() {
		return transfers;
	}

	/**
	 * @param transfers the transfers to set
	 */
	public void setTransfers(int transfers) {
		this.transfers = transfers;
	}

	/**
	 * @return the duration
	 */
	public Date getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Date duration) {
		this.duration = duration;
	}

	/**
	 * @return the connectionlist
	 */
	public Vector<XMLConnection> getConnectionlist() {
		return connectionlist;
	}

	/**
	 * @param connectionlist the connectionlist to set
	 */
	public void setConnectionlist(Vector<XMLConnection> connectionlist) {
		this.connectionlist = connectionlist;
	}
	

}
