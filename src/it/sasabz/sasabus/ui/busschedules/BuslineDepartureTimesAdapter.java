package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.ui.Utility;

import java.util.List;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BuslineDepartureTimesAdapter extends ArrayAdapter<Itinerary> {

	private Context context;
	private int selectedIndex;
	private int normalColor;
	private int selectedColor;
	
	public BuslineDepartureTimesAdapter(Context context, int resource,
			int textViewResourceId, List<Itinerary> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		normalColor = context.getResources().getColor(R.color.transparent);
		selectedColor = context.getResources().getColor(R.color.orange_pressed);
	}
	
	
	private class ViewHolder {
        RelativeLayout llItem;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi = convertView;
        ViewHolder holder;
        if(convertView == null) {
            vi = LayoutInflater.from(context).inflate(R.layout.listview_item_busline_departure_time, null);
            holder = new ViewHolder();

            holder.llItem = (RelativeLayout) vi;

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if(selectedIndex!= -1 && position == selectedIndex) {
            holder.llItem.setBackgroundColor(selectedColor);
        } else {
            holder.llItem.setBackgroundColor(normalColor);
        }
        
        TextView textviewTime = (TextView) holder.llItem.findViewById(R.id.textview_time);
		
		Time timeTime = super.getItem(position).getTime();
		String hour = Utility.getTimeWithZero(timeTime.hour);
		String minute = Utility.getTimeWithZero(timeTime.minute);
		
		String time = hour+":"+minute;
		
		textviewTime.setText(time);

        return vi;

	}
	
	public void setSelectedIntex(int selectedIntex) {
		this.selectedIndex = selectedIntex;
		notifyDataSetChanged();
	}

}