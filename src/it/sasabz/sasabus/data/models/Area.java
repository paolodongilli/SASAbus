/**
 *
 * Bacino.java
 * 
 * Created: Dez 13, 2011 16:12:03 PM
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

package it.sasabz.sasabus.data.models;

import java.util.Locale;

import android.database.Cursor;

/**
 *Area (bacino) object (Bolzano, Merano,...) 
 */
public class Area extends DBObject {
	
	/** Name of the current area (bacino) in German */
	private String area_de = null;
	
	/** Name of the current area (bacino) in Italian */
	private String area_it = null;
	
	private String table_prefix = null;
	
	/**
	 * Creates an object from the data id and the name of the area (bacino)
	 * @param identifier id from the database
	 * @param area_it name of the area (bacino) in Italian
	 * @param area_de name of the area (bacino) in German
	 */
	public Area(int identifier, String area_it, String area_de){
		super(identifier);
		this.area_it = area_it;
		this.area_de = area_de;
	}
	
	/**
	 * Creates an object from the database providing directly the
	 * default cursor from the database
	 * @param cursor	is the cursor on an object from the database
	 */
	public Area(Cursor cursor)
	{
		super(cursor.getInt(cursor.getColumnIndex("id")));
		this.setArea_de(cursor.getString(cursor.getColumnIndex("nome_de")));
		this.setArea_it(cursor.getString(cursor.getColumnIndex("nome_it")));
		this.setTable_prefix(cursor.getString(cursor.getColumnIndex("nome_table")));
	}
	
	/**
	 * @return the area name in German
	 */
	public String getArea_de() {
		return area_de;
	}
	
	/**
	 * @param area_de the area name in German
	 */
	public void setArea_de(String area_de) {
		this.area_de = area_de;
	}

	/**
	 * @return the area name in Italian
	 */
	public String getArea_it() {
		return area_it;
	}

	/**
	 * @param area_it the area name in Italian
	 */
	public void setArea_it(String area_it) {
		this.area_it = area_it;
	}

	/**
	 * @return the table_prefix
	 */
	public String getTable_prefix() {
		return this.table_prefix;
	}
	
	/**
	 * @param table_prefix the tab
	 */
	public void setTable_prefix(String table_prefix) {
		this.table_prefix = table_prefix;
	}
	
	/**
	 * @return the string related to the locale set on the smartphone
	 */
	@Override
	public String toString() {
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			return this.getArea_de().trim();
		}
		return this.getArea_it().trim();
	}
	
}