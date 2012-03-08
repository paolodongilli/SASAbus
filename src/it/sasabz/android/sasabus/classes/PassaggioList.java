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
import android.util.Log;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class PassaggioList {
	
	
	/**                                                                                                                                                                                                          
	 * This function returns a vector of the entire timetable                                                                                                                     
	 * @return a vector of Passaggio                                                                                                                              
	 */
	public static  Vector <DBObject>  getList()
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		Cursor cursor = sqlite.rawQuery("select * from orari_passaggio", null);
		Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
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
	 * linea in the city bacino from a departure to a destination
	 * @param bacino is the city of the line
	 * @param linea is the bus line
	 * @param destinazione is the destination
	 * @param partenza is the departure busstop
	 * @return a vector with all the times when the bus pass the bus stop
	 */
	public static Vector <DBObject> getList(String bacino, String linea,String destinazione,String partenza)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] selectionArgs = {bacino, linea, destinazione, partenza};
    	Cursor cursor = null;
    	try
    	{
    		cursor = sqlite.rawQuery("select strftime('%H:%M',o1.orario) as _id " +
    				"from "+
    				"(select id, lineaId " +
    				"from corse "+
    				"where "+
    				"substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', " + Config.getStartDate() + ")) + 1,1)='1' "+ 
    				"and lineaId = ? ) as c, " +
    				"(select progressivo, orario, corsaId "+
    				"from orarii "+
    				"where palinaId IN (" +
    				"select id from paline where nome_de = ?" +
    				")) as o1, " +
    				"(select progressivo , corsaId "+
    				"from orarii " +
    				"where palinaId IN (" +
    				"select id from paline where nome_de = ?" +
    				")) as o2 " +
    				"where o1.progressivo < o2.progressivo " +
    				"and c.id = o1.corsaId " +
    				"and c.id = o2.corsaId " +
    				"order by _id "
                , selectionArgs);
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		System.exit(-1);
    	}
		
    	Vector <DBObject> list = null;
		if(cursor.moveToFirst())
		{
			list = new Vector<DBObject>();
			do {
				Passaggio element = new Passaggio();
				list.add(element);
			} while(cursor.moveToNext());
		}
		cursor.close();
		sqlite.close();
		return list;
	}
	
	/**
	 * This method returns a vector of all the times passed the bus this bus stop when executing the line 
	 * linea in the city bacino from a departure to a destination
	 * @param linea is the bus line
	 * @param destinazione is the destination
	 * @param partenza is the departure busstop
	 * @return a cursor with all the times when the bus pass the bus stop
	 */
	public static Cursor getCursor(int linea,String destinazione,int partenza)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] selectionArgs = {Integer.toString(linea), Integer.toString(partenza), destinazione};
		Cursor c = null;
		String query = "select distinct strftime('%H:%M',o1.orario) as _id " +
				"from "+
				"(select id, lineaId " +
				"from corse "+
				"where "+
				"substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' "+ 
				"and lineaId = ?) as c, " +
				"(select progressivo, orario, corsaId "+
				"from orarii "+
				"where palinaId = ? ) as o1, " +
				"(select progressivo , corsaId "+
				"from orarii " +
				"where palinaId IN ( " +
				"select id from paline where nome_de = ? " +
				")) as o2 " +
				"where c.id = o1.corsaId " +
				"and c.id = o2.corsaId " +
				"and o1.progressivo < o2.progressivo " +
				"order by _id";
		try
		{
			c = sqlite.rawQuery(query, selectionArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.v("EXITSQL", "fehler bei rawQuery");
			System.exit(-1);
		}
		return c;
	}
	
	/**
	 * This is a prorotype for the next version to add a function to see the way which the bus is driving
	 * @param passaggio is the id of the selected departure time in the timetable
	 * @param destinazione is the destination
	 * @return a list of times which were ordered by the progressivo and rappresent the way from departure 
	 * to destination
	 */
	public static Cursor getCursorPercorso(int passaggio, String destinazione)
	{
		return null;
	}
	
	/**
	 * This method returns a vector of all the times passed the bus this bus stop when executing the line 
	 * linea in the city bacino from a departure to a destination
	 * @param linea is the bus line
	 * @param destinazione is the destination
	 * @param partenza is the departure busstop
	 * @return a cursor with all the times when the bus pass the bus stop
	 */
	public static Cursor getCursor(int linea,String destinazione,String partenza)
	{
		MySQLiteDBAdapter sqlite = MySQLiteDBAdapter.getInstance(SASAbus.getContext());
		String[] selectionArgs = {Integer.toString(linea), partenza, destinazione};
		Cursor c = null;
		String query = "select distinct strftime('%H:%M',o1.orario) as _id " +
				"from "+
				"(select id, lineaId " +
				"from corse "+
				"where "+
				"substr(corse.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J', '" + Config.getStartDate() + "')) + 1,1)='1' "+ 
				"and lineaId = ?) as c, " +
				"(select progressivo, orario, corsaId "+
				"from orarii "+
				"where palinaId IN (" +
				"select id from paline where nome_de = ? " +
				")) as o1, " +
				"(select progressivo , corsaId "+
				"from orarii " +
				"where palinaId IN ( " +
				"select id from paline where nome_de = ? " +
				")) as o2 " +
				"where c.id = o1.corsaId " +
				"and c.id = o2.corsaId " +
				"and o1.progressivo < o2.progressivo " +
				"order by _id";
		try
		{
			c = sqlite.rawQuery(query, selectionArgs);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.v("EXITSQL", "fehler bei rawQuery");
			System.exit(-1);
		}
		return c;
	}
	
}
