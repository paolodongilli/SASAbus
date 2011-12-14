/**
 * 
 *
 * PassaggioList.java
 * 
 * Created: 14.12.2011 16:51:15
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
public class PassaggioList {
	
	private static Vector<Passaggio> list= null;
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of the entire timetable                                                                                                                     
	 * @return a vector of Passaggio                                                                                                                              
	 */
	public static  Vector <Passaggio>  getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select * from orari_passaggio", null);
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<Passaggio>();
			do {
				Passaggio element = new Passaggio(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * This method returns a cursor over all the timetable with all the bus stops in every line on every course
	 * @return a cursor over all the timtable
	 */
	public static Cursor getCursor()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		return sqlite.rawQuery("select * from orari_passaggio", null); 
	}
	
	
	/**
	 * This method returns a vector of all the times passed the bus this bus stop when executing the line 
	 * linea in the city bacino. 
	 * @param bacino is the city of the line
	 * @param linea is the bus line
	 * @param destinazione is the destination
	 * @param palina is the busstop
	 * @param progressivo is the number of the palina/busstop in the line
	 * @return a vector with all the times when the bus pass the bus stop
	 */
	public static Vector <Passaggio> getList(String bacino, String linea,String destinazione,String palina,String progressivo)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] selectionArgs = {bacino, linea, destinazione, palina, progressivo};
    	Cursor cursor = null;
    	try
    	{
    		cursor = sqlite.rawQuery(
    			"select _id, id_palina, codice corsa, progrossivo, orario " + 
                "  from linee_corse as linee, orari_passaggio as orari, paline " +
                "  where bacino=? " +
                "  and linee.id_linea_breve=? " + 
                "  and linee.destinazione_it=? " + 
                "  and substr(linee.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
                Config.getStartDate() + "')) + 1,1)='1' " + 
                "  and linee._id=orari.codice_corsa " + 
                "  and linee.codice_linea=orari.codice_linea " +
                "  and orari.id_palina=paline._id " +
                "  and paline._id=? " +
                "  and orari.progressivo=? " +
                //"  and time(orari.orario) >= strftime('%H:%M','now','localtime') " +
                "  order by _id "
                //+ "  limit 6"
                , selectionArgs);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		System.exit(-1);
    	}
		
		list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<Passaggio>();
			do {
				Passaggio element = new Passaggio();
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	public static Cursor getCursor(String bacino, String linea,String destinazione,String palina,String progressivo)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] selectionArgs = {bacino, linea, destinazione, palina, progressivo};
		Cursor c = null;
		try
		{
			c = sqlite.rawQuery(
				"select strftime('%H:%M',orari.orario) as _id " + 
						"  from linee_corse as linee, orari_passaggio as orari, paline " +
						"  where bacino=? " +
						"  and linee.id_linea_breve=? " + 
						"  and linee.destinazione_it=? " + 
						"  and substr(linee.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
						Config.getStartDate() + "')) + 1,1)='1' " + 
						"  and linee._id=orari.codice_corsa " + 
						"  and linee.codice_linea=orari.codice_linea " +
						"  and orari.id_palina=paline._id " +
						"  and paline._id=? " +
						"  and orari.progressivo=? " +
						//"  and time(orari.orario) >= strftime('%H:%M','now','localtime') " +
						"  order by _id "
						//+ "  limit 6"
						, selectionArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		return c;
	}
	
	
}
