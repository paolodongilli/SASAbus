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
package it.sasabz.android.sasabus.classes;

import android.database.Cursor;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class Palina extends DBObject{
	
	
	private String name_de = null;
	
	private String name_it = null;
	
	private double longitude = 0;
	
	private double latitude = 0;
	
	
	/**
	 * Standardconstructor
	 */
	public Palina()
	{
		super();
	}
	
	

	/**
	 * This is the constructor to fill the object entirely with id, name in it an de
	 * @param identifier is the id from the database
	 * @param name_it is the italian name of the busstop
	 * @param name_de is the german name of the busstop
	 */
	public Palina(int identifier, String name_it, String name_de)
	{
		super(identifier);
		this.setName_de(name_de);
		this.setName_it(name_it);
	}
	
	
	/**
	 * This is the constructor to fill the object with the names in it and de, an to add the coordinates to the bus stop
	 * @param identifier is the id from th db
	 * @param name_it is the italian name of the busstop
	 * @param name_de is the german name of the busstop
	 * @param longitude is the longitude of the busstop
	 * @param latitude is the latitude of the busstop
	 */
	public Palina(int identifier, String name_it, String name_de, double longitude, double latitude)
	{
		super(identifier);
		this.setName_de(name_de);
		this.setName_it(name_it);
		this.setLongitude(longitude);
		this.setLatitude(latitude);
	}

	
	/**
	 * This constructer fills the palina-object with information from a cursor
	 * if the position flag is set, then it fills the position variables longitude and latitude
	 * @param c is the cursor with the information from the db
	 * @param position is the flag which sets the position when it's true, don't set it when it's false
	 */
	public Palina(Cursor c, boolean position)
	{
		super(c.getInt(c.getColumnIndex("_id")));
		String luogo = c.getString(c.getColumnIndex("luogo"));
		String [] splitted = luogo.split("-");
		if(splitted.length == 2)
		{
			this.setName_de(splitted[1]);
			this.setName_it(splitted[0]);
		}
		if(position)
		{
			this.setLongitude(c.getDouble(c.getColumnIndex("longitudine")));
			this.setLatitude(c.getDouble(c.getColumnIndex("latitudine")));
		}
	}
	
	/**  
	 * Returns if the Locationvariables are loadet or not
	 * @return true when the location variables are loadet, false otherwise
	 */
	public boolean isLocationLoadet()
	{
		if(longitude == 0 && latitude == 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 * @return the name_de
	 */
	public String getName_de() {
		return name_de;
	}

	/**
	 * @param name_de the name_de to set
	 */
	public void setName_de(String name_de) {
		this.name_de = name_de;
	}

	/**
	 * @return the name_it
	 */
	public String getName_it() {
		return name_it;
	}

	/**
	 * @param name_it the name_it to set
	 */
	public void setName_it(String name_it) {
		this.name_it = name_it;
	}
	
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
}
