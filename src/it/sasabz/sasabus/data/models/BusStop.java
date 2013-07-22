/**
 * 
 *
 * Palina.java
 * 
 * Created: 14.12.2011 11:49:58
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
package it.sasabz.sasabus.data.models;

import java.util.Locale;

import android.database.Cursor;

/**
 * Bus stop (palina) object
 */
public class BusStop extends DBObject{
	
	/** name of the bus stop (palina) in German */
	private String name_de = null;
	
	/** name of the bus stop (palina) in Italian */
	private String name_it = null;
	
	/** longitude of the bus stop (palina) */
	private double longitude = 0;
	
	/** latitude of the bus stop (palina) */
	private double latitude = 0;
	

	/**
	 * Creates a new BusStop object with the id, name in Italian and German
	 * @param identifier is the id from the database
	 * @param name_it is the Italian name of the bus stop
	 * @param name_de is the German name of the bus stop
	 */
	public BusStop(int identifier, String name_it, String name_de) {
		super(identifier);
		this.setName_de(name_de);
		this.setName_it(name_it);
	}
	
	
	/**
	 * Creates a new BusStop object with the id, name in Italian an German and the coordinates of the bus stop (palina)
	 * @param identifier is the id from the database
	 * @param name_it is the Italian name of the bus stop
	 * @param name_de is the German name of the bus stop
	 * @param longitude is the longitude of the bus stop
	 * @param latitude is the latitude of the bus stop
	 */
	public BusStop(int identifier, String name_it, String name_de, double longitude, double latitude) {
		super(identifier);
		this.setName_de(name_de);
		this.setName_it(name_it);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
	}

	
	/**
	 * Creates a new BusStop object with information from a cursor.
	 * If the position flag is set, the position variables longitude and latitude are being set.
	 * @param cursor is the cursor with the information from the database
	 */
	public BusStop(Cursor cursor) {
		if (cursor.getColumnIndex("id") != -1)
			this.setId(cursor.getInt(cursor.getColumnIndex("id")));
		this.setName_de(cursor.getString(cursor.getColumnIndex("nome_de")));
		this.setName_it(cursor.getString(cursor.getColumnIndex("nome_it")));
		if(cursor.getColumnIndex("longitudine") != -1)
			this.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitudine")));
		if (cursor.getColumnIndex("latitudine") != -1)
			this.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitudine")));
	}

	/**
	 * @return the name of the bus stop (palina) in Italian and German
	 */
	public String getBusStop() {
		return this.name_it.trim() + " - " + this.name_de.trim(); 
	}
	
	/**
	 * @return the name of the bus stop (palina) in German
	 */
	public String getName_de() {
		return name_de;
	}

	/**
	 * @param name_de is the name of the bus stop (palina) in German
	 */
	public void setName_de(String name_de) {
		this.name_de = name_de;
	}

	/**
	 * @return the name name of the bus stop (palina) in Italian
	 */
	public String getName_it() {
		return name_it;
	}

	/**
	 * @param name_it is the name of the bus stop (palian) in Italian
	 */
	public void setName_it(String name_it) {
		this.name_it = name_it;
	}
	
	/**
	 * @return the longitude of the bus stop (palina)
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude is the longitude of the bus stop
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude of the bus stop
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude is the latitude of the bus stop 
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public String toString() {
		String ret = "";
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			ret = this.getName_de().trim();
		} else {
			ret = this.getName_it().trim();
		}
		if(ret.indexOf(")") != -1 && ret.indexOf("(") != -1)
			ret = ret.substring(1, ret.indexOf(")")) + " -" + ret.substring(ret.indexOf(")") + 1);
		return ret;
	}
	
}