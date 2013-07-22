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
package it.sasabz.android.sasabus.classes.services;


import it.sasabz.android.sasabus.InfoActivity;
import it.sasabz.android.sasabus.SASAbus;
import it.sasabz.android.sasabus.classes.Information;
import it.sasabz.android.sasabus.classes.dbobjects.DBObject;
import it.sasabz.android.sasabus.classes.network.SasabusHTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;


public class InformationList extends AsyncTask<Integer, Void, Vector<DBObject>> {
	
	private final InfoActivity activity;
	
	
	public InformationList(InfoActivity activity)
	{
		super();
		this.activity = activity;
	}

	@Override
	protected Vector<DBObject> doInBackground(Integer... params) {
		Vector <DBObject> list = null;
		try
		{
			
			String newsserver = SASAbus.getContext().getString(R.string.newsserver);
			SasabusHTTP http = new SasabusHTTP(newsserver);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("city", Integer.toString(params[0])));
			
			String xml = http.postData(nameValuePairs);
			if(xml == null)
			{
				throw new IOException("XML request string is NULL");
			}
			
			String [] stringarray = xml.split("<meldung>");
			
			String id = "";
			String titel_de = "";
			String titel_it = "";
			String nachricht_de = "";
			String nachricht_it = "";
			String stadt = "";
			for(int j = 1; j < stringarray.length;++j)
			{
				id = stringarray[j].substring(stringarray[j].indexOf("<id>") + 4, stringarray[j].indexOf("</id>"));
				titel_de = stringarray[j].substring(stringarray[j].indexOf("<titel_de>") + 10, stringarray[j].indexOf("</titel_de>"));
				titel_it = stringarray[j].substring(stringarray[j].indexOf("<titel_it>") + 10, stringarray[j].indexOf("</titel_it>"));
				nachricht_de = stringarray[j].substring(stringarray[j].indexOf("<nachricht_de>") + 14, stringarray[j].indexOf("</nachricht_de>"));
				nachricht_it = stringarray[j].substring(stringarray[j].indexOf("<nachricht_it>") + 14, stringarray[j].indexOf("</nachricht_it>"));
				stadt = stringarray[j].substring(stringarray[j].indexOf("<gebiet>") + 8, stringarray[j].indexOf("</gebiet>"));
				nachricht_de = nachricht_de.replaceAll("(\r\n|\n)", "<br />");
				nachricht_it = nachricht_it.replaceAll("(\r\n|\n)", "<br />");
				
				Information info = new Information(Integer.parseInt(id), titel_de, titel_it, nachricht_de, nachricht_it, Integer.parseInt(stadt));
				if(list == null)
				{
					list = new Vector<DBObject>();
				}
				list.add(info);
			}
			/**
			for(int j = 0; j < stringarray.length;++j)
			{
				if(stringarray[j].contains("<id>"))
				{
					id = stringarray[j].substring(4, stringarray[j].indexOf("</id>"));
				}
				else if(stringarray[j].contains("<titel_de>"))
				{
					titel_de = stringarray[j].substring(10, stringarray[j].indexOf("</titel_de>"));
				}
				else if(stringarray[j].contains("<titel_it>"))
				{
					titel_it = stringarray[j].substring(10, stringarray[j].indexOf("</titel_it>"));
				}
				else if(stringarray[j].contains("<nachricht_de>"))
				{
					nachricht_de += stringarray[j];
					while(stringarray[j].indexOf("</nachricht_de>") == -1)
					{
						++j;
						nachricht_de += stringarray[j];
					}
					if(stringarray[j].indexOf("</nachricht_de>") != -1)
						nachricht_de = stringarray[j].substring(14, stringarray[j].indexOf("</nachricht_de>"));
				}
				else if(stringarray[j].contains("<nachricht_it>"))
				{
					if(stringarray[j].indexOf("</nachricht_it>") != -1)
						nachricht_it = stringarray[j].substring(14, stringarray[j].indexOf("</nachricht_it>"));
					else
						nachricht_it = stringarray[j].substring(14);
				}
				else if(stringarray[j].contains("<gebiet>"))
				{
					stadt = stringarray[j].substring(8, stringarray[j].indexOf("</gebiet>"));
				}
				
				if(!id.equals("") && !titel_de.equals("") && !titel_it.equals("") && !nachricht_de.equals("")
						&& !nachricht_it.equals("") && !stadt.equals(""))
				{
					Information info = new Information(Integer.parseInt(id), titel_de, titel_it, nachricht_de, nachricht_it, Integer.parseInt(stadt));
					if(list == null)
					{
						list = new Vector<DBObject>();
					}
					list.add(info);
					
					id = "";
					titel_de = "";
					titel_it = "";
					nachricht_de = "";
					nachricht_it = "";
					stadt = "";
				}
			}
			*/
		}
		catch(Exception e)
		{
			Log.v("INFORMATION LIST", "FAILURE", e);
		}
		return list;
	}
	
	@Override
	protected void onPostExecute(Vector<DBObject> result) {
		super.onPostExecute(result);
		activity.fillList(result);

	}
	
	
}
