/**
 *
 * SasaDbAdapter.java
 * 
 * Created: Jan 16, 2011 11:41:06 AM
 * 
 * Copyright (C) 2011 Paolo Dongilli
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

package it.sasabz.android.sasabus;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database access helper class.
 */
public class SasaDbAdapter {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;
	//private String dbDate;

    private static class DatabaseHelper extends ExternalStorageReadOnlyOpenHelper {

        DatabaseHelper(String dbFileName, SQLiteDatabase.CursorFactory factory) {
            super(dbFileName, factory);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public SasaDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the SASAbus database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public SasaDbAdapter open() throws SQLException {
    	Resources res = mCtx.getResources();
		String appName = res.getString(R.string.app_name);
		String dbFileName = appName + ".db";
        mDbHelper = new DatabaseHelper(dbFileName,null);
        mDb = mDbHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Return a String containing the db validity start date
     * 
     * @return String containing a date in format YYYY-MM-DD
     * @throws SQLException
     */
    public String fetchStartDate() throws SQLException {
    	Cursor c = mDb.rawQuery("select da_data from validita", null);
    	c.moveToFirst();
        String date = c.getString(0);
        c.close();
        return date;
    }

    /**
     * Return a String containing the db validity end date
     * 
     * @return String containing a date in format YYYY-MM-DD
     * @throws SQLException
     */
    public String fetchEndDate() throws SQLException {
    	Cursor c = mDb.rawQuery("select a_data from validita", null);
    	c.moveToFirst();
        String date = c.getString(0);
        c.close();
        return date;
    }
    
    /**
     * Return a Cursor over the list of all 'bacini' in database linee_corse
     * 
     * @return Cursor over all 'bacini'
     */
    public Cursor fetchBacini() throws SQLException {

    	return mDb.rawQuery("select distinct bacino as _id from linee_corse where bacino <> ''", null);
    }

    /**
     * Return a Cursor over the list of all 'linee' for a given bacino
     * 
     * @param bacino city of South Tyrol
     * @return Cursor over all 'linee' in a given city
     */
    public Cursor fetchLinee(String bacino) throws SQLException {
    	
    	String[] selectionArgs = {bacino};
    	return mDb.rawQuery("select distinct id_linea_breve as _id from linee_corse where bacino=? and id_linea_breve <> ''", selectionArgs);        
    }
    
    /**
     * Return a Cursor over the list of the 'destinazioni' for a given bacino and linea
     * 
     * @param bacino city of South Tyrol
     * @param linea bus line for a given city
     * @return Cursor over the 'destinazioni' of a given line
     */
    public Cursor fetchDestinazioni(String bacino, String linea) throws SQLException {
    	
    	String[] selectionArgs = {bacino, linea};
    	return mDb.rawQuery("select distinct destinazione_it as _id from linee_corse where bacino=? and id_linea_breve=?", selectionArgs);
    }

    /**
     * Return a Cursor over the list of all 'paline' (bus stops) for a given bacino, linea, and destinazione
     * 
     * @param bacino city of South Tyrol
     * @param linea bus line for a given city
     * @param destinazione destination chosen for a given linea
     * @return Cursor over the 'paline' of a given linea
     */
    public Cursor fetchPaline(String bacino, String linea, String destinazione) throws SQLException {
    	
    	String[] selectionArgs = {bacino, linea, destinazione};
    	return mDb.rawQuery(
    			"select orari.id_palina as _id, paline.luogo as luogo from orari_passaggio as orari, paline " +
    			"where orari.codice_corsa = " +
    			" (select _id from linee_corse " + 
    			"	where bacino=? " +
    			"	    and  id_linea_breve=? " +
    			"	    and destinazione_it=? " + 
    			"	    and substr(effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
    			fetchStartDate() + "')) + 1,1)='1' " + 
    			"	    and orario_partenza > '0800' " +
    			"	    limit 1) " + 
    			"	  and orari.id_palina=paline._id " +
    			"	 order by orari.progressivo", selectionArgs);
    }

    /**
     * Return a Cursor over the list of next 'orari' for a given bacino, linea, destinazione, and palina
     * 
     * @param bacino city of South Tyrol
     * @param linea bus line for a given city
     * @param destinazione destination chosen for a given linea
     * @param palina bus stop
     * @return Cursor over the 'orari' of a given linea
     */
    public Cursor fetchOrari(String bacino, String linea, String destinazione, String palina) throws SQLException {
    	
    	String[] selectionArgs = {bacino, linea, destinazione, palina};
    	return mDb.rawQuery(
    			"select strftime('%H:%M',orari.orario) as _id " + 
                "  from linee_corse as linee, orari_passaggio as orari, paline " +
                "  where bacino=? " +
                "  and linee.id_linea_breve=? " + 
                "  and linee.destinazione_it=? " + 
                "  and substr(linee.effettuazione,round(strftime('%J','now','localtime')) - round(strftime('%J','" +
                fetchStartDate() + "')) + 1,1)='1' " + 
                "  and linee._id=orari.codice_corsa " + 
                "  and linee.codice_linea=orari.codice_linea " +
                "  and orari.id_palina=paline._id " +
                "  and paline._id=? " +
                //"  and time(orari.orario) >= strftime('%H:%M','now','localtime') " +
                "  order by _id "
                //+ "  limit 6"
                , selectionArgs);
    }
}
