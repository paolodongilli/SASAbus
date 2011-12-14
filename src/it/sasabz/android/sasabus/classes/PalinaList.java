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

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class PalinaList {
	
	private static Vector <Palina> list = new Vector<Palina> ();
	
	public static Vector <Palina> getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select *  from paline", null);
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<Palina>();
			do {
				Palina element = new Palina(cursor, true);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	public static Cursor getCursorBacinoLineaDest(String bacino, String linea, String destinazione)
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
			System.exit(-1);
		}
		return c;
	}
	
}
