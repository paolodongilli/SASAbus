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

	private String string_de = null;
	private String string_it = null;
	
	/**
	 * @return the string_de
	 */
	public String getString_de() {
		return string_de;
	}

	/**
	 * @param string_de the string_de to set
	 */
	public void setString_de(String string_de) {
		this.string_de = string_de;
	}

	/**
	 * @return the string_it
	 */
	public String getString_it() {
		return string_it;
	}

	/**
	 * @param string_it the string_it to set
	 */
	public void setString_it(String string_it) {
		this.string_it = string_it;
	}


	@Override
	public String toString()
	{
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			return this.getString_de();
		}
		return this.getString_it();
	}
}
