/**
 *
 * SelectFavoritenActivity.java
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

package it.sasabz.android.sasabus;

import java.util.Locale;
import java.util.Vector;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.classes.About;
import it.sasabz.android.sasabus.classes.Bacino;
import it.sasabz.android.sasabus.classes.BacinoList;
import it.sasabz.android.sasabus.classes.Credits;
import it.sasabz.android.sasabus.classes.DBObject;
import it.sasabz.android.sasabus.classes.Favorit;
import it.sasabz.android.sasabus.classes.FavoritenDB;
import it.sasabz.android.sasabus.classes.FavoritenList;
import it.sasabz.android.sasabus.classes.LineaList;
import it.sasabz.android.sasabus.classes.Modus;
import it.sasabz.android.sasabus.classes.MyFavoritenListAdapter;
import it.sasabz.android.sasabus.classes.MyListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SelectFavoritenActivity extends ListActivity {

    
    private Vector<Favorit> list = null;
    
    public SelectFavoritenActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.standard_listview_layout);
        
        TextView titel = (TextView)findViewById(R.id.titel);
        titel.setText(R.string.mode_favoriten);
        
        TextView line = (TextView)findViewById(R.id.line);
        TextView from = (TextView)findViewById(R.id.from);
        TextView to = (TextView)findViewById(R.id.to);
        
        line.setText("");
        from.setText("");
        to.setText("");
        fillData();
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Favorit fav = list.get(position);
    	Intent showOrari = new Intent(this, ShowOrariActivity.class);
    	showOrari.putExtra("linea", fav.getLinea());
    	showOrari.putExtra("palina", fav.getPartenza_de());
    	showOrari.putExtra("destinazione", fav.getDestinazione_de());
    	startActivity(showOrari);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      if (v.getId() == android.R.id.list) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle(list.get(info.position).toString());
        menu.add(Menu.NONE, 0, 0, R.string.delete);		
      }
    }
    
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int menuItemIndex = item.getItemId();
      if(menuItemIndex == 0)
      {
    	  Favorit fav = list.get(info.position);
    	  SQLiteDatabase db = new FavoritenDB(this).getWritableDatabase();
    	  if(fav.delete(db))
    		  Log.v("FAVORITENLOESCHEN", "OK");
    	  else 
    		  Log.v("FAVORITENLOESCHEN", "FEHLER");
    	  db.close();
    	  list.remove(info.position);
    	  MyFavoritenListAdapter favoriten = new MyFavoritenListAdapter(SASAbus.getContext(), R.id.text, R.layout.standard_row, list);
    	  setListAdapter(favoriten);
      }
      return false;
    }
    
    
    /**
     * fills the list_view with the modes which are offered to the user
     */
    public void fillData()
    {
    	 list = FavoritenList.getList();
    	 MyFavoritenListAdapter favoriten = new MyFavoritenListAdapter(SASAbus.getContext(), R.id.text, R.layout.standard_row, list);
    	 ListView liste = (ListView)findViewById(android.R.id.list);
         setListAdapter(favoriten); 
         registerForContextMenu(liste);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
    	 MenuInflater inflater = getMenuInflater();
    	 inflater.inflate(R.menu.optionmenu, menu);
         return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
			{
				new About(this).show();
				return true;
			}
			case R.id.menu_credits:
			{
				new Credits(this).show();
				return true;
			}	
			case R.id.menu_infos:
			{
				Intent infos = new Intent(this, InfoActivity.class);
				startActivity(infos);
				return true;
			}
		}
		return false;
	}
}
