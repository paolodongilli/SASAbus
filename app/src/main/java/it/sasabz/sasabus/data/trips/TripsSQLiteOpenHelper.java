package it.sasabz.sasabus.data.trips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TripsSQLiteOpenHelper extends SQLiteOpenHelper
{

	static TripsSQLiteOpenHelper instance = null;
	private static final String DATENBANK_NAME = "sasabusTrips.db";
	private static final int DATENBANK_VERSION = 1;

	private static String CREATE_TRIPS = "CREATE TABLE trips( "
			+ "  busstop_start INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
			+ "  busstop_finish INTEGER NOT NULL, "
			+ "  lid INTEGER NOT NULL,"
			+ "  fid INTEGER NOT NULL,"
			+ "  tagesart INTEGER NOT NULL,"
			+ "  time_start TIMESTAMP NOT NULL,"
			+ "  time_finish TIMESTAMP NOT NULL);";

	private TripsSQLiteOpenHelper(Context context) {
		super(context, DATENBANK_NAME, null, DATENBANK_VERSION);
	}

	public static TripsSQLiteOpenHelper getInstance(Context context) {
		if (instance == null)
			instance = new TripsSQLiteOpenHelper(context);
		return instance;
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//TODO
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TRIPS);
	}

	public ArrayList<FinishedTrip> getFinishedTrips() {
		ArrayList<FinishedTrip> ret = new ArrayList<FinishedTrip>();;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			db = getWritableDatabase();
			c = db.rawQuery(
				"SELECT * " +
				"  FROM trips;", null);
			DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Log.e("size", c.getCount() + "");
			while (c.moveToNext()) {
				ret.add(new FinishedTrip(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3), c.getInt(4), iso8601Format.parse(c.getString(5)), iso8601Format.parse(c.getString(6))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { c.close(); } catch (Exception e) { ; }
			try { db.close(); } catch (Exception e) { ; }
		}
		return ret;
	}

	public boolean addTrip(FinishedTrip trip) {
		if (trip == null)
			return false;
		boolean ret = false;
		// Es wird versucht, Fach in die Datenbank aufzunehmen
		SQLiteDatabase db = null;
		try {
			DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			db = getWritableDatabase();
			ContentValues werte = new ContentValues(7);
			werte.put("busstop_start", trip.getStartOrt());
			werte.put("busstop_finish", trip.getFinishOrt());
			werte.put("lid", trip.getLineId());
			werte.put("fid", trip.getTripId());
			werte.put("tagesart", trip.getTagesart());
			werte.put("time_start", iso8601Format.format(trip.getStartTime()));
			werte.put("time_finish", iso8601Format.format(trip.getFinishTime()));
			Log.e("insert", trip.toString());
			ret = db.insertOrThrow("trips", null, werte) > 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				db.close();
			} catch (Exception e) {
				;
			}
		}
		return ret;
	}
}
