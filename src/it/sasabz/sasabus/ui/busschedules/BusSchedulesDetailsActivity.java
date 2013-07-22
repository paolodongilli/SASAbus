package it.sasabz.sasabus.ui.busschedules;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.BusSchedulesDatabase;
import it.sasabz.sasabus.ui.Utility;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BusSchedulesDetailsActivity extends SherlockActivity {

	private TextView textviewLinenumber;
	private TextView textviewBusstop;
	private ListView listviewDepartureTimes;
	private ListView listviewCourse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_busline_details);
		
		initializeViews();
		
		addAdapterToListViewDepartureTimes();
		addOnItemSelectedListenerToListViewDepartureTimes();
		
		addAdapterToListViewCourse();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void initializeViews() {
		textviewLinenumber = (TextView) findViewById(R.id.textview_busline_number);
		textviewBusstop = (TextView) findViewById(R.id.textview_busstop);
		listviewDepartureTimes = (ListView) findViewById(R.id.listview_departure_times);
		listviewCourse = (ListView) findViewById(R.id.listview_line_course);
	}
	
	
	private void addAdapterToListViewDepartureTimes() {
		ListAdapter adapter = new BuslineDepartureTimesAdapter(this,
				R.layout.listview_item_busline_departure_time, R.id.textview_time, 
				BusSchedulesDatabase.getDepartureTimesForBusstop(null, null, null));
		listviewDepartureTimes.setAdapter(adapter);
	}
	
	private void addOnItemSelectedListenerToListViewDepartureTimes() {
		listviewDepartureTimes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BuslineDepartureTimesAdapter adapter = (BuslineDepartureTimesAdapter) 
						parent.getAdapter();
				adapter.setSelectedIntex(position);
				
				
				//TODO refresh the course
			}
		});
	}
	
	
	private void addAdapterToListViewCourse() {
		ListAdapter adapter = new BuslineCourseAdapter(this,
				R.layout.listview_item_busline_course, R.id.textview_time, 
				BusSchedulesDatabase.getItineraryForCourse(null, null, null));
				listviewCourse.setAdapter(adapter);
	}
	
}