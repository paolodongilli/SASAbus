package it.sasabz.android.sasabus.hafas;

import it.sasabz.android.sasabus.classes.SASAbusXML;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLConnectionRequestList {
	
	public static XMLConnectionRequest getRequest(XMLStation from, XMLStation to, Date datetime)
	{
		XMLConnectionRequest req = new XMLConnectionRequest();
		String xml = XMLRequest.conRequest(from, to, datetime);
		if(xml == "" || XMLRequest.containsError(xml))
		{
			Log.e("XML-ERROR", xml);
			return null;
		}
		SASAbusXML parser = new SASAbusXML();
		
		Document doc = parser.getDomElement(xml);
		
		NodeList nl = doc.getElementsByTagName("ConResCtxt");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			Node node = nl.item(0).getChildNodes().item(0);	
			req.setContext(node.getNodeValue());
		}
		
		
		nl = doc.getElementsByTagName("Overview");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			NodeList overview = nl.item(0).getChildNodes();
			for(int i = 0; i < overview.getLength(); ++ i)
			{
				Node node = overview.item(i);
				if(node.getNodeName().equals("Departure"))
				{
					XMLBasicStop departurestop = new XMLBasicStop();
					NodeList departure = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < departure.getLength(); ++ j)
					{
						Node depnode = departure.item(j);
						if(depnode.getNodeName().equals("Station"))
						{
							XMLStation depstat = new XMLStation();
							NamedNodeMap attributelist = depnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								depstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							departurestop.setStation(depstat);
						}
						else if(depnode.getNodeName().equals("Dep"))
						{
							NodeList dep = depnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node depnode2 = dep.item(k);
								if(depnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										departurestop.setArrtime(simple.parse((depnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setDeparture(departurestop);
					}
				}
				else if (node.getNodeName().equals("Arrival"))
				{
					XMLBasicStop arrivalstop = new XMLBasicStop();
					NodeList arrival = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < arrival.getLength(); ++ j)
					{
						Node arrnode = arrival.item(j);
						if(arrnode.getNodeName().equals("Station"))
						{
							XMLStation arrstat = new XMLStation();
							NamedNodeMap attributelist = arrnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								arrstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							arrivalstop.setStation(arrstat);
						}
						else if(arrnode.getNodeName().equals("Arr"))
						{
							NodeList dep = arrnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node arrnode2 = dep.item(k);
								if(arrnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										arrivalstop.setArrtime(simple.parse((arrnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setArrival(arrivalstop);
					}
				}
				else if (node.getNodeName().equals("Transfers"))
				{
					req.setTransfers(Integer.parseInt(node.getChildNodes().item(0).getNodeValue()));
				}
				else if(node.getNodeName().equals("Duration"))
				{
					SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
					try
					{
						req.setDuration(simple.parse((node.getChildNodes().item(0).getChildNodes().item(0).getNodeValue()).substring(3)));
					}
					catch(Exception e)
					{
						Log.e("XML-LOGGER", "Datumskonvertierung falsch", e);
					}
				}
			}
			
		}
		
		nl = doc.getElementsByTagName("ConSectionList");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			NodeList conlist = nl.item(0).getChildNodes();
			Vector<XMLConnection> convect = new Vector<XMLConnection>();
			for(int i = 0; i < conlist.getLength(); ++ i)
			{
				Node conlistitem = conlist.item(i);
				if(conlistitem.getNodeName().equals("ConSection"))
				{
					XMLBasicStop departurestop = null;
					XMLBasicStop arrivalstop = null;
					XMLConnection con = null;
					NodeList consection = conlistitem.getChildNodes();
					for(int k = 0; k < consection.getLength(); ++ k)
					{
						Node node = consection.item(k);
						if(node.getNodeName().equals("Departure"))
						{
							departurestop = new XMLBasicStop();
							NodeList departure = node.getChildNodes().item(0).getChildNodes();
							for(int j = 0; j < departure.getLength(); ++ j)
							{
								Node depnode = departure.item(j);
								if(depnode.getNodeName().equals("Station"))
								{
									XMLStation depstat = new XMLStation();
									NamedNodeMap attributelist = depnode.getAttributes();
									for(int n = 0; n < attributelist.getLength(); ++n)
									{
										depstat.setProperty(attributelist.item(n).getNodeName(), attributelist.item(n).getNodeValue());
									}
									departurestop.setStation(depstat);
								}
								else if(depnode.getNodeName().equals("Dep"))
								{
									NodeList dep = depnode.getChildNodes();
									for(int n = 0; n < dep.getLength(); ++ n)
									{
										Node depnode2 = dep.item(n);
										if(depnode2.getNodeName().equals("Time"))
										{
											SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
											try
											{
												departurestop.setArrtime(simple.parse((depnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
											}
											catch(Exception e)
											{
												Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
											}
										}
									}
								}
							}
						}
						else if (node.getNodeName().equals("Arrival"))
						{
							arrivalstop = new XMLBasicStop();
							NodeList arrival = node.getChildNodes().item(0).getChildNodes();
							for(int j = 0; j < arrival.getLength(); ++ j)
							{
								Node arrnode = arrival.item(j);
								if(arrnode.getNodeName().equals("Station"))
								{
									XMLStation arrstat = new XMLStation();
									NamedNodeMap attributelist = arrnode.getAttributes();
									for(int n = 0; n < attributelist.getLength(); ++n)
									{
										arrstat.setProperty(attributelist.item(n).getNodeName(), attributelist.item(n).getNodeValue());
									}
									arrivalstop.setStation(arrstat);
								}
								else if(arrnode.getNodeName().equals("Arr"))
								{
									NodeList dep = arrnode.getChildNodes();
									for(int n = 0; n < dep.getLength(); ++ n)
									{
										Node arrnode2 = dep.item(n);
										if(arrnode2.getNodeName().equals("Time"))
										{
											SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
											try
											{
												arrivalstop.setArrtime(simple.parse((arrnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
											}
											catch(Exception e)
											{
												Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
											}
										}
									}
								}
								req.setArrival(arrivalstop);
							}
						}
						else if(node.getNodeName().equals("Journey"))
						{
							con = new XMLJourney();
						}
						else if (node.getNodeName().equals("Walk"))
						{
							con = new XMLWalk();
							NodeList walklist = node.getChildNodes();
							for(int m = 0; m < walklist.getLength(); ++ m)
							{
								Node walklistitem = walklist.item(m);
								if(walklistitem.getNodeName().equals("Duration"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										con.setDuration(simple.parse((walklistitem.getChildNodes().item(0).getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
					}
					if(con == null)
					{
						Log.v("XML-LOGGER", "No connection type detected!");
						System.exit(-3);
					}
					con.setDeparture(departurestop);
					con.setArrival(arrivalstop);
					if(con instanceof XMLJourney)
					{
						Date duration = new Date(arrivalstop.getArrtime().getTime() - departurestop.getArrtime().getTime());
						con.setDuration(duration);
					}
					convect.add(con);
				}
				req.setConnectionlist(convect);
			}
		}
		return req;
	}
	
	public static XMLConnectionRequest scrollForward(XMLConnectionRequest request)
	{
		XMLConnectionRequest req = new XMLConnectionRequest();
		String xml = XMLRequest.conScrollRequest(request.getContext(), true);
		if(xml == "" || XMLRequest.containsError(xml))
		{
			Log.e("XML-ERROR", xml);
			return null;
		}
		SASAbusXML parser = new SASAbusXML();
		
		Document doc = parser.getDomElement(xml);
		
		NodeList nl = doc.getElementsByTagName("ConResCtxt");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			Node node = nl.item(0).getChildNodes().item(0);	
			req.setContext(node.getNodeValue());
		}
		
		
		nl = doc.getElementsByTagName("Overview");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			NodeList overview = nl.item(0).getChildNodes();
			for(int i = 0; i < overview.getLength(); ++ i)
			{
				Node node = overview.item(i);
				if(node.getNodeName().equals("Departure"))
				{
					XMLBasicStop departurestop = new XMLBasicStop();
					NodeList departure = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < departure.getLength(); ++ j)
					{
						Node depnode = departure.item(j);
						if(depnode.getNodeName().equals("Station"))
						{
							XMLStation depstat = new XMLStation();
							NamedNodeMap attributelist = depnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								depstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							departurestop.setStation(depstat);
						}
						else if(depnode.getNodeName().equals("Dep"))
						{
							NodeList dep = depnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node depnode2 = dep.item(k);
								if(depnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										departurestop.setArrtime(simple.parse((depnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setDeparture(departurestop);
					}
				}
				else if (node.getNodeName().equals("Arrival"))
				{
					XMLBasicStop arrivalstop = new XMLBasicStop();
					NodeList arrival = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < arrival.getLength(); ++ j)
					{
						Node arrnode = arrival.item(j);
						if(arrnode.getNodeName().equals("Station"))
						{
							XMLStation arrstat = new XMLStation();
							NamedNodeMap attributelist = arrnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								arrstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							arrivalstop.setStation(arrstat);
						}
						else if(arrnode.getNodeName().equals("Arr"))
						{
							NodeList dep = arrnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node arrnode2 = dep.item(k);
								if(arrnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										arrivalstop.setArrtime(simple.parse((arrnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setArrival(arrivalstop);
					}
				}
				else if (node.getNodeName().equals("Transfers"))
				{
					req.setTransfers(Integer.parseInt(node.getChildNodes().item(0).getNodeValue()));
				}
				else if(node.getNodeName().equals("Duration"))
				{
					SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
					try
					{
						req.setDuration(simple.parse((node.getChildNodes().item(0).getChildNodes().item(0).getNodeValue()).substring(3)));
					}
					catch(Exception e)
					{
						Log.e("XML-LOGGER", "Datumskonvertierung falsch", e);
					}
				}
			}
			
		}
		return req;
	}
	
	public static XMLConnectionRequest scrollBackward(XMLConnectionRequest request)
	{
		XMLConnectionRequest req = new XMLConnectionRequest();
		String xml = XMLRequest.conScrollRequest(request.getContext(), false);
		if(xml == "" || XMLRequest.containsError(xml))
		{
			Log.e("XML-ERROR", xml);
			return null;
		}
		SASAbusXML parser = new SASAbusXML();
		
		Document doc = parser.getDomElement(xml);
		
		NodeList nl = doc.getElementsByTagName("ConResCtxt");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			Node node = nl.item(0).getChildNodes().item(0);	
			req.setContext(node.getNodeValue());
		}
		
		
		nl = doc.getElementsByTagName("Overview");
		
		if(nl.item(0) != null && nl.getLength() >= 1)
		{
			NodeList overview = nl.item(0).getChildNodes();
			for(int i = 0; i < overview.getLength(); ++ i)
			{
				Node node = overview.item(i);
				if(node.getNodeName().equals("Departure"))
				{
					XMLBasicStop departurestop = new XMLBasicStop();
					NodeList departure = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < departure.getLength(); ++ j)
					{
						Node depnode = departure.item(j);
						if(depnode.getNodeName().equals("Station"))
						{
							XMLStation depstat = new XMLStation();
							NamedNodeMap attributelist = depnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								depstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							departurestop.setStation(depstat);
						}
						else if(depnode.getNodeName().equals("Dep"))
						{
							NodeList dep = depnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node depnode2 = dep.item(k);
								if(depnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										departurestop.setArrtime(simple.parse((depnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setDeparture(departurestop);
					}
				}
				else if (node.getNodeName().equals("Arrival"))
				{
					XMLBasicStop arrivalstop = new XMLBasicStop();
					NodeList arrival = node.getChildNodes().item(0).getChildNodes();
					for(int j = 0; j < arrival.getLength(); ++ j)
					{
						Node arrnode = arrival.item(j);
						if(arrnode.getNodeName().equals("Station"))
						{
							XMLStation arrstat = new XMLStation();
							NamedNodeMap attributelist = arrnode.getAttributes();
							for(int k = 0; k < attributelist.getLength(); ++k)
							{
								arrstat.setProperty(attributelist.item(k).getNodeName(), attributelist.item(k).getNodeValue());
							}
							arrivalstop.setStation(arrstat);
						}
						else if(arrnode.getNodeName().equals("Arr"))
						{
							NodeList dep = arrnode.getChildNodes();
							for(int k = 0; k < dep.getLength(); ++ k)
							{
								Node arrnode2 = dep.item(k);
								if(arrnode2.getNodeName().equals("Time"))
								{
									SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
									try
									{
										arrivalstop.setArrtime(simple.parse((arrnode2.getChildNodes().item(0).getNodeValue()).substring(3)));
									}
									catch(Exception e)
									{
										Log.e("XML-LOGGER", "Datumkonvertierungsfehler", e);
									}
								}
							}
						}
						req.setArrival(arrivalstop);
					}
				}
				else if (node.getNodeName().equals("Transfers"))
				{
					req.setTransfers(Integer.parseInt(node.getChildNodes().item(0).getNodeValue()));
				}
				else if(node.getNodeName().equals("Duration"))
				{
					SimpleDateFormat simple = new SimpleDateFormat("HH:mm:ss");
					try
					{
						req.setDuration(simple.parse((node.getChildNodes().item(0).getChildNodes().item(0).getNodeValue()).substring(3)));
					}
					catch(Exception e)
					{
						Log.e("XML-LOGGER", "Datumskonvertierung falsch", e);
					}
				}
			}
			
		}
		return req;
	}
}
