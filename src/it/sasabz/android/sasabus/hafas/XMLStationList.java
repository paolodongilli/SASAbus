/**
 *
 * XMLStationList.java
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
package it.sasabz.android.sasabus.hafas;

import it.sasabz.android.sasabus.classes.SASAbusXML;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLStationList {
	
	public static Vector<XMLStation> getList(String pattern)
	{
		Vector<XMLStation> list = null;
		String xml = XMLRequest.locValRequest(pattern);
		if(xml == "" || XMLRequest.containsError(xml))
		{
			Log.e("XML-ERROR", xml);
			return list;
		}
		SASAbusXML parser = new SASAbusXML();
		
		Document doc = parser.getDomElement(xml);
		
		NodeList nl = doc.getElementsByTagName("Station");
		
		for(int i = 0; i < nl.getLength(); ++i)
		{
			Node node = nl.item(i);
			if(node.hasAttributes())
			{
				if(list == null)
				{
					list = new Vector<XMLStation>();
				}
				XMLStation station = new XMLStation();
				NamedNodeMap attributelist = node.getAttributes();
				for(int j = 0; j < attributelist.getLength(); ++ j)
				{
					station.setProperty(attributelist.item(j).getNodeName(), attributelist.item(j).getNodeValue());
				}
				list.add(station);
			}
		}
		return list;
	}
}
