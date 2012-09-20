package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritenDB extends SQLiteOpenHelper{

	 public static final int DATABASE_VERSION = 1;
	
	 public static final String FAVORITEN_TABLE_NAME = "favoriten";
	 
	 private static final String FAVORITEN_TABLE_CREATE =
	                "CREATE TABLE " + FAVORITEN_TABLE_NAME + "(" +
	                		"id INT AUTO_INCREMENT, " +
	                		"linea INT, " +
	                		"partenza_de TEXT, " +
	                		"destinazione_de TEXT );";
	
	
	
	public FavoritenDB(Context context) {
		super(context, context.getResources().getString(R.string.favoriten_db_name), null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FAVORITEN_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("delete from " + FAVORITEN_TABLE_NAME + ";");
		
	}

}
