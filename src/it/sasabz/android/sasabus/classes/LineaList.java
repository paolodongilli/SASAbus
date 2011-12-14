/**
 * 
 *
 * LineaList.java
 * 
 * Created: 13.12.2011 20:13:31
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
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class LineaList {
	
	private static Vector <Linea> list = new Vector<Linea>();
	
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static  Vector <Linea>  getList()
	{
		SQLiteDatabase sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select  _id, abbrev, denom_it, denom_de, descr_it, descr_de, localita, linea_it, linea_de, " +
				"var_a, var_r, num_lin from linee", null);
		if(cursor.moveToFirst())
		{
			do {
				Linea element = new Linea(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		else
		{
			list = null;
		}
		cursor.close();
		return list;
	}
	
	/**
	 * This method returns a cursor to the db-table content of linee
	 * @return a cursor which point on the content of the linee-table in the database
	 */
	public static Cursor getCursor ()
	{
		SQLiteDatabase sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select _id, abbrev, denom_it, denom_de, descr_it, descr_de, localita, linea_it, linea_de, " +
				"var_a, var_r, num_lin from linee", null);
		return cursor;
	}
	
	/**
	 * This method returns a vector of linee which are located in the bacino 
	 * @param bacino is the bacino where we are searching the linee 
	 * @return a vector of DBObjects with the linee located in the bacino
	 */
	public static Vector <Linea> getListBacino(String bacino)
	{
		SQLiteDatabase sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {bacino};
		Cursor cursor = sqlite.rawQuery("select _id, abbrev, denom_it, denom_de, descr_it, descr_de, localita, linea_it, linea_de, " +
				"var_a, var_r, num_lin  from linee where localita = ?", args);
		if(cursor.moveToFirst())
		{
			do {
				Linea element = new Linea(cursor);
				list.add(element);
			} while(cursor.moveToNext());
		}
		else
		{
			list = null;
		}
		cursor.close();
		return list;
	}
	
	
	/**
	 * This ethod returns a cursor tho the linee present in the bacino
	 * @param bacino is the location where we search the linee
	 * @return a cursor which contains the linee present in the database
	 */
	public static Cursor getCursorBacino(String bacino)
	{
		SQLiteDatabase sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {bacino};
		Cursor cursor = sqlite.rawQuery("select _id, abbrev, denom_it, denom_de, descr_it, descr_de, localita, linea_it, linea_de, " +
				"var_a, var_r, num_lin from linee where localita = ?", args);
		return cursor;
	}
	
	
	/**
	 * This method prepares the cursor for the list view, the lines are showed just 1 time, there are no duplicates
	 * @param bacino location where we search the linee
	 * @return a cursor to the linee which we can add to a listview
	 */
	public static Cursor getCursorBacinoView(String bacino)
	{
		SQLiteDatabase sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] args = {bacino};
		Cursor cursor = sqlite.rawQuery("select distinct num_lin as _id from linee where localita = ?", args);
		return cursor;
	}
	
	
	
}
