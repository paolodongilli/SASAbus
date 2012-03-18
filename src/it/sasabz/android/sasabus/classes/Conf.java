/**
 * 
 *
 * XmlReader.java
 * 
 * Created: 17.03.2012 15:01:37
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.acl.LastOwnerException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.os.Environment;
import android.util.AndroidRuntimeException;
import android.util.Log;

public class Conf {
	
	private static String filename = "";
	
	private static boolean init = false;
	
	private static Hashtable<String, Property> liste = new Hashtable<String, Property>();

	/**
	 * @return the filename
	 */
	public static String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public static void setFilename(String filename) {
		Conf.filename = filename;
	}
	
	
	public static Property getByName(String name) throws Exception
	{
		if(!Conf.init)
		{
			init();
		}
		return Conf.liste.get(name);
	}
	
	public static boolean putInto(Property property) throws Exception
	{
		if(!Conf.init)
		{
			init();
		}
		if(property == null)
			return false;
		Conf.liste.put(property.getName(), property);
		return true;
	}

	public static void init() throws Exception {
		if (Conf.filename == null || Conf.filename == "")
			throw new Exception("filename not set");
		if(Conf.liste.size() != 0)
			return;
		File xml = null;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			throw new AndroidRuntimeException(
					"External storage (SD-Card) not mounted");
		}
		Resources res = SASAbus.getContext().getResources();
		String filepath = res.getString(R.string.conf_dir);
		File appDir = new File(Environment.getExternalStorageDirectory(),
				filepath);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		xml = new File(appDir, filename);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = null;
		builder = builderFactory.newDocumentBuilder();
		Document document = null;
		try 
		{
			document = builder.parse(new FileInputStream(xml));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			Conf.createFile();
			document = builder.parse(new FileInputStream(xml));
		}
		Element element = document.getDocumentElement();

		NodeList nodes = element.getElementsByTagName("config");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				// a child element to process
				Element child = (Element) node;
				Property newprop = new Property();
				newprop.setName(child.getAttribute("name"));
				Log.v("DEBUG ---===--- Name", child.getAttribute("name"));
				
				NodeList list = child.getElementsByTagName("value");
				Element element1 = (Element) list.item(0);
				NodeList fstNm = element1.getChildNodes();
				newprop.setValue((fstNm.item(0)).getNodeValue());
				Log.v("DEBUG ---===--- Text Value: ", "Value: " + (fstNm.item(0)).getNodeValue());
				
				list = child.getElementsByTagName("comment");
				element1 = (Element) list.item(0);
				fstNm = element1.getChildNodes();
				newprop.setComment((fstNm.item(0)).getNodeValue());
				Log.v("DEBUG ---===--- Text Comment: ", "Comment: " + (fstNm.item(0)).getNodeValue());
				
				Conf.liste.put(newprop.getName(), newprop);
			}
		}
		if(Conf.liste.size() == 0)
		{
			throw new Exception("Conf initialisation failed, 0 elements in the hashtable");
		}
		Conf.init = true;
	}
	
	public static void storeConf() throws Exception
	{
		if(Conf.liste.size() == 0)
		{
			throw new Exception("Cannot store an empty list");
		}
		Resources res = SASAbus.getContext().getResources();
		String filepath = res.getString(R.string.conf_dir);
		File appDir = new File(Environment.getExternalStorageDirectory(),
				filepath);
		if (!appDir.exists()) {
			appDir.mkdirs();
		}
		File xml = new File(appDir, filename);
		if(!xml.exists())
		{
			xml.createNewFile();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(xml));
		Enumeration<Property> enumer = liste.elements();
		out.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		out.newLine();
		out.write("<configs>");
		out.newLine();
		while(enumer.hasMoreElements())
		{
			Property prop = enumer.nextElement();
			out.write(prop.toString());
			out.newLine();
		}
		out.write("</configs>");
		out.close();
		Conf.init = true;
	}
	
	
	
	public static void createFile() throws Exception
	{
		Resources res = SASAbus.getContext().getResources();
		
		liste.clear();
		
		Property mode = new Property();
		mode.setName(res.getString(R.string.conf_mode_name));
		mode.setValue(res.getString(R.string.conf_mode_value));
		mode.setComment(res.getString(R.string.conf_mode_comment));
		liste.put(mode.getName(), mode);
		
		Property delta_long = new Property();
		delta_long.setName(res.getString(R.string.conf_delta_long_name));
		delta_long.setValue(res.getString(R.string.conf_delta_long_value));
		delta_long.setComment(res.getString(R.string.conf_delta_long_comment));
		liste.put(delta_long.getName(), delta_long);
		
		Property delta_lat = new Property();
		delta_lat.setName(res.getString(R.string.conf_delta_lat_name));
		delta_lat.setValue(res.getString(R.string.conf_delta_lat_value));
		delta_lat.setComment(res.getString(R.string.conf_delta_lat_comment));
		liste.put(delta_lat.getName(), delta_lat);
		
		Property delta = new Property();
		delta.setName(res.getString(R.string.conf_delta_name));
		delta.setValue(res.getString(R.string.conf_delta_value));
		delta.setComment(res.getString(R.string.conf_delta_comment));
		liste.put(delta.getName(), delta);
		
		Conf.storeConf();
	}
	
}
