/**
 * 
 * LineaList.java
 * 
 * Created: 13.12.2011 20:13:31
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
package it.sasabz.sasabus.data.orm;

import it.sasabz.sasabus.data.Config;
import it.sasabz.sasabus.data.MySQLiteDBAdapter;
import it.sasabz.sasabus.data.models.BusLine;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.ui.SASAbus;

import java.util.ArrayList;

import android.database.Cursor;

/**
 *List of Bus lines (Linea)
 */
public class BusLineList {
	
	
	/**                                                                                                                                                                                                          
	 * Searches for all bus lines (linee) in the database                                                                                                                     
	 * @return an ArrayList of all bus lines (linee) currently available in the database                                                                                           
	 */
	public static ArrayList<DBObject> getList() {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select * from linee", null);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusLine element = new BusLine(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus lines (linee) which are located in the area (bacino)
	 * @param table_prefix is the prefix of the table
	 * @return an ArrayList of bus lines (linee) with the bus line (linea) located in the area (bacino)
	 */
	public static ArrayList<DBObject> getList(String table_prefix) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = null;
		Cursor cursor = sqlite.rawQuery("select * from " + table_prefix + "linee order by num_lin", args);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusLine element = new BusLine(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus lines (linee) which are connecting the departure (palina) with the arrival (palina)
	 * @param arrival is the name of the end bus stop (palina)
	 * @param departure is the name of the start bus stop (palina)
	 * @return an ArrayList of bus lines (linee) which connect the departure bus stop with the arrival bus stopo
	 */
	public static ArrayList<DBObject> getBusLinesForArrivalAndDeparture(String arrival, String departure) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {arrival, departure};
		
		/**
		 * This is a special query to provide the cases which the departure is 
		 * yesterday near midnight and the destination is now, for showing non-negativ
		 * differences
		 */
		Cursor cursor = sqlite.rawQuery("select distinct l.id as id, l.num_lin as num_lin, l.abbrev as abbrev, " +
				"l.bacinoId as bacinoId, l.descr_it as descr_it, l.descr_de as descr_de " +
				" from linee l, " +
				"(select * from orarii where palinaId IN (" +
				"select id from paline where nome_de = ?" +
				")) as o1, " +
				"(select * from orarii where palinaId IN (" +
				"select id from paline where nome_de = ?" +
				")) as o2, " +
				"(select id, lineaId " +
				"from corse " +
				"where  " +
				"substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' " + 
				") as c " +
				"where c.lineaId = l.id " +
				"and o1.corsaId = o2.corsaId " +
				"and o2.corsaId = c.id " +
				"and o2.progressivo < o1.progressivo " +
				"order by l.abbrev", args);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusLine element = new BusLine(cursor);
				if(!list.contains(element))
					list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Sorts an ArrayList of bus lines
	 * @param list is the list of bus lines (linee) to sort
	 * @return the sorted list of bus lines (linee)
	 */
	public static ArrayList<DBObject> sort(ArrayList<DBObject> list) {
		int j, i;
		try {
			for (j=list.size(); j > 1;--j) {
				for(i=0;i < j-1;++i) {
					BusLine current = (BusLine)list.get(i);
					BusLine next = (BusLine)list.get(i+1);
					if(current.compareTo(next) > 0) {
						list.remove(i);
						list.add(i+1, current);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * Searches for a specific bus line (linea) by ID in the database
	 * @param busLineId is the id of the bus line (linea)
	 * @param table_prefix is the prefix of the table
	 * @return the BusLine object that matches the given id
	 */
	public static BusLine getBusLineById(int busLineId, String table_prefix) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {Integer.toString(busLineId)};
		Cursor cursor = sqlite.rawQuery("select * from " + table_prefix + "linee where id = ?", args);
		BusLine line = null;
		if(cursor.moveToFirst()) {
			line = new BusLine(cursor);
		}
		cursor.close();
		sqlite.close();
		return line;
	}
	
	
	/**
	 * Searches for a specific bus line (linea) by the code of the bus line
	 * @param linecode is the code of the bus line (linea)
	 * @param table_prefix is the prefix of the table
	 * @return the BusLine object that matches the given linecode 
	 */
	public static  BusLine  getBusLineByLineCode(String linecode, String table_prefix) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {linecode};
		Cursor cursor = sqlite.rawQuery("select * from " + table_prefix + "linee where num_lin = ?", args);
		BusLine line = null;
		if(cursor.moveToFirst()) {
			line = new BusLine(cursor);
		}
		cursor.close();
		sqlite.close();
		return line;
	}
}