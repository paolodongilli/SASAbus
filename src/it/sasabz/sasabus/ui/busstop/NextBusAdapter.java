package it.sasabz.sasabus.ui.busstop;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.ui.Utility;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class NextBusAdapter extends ArrayAdapter<Itinerary> {

	public NextBusAdapter(Context context, int resource,
			int textViewResourceId, List<Itinerary> objects) {
		super(context, resource, textViewResourceId, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = super.getView(position, convertView, parent);

		TextView textviewLine = (TextView) view.findViewById(R.id.textview_busline);
		TextView textviewTime = (TextView) view.findViewById(R.id.textview_minutes);
		
		String busline = "" + super.getItem(position).getBusStopId();
		
		Time timeTime = super.getItem(position).getTime();
		String minutes = Utility.getTimeWithZero(timeTime.minute);
		String seconds = Utility.getTimeWithZero(timeTime.second);
		String time = minutes+":"+seconds;
		
		textviewLine.setText("Line 1 - Via Fago");
		textviewTime.setText(time);
		
		return view;
	}

}