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
	
	private String abbrev = null;
	
	private String denom_it = null;
	
	private String denom_de = null;
	
	private String descr_it = null;
	
	private String descr_de = null;
	
	private String localita = null;
	
	private String linea_it = null;
	
	private String linea_de = null;
	
	private String var_a = null;
	
	private String var_r = null;
	
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
	 * @param identifyer is the _id from the database
	 * @param abbrev is the abbrev from the database
	 * @param denom_it is the denom_it from the database
	 * @param denom_de is the denom_de from the database
	 * @param desrcr_it is the descr_it from the database
	 * @param descr_de is the descr_de from the database
	 * @param localita is the localita from the database
	 * @param linea_it is the linea_it from the database
	 * @param linea_de is the linea_de from the database
	 * @param var_a is the var_a from the database
	 * @param var_r is the var_r from the database
	 * @param num_lin is the num_lin from the database
	 */
	public Linea(int identifyer, String abbrev, String denom_it, String denom_de, 
			String descr_it, String descr_de, String localita, String linea_it, String linea_de, String var_a, String var_r, String num_lin)
	{
		super(identifyer);
		this.setAbbrev(abbrev);
		this.setDenom_it(denom_it);
		this.setDenom_de(denom_de);
		this.setDescr_it(descr_it);
		this.setDescr_de(descr_de);
		this.setLocalita(localita);
		this.setLinea_it(linea_it);
		this.setLinea_de(linea_de);
		this.setVar_a(var_a);
		this.setVar_r(var_r);
		this.setNum_lin(num_lin);
	}
	
	public Linea(Cursor c)
	{
		super(c.getInt(c.getColumnIndex("_id")));
		this.setAbbrev(c.getString(c.getColumnIndex("abbrev")));
		this.setDenom_it(c.getString(c.getColumnIndex("denom_it")));
		this.setDenom_de(c.getString(c.getColumnIndex("denom_de")));
		this.setDescr_it(c.getString(c.getColumnIndex("descr_it")));
		this.setDescr_de(c.getString(c.getColumnIndex("descr_de")));
		this.setLocalita(c.getString(c.getColumnIndex("localita")));
		this.setLinea_it(c.getString(c.getColumnIndex("linea_it")));
		this.setLinea_de(c.getString(c.getColumnIndex("linea_de")));
		this.setVar_a(c.getString(c.getColumnIndex("var_a")));
		this.setVar_r(c.getString(c.getColumnIndex("var_r")));
		this.setNum_lin(c.getString(c.getColumnIndex("num_lin")));
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
	 * @return the denom_it
	 */
	public String getDenom_it() {
		return denom_it;
	}

	/**
	 * @param denom_it the denom_it to set
	 */
	public void setDenom_it(String denom_it) {
		this.denom_it = denom_it;
	}

	/**
	 * @return the denom_de
	 */
	public String getDenom_de() {
		return denom_de;
	}

	/**
	 * @param denom_de the denom_de to set
	 */
	public void setDenom_de(String denom_de) {
		this.denom_de = denom_de;
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
	 * @return the localita
	 */
	public String getLocalita() {
		return localita;
	}

	/**
	 * @param localita the localita to set
	 */
	public void setLocalita(String localita) {
		this.localita = localita;
	}

	/**
	 * @return the linea_it
	 */
	public String getLinea_it() {
		return linea_it;
	}

	/**
	 * @param linea_it the linea_it to set
	 */
	public void setLinea_it(String linea_it) {
		this.linea_it = linea_it;
	}

	/**
	 * @return the linea_de
	 */
	public String getLinea_de() {
		return linea_de;
	}

	/**
	 * @param linea_de the linea_de to set
	 */
	public void setLinea_de(String linea_de) {
		this.linea_de = linea_de;
	}

	/**
	 * @return the var_a
	 */
	public String getVar_a() {
		return var_a;
	}

	/**
	 * @param var_a the var_a to set
	 */
	public void setVar_a(String var_a) {
		this.var_a = var_a;
	}

	/**
	 * @return the var_r
	 */
	public String getVar_r() {
		return var_r;
	}

	/**
	 * @param var_r the var_r to set
	 */
	public void setVar_r(String var_r) {
		this.var_r = var_r;
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
