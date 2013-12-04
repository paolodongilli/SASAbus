/**
 *
 * XMLRequest.java
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


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.network.SASAbusXML;
import it.sasabz.sasabus.ui.SASAbus;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class XMLRequest {
	
	/**
	 * Requests a validation of a Station via the xml Interface of SASA
	 * @param bahnhof
	 * @return
	 */
	public static String locValRequest(String bahnhof)
	{
		String xmlrequest = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
					"<ReqC xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=" +
					"\"http://hafassrv.hacon.de/xml/hafasXMLInterface.xsd\" prod=\"manuell\" ver=\"1.1\" lang=\"DE\" "+
					"accessId=\"" + SASAbus.getContext().getResources().getString(R.string.accessId) + "\">\n<LocValReq id=\"toInput\" >\n" +
					"<ReqLoc match=\"" + bahnhof + "\" type=\"ST\"/>\n" +
					"</LocValReq>\n" +
					"</ReqC>";
		return execute(xmlrequest);
	}
	
	public static String conRequest(XMLStation from, XMLStation to, Date datetime)
	{
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");
		String xmlrequest = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
					"<ReqC xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=" +
					"\"http://hafassrv.hacon.de/xml/hafasXMLInterface.xsd\" prod=\"manuell\" ver=\"1.1\" lang=\"DE\" "+
					"accessId=\"" + SASAbus.getContext().getResources().getString(R.string.accessId) + "\">\n<ConReq><Start>" + from.toXMLString() + "<Prod bike=\"0\" couchette=\"0\" " +
					"direct=\"0\" sleeper=\"0\"/>" +
					"</Start><Dest>" + to.toXMLString() + "</Dest><ReqT a=\"0\" date=\"" + date.format(datetime) + "\" " +
					"time=\"" + time.format(datetime) + "\"/>\n" +
					"<RFlags b=\"0\" chExtension=\"0\" f=\"1\" sMode=\"N\"/>\n" +
					"</ConReq>\n</ReqC>";
		return execute(xmlrequest);
	}
	
	public static String conScrollRequest(String context, boolean forward)
	{
		String xmlrequest= "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
						"<ReqC xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=" +
						"\"http://hafassrv.hacon.de/xml/hafasXMLInterface.xsd\" prod=\"manuell\" ver=\"1.1\" lang=\"DE\" "+
						" accessId=\"" + SASAbus.getContext().getResources().getString(R.string.accessId) + "\">\n" +
						"<ConScrReq nrCons=\"1\" scrDir=\"";
		if (forward)
		{
			xmlrequest += "F";
		}
		else
		{
			xmlrequest += "B";
		}
		xmlrequest += "\"><ConResCtxt>" + context + "</ConResCtxt>\n</ConScrReq>\n</ReqC";
		return execute(xmlrequest);
	}
	
	/**
	 * this method checks if a network-connection is active or not
	 * @return boolean if the network is reachable or not
	 */
	public static boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (SASAbus.getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			//testing WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			//testing GPRS/EDGE/UMTS/HDSPA/HUSPA/LTE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	
	
	/**
	 * Sends a HTTP-Post request to the xml-interface of the hafas travelplanner
	 * @param xml is the xml-file containing a xml-request for the hafas travelplanner
	 * @return the response of the hafas travelplanner xml interface
	 */
	private static String execute(String xml)
	{
		String ret = "";
		if(!haveNetworkConnection())
		{
			return ret;
		}
		try {
			HttpClient http = new DefaultHttpClient();
			HttpPost post = new HttpPost(SASAbus.getContext().getString(R.string.xml_server));
			StringEntity se = new StringEntity(xml, HTTP.UTF_8);
			se.setContentType("text/xml");
			post.setEntity(se);
			
			HttpResponse response = http.execute(post);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				ret = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	
	public static boolean containsError(String xml)
	{
		SASAbusXML parser = new SASAbusXML();
		
		Document doc = parser.getDomElement(xml);
		
		NodeList nl = doc.getElementsByTagName("Err");
		
		if(nl.getLength() == 0)
		{
			return false;
		}
		return true;
		
	}
}
