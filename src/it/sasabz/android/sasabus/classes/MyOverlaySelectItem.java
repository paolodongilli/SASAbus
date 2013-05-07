/**
 *
 * MyOverlayItem.java
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
package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.MapSelectActivity;
import it.sasabz.android.sasabus.classes.dbobjects.Palina;
import it.sasabz.android.sasabus.classes.dialogs.SelectDialog;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class MyOverlaySelectItem extends OverlayItem{
	
	private MapSelectActivity activity = null;
	
	private Palina palina = null;
	
	public MyOverlaySelectItem(GeoPoint point, Drawable bus, MapSelectActivity activity, Palina palina) {
		super(point, "", palina.toString(), bus);
		this.activity = activity;
		this.palina = palina;
	}

	public void onTap(Context context)
	{
		SelectDialog dialog = new SelectDialog(activity, palina);
		dialog.show();
	}
	
}
