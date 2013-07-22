package it.sasabz.sasabus.ui.routing;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.BusSchedulesDatabase;
import it.sasabz.sasabus.logic.SearchConnection;
import it.sasabz.sasabus.ui.Utility;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class SearchResultsActivity extends SherlockActivity{
	
	private SearchConnection connection;
	private ExpandableListView expandablelistviewResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_search_results);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		connection = new SearchConnection();
		
		
		initializeViews();
		
		searchForConnection();
		
		addAdapterToExpandableListView();
		
		setGroupIndicatorToRight();
		
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initializeViews() {
		
		expandablelistviewResults = (ExpandableListView) findViewById
				(R.id.expandablelistview_results);
		
	}
	
	private void searchForConnection() {
		
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
	
}