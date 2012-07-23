package it.sasabz.android.sasabus.classes;

import it.sasabz.android.sasabus.R;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class MyOverlayItem extends OverlayItem{
	
	public MyOverlayItem(GeoPoint point, String titel, String message,
			Drawable bus) {
		super(point, titel, message, bus);
	}

	public void onTap(Context context)
	{
		new AlertDialog.Builder(context).setTitle(this.getTitle()).setMessage(this.getSnippet()).setNeutralButton("Ok", null).create().show();
	}
	
}
