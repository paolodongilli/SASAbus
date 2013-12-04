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
package it.sasabz.sasabus.data.orm;

import it.sasabz.sasabus.data.Config;
import it.sasabz.sasabus.data.MySQLiteDBAdapter;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.ui.SASAbus;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.preference.PreferenceManager;

import android.util.Log;


/**
 * List of bus stops (Paline)
 */
public class BusStopList {
	

	/**
	 * Searches for all bus stops available in the database
	 * @return an ArrayList of all bus stops (paline) currently available in the database
	 */
	public static ArrayList<DBObject> getList() {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline", null);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus stops (palina) available in the database and orders them by their German name
	 * @return an ArrayList of all bus stops currently available in the database
	 */
	public static ArrayList<DBObject> getMapList() {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline order by nome_de", null);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				if (list.size() > 0 && !list.get(list.size()).toString().trim().equals(element.toString().trim())) {
					list.add(element);
				} else if (list.size() == 0) {
					list.add(element);
				}
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}

	
	/**
	 * Searches for all bus stop (paline) names available in the database 
	 * @return an ArrayList of all names in German and Italian of the bus stops
	 */
	public static ArrayList<DBObject> getNameList() {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it from paline", null);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus stops available in the database
	 * @param linea is the number of the bus line
	 * @return an ArrayList of the bus stops in the database
	 */
	public static ArrayList <DBObject> getBusLineList(int linea) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea)};
		Cursor cursor = null;
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
					"from paline, " +
					"(select id from corse where lineaId = ?) as corse, " +
					"orarii " +
					"where orarii.corsaId = corse.id " +
					"AND orarii.palinaId = paline.id " +
					"order by paline.nome_de", args);
		} else {
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from corse where lineaId = ?) as corse, " +
				"orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id " +
				"order by paline.nome_it", args);
		}
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus stops available in the database
	 * @param linea is the number of the bus line
	 * @param table_prefix is the prefix of the table
	 * @return an ArrayList with all the bus stop in the database
	 */
	public static ArrayList<DBObject> getBusLineList(int linea, String table_prefix) {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea)};
		Cursor cursor = null;
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from " + table_prefix + "corse where lineaId = ?) as corse, " + table_prefix +
				"orarii as orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id " +
				"order by paline.nome_de", args);
		} else {
			cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, " +
				"(select id from " + table_prefix + "corse where lineaId = ?) as corse, " + table_prefix +
				"orarii as orarii " +
				"where orarii.corsaId = corse.id " +
				"AND orarii.palinaId = paline.id " +
				"order by paline.nome_it", args);
		}
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus stops (paline) available in the database
	 * which are connected via the bus line (linea) to the arrival (
	 * @param arrival is the name of the arrival bus stop (palina)
	 * @param busline is the number of the bus line
	 * @param table_prefix is the prefix of the table
	 * @return an ArrayList of all bus-stops in the database
	 * which are connected via the bus line (linea) to the arrival
	 */
	public static ArrayList<DBObject> getBustStopListByArrival(String arrival, int busline, String table_prefix) {	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(busline),arrival};
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
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
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
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Searches for all bus stops which are in a certain radius of a location fix
	 * @param location is the location to search for
	 * @return an ArrayList of all bus stop which match the criteria
	 * @throws Exception
	 */
	public static ArrayList<DBObject> getBustStopListByGPS (Location location) throws Exception {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		
//		The following values are calculated with the formula 40045km : deltakm = 360deg : deltadeg
//		(40045 is an estimation of the ratio of Earth)
		
		Context context = SASAbus.getContext();
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
		
		double deltadegrees = Double.parseDouble(shared.getString("radius_gps", "0.0018"));
		
		Log.v("preferences", "delta: " + deltadegrees);
		
		String latitudemin = Double.toString(location.getLatitude() - deltadegrees);
		String longitudemin = Double.toString(location.getLongitude() - deltadegrees);
		String latitudemax = Double.toString(location.getLatitude() + deltadegrees);
		String longitudemax = Double.toString(location.getLongitude() + deltadegrees);
		String [] args = {longitudemin, longitudemax, latitudemin, latitudemax, 
				Double.toString(deltadegrees), Double.toString(deltadegrees), longitudemin, 
				longitudemax, latitudemin, latitudemax};
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it from paline where " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?) <= ? * ?" +
				" order by (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?)", args);
		ArrayList<DBObject> list = null;
		if(cursor.moveToFirst()) {
			list = new ArrayList<DBObject>();
			do {
				BusStop element = new BusStop(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * Search for the nearest bus stop (palina) to a certain location fix 
	 * @param location is the location to search for
	 * @return the BusStop object, that matches the given location, NULL if the query was empty
	 * @throws Exception
	 */
	public static BusStop getBusStopByGPS(Location location) throws Exception {
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());			
		String latitude = Double.toString(location.getLatitude());
		String longitude = Double.toString(location.getLongitude());
		String [] args = {longitude, longitude, latitude, latitude};
		Cursor cursor = sqlite.rawQuery("select distinct nome_de, nome_it, longitudine, latitudine from paline order by " +
				" (longitudine - ?) * (longitudine - ?) + (latitudine - ? ) * (latitudine - ?)", args);
		BusStop palina = null;
		if(cursor.moveToFirst()) {
			palina = new BusStop(cursor);
		}
		cursor.close();
		sqlite.close();
		return palina;
	}

	
	/**
	 * Searches for a bus stop (palina) by the given id
	 * @param id is the id of the bus stop in the database
	 * @return the BusStop object, that matches the given id, NULL if the query was empty
	 */
	public static BusStop getBusStopById(int id) {	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(id)};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it, p.latitudine as latitudine, p.longitudine as longitudine " +
			"from paline p " +
			"where id = ? " +
			"LIMIT 1";
		Cursor cursor = sqlite.rawQuery(query, args);
		BusStop element = null;
		if(cursor.moveToFirst()) {
			element = new BusStop(cursor);
			element.setId(id);
		}
		cursor.close();
		sqlite.close();
		return element;
	}

	
	/**
	 * Searches for a bus stop (palina) by name
	 * @param name is the name of the bus stop
	 * @param language is the language of the name (it or de)
	 * @return a BusStop object that matches the given name, but without the coordinates,
	 * because one bus stop can have 2 of them, NULL if the bus stop was not found
	 */
	public static BusStop getBusStopTranslation(String name, String language) {	
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {name};
		String query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
			"from paline p " +
			"where nome_de = ? " +
			"LIMIT 1";
		if (language.equals("it")) {
			query = "select distinct p.nome_de as nome_de, p.nome_it as nome_it " +
					"from paline p " +
					"where nome_it = ? " +
					"LIMIT 1";
		}
		Cursor cursor = sqlite.rawQuery(query, args);
		BusStop element = null;
		if(cursor.moveToFirst()) {
			element = new BusStop(cursor);
		}
		cursor.close();
		sqlite.close();
		return element;
	}
}