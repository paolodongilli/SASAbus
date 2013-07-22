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
package it.sasabz.android.sasabus.classes.dbobjects;

import it.sasabz.android.sasabus.SASAbus;
import it.sasabz.android.sasabus.classes.Config;
import it.sasabz.android.sasabus.classes.adapter.MySQLiteDBAdapter;

import java.util.Locale;
import java.util.Vector;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;

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
	public static Vector <DBObject> getMapList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline order by nome_de", null);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				if(list.size() > 0 && !list.lastElement().toString().trim().equals(element.toString().trim()))
					list.add(element);
				else if(list.size() == 0)
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
	public static Vector <DBObject> getNameList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it from paline", null);
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
		Cursor cursor = null;
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
					"from paline, " +
					"(select id from corse where lineaId = ?) as corse, " +
					"orarii " +
					"where orarii.corsaId = corse.id " +
					"AND orarii.palinaId = paline.id " +
					"order by paline.nome_de", args);
		}
		else
		{
				cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from corse where lineaId = ?) as corse, " +
				"orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id " +
				"order by paline.nome_it", args);
		}
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
	public static Vector <DBObject> getListLinea(int linea, String table_prefix)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea)};
		Cursor cursor = null;
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
					"from paline, " +
					"(select id from " + table_prefix + "corse where lineaId = ?) as corse, " + table_prefix +
					"orarii as orarii " +
					"where orarii.corsaId = corse.id " +
					"AND orarii.palinaId = paline.id " +
					"order by paline.nome_de", args);
		}
		else
		{
				cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from " + table_prefix + "corse where lineaId = ?) as corse, " + table_prefix +
				"orarii as orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id " +
				"order by paline.nome_it", args);
		}
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
	public static Vector <DBObject> getListDestinazione(String destinazione, int linea, String table_prefix)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea),destinazione};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
				"from " +
				"(select id, lineaId " +
				"from " + table_prefix + "corse as corse " +
				"where  " +
				"lineaId = ? " +
				"and substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' " + 
				") as c, " +
				"(select progressivo, orario, corsaId " +
				"from " + table_prefix + "orarii " +
				"where palinaId IN ( " +
				"select id from paline where nome_de = ? " +		
				")) as o1, " +
				"" + table_prefix + "orarii as o2, " +
				"paline p " +
				"where " +
				"o1.corsaId = c.id " +
				"and o2.corsaId = o1.corsaId " +
				"and o2.palinaId = p.id " +
				"and o1.progressivo < o2.progressivo " +
				"order by p.nome_it";
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
					"from " +
					"(select id, lineaId " +
					"from " + table_prefix + "corse as corse " +
					"where  " +
					"lineaId = ? " +
					"and substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' " + 
					") as c, " +
					"(select progressivo, orario, corsaId " +
					"from " + table_prefix + "orarii " +
					"where palinaId IN ( " +
					"select id from paline where nome_de = ? " +		
					")) as o1, " +
					"" + table_prefix + "orarii as o2, " +
					"paline p " +
					"where " +
					"o1.corsaId = c.id " +
					"and o2.corsaId = o1.corsaId " +
					"and o2.palinaId = p.id " +
					"and o1.progressivo < o2.progressivo " +
					"order by p.nome_de";
		}
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
	 * Gets a lists of palinas which are in a certain radius of a location fix
	 */
	public static Vector<DBObject> getListGPS (Location loc) throws Exception
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		/*
		 * The following values are calculated with the formula 40045km : deltakm = 360deg : deltadeg
		 * (40045 is an estimation of the ratio of Earth)
		 */
		
		Context con = SASAbus.getContext();
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(con);
		
		double deltadegrees = Double.parseDouble(shared.getString("radius_gps", "0.0018"));
		
		Log.v("preferences", "delta: " + deltadegrees);
		
		String latitudemin = Double.toString(loc.getLatitude() - deltadegrees);
		String longitudemin = Double.toString(loc.getLongitude() - deltadegrees);
		String latitudemax = Double.toString(loc.getLatitude() + deltadegrees);
		String longitudemax = Double.toString(loc.getLongitude() + deltadegrees);
		String [] args = {longitudemin, longitudemax, latitudemin, latitudemax, 
				Double.toString(deltadegrees), Double.toString(deltadegrees), longitudemin, 
				longitudemax, latitudemin, latitudemax};
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it from paline where " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?) <= ? * ?" +
				" order by (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?)", args);
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
	 * Get the nearest palina of a locationfix
	 * @param loc is the location of the last fix
	 * @return a palina
	 * @throws Exception
	 */
	public static Palina getPalinaGPS (Location loc) throws Exception
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());			
		String latitude = Double.toString(loc.getLatitude());
		String longitude = Double.toString(loc.getLongitude());
		String [] args = {longitude, longitude, latitude, latitude};
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it, longitudine, latitudine from paline order by " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?)", args);
		Palina palina = null;
		if(cursor.moveToFirst())
		{
			palina = new Palina(cursor);
		}
		cursor.close();
		sqlite.close();
		return palina;
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
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it, p.latitudine as latitudine, p.longitudine as longitudine " +
				"from paline p " +
				"where id = ? " +
				"LIMIT 1";
		Cursor cursor = sqlite.rawQuery(query, args);
		Palina element = null;
		if(cursor.moveToFirst())
		{
			element = new Palina(cursor);
			element.setId(id);
		}
		cursor.close();
		sqlite.close();
		return element;
	}
	
	/**
	 * Returns a Palina filled with all the information, excluded the coordinates, becaus one name can have 2 coordinates
	 * @param name is the name of the palina to search
	 * @param lang is the language of the name
	 * @return the palina with the name name, otherwise NULL if the plaina was not found
	 */
	public static Palina getTranslation(String name, String lang)
	{	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {name};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
				"from paline p " +
				"where nome_de = ? " +
				"LIMIT 1";
		if (lang.equals("it"))
		{
			query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
					"from paline p " +
					"where nome_it = ? " +
					"LIMIT 1";
		}
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
