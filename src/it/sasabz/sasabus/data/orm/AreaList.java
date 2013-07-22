/**
 *
 * BacinoList.java
 * 
 * Created: Dez 13, 2011 16:20:40 PM
 * 
 * Copyright (C) 2011 Markus Windegger
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

package it.sasabz.sasabus.data.orm;

import it.sasabz.sasabus.data.MySQLiteDBAdapter;
import it.sasabz.sasabus.data.models.Area;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.ui.SASAbus;

import java.util.ArrayList;
import java.util.Iterator;

import android.database.Cursor;

/**
 *List of Areas (bacini)
 */
public class AreaList {
	
	 
	/** 
	 * Searches for all areas in the database                                                                                                                                                                                                                                                                                                                       
	 * @return an ArrayList of Areas currently available in the database
	 * @throws MyError                                                                                                                              
	 */
	public static ArrayList<DBObject> getList() {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select * from  bacini", null);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			int i = 0;
			list = new ArrayList<DBObject>();
			do {
				Area element = new Area(cursor);
				list.add(i, element);
				++i;
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * Searches for a specific area in the database, in which the departure and arrival bus stop can be found
	 * @param departure is name of the bus stop where to start
	 * @param arrival is name of the bus stop where to stop
	 * @param linecode is the number of the line
	 * @return the Area object that matches the given parameters
	 */
	public static Area getArea(String departure, String arrival, String linecode) {
		Area area = null;
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		ArrayList<DBObject> list = getList();
		Iterator<DBObject> iterator = list.iterator();
		boolean found = false;
		while(iterator.hasNext() && !found) {
			Area nextArea = (Area)iterator.next();
			String[] args = {linecode, departure, arrival};
			Cursor cursor = sqlite.rawQuery("select * " +
    				"from "+
    				"(select id, lineaId " +
    				"from " + nextArea.getTable_prefix() + "corse as corse "+
    				"where " +
    				"lineaId = (" +
    				"Select id from " + nextArea.getTable_prefix() + "linee where num_lin = ?) ) as c, " +
    				"(select progressivo, orario, corsaId "+
    				"from " + nextArea.getTable_prefix() + "orarii "+
    				"where palinaId IN (" +
    				"select id from paline where nome_de = ?" +
    				")) as o1, " +
    				"(select progressivo , corsaId "+
    				"from " + nextArea.getTable_prefix() + "orarii " +
    				"where palinaId IN (" +
    				"select id from paline where nome_de = ?" +
    				")) as o2 " +
    				"where o1.progressivo < o2.progressivo " +
    				"and c.id = o1.corsaId " +
    				"and c.id = o2.corsaId " +
    				"LIMIT 1 ", 
    				args);
			if(cursor.moveToFirst()) {
				area = nextArea;
				found = true;
			}
			cursor.close();
		}
		sqlite.close();
		return area;
	}
	
	/**
	 * Searches in the database for a specific area by its ID
	 * @param id is the id of the area
	 * @return the Area object that matches the given id
	 */
	public static  Area  getById(int id) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String args[] = {Integer.toString(id)};
		Cursor cursor = sqlite.rawQuery("select * from  bacini where id = ?", args);
		Area area = null;
		if(cursor.moveToFirst()) {
			area = new Area(cursor);
		}
		cursor.close();
		sqlite.close();
		return area;
	}

}