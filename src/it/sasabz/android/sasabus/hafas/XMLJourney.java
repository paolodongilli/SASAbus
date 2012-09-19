package it.sasabz.android.sasabus.hafas;

import java.util.Iterator;
import java.util.Vector;

public class XMLJourney extends XMLConnection{
	
	private Vector<XMLAttributVariante> attributlist = null;
	
	private Vector<XMLBasicStop> stoplist = null;

	/**
	 * @return the attributlist
	 */
	public Vector<XMLAttributVariante> getAttributlist() {
		return attributlist;
	}

	/**
	 * @param attributlist the attributlist to set
	 */
	public void setAttributlist(Vector<XMLAttributVariante> attributlist) {
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
	
	public String getAttribut(String attrname)
	{
		String ret = "";
		Iterator<XMLAttributVariante> attriter = attributlist.iterator();
		while (attriter.hasNext())
		{
			XMLAttributVariante item = attriter.next();
			if(item.getType().equalsIgnoreCase(attrname))
			{
				ret = item.getText();
				break;
			}
		}
		return ret;
	}
}
