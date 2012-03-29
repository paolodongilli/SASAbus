/**
 *
 * SharedMenu.java
 * 
 * Created: Jun 20, 2011 9:47:34 PM
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
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.R.string;
import android.view.Menu;

public class SharedMenu {

	public static final int MENU_ABOUT = 0;
	public static final int MENU_CREDITS = 1;
	public static final int MENU_SETTINGS = 2;
	
	public static void onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_ABOUT, 0, R.string.menu_about);
    	menu.add(0, MENU_CREDITS, 1, R.string.menu_credits);
    	menu.add(0,MENU_SETTINGS, 2, R.string.menu_settings);
    }
}
