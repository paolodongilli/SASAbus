/**
 *
 * InformationList.java
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
package it.sasabz.android.sasabus.classes;


import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.SASAbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class InformationList {
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static  Vector <DBObject>  getList() throws ClientProtocolException, IOException
	{
		Vector <DBObject> list = null;
		SASAbusXML parser = new SASAbusXML();
		
		String newsserver = SASAbus.getContext().getString(R.string.newsserver);
		SasabusHTTP http = new SasabusHTTP(newsserver);
		String xml = http.postData();
		if(xml == null)
		{
			throw new IOException("XML request string is NULL");
		}
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName("meldung");
		 
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			if(list == null)
			{
				list = new Vector<DBObject>();
			}
			Element e = (Element) nl.item(i);
			
			String id = parser.getValue(e, "id");
			String titel_de = parser.getValue(e, "titel_de"); // name child value
		    String titel_it = parser.getValue(e, "titel_it"); // cost child value
		    String nachricht_de = parser.getValue(e, "nachricht_de"); // description child value
		    String nachricht_it = parser.getValue(e, "nachricht_it");
		    String stadt = parser.getValue(e, "gebiet");
			
			Information info = new Information(Integer.parseInt(id), titel_de, titel_it, nachricht_de, nachricht_it, Integer.parseInt(stadt));
			
			list.add(info);
				
		}
		
		
		return list;
	}
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static DBObject getById(int newsId) throws ClientProtocolException, IOException
	{
		SASAbusXML parser = new SASAbusXML();
		
		String newsserver = SASAbus.getContext().getString(R.string.newsserver);
		SasabusHTTP http = new SasabusHTTP(newsserver);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("id", Integer.toString(newsId)));
		
		String xml = http.postData(nameValuePairs);
		if(xml == null)
		{
			throw new IOException("XML request string is NULL");
		}
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName("meldung");
		 
		Information info = null;
		
		// looping through all item nodes <item>
		if(nl.getLength() > 0) 
		{
			Element e = (Element) nl.item(0);
			
			String id = parser.getValue(e, "id");
			String titel_de = parser.getValue(e, "titel_de"); // name child value
		    String titel_it = parser.getValue(e, "titel_it"); // cost child value
		    String nachricht_de = parser.getValue(e, "nachricht_de"); // description child value
		    String nachricht_it = parser.getValue(e, "nachricht_it");
		    String stadt = parser.getValue(e, "gebiet");
			
			info = new Information(Integer.parseInt(id), titel_de, titel_it, nachricht_de, nachricht_it, Integer.parseInt(stadt));	
		}
		
		
		return info;
	}
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static Vector<DBObject> getByCity(int city) throws ClientProtocolException, IOException
	{
		Vector <DBObject> list = null;
		SASAbusXML parser = new SASAbusXML();
		
		String newsserver = SASAbus.getContext().getString(R.string.newsserver);
		SasabusHTTP http = new SasabusHTTP(newsserver);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("city", Integer.toString(city)));
		
		String xml = http.postData(nameValuePairs);
		if(xml == null)
		{
			throw new IOException("XML request string is NULL");
		}
		Document doc = parser.getDomElement(xml); // getting DOM element
		
		NodeList nl = doc.getElementsByTagName("meldung");
		 
		// looping through all item nodes <item>
		for (int i = 0; i < nl.getLength(); i++) {
			if(list == null)
			{
				list = new Vector<DBObject>();
			}
			Element e = (Element) nl.item(i);
			
			String id = parser.getValue(e, "id");
			String titel_de = parser.getValue(e, "titel_de"); // name child value
		    String titel_it = parser.getValue(e, "titel_it"); // cost child value
		    String nachricht_de = parser.getValue(e, "nachricht_de"); // description child value
		    String nachricht_it = parser.getValue(e, "nachricht_it");
		    String stadt = parser.getValue(e, "gebiet");
			
			Information info = new Information(Integer.parseInt(id), titel_de, titel_it, nachricht_de, nachricht_it, Integer.parseInt(stadt));
			
			list.add(info);
				
		}
		
		
		return list;
	}
	
	
	
}
