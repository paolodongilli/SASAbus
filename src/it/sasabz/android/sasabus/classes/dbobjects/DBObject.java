/**
 *
 * DBObject.java
 * 
 * Created: Dez 13, 2011 15:45:15 PM
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

package it.sasabz.android.sasabus.classes.dbobjects;

public class DBObject {
	
	/*
	 * The id is the integer which identifies the object in the database
	 */
	private int id = 0;

	/**
	 * This constructor creates an dbobject
	 */
	public DBObject()
	{
		super();
		//Nothing to do
	}
	
	/**
	 * this creates a dbobject with an identifier, which is the id
	 * provided in the database 
	 * @param identifier is the identifier from the database
	 */
	public DBObject(int identifier)
	{
		super();
		setId(identifier);
	}
	
	/**
	 * 
	 * @return the integer which identifies the object in the database
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @param id is the integer which identifies the object in the database
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	
	
}
