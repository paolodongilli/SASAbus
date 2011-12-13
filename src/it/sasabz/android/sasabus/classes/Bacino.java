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

package it.sasabz.android.sasabus.classes;

import android.database.Cursor;

public class Bacino extends DBObject {
	
	/*
	 * Is the name of the current Bacino
	 */
	private String bacinoName = null;
	
	
	/*
	 * This are the localized strings for the bacinoNames, they are not in the database
	 */
	/*
	private String bacinoName_de = null;

	private String bacinoName_it = null;
	 */
	
	/*
	 * The standard-constructor
	 */
	public Bacino() {
		super();
	}
	
	/**
	 * This constructor creates an object from the data id and the name of the bacino
	 * @param identifyer is the id from the database
	 * @param bacinoName is the name of the bacino
	 */
	public Bacino (int identifyer, String bacinoName){
		super(identifyer);
		this.bacinoName = bacinoName;
	}
	
	
	public Bacino(Cursor c)
	{
		super(c.getInt(c.getColumnIndex("_id")));
		this.setBacinoName(c.getString(c.getColumnIndex("bacino")));
	}
	
	/**
	 * Is the getter method for BacinoName
	 * @return a String which is the name of the bacino
	 */
	public String getBacinoName()
	{
		return this.bacinoName;
	}
	
	/**
	 * This is the setter method for bacinoNames, it allows you to set the name of the bacino
	 * @param bacinoName is the name of the bacino to set in the object
	 */
	public void setBacinoName(String bacinoName)
	{
		this.bacinoName = bacinoName;
	}
	
	
	/**
	 * @return the bacinoName_de
	 */
	/*
	public String getBacinoName_de()
	{
		return bacinoName_de;
	}
	 */	
	/**
	 * @param bacinoName_de the bacinoName_de to set
	 */
	/*
	public void setBacinoName_de(String bacinoName_de) 
	{
		this.bacinoName_de = bacinoName_de;
	}
	 */

	/**
	 * @return the bacinoName_it
	 */
	/*
	public String getBacinoName_it() 
	{
		return bacinoName_it;
	}
	 */
	/**
	 * @param bacinoName_it the bacinoName_it to set
	 */
	/*
	public void setBacinoName_it(String bacinoName_it) 
	{
		this.bacinoName_it = bacinoName_it;
	}
	*/

	
}
