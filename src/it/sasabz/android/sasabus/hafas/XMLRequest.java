package it.sasabz.android.sasabus.hafas;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.SASAbus;
import it.sasabz.android.sasabus.classes.SASAbusXML;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.Context;


public class XMLRequest {
	
	/**
	 * Requests a validation of a Station via the xml Interface of SASA
	 * @param bahnhof
	 * @return
	 */
	public static String locValRequest(String bahnhof)
	{
		String filecontent = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
					"<ReqC xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=" +
					"\"http://hafassrv.hacon.de/xml/hafasXMLInterface.xsd\" prod=\"manuell\" ver=\"1.1\" lang=\"DE\" "+
					"accessId=\"openSASA\">\n<LocValReq id=\"toInput\" >\n" +
					"<ReqLoc match=\"" + bahnhof + "\" type=\"ST\"/>\n" +
					"</LocValReq>\n" +
					"</ReqC>";
		return execute(filecontent);
	}
	
	
	/**
	 * Sends a HTTP-Post request to the xml-interface of the hafas travelplanner
	 * @param xml is the xml-file containing a xml-request for the hafas travelplanner
	 * @return the response of the hafas travelplanner xml interface
	 */
	private static String execute(String xml)
	{
		String ret = "";
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
