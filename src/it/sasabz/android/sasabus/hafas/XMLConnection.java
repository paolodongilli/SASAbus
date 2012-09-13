package it.sasabz.android.sasabus.hafas;

import java.util.Date;

public abstract class XMLConnection {
	
	private Date duration = null;
	
	private XMLBasicStop departure = null;
	
	private XMLBasicStop arrival = null;

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
	
	
}
