package it.sasabz.android.sasabus.classes;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;


public class MyMapRouteOverlay extends Overlay {

	  private GeoPoint gp1;
	  private GeoPoint gp2;

	  private int mode=0;
	  private int defaultColor;




	  public MyMapRouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode, Context context) // GeoPoint is a int. (6E)
	  {
		super(context);
	    this.gp1 = gp1;
	    this.gp2 = gp2;
	    this.mode = mode;
	    defaultColor = 999; // no defaultColor

	  }

	  public MyMapRouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor, Context context)
	  {
		  	super(context);
		  	this.gp1 = gp1;
		  	this.gp2 = gp2;
		  	this.mode = mode;
		  	this.defaultColor = defaultColor;
	  }

	  public int getMode()
	  {
	    return mode;
	  }

	  public void draw(Canvas canvas, MapView mapView, boolean shadow, long when)
	    {
	      Projection projection = mapView.getProjection();
	      if (shadow == false)
	      {

	        Paint paint = new Paint();
	        paint.setAntiAlias(true);
	        Point point = new Point();
	        projection.toPixels(gp1, point);

	        if(mode==2)
	        {
	          if(defaultColor==999)
	            paint.setColor(Color.RED);
	          else
	            paint.setColor(defaultColor);
	          Point point2 = new Point();
	          projection.toPixels(gp2, point2);
	          paint.setStrokeWidth(5);
	          paint.setAlpha(120);
	          canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
	        }

	      }
	    }

	@Override
	protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
		 Projection projection = mapView.getProjection();
	      if (shadow == false)
	      {

	        Paint paint = new Paint();
	        paint.setAntiAlias(true);
	        Point point = new Point();
	        projection.toPixels(gp1, point);

	        if(mode==2)
	        {
	          if(defaultColor==999)
	            paint.setColor(Color.RED);
	          else
	            paint.setColor(defaultColor);
	          Point point2 = new Point();
	          projection.toPixels(gp2, point2);
	          paint.setStrokeWidth(5);
	          paint.setAlpha(120);
	          canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
	        }

	      }
	}
}
