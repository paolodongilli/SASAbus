package it.sasabz.sasabus.ui.routing;

import it.sasabz.android.sasabus.R;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseExpandableListAdapter{

	
	private Context context;
	private List<String> connections;
	private Map<Integer, List<String>> connectiondetails;
	
	public SearchResultsAdapter(Context context, List<String> connections,
			Map<Integer, List<String>> connectiondetails) {
		this.context = context;
		this.connections = connections;
		this.connectiondetails = connectiondetails;
	}
	
	@Override
	public int getGroupCount() {
		return connections.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return connectiondetails.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return connections.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return connectiondetails.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		View view;
		String test = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listview_item_search_result, null);
		} else {
			view = convertView;
		}
		
		TextView departureTime = (TextView) view.findViewById(R.id.textview_time_departure);
		departureTime.setText(test);
		
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
	
		View view;
		String test = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.listview_item_search_result_detail, null);
		} else {
			view = convertView;
		}
		
		TextView departureTime = (TextView) view.findViewById(R.id.textview_time_departure);
		departureTime.setText(test);
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}