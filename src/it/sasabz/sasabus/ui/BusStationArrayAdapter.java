
package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BusStationArrayAdapter extends ArrayAdapter<BusTripBusStopTime>
{
   private final MainActivity     context;
   private final BusDepartureItem item;

   public BusStationArrayAdapter(MainActivity context, BusDepartureItem item)
   {
      super(context, R.layout.trip_detail_row);
      this.context = context;
      this.item = item;
   }

   @Override
   public View getView(int position, View conView, ViewGroup parent)
   {
      LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      View superView = inflater.inflate(R.layout.trip_detail_row, parent, false);

      ImageView imageView = (ImageView) superView.findViewById(R.id.image_route);

      BusTripBusStopTime element = this.getItem(position);

      /*
       * The conditions to find out what image do we need for displaying
       */
      if (position == this.item.getDeparture_index())
      {
         imageView.setImageResource(R.drawable.middle_bus);
      }
      else if (position == 0)
      {
         imageView.setImageResource(R.drawable.ab_punkt);
      }
      else if (position == this.getCount() - 1)
      {
         imageView.setImageResource(R.drawable.an_punkt);
      }
      else
      {
         imageView.setImageResource(R.drawable.middle_punkt);
      }

      TextView txt_time = (TextView) superView.findViewById(R.id.txt_time);
      txt_time.setText(DeparturesThread.formatSeconds(element.getSeconds()));

      TextView txt_delay = (TextView) superView.findViewById(R.id.txt_delay);
      String delay = "";
      if (position >= this.item.getDelay_index())
      {
         delay = this.item.getDelay();
      }
      txt_delay.setText(delay);
      TextView txt_busstopname = (TextView) superView.findViewById(R.id.txt_busstopname);
      String busStationName = "";
      try
      {
         busStationName = this.context.getBusStationNameUsingAppLanguage(this.context.getOpenDataStorage().getBusStations().findBusStop(element.getBusStop()).getBusStation());
      }
      catch (Exception exxooo)
      {
         System.out.println("Do nothing");
      }
      txt_busstopname.setText(busStationName);

      /*
       * Set Colors of the various busstops in the list
       */

      if (position < this.item.getDeparture_index())
      {
         txt_busstopname.setTextColor(this.context.getResources().getColor(R.color.divider_grey));
         txt_delay.setTextColor(this.context.getResources().getColor(R.color.divider_grey));
         txt_time.setTextColor(this.context.getResources().getColor(R.color.divider_grey));
      }
      else if (position == this.item.getSelectedIndex())
      {
         superView.setBackgroundColor(Color.LTGRAY);
      }
      else
      {
         superView.setBackgroundColor(Color.WHITE);
      }

      return superView;
   }
}
