package it.sasabz.sasabus.ui.news;

import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.DBObject;
import it.sasabz.sasabus.data.models.News;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NewsAdapter extends ArrayAdapter<News>{
	
	private Context context;
	private int resource;
	
	public NewsAdapter(Context context, int resource, int textViewResourceId,
			List<News> objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
		this.resource = resource;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view;

        if (convertView == null) {
        	view = LayoutInflater.from(context).inflate(resource, null);
        } else {
            view = convertView;
        }
        
        TextView textviewBusline = (TextView) view.findViewById(R.id.textview_busline);
        
        String buslines = "";
        String linesAffected = getItem(position).getLinesAffectedAsString();
        if (linesAffected != "") {
        	buslines = context.getResources().getString(R.string.lines)+": "+ linesAffected;
        } else {
        	textviewBusline.setVisibility(View.GONE);
        }
        
        textviewBusline.setText(buslines);
        
        TextView textviewNewsTitle = (TextView) view.findViewById(R.id.textview_title);
        String newsTitle = getItem(position).getTitle();
        textviewNewsTitle.setText(newsTitle);
        

        return view;		
		
	}
}