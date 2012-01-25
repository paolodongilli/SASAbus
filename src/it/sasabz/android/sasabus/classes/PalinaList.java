/**
 * 
 *
 * PalinaList.java
 * 
 * Created: 14.12.2011 12:27:11
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

import it.sasabz.android.sasabus.SASAbus;

import java.util.Vector;


import android.database.Cursor;
import android.location.Location;

import android.util.Log;


/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class PalinaList {
	
	
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline", null);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getListLinea(int linea)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea)};
		Cursor cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from corse where lineaId = ?) as corse, " +
				"orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id", args);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getListDestinazione(String nome_de, int linea)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea),nome_de};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it, p.id as id " +
				"from " +
				"(select id, lineaId " +
				"from corse " +
				"where  " +
				"lineaId = ? " +
				"and substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' " + 
				") as c, " +
				"(select progressivo, orario, corsaId " +
				"from orarii " +
				"where palinaId IN ( " +
				"select id from paline where nome_de = ? " +		
				")) as o1, " +
				"orarii as o2, " +
				"paline p " +
				"where " +
				"o1.corsaId = c.id " +
				"and o2.corsaId = o1.corsaId " +
				"and o2.palinaId = p.id " +
				"and o1.progressivo > o2.progressivo " +
				"order by o2.progressivo";
		Cursor cursor = sqlite.rawQuery(query, args);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	public static Vector<DBObject> getListGPS (Location loc)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String latitudemin = Double.toString(loc.getLatitude() - Config.DELTA + Config.DELTALAT);
		String longitudemin = Double.toString(loc.getLongitude() - Config.DELTA + Config.DELTALONG);
		String latitudemax = Double.toString(loc.getLatitude() + Config.DELTA + Config.DELTALAT);
		String longitudemax = Double.toString(loc.getLongitude() + Config.DELTA + Config.DELTALONG);
		String [] args = {longitudemin, longitudemax, latitudemin, latitudemax, Double.toString(Config.DELTA), Double.toString(Config.DELTA), longitudemin, longitudemax, latitudemin, latitudemax};
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it from paline where " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?) <= ? * ?" +
				" order by min(abs(longitudine - ?), abs(longitudine - ?)) + min(abs(latitudine - ?), abs(latitudine - ?)) DESC", args);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	
	
	public static Cursor getCursorGPS (Location loc)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String latitudemin = Double.toString(loc.getLatitude() - Config.DELTA + Config.DELTALAT);
		String longitudemin = Double.toString(loc.getLongitude() - Config.DELTA + Config.DELTALONG);
		String latitudemax = Double.toString(loc.getLatitude() + Config.DELTA + Config.DELTALAT);
		String longitudemax = Double.toString(loc.getLongitude() + Config.DELTA + Config.DELTALONG);
		String [] args = {longitudemin, longitudemax, latitudemin, latitudemax, Double.toString(Config.DELTA), Double.toString(Config.DELTA), longitudemin, longitudemax, latitudemin, latitudemax};
		return sqlite.rawQuery("Select * from paline where " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?) <= ? * ?" +
				" order by min(abs(longitudine - ?), abs(longitudine - ?)) + min(abs(latitudine - ?), abs(latitudine - ?)) order by nome_de", args);
	}

	
	public static Vector <DBObject> getListPartenza(String partenza)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {partenza};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it, p.id as id " +
				"from " +
				"(select id, lineaId " +
				"from corse " +
				"where  " +
				"substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' " + 
				") as c, " +
				"(select progressivo, orario, corsaId " +
				"from orarii " +
				"where palinaId IN ( " +
				"select id from paline where nome_de = ? " +		
				")) as o1, " +
				"orarii as o2, " +
				"paline p " +
				"where " +
				"o1.corsaId = c.id " +
				"and o2.corsaId = o1.corsaId " +
				"and o2.palinaId = p.id " +
				"and o1.progressivo > o2.progressivo " +
				"order by p.nome_de";
		Cursor cursor = sqlite.rawQuery(query, args);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
}
