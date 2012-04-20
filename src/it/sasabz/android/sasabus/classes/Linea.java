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

import java.util.Locale;

import android.database.Cursor;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class Linea extends DBObject {
	
	
	private String num_lin = null;
	
	private String descr_it = null;
	
	private String descr_de = null;
	
	private String abbrev = null;
	
	private int bacinoId = 0;
	
	private int differenza = -1;

	/**
	 * This is the standardconstructor, which make an empty object
	 */
	public Linea()
	{
		super();
	}
	
	/**
	 * this constructor creates an object from the standtart
	 * cursor to an entry in the database
	 * @param c is the curser to an entry in the database
	 */
	public Linea(Cursor c)
	{
		super(c.getInt(c.getColumnIndex("id")));
		this.setNum_lin(c.getString(c.getColumnIndex("num_lin")));
		this.setAbbrev(c.getString(c.getColumnIndex("abbrev")));
		this.setBacinoId(c.getInt(c.getColumnIndex("bacinoId")));
		this.setDescr_de(c.getString(c.getColumnIndex("descr_de")));
		this.setDescr_it(c.getString(c.getColumnIndex("descr_it")));
		if(c.getColumnIndex("differenza") != -1)
		{
			this.setDifferenza(c.getInt(c.getColumnIndex("differenza")));
		}
	}

	
	
	/**
	 * @return the descr_it
	 */
	public String getDescr_it() {
		return descr_it;
	}


	/**
	 * @param descr_it the descr_it to set
	 */
	public void setDescr_it(String descr_it) {
		this.descr_it = descr_it;
	}


	/**
	 * @return the descr_de
	 */
	public String getDescr_de() {
		return descr_de;
	}


	/**
	 * @param descr_de the descr_de to set
	 */
	public void setDescr_de(String descr_de) {
		this.descr_de = descr_de;
	}


	/**
	 * @return the abbrev
	 */
	public String getAbbrev() {
		return abbrev;
	}


	/**
	 * @param abbrev the abbrev to set
	 */
	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}


	/**
	 * @return the bacinoId
	 */
	public int getBacinoId() {
		return bacinoId;
	}


	/**
	 * @param bacinoId the bacinoId to set
	 */
	public void setBacinoId(int bacinoId) {
		this.bacinoId = bacinoId;
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
	
	
	/**
	 * @return the num_lin
	 */
	public int getDifferenza() {
		return differenza;
	}

	/**
	 * @param num_lin the num_lin to set
	 */
	public void setDifferenza(int differenza) {
		this.differenza = differenza;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof Linea))
			return false;
		Linea linea = (Linea)object;
		if(linea.getId() != this.getId())
			return false;
		return true;
	}
	
	
	/**
	 * This toString takes control of the localized output
	 */
	@Override
	public String toString()
	{
		String diff = "";
		if(this.differenza != -1)
		{
			diff = " (" + this.differenza + " min.)";
		}
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1)
		{
			return (this.getNum_lin() + diff).trim();
		}
		return (this.getNum_lin()  + diff).trim();
	}
	
	
}
