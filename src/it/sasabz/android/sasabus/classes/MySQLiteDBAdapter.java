/**
 *
 * MySQLiteDBAdapter.java
 * 
 * Created: Dez 13, 2011 15:30:08 PM
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

import it.sasabz.android.sasabus.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MySQLiteDBAdapter {
	
	private static SQLiteDatabase sqlite= null;
	private static DatabaseHelper helper = null;
	private static int counteropen = 0;
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static MySQLiteDBAdapter getInstance(Context context)
	{
		if(counteropen == 0)
		{
			Resources res = context.getResources();
			String appName = res.getString(R.string.app_name);
			String dbFileName = appName + ".db";
	        helper = new DatabaseHelper(dbFileName,null);
	        sqlite = helper.getReadableDatabase();
		}
		++counteropen;
		return new MySQLiteDBAdapter();
	}
	
	private MySQLiteDBAdapter()
	{
		//do nothing
	}
	
	public static void closeAll() 
	{
			helper.close();
			sqlite.close();
	}
	
	public void close() 
	{
		--counteropen;
		if(counteropen == 0)
		{
			helper.close();
			sqlite.close();
		}
	}
	
	public Cursor rawQuery(String query, String[] args)
	{
		Cursor ret = null;
		ret = sqlite.rawQuery(query, args);
		return ret;
	}
	

	/**
	 * This classes helps to open the database read only and close it correctly after using
	 * @author Markus Windegger (markus@mowiso.com)
	 *
	 */
	private static class DatabaseHelper extends DBFileManager {

        DatabaseHelper(String dbFileName, SQLiteDatabase.CursorFactory factory) {
            super(dbFileName, factory);
        }
    }
	
}
