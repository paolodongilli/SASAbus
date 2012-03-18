/**
 * 
 *
 * Property.java
 * 
 * Created: 17.03.2012 20:35:20
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

import java.util.Vector;

public class Property {
	
	//Stores the tag/name of the property
	private String name = null;
	
	//Stores the value of the property
	private String value = null;
	
	//Stores the comment of the property
	private String comment = null;
	
	/**
	 * The standardconstructor of the class Property
	 */
	public Property()
	{
		super();
	}


	/**
	 * @return the tag
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param tag the tag to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}


	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
		
	
	@Override
	public String toString()
	{
		String ret = "";
		ret += "<config name=\"" + this.getName() + "\">";
		ret += "<value>" + this.getValue() + "</value>";
		ret += "<comment>" + this.getComment() + "</comment>";
		ret += "</config>";
		return ret;
	}
}
