/**
 * 
 *
 * Destinazioni.java
 * 
 * Created: 14.12.2011 00:28:50
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
public class Destinazione extends DBObject {
	
	private String nome_de = null;
	
	private String nome_it = null;

	
	public Destinazione()
	{
		super();
	}
	
	public Destinazione(int identifyer, String nome_de, String nome_it)
	{
		super(identifyer);
		this.setNome_de(nome_de);
		this.setNome_it(nome_it);
	}
	
	public Destinazione(Cursor c)
	{
		super();
		this.setId(c.getInt(c.getColumnIndex("_id")));
		this.setNome_de(c.getString(c.getColumnIndex("destinazione_de")));
		this.setNome_it(c.getString(c.getColumnIndex("destinazione_it")));
	}
	
	
	/**
	 * @return the nome_de
	 */
	public String getNome_de() {
		return nome_de;
	}

	/**
	 * @param nome_de the nome_de to set
	 */
	public void setNome_de(String nome_de) {
		this.nome_de = nome_de;
	}

	/**
	 * @return the nome_it
	 */
	public String getNome_it() {
		return nome_it;
	}

	/**
	 * @param nome_it the nome_it to set
	 */
	public void setNome_it(String nome_it) {
		this.nome_it = nome_it;
	}
	
	
	
	
}
