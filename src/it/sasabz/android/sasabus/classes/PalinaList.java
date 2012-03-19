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
	 * Returns a list of all bus-stops avaiable in the database which were connected via linea to
	 * the destination destinazione
	 * @param destinazione is the name of the destination busstop
	 * @param linea is the number of the line tho connect 
	 * @return a vector of all bus-stops in the database which were connected via linea to the destination
	 */
	public static Vector <DBObject> getListDestinazione(String destinazione, int linea)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea),destinazione};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
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
	
	/**
	 * 
	 */
	public static Vector<DBObject> getListGPS (Location loc) throws Exception
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		/*
		 * The following values are calculated with the formula 40045km : delta = 360deg : deltadeg
		 * (40045 is an estimation of the ratio of Earth)
		 */
		double deltadegrees = Double.parseDouble(Conf.getByName("delta").getValue()) * 360.0 / 40045.0;
		double deltalatdeg =  Double.parseDouble(Conf.getByName("delta_lat").getValue()) * 360.0 / 40045.0;
		double deltalongdeg =  Double.parseDouble(Conf.getByName("delta_long").getValue()) * 360.0 / 40045.0;
		String latitudemin = Double.toString(loc.getLatitude() - deltadegrees + deltalatdeg);
		String longitudemin = Double.toString(loc.getLongitude() - deltadegrees + deltalongdeg);
		String latitudemax = Double.toString(loc.getLatitude() + deltadegrees + deltalatdeg);
		String longitudemax = Double.toString(loc.getLongitude() + deltadegrees + deltalongdeg);
		String [] args = {longitudemin, longitudemax, latitudemin, latitudemax, Double.toString(deltadegrees), Double.toString(deltadegrees), longitudemin, longitudemax, latitudemin, latitudemax};
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
	
	

	/**
	 * This methode gives me all the busstops which are connected to the departure busstop called partenza
	 * (all possible destinations)
	 * @param partenza is the departure busstop
	 * @return a list of all possible destinations without changing bus
	 */
	public static Vector <DBObject> getListPartenza(String partenza)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {partenza};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
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
				"and o1.progressivo < o2.progressivo " +
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
	
	
	/**
	 * Returns a Palina identified by the given id
	 * @param id is the id of the Palina in the database
	 * @return the palina with the id id from the database, NULL if the query was empty
	 */
	public static Palina getById(int id)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(id)};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
				"from paline p " +
				"where id = ? " +
				"LIMIT 1";
		Cursor cursor = sqlite.rawQuery(query, args);
		Palina element = null;
		if(cursor.moveToFirst())
		{
			element = new Palina(cursor);
		}
		cursor.close();
		sqlite.close();
		return element;
	}
	
}
