/**
 *
 * FavoritenList.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
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
package it.sasabz.sasabus.data.orm;

import it.sasabz.sasabus.data.FavoritenDB;
import it.sasabz.sasabus.data.models.Favorit;
import it.sasabz.sasabus.ui.SASAbus;

import java.util.Vector;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FavoritenList {
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static  Vector <Favorit>getList()
	{
		SQLiteDatabase sqlite = new FavoritenDB(SASAbus.getContext()).getReadableDatabase();
		Cursor cursor = sqlite.rawQuery("select * from " + FavoritenDB.FAVORITEN_TABLE_NAME + " order by id DESC", null);
		Vector <Favorit> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<Favorit>();
			do {
				Log.v("Listenerstellung", "ID: " + cursor.getInt(cursor.getColumnIndex("id")));
				Favorit element = new Favorit(cursor.getInt(cursor.getColumnIndex("id")),
						cursor.getString(cursor.getColumnIndex("partenza")),
						cursor.getString(cursor.getColumnIndex("destinazione")));
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of all the objects momentanly avaiable in the database                                                                                                                     
	 * @return a vector of objects if all goes right, alternativ it returns a MyError                                                                                                                              
	 */
	public static Favorit getById(int ident)
	{
		SQLiteDatabase sqlite = new FavoritenDB(SASAbus.getContext()).getReadableDatabase();
		Cursor cursor = sqlite.rawQuery("select * from " + FavoritenDB.FAVORITEN_TABLE_NAME + " where id = " 
				+ ident, null);
		Favorit list = null;
		if(cursor.moveToFirst())
		{
			list =  new Favorit(cursor.getInt(cursor.getColumnIndex("id")),
						cursor.getString(cursor.getColumnIndex("partenza")),
						cursor.getString(cursor.getColumnIndex("destinazione")));
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
}
