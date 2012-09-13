package it.sasabz.android.sasabus.hafas;

import java.util.Vector;

public class XMLJourney extends XMLConnection{
	
	private Vector<XMLAttribut> attributlist = null;
	
	private Vector<XMLBasicStop> stoplist = null;

	/**
	 * @return the attributlist
	 */
	public Vector<XMLAttribut> getAttributlist() {
		return attributlist;
	}

	/**
	 * @param attributlist the attributlist to set
	 */
	public void setAttributlist(Vector<XMLAttribut> attributlist) {
		this.attributlist = attributlist;
	}

	/**
	 * @return the stoplist
	 */
	public Vector<XMLBasicStop> getStoplist() {
		return stoplist;
	}

	/**
	 * @param stoplist the stoplist to set
	 */
	public void setStoplist(Vector<XMLBasicStop> stoplist) {
		this.stoplist = stoplist;
	}
}
