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

import it.sasabz.android.sasabus.ExternalStorageReadOnlyOpenHelper;
import it.sasabz.android.sasabus.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

public class MySQLiteDBAdapter {
	
	private static SQLiteDatabase sqlite= null;
	private static DatabaseHelper helper = null;
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static SQLiteDatabase getInstance(Context context)
	{
		if(sqlite == null)
		{
			Resources res = context.getResources();
			String appName = res.getString(R.string.app_name);
			String dbFileName = appName + ".db";
	        helper = new DatabaseHelper(dbFileName,null);
	        sqlite = helper.getReadableDatabase();
		}
		return sqlite;
	}
	
	public static boolean close() 
	{
		helper.close();
		return true;
	}
	
	
	

	/**
	 * This classes helps to open the database read only and close it correctly after using
	 * @author Markus Windegger (markus@mowiso.com)
	 *
	 */
	private static class DatabaseHelper extends ExternalStorageReadOnlyOpenHelper {

        DatabaseHelper(String dbFileName, SQLiteDatabase.CursorFactory factory) {
            super(dbFileName, factory);
        }
    }
	
}
