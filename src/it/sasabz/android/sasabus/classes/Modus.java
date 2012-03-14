/**
 * 
 *
 * Modus.java
 * 
 * Created: 23.01.2012 21:18:31
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

import java.util.Locale;

import it.sasabz.android.sasabus.R;

public class Modus extends DBObject {

	private String string = null;
	
	

	/**
	 * @return the string
	 */
	public String getString() {
		return string;
	}



	/**
	 * @param string the string to set
	 */
	public void setString(String string) {
		this.string = string;
	}



	@Override
	public String toString()
	{
		return this.getString();
	}
}
