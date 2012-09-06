package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.SASAbus;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class Favorit {
	
	private int id = 0;

	private int linea = 0;
	
	private String partenza_de = null;
	
	private String destinazione_de = null;
	
	public Favorit()
	{
		
	}
	
	public Favorit(int linea, String partenza_de, String destinazione_de)
	{
		this.linea = linea;
		this.partenza_de = partenza_de;
		this.destinazione_de = destinazione_de;
	}
	
	public Favorit(int id, int linea, String partenza_de, String destinazione_de)
	{
		this.id = id;
		this.linea = linea;
		this.partenza_de = partenza_de;
		this.destinazione_de = destinazione_de;
	}

	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the linea
	 */
	public int getLinea() {
		return linea;
	}

	/**
	 * @param linea the linea to set
	 */
	public void setLinea(int linea) {
		this.linea = linea;
	}

	/**
	 * @return the partenza_de
	 */
	public String getPartenza_de() {
		return partenza_de;
	}

	/**
	 * @param partenza_de the partenza_de to set
	 */
	public void setPartenza_de(String partenza_de) {
		this.partenza_de = partenza_de;
	}

	/**
	 * @return the destinazione_de
	 */
	public String getDestinazione_de() {
		return destinazione_de;
	}

	/**
	 * @param destinazione_de the destinazione_de to set
	 */
	public void setDestinazione_de(String destinazione_de) {
		this.destinazione_de = destinazione_de;
	}
	
	public boolean insert(SQLiteDatabase db)
	{
		if(this.linea == 0 || this.partenza_de == null || this.destinazione_de == null)
			return false;
		if(db == null || !db.isOpen() || db.isReadOnly())
			return false;
		String query_select = "Select * from " + FavoritenDB.FAVORITEN_TABLE_NAME + " where linea=\"" + linea + "\" and " +
				"partenza_de=\"" + partenza_de + "\" and destinazione_de=\"" + destinazione_de + "\";";
		Cursor cursor = db.rawQuery(query_select,null);
		if(cursor.getCount() != 0)
			return false;
		String query = "Insert into " + FavoritenDB.FAVORITEN_TABLE_NAME + 
				" VALUES ((select count(*) from " + FavoritenDB.FAVORITEN_TABLE_NAME + "), " +
						"\"" + linea + "\", \"" 
			+ partenza_de + "\", \"" + destinazione_de + "\");";
		db.execSQL(query);
		return true;
	}
	
	public boolean delete(SQLiteDatabase db)
	{
		if(db == null || !db.isOpen() || db.isReadOnly())
		{
			Log.v("FAVORITENLOESCHEN", "DB");
			return false;	
		}
		String query_select = "delete from " + FavoritenDB.FAVORITEN_TABLE_NAME + " where id=" + this.id + ";";
		Log.v("SQL_QUERY", query_select);
		db.execSQL(query_select);
		return true;
	}

	public String toString()
	{
		Palina partenza = PalinaList.getTranslation(partenza_de, "de");
		Palina destinazione = PalinaList.getTranslation(destinazione_de, "de");
		Linea linea = LineaList.getById(this.linea);
		return linea.toString() + " - " + partenza.toString() + " - " + destinazione.toString();
	}
}
