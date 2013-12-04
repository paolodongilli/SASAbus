package it.sasabz.sasabus.ui.searchinputfield;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.ui.Utility;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FavoriteBusStopsAdapter extends ArrayAdapter<String> {

	private Context context;
	private int resource;
	
	public FavoriteBusStopsAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		if (convertView == null) {
        	view = LayoutInflater.from(context).inflate(resource, null);
        } else {
            view = convertView;
        }

		TextView textviewBusstop = (TextView) view.findViewById(R.id.busstop);
//		String busstop = ""+super.getItem(position);
//		textviewBusstop.setText("Line 1 - Via Fago");
		
		ImageButton imagebuttonDelete = (ImageButton) view.findViewById(R.id.imagebutton_delete);
		imagebuttonDelete.setFocusable(false);
		imagebuttonDelete.setFocusableInTouchMode(false);
		
		
		return view;
	}

}