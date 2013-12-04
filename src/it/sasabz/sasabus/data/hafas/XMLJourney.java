/**
 *
 * XMLJourney.java
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
