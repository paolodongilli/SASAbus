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
package it.sasabz.sasabus.data.models;

import java.util.Locale;

import android.database.Cursor;

/**
 *BusLine (linea) object (1, 2,...)
 */
public class BusLine extends DBObject {
	
	
	private String buslineNumber = null;
	
	private String description_it = null;
	
	private String description_de = null;
	
	private String abbreviation = null;
	
	private int difference = -1;
	
	
	/**
	 * Creates a new object from the standard
	 * cursor to an entry in the database
	 * @param cursor is the cursor to an entry in the database
	 */
	public BusLine(Cursor cursor) {
		super(cursor.getInt(cursor.getColumnIndex("id")));
		this.setBuslineNumber(cursor.getString(cursor.getColumnIndex("num_lin")));
		this.setAbbreviation(cursor.getString(cursor.getColumnIndex("abbrev")));
		this.setDescription_de(cursor.getString(cursor.getColumnIndex("descr_de")));
		this.setDescription_it(cursor.getString(cursor.getColumnIndex("descr_it")));
		if(cursor.getColumnIndex("differenza") != -1) {
			this.setDifferenza(cursor.getInt(cursor.getColumnIndex("differenza")));
		}
	}
	
	public BusLine(String buslineNumber, String description_it, String description_de, String abbreviation, int difference) {
		setBuslineNumber(buslineNumber);
		setDescription_it(description_it);
		setDescription_de(description_de);
		setAbbreviation(abbreviation);
		setDifferenza(difference);
	}
	
	/**
	 * 
	 * @return the bus line description in Italian
	 */
	public String getDescription_it() {
		return description_it;
	}

	/**
	 * @param description_it the bus line description in Italian
	 */
	public void setDescription_it(String description_it) {
		this.description_it = description_it;
	}

	/**
	 * @return the bus line description in German
	 */
	public String getDescription_de() {
		return description_de;
	}

	/**
	 * @param description_de the bus line description in German
	 */
	public void setDescription_de(String description_de) {
		this.description_de = description_de;
	}

	/**
	 * @return the abbreviation of the bus line
	 */
	public String getAbbreviation() {
		return abbreviation;
	}

	/**
	 * @param abbreviation the abbreviation of the bus line
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}


	/**
	 * @return the number of the bus line
	 */
	public String getBuslineNumber() {
		return buslineNumber;
	}

	/**
	 * @param buslineNumber the num_lin to set
	 */
	public void setBuslineNumber(String buslineNumber) {
		this.buslineNumber = buslineNumber;
	}
	
	/**
	 * @return the difference
	 */
	public int getDifference() {
		return difference;
	}

	/**
	 * @param buslineNumber the num_lin to set
	 */
	public void setDifferenza(int difference) {
		this.difference = difference;
	}
	
	/**
	 * check if an object is the same as the bus line object
	 * @param object the object to get checked
	 * @return true if the object is the bus line object
	 */
	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if(!(object instanceof BusLine))
			return false;
		BusLine linea = (BusLine)object;
		if(linea.getId() != this.getId())
			return false;
		return true;
	}
	
	
	/**
	 * Compares two {@link BusLine} objects and returns their difference value
	 * @param busLine is the line to compare with
	 * @return 0, if the two lines are equal, -1 if this line is less then, and 1 
	 * if this line is greater then the line to compare with
	 */
	public int compareTo(BusLine busLine) throws NumberFormatException {
		int sortnum = 0;
		boolean failure = false;
		boolean failure_lin = false;
		try {
			sortnum = Integer.parseInt(this.getBuslineNumber());
		}
		catch (NumberFormatException e) {
			try {
				sortnum = Integer.parseInt(this.getBuslineNumber().substring(0, this.getBuslineNumber().length()-1));
			}
			catch(Exception ex) {
				failure = true;
			}
			
		}
		int linsortnum = 0;
		try {
			linsortnum = Integer.parseInt(busLine.getBuslineNumber());
		}
		catch (NumberFormatException e) {
			try {
				linsortnum = Integer.parseInt(busLine.getBuslineNumber().substring(0, busLine.getBuslineNumber().length()-1));
			}
			catch(Exception ex) {
				failure_lin = true;
			}
		}
		if(failure && failure_lin) {
			return this.getBuslineNumber().compareTo(busLine.getBuslineNumber());
		} else if(failure) {
			return 1;
		} else if(failure_lin) {
			return -1;
		}
		if(sortnum == linsortnum)
			return 0;
		if (sortnum > linsortnum)
			return 1;
		return -1;
	}
	
	
	/**
	 * Takes control of the localized output
	 */
	@Override
	public String toString()
	{
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			return (this.getBuslineNumber()).trim();
		}
		return (this.getBuslineNumber()).trim();
	}
		
}