/**
 * 
 *
 * Linea.java
 * 
 * Created: 13.12.2011 19:36:47
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
public class Linea extends DBObject {
	
	
	private String num_lin = null;

	/**
	 * This is the standardconstructor, which make an empty object
	 */
	public Linea()
	{
		super();
	}
	
	
	/**
	 * This is the constructor which makes an object from the data stored in the database
	 * for more information: /doc/SASAbus-db-schema.txt
	 * @param num_lin is the num_lin from the database
	 */
	public Linea(int identifyer, String num_lin)
	{
		super(identifyer);
		this.setNum_lin(num_lin);
	}
	
	public Linea(Cursor c)
	{
		super(c.getInt(c.getColumnIndex("_id")));
		this.setNum_lin(c.getString(c.getColumnIndex("num_lin")));
	}

	/**
	 * @return the num_lin
	 */
	public String getNum_lin() {
		return num_lin;
	}

	/**
	 * @param num_lin the num_lin to set
	 */
	public void setNum_lin(String num_lin) {
		this.num_lin = num_lin;
	}
	
	
	
}
