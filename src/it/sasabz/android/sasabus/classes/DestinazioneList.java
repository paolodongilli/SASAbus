/**
 * 
 *
 * DestinazioneList.java
 * 
 * Created: 14.12.2011 00:33:00
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

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import android.database.Cursor;


/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class DestinazioneList{
	
	private static Vector <Destinazione> list = null;
	
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static  Vector <Destinazione>  getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		
    	Cursor cursor = sqlite.rawQuery("select  distinct destinazione_it, destinazione_de from linee_corse", null);
		
    	list = null;
    	
		if(cursor.moveToFirst())
		{
			int id = 0;
			list = new Vector<Destinazione>();
			do {
				Destinazione element = new Destinazione();
				element.setNome_de(cursor.getString(cursor.getColumnIndex("destinazione_de")));
				element.setNome_it(cursor.getString(cursor.getColumnIndex("destinazione_it")));
				element.setId(id);
				list.add(element);
				++id;
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the destinations momentanly avaiable in the database related to the
	 * bacino and the linea                                                                                                                      
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static  Vector <Destinazione>  getList(String bacino, String linea) throws Exception
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		
    	String[] selectionArgs = {bacino, linea};
    	Cursor cursor = sqlite.rawQuery("select distinct destinazione_it as _id, destinazione_de from linee_corse where bacino=? and id_linea_breve=?", selectionArgs);
		
    	list = null;
    	
		if(cursor.getCount() != 0)
		{
			int id = 0;
			list = new Vector<Destinazione>();
			do {
				Destinazione element = new Destinazione();
				element.setNome_de(cursor.getString(cursor.getColumnIndex("destinazione_de")));
				element.setNome_it(cursor.getString(cursor.getColumnIndex("destinazione_it")));
				element.setId(id);
				list.add(element);
				++id;
			} while(!cursor.isLast());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	
	/**
	 * This method returns a Cursor of all bacinos present in the database
	 * @return a cursor to the bacinos present in the database
	 */
	public static Cursor getCursor(String bacino, String linea)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		
		String[] selectionArgs = {bacino, linea};
    	Cursor cursor = sqlite.rawQuery("select distinct destinazione_it as _id, destinazione_de from linee_corse where bacino=? and id_linea_breve=?", selectionArgs);

		return cursor;
	}
	
	/**
	 * This method maps a destinazione (String) to a object, which contains the italian and the german name
	 * @param destinazione is the string to compare with
	 * @param locale is the locale from the string destinazione
	 * @return a Destinazione object with the german and italian name of the destination
	 */
	public static Destinazione getFromLocalString(String destinazione, Locale locale)
	{
		Destinazione ret = null;
		Vector <Destinazione> v = DestinazioneList.getList();
		Iterator<Destinazione> iterator = v.iterator();
		while(iterator.hasNext() && ret == null)
		{
			Destinazione dest = iterator.next();
			if(Locale.getDefault().equals(Locale.GERMANY))
			{
				if(destinazione.equals(dest.getNome_de()))
				{
					ret = dest;
				}
			}
			else
			{
				if(destinazione.equals(((Destinazione)(dest)).getNome_it()))
				{
					ret = (Destinazione)dest;
				}
			}
		}
		return ret;
	}

	
	
}
