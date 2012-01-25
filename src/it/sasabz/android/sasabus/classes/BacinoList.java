/**
 *
 * BacinoList.java
 * 
 * Created: Dez 13, 2011 16:20:40 PM
 * 
 * Copyright (C) 2011 Markus Windegger
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

public class BacinoList {
	
	 
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static  Vector <DBObject>  getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select * from  bacini", null);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			int i = 0;
			list = new Vector<DBObject>();
			do {
				Bacino element = new Bacino(cursor);
				list.add(i, element);
				++i;
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * This method returns a Cursor of all bacinos present in the database
	 * @return a cursor to the bacinos present in the database
	 */
	public static Cursor getCursor ()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select id as _id, nome_de, nome_it from bacini", null);
		return cursor;
	}

}
