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
