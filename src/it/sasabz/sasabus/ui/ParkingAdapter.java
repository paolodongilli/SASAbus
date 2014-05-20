
package it.sasabz.sasabus.ui;

import it.sasabz.android.sasabus.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ParkingAdapter extends ArrayAdapter<ParkingData>
{

   public ParkingAdapter(Context context)
   {
      super(context, 0);
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      if (convertView == null)
      {
         convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.parking_free_slots, parent, false);
      }
      ParkingData data = this.getItem(position);

      TextView nameView = (TextView) convertView.findViewById(R.id.parkName);
      TextView slotsView = (TextView) convertView.findViewById(R.id.parkSlots);
      ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.parkProgressBar);

      nameView.setText(data.name);
      slotsView.setText("" + (data.tot - data.free) + "/" + data.tot);
      progressBar.setMax(data.tot);
      progressBar.setProgress(data.tot - data.free);

      return convertView;
   }

}
