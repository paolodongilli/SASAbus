package it.sasabz.sasabus.ui.routing;

import java.util.ArrayList;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.hafas.XMLConnection;
import it.sasabz.sasabus.logic.BusSchedulesDatabase;
import it.sasabz.sasabus.logic.SearchConnection;
import it.sasabz.sasabus.ui.Utility;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class SearchResultsActivity extends SherlockActivity{
	
	private MenuItem optionsMenuitemSave;
	
	private TextView textviewDeparture;
	private TextView textviewArrival;
	private SearchConnection connection;
	private ExpandableListView expandablelistviewResults;
	
	private String departure = "";
	private String arrival = "";
	private String date = "";
	private String time = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_search_results);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		connection = new SearchConnection();
		
		Bundle extras = getIntent().getExtras();
		
		getParametersForSearch(extras);
		
		initializeViews();
		
		addBusstopsToTextview();
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.search_results_activity, menu);
		optionsMenuitemSave = menu.findItem(R.id.menu_save);
		
		searchForConnection(departure, arrival, date, time);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.menu_save:
			saveResults();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setSaveActionButtonState(boolean refreshing) {
		if(refreshing) {
			optionsMenuitemSave.setActionView(R.layout.actionbar_indeterminate_progress);
		} else {
			optionsMenuitemSave.setActionView(null);
		}
	}
	
	
	private void getParametersForSearch(Bundle extras) {
		try {
			departure = extras.getString("departure");
			arrival = extras.getString("arrival");
			date = extras.getString("date");
			time = extras.getString("time");
		} catch (Exception e) {
			Log.e("NULL", "Parameters for connection search are null");
		}
	}
	
	
	private void initializeViews() {
		textviewDeparture = (TextView) findViewById(R.id.textview_departure);
		textviewArrival = (TextView) findViewById(R.id.textview_arrival);
		expandablelistviewResults = (ExpandableListView) findViewById
				(R.id.expandablelistview_results);
	}
	
	private void addBusstopsToTextview() {
		textviewDeparture.setText(departure);
		textviewArrival.setText(arrival);
	}
	
	
	private void searchForConnection(String departure, String arrival, String date, String time) {
		setSaveActionButtonState(true);
		connection.searchForConnection(departure, arrival, date, time, this, new SearchCallback() {
			@Override
			public void searchIsFinished(ArrayList<XMLConnection> connections) {
				addAdapterToExpandableListView();
				setGroupIndicatorToRight();
				setSaveActionButtonState(false);
				expandablelistviewResults.setVisibility(View.VISIBLE);
			}
		});
	}
	
	public interface SearchCallback {
		void searchIsFinished(ArrayList<XMLConnection> connections);
	}
	
	
	private void addAdapterToExpandableListView() {
		SearchResultsAdapter adapter = new SearchResultsAdapter(this,
				connection.getConncections(), connection.getConnectionDetails());
		expandablelistviewResults.setAdapter(adapter);
	}

	private void setGroupIndicatorToRight() {
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
 
        expandablelistviewResults.setIndicatorBounds(width - Utility.getDipsFromPixel(this, 100), width
                - Utility.getDipsFromPixel(this, 10));
	}
	
	
	private void saveResults() {
		// TODO Save Results so that they can be viewed offline
		
	}
	
}