/**
 * 
 *
 * PalinaList.java
 * 
 * Created: 14.12.2011 12:27:11
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

import it.sasabz.android.sasabus.SASAbus;

import java.util.Vector;

import android.database.Cursor;
import android.util.Log;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class PalinaList {
	
	private static Vector <DBObject> list = new Vector<DBObject> ();
	
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline", null);
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getListLinea(int linea)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea)};
		Cursor cursor = sqlite.rawQuery("select distinct paline.nome_de as nome_de, paline.nome_it as nome_it " +
				"from paline, corse, orarii where corse.lineaId = ? AND " +
				"orarii.corsaId = corse.id AND orarii.palinaId = paline.id", args);
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * Returns a list of all bus-stops avaiable in the database
	 * @return a vector of all bus-stops in the database
	 */
	public static Vector <DBObject> getListDestinazione(String nome_de, int linea)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String [] args = {Integer.toString(linea), nome_de};
		Cursor cursor = sqlite.rawQuery("SELECT DISTINCT part_nome_it as nome_it, part_nome_de as nome_de " +
				"from palineProgressive where lineaId = ? and dest_nome_de = ? " +
				"AND substr(linee.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
                Config.getStartDate() + "')) + 1,1)='1'", args);
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Palina element = new Palina(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	
	/**
	 * Retuns all busstops which have the following properties
	 * @param bacino is the city of the busstop
	 * @param linea is the line which passes the busstop
	 * @param destinazione is the destination of the line, important for the direction of the line
	 * @return a cursor ouver all the selected busstops
	 */
	public static Cursor getCursor(String bacino, String linea, String destinazione)
	{
		String[] selectionArgs = {bacino, linea, destinazione};
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor c = null;
		try {
		c = sqlite.rawQuery(
			"select orari.progressivo as progressivo, orari.id_palina as _id, paline.luogo as luogo from orari_passaggio as orari, paline " +
			"where orari.codice_corsa = " +
			" (select _id from linee_corse " + 
			"	where bacino=? " +
			"	    and  id_linea_breve=? " +
			"	    and destinazione_it=? " + 
			"	    and substr(effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
			Config.getStartDate() + "')) + 1,1)='1' " + 
			"	    and orario_partenza > '0800' " +
			"	    limit 1) " + 
			"	  and orari.id_palina=paline._id " +
			"	 order by orari.progressivo", selectionArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return c;
	}
	
}
