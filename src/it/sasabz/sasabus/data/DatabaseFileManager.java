/**
 *
 * DBFileManager.java
 * 
 * Created: Jan 16, 2011 5:30:31 PM
 * 
 * Copyright (C) 2011 Paolo Dongilli and Markus Windegger
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
package it.sasabz.sasabus.data;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;

/**
 * File manager for the database
 */
public abstract class DatabaseFileManager {
	
	//providing name, file and sqlite databases
	private SQLiteDatabase database;
	private File dbFile;
	private SQLiteDatabase.CursorFactory factory;

	/**
	 * Create a new {@link DatabaseFileManager}
	 * @param dbFileName the name of the database file
	 * @param factory the actual factory of the activity
	 */
	public DatabaseFileManager(String dbFileName, SQLiteDatabase.CursorFactory factory) {
		this.factory = factory;

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			throw new AndroidRuntimeException(
					"External storage (SD-Card) not mounted");
		}
		File appDbDir = new File(Environment.getExternalStorageDirectory(),
				"Android/data/it.sasabz.sasabus/db");
		if (!appDbDir.exists()) {
			appDbDir.mkdirs();
		}
		this.dbFile = new File(appDbDir, dbFileName);
	}

	/**
	 * Control if the database-file exists
	 * @return true if the file exists, false if not
	 */
	public boolean databaseFileExists() {
		return dbFile.exists();
	}

	/**
	 * Open the database-file in read-only-mode
	 */
	private void open() {
		if (dbFile.exists()) {
			database = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(),
					factory, SQLiteDatabase.OPEN_READONLY);
		}
	}

	/**
	 * Close the database
	 */
	public void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}

	/**
	 * Get the read-only database
	 * @return the database database
	 */
	public synchronized SQLiteDatabase getReadableDatabase() {
		return getDatabase();
	}

	/**
	 * Get a read-only database (gets opened if not already open)
	 * @return the open read-only database
	 */
	private SQLiteDatabase getDatabase() {
		if (database == null) {
			open();
		}
		return database;
	}
}