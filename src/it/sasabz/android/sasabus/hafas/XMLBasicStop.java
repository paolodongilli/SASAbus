package it.sasabz.android.sasabus.hafas;

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
