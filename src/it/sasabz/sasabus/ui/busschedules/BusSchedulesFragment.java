package it.sasabz.sasabus.ui.busschedules;

import java.util.List;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.Area;
import it.sasabz.sasabus.data.models.BusLine;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.Itinerary;
import it.sasabz.sasabus.logic.BusSchedulesDatabase;
import it.sasabz.sasabus.ui.Utility;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class BusSchedulesFragment extends SherlockFragment{

	private ScrollView scrollViewBusSchedules;
	private Spinner spinnerArea;
	private Spinner spinnerBusLine;
	private Spinner spinnerBuslineDirection;
	private ListView listviewBuslineDepartures;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_bus_schedules, container, false);
		
		initializeViews(view);
		
		//Spinners
		addEntriesToSpinnerArea();
		addOnItemSelectedListenerToSpinnerArea();
		
		addEntriesToSpinnerBusline();
		addOnItemSelectedListenerToSpinnerBusline();
		
		addEntriesToSpinnerDirection();
		addOnItemSelectedListenerToSpinnerBuslineDirection();
		
		
		//ListView
		addAdapterToListView();
		addOnItemSelectedListenerToListView();
		
		return view;
	}
	
	private void initializeViews(View view) {
		scrollViewBusSchedules = (ScrollView) view.findViewById(R.id.scrollview_busschedules);
		spinnerArea = (Spinner) view.findViewById(R.id.spinner_area);
		spinnerBusLine = (Spinner) view.findViewById(R.id.spinner_busline);
		spinnerBuslineDirection = (Spinner) view.findViewById(R.id.spinner_busline_direction);
		listviewBuslineDepartures = (ListView) view.findViewById(R.id.listview_busline_departures);
	}
	
	
	private void addEntriesToSpinnerArea() {
		List<Area> areas = BusSchedulesDatabase.getAllAreas();
		
		ArrayAdapter<Area> adapterArea = new ArrayAdapter<Area>(getSherlockActivity(),
				android.R.layout.simple_spinner_item, areas);
		adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerArea.setAdapter(adapterArea);
	}
	
	private void addEntriesToSpinnerBusline() {
		List<BusLine> buslines = BusSchedulesDatabase.getAllBusLinesForArea((Area) spinnerArea.getSelectedItem());
		
		ArrayAdapter<BusLine> adapterBusLines = new ArrayAdapter<BusLine>(getSherlockActivity(),
				android.R.layout.simple_spinner_item, buslines);
		adapterBusLines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerBusLine.setAdapter(adapterBusLines);
	}
	
	private void addEntriesToSpinnerDirection() {
		List<String> directions = BusSchedulesDatabase.getStartAndEndBusStopForBusline(null, null);
		
		ArrayAdapter<String> adapterDirections = new ArrayAdapter<String>(getSherlockActivity(),
				android.R.layout.simple_spinner_item, directions);
		adapterDirections.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinnerBuslineDirection.setAdapter(adapterDirections);
	}
	
	
	private void addOnItemSelectedListenerToSpinnerArea() {
		spinnerArea.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO change the adapter of the spinnerBusline and spinnerDirection
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private void addOnItemSelectedListenerToSpinnerBusline() {
		spinnerBusLine.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO change the adapter of the sinnerDirection
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	private void addOnItemSelectedListenerToSpinnerBuslineDirection() {
		spinnerBuslineDirection.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO display the data
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}
	
	
	private void addAdapterToListView() {
		ListAdapter adapter = new BuslineDepartureAdapter(getSherlockActivity(),
				R.layout.listview_item_busline_departure, R.id.textview_busstop, 
				BusSchedulesDatabase.getItineraryForLine(null, null, null));
		listviewBuslineDepartures.setAdapter(adapter);
		Utility.getListViewSize(listviewBuslineDepartures);
	}
	
	private void addOnItemSelectedListenerToListView() {
		listviewBuslineDepartures.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intentBusSchedulesDetails = new Intent(getSherlockActivity(),
						BusSchedulesDetailsActivity.class);
//				intentBusSchedulesDetails.putExtra("view", view.getTag());
				startActivity(intentBusSchedulesDetails);
				
			}
		});
	}
	
}