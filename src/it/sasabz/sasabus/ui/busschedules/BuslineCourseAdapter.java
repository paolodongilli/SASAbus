package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.ui.Utility;

import java.util.List;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BuslineCourseAdapter extends ArrayAdapter<Itinerary> {

	private Context context;
	private Time timeNow;
	
	public BuslineCourseAdapter(Context context, int resource,
			int textViewResourceId, List<Itinerary> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		timeNow = new Time();
		timeNow.setToNow();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = super.getView(position, convertView, parent);
		
		ImageView imagebuttonBusstop = (ImageView) view.findViewById(R.id.imageview_busstop);
		TextView textviewTime = (TextView) view.findViewById(R.id.textview_time);
		TextView textviewBusstop = (TextView) view.findViewById(R.id.textview_busstop);
		
		String busstop = "" + super.getItem(position).getBusStopId();
		textviewBusstop.setText("Meran - Seilb. Meran 2000 test test");
		
		Time timeTime = super.getItem(position).getTime();
		String hour = Utility.getTimeWithZero(timeTime.hour);
		String minute = Utility.getTimeWithZero(timeTime.minute);
		String time = hour+":"+minute;
		
		int timeresult = Time.compare(timeTime, timeNow);
		Log.i("timeresult", ""+timeresult);
		if (timeresult < 0) {
			imagebuttonBusstop.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_radio_off_holo_light));
		} else if (timeresult == 0) {
			imagebuttonBusstop.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_radio_on_focused_holo_light));
		} else {
			imagebuttonBusstop.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_radio_off_focused_holo_light));
		}
		
		Log.i("comparison", timeNow + " " + timeTime);
		
		textviewTime.setText(time);
		
		
		return view;
	}
	
}