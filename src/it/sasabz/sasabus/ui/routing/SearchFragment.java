package it.sasabz.sasabus.ui.routing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.searchinputfield.SearchInputField;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class SearchFragment extends SherlockFragment {
	
	private AutoCompleteTextView autocompletetextviewDeparture;
	private ImageButton imagebuttonMapDeparture;
	
	private AutoCompleteTextView autoCompleteTextViewArrival;
	private ImageButton imagebuttonMapArrival;
	
	private ImageButton imagebuttonSwitch;
	
	private Button buttonDate;
	private Button buttonTime;
	private Button buttonSearch;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		
		initializeViews(view);
		
		addOnClickListenerForSwitch();
		
		insertCurrentDateIntoButton();
		addOnclickListenerForDate();
		
		insertCurrentTimeIntoButton();
		addOnclickListenerForTime();
		
		addOnclickListenerForSearchButton();
		
		setHasOptionsMenu(true);
		
		return view;
	}

	
	/**
	 * Initialize all the views present in the search fragment
	 * @param view the fragment which gets inflated
	 */
	private void initializeViews(View view) {
		
		autocompletetextviewDeparture = (AutoCompleteTextView) view.findViewById(
				R.id.autocompletetextview_departure);
		imagebuttonMapDeparture = (ImageButton) view.findViewById(
				R.id.imagebutton_map_departure);
		
		autoCompleteTextViewArrival = (AutoCompleteTextView) view.findViewById(
				R.id.autocompletetextview_arrival);
		imagebuttonMapArrival = (ImageButton) view.findViewById(
				R.id.imagebutton_map_arrival);
		
		imagebuttonSwitch = (ImageButton) view.findViewById(R.id.imagebutton_switch);
		
		buttonDate = (Button) view.findViewById(R.id.button_date);
		buttonTime = (Button) view.findViewById(R.id.button_time);
		
		View bs = view.findViewById(R.id.button_search);
		buttonSearch = (Button) bs.findViewById(R.id.button_search);
	}
	
	private void addOnClickListenerForSwitch() {
		imagebuttonSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String departure = autocompletetextviewDeparture.getText().toString();
				String arrival = autoCompleteTextViewArrival.getText().toString();
				
				autocompletetextviewDeparture.setText(arrival);
				autoCompleteTextViewArrival.setText(departure);
			}
		});
	}


	//Date
	/**
	 * Insert the current date into the date-button
	 * @param view
	 * 			is the total view of the fragment
	 */
	private void insertCurrentDateIntoButton(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePicker.dateFormat, Locale.ITALY);
        String currentDate = simpleDateFormat.format(new Date());
        
        buttonDate.setText(currentDate);
	}
	
	/**
	 * Add an OnClickListener to the date-button with the date,
	 * so that it can open up a DatePicker when the user clicks
	 * @param view
	 * 			is the total view of the fragment
	 */
	private void addOnclickListenerForDate(){
		buttonDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				openDatePickerDialog(b);
			}
		});
	}
	
	/**
	 * Actually open the DatePicker
	 */
	public void openDatePickerDialog(Button button){
		String dateButtonText = button.getText().toString();
		
		DatePicker datePicker = new DatePicker();
		datePicker.setDateAlreadySetString(dateButtonText);
		datePicker.setCallback(new DateHasBeenSetListener() {
			@Override
			public void dateHasBeenSet(String date) {
				buttonDate.setText(date);
			}
		});
		datePicker.show(getSherlockActivity().getSupportFragmentManager(), "Date Picker");
	}
	
	public interface DateHasBeenSetListener {
		void dateHasBeenSet(String date);
	}
	
	
	//Time
	/**
	 * get the time-button from the view and insert the current time
	 * @param view
	 * 			is the total view of the fragment
	 */
	private void insertCurrentTimeIntoButton(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimePicker.timeFormat, Locale.ITALY);
        String currentTime = simpleDateFormat.format(new Date());
        
        buttonTime.setText(currentTime);
	}
	
	/**
	 * adds an OnClickListener to the date-button with the date,
	 * so that it can open up a DatePicker when the user clicks
	 * @param view
	 * 			is the total view of the fragment
	 */
	private void addOnclickListenerForTime(){
		buttonTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				openTimePickerDialog(b);
			}
		});
	}
	
	/**
	 * actually opens the TimePicker
	 * @param view
	 * 			is the total view of the fragment
	 */
	public void openTimePickerDialog(Button button){
		String timeButtonText = button.getText().toString();
		
		TimePicker timePicker = new TimePicker();
		timePicker.setTimeAlreadySetString(timeButtonText);
		timePicker.setCallback(new TimeHasBeenSetListener() {
			@Override
			public void timeHasBeenSet(String time) {
				buttonTime.setText(time);
				Log.i("testtime",time);
			}
		});
		timePicker.show(getSherlockActivity().getSupportFragmentManager(), "Time Picker");
		
	}
	
	public interface TimeHasBeenSetListener {
		void timeHasBeenSet(String time);
	}
	
	
	//Search Button
	private void addOnclickListenerForSearchButton() {
		buttonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//Get the contents of the views
				String departurePredefined = (String) autocompletetextviewDeparture.getHint();
				String departure = autocompletetextviewDeparture.getText().toString();
				String arrival = autoCompleteTextViewArrival.getText().toString();
				String date = buttonDate.getText().toString();
				String time = buttonTime.getText().toString();
				
				//Check if the input fields are not empty
				if (!departure.trim().equals("") || !departurePredefined.trim().equals("") && !arrival.trim().equals("")){
					
					//check whether the user has inserted a departure bus stop,
					//or whether we should use the predefined one, which is 
					//the closest bus stop to the last known location
//					if(!departurePredefined.trim().equals("")){
//						departure = departurePredefined;
//					}
					
//					departure = "(" + departure.replace(" -", ")");
//					arrival = "(" + arrival.replace(" -", ")");
					
				}
				
				Intent intentSearchResults = new Intent(getSherlockActivity(), SearchResultsActivity.class);
				intentSearchResults.putExtra("departure", departure);
				intentSearchResults.putExtra("arrival", arrival);
				intentSearchResults.putExtra("date", date);
				intentSearchResults.putExtra("time", time);
				startActivity(intentSearchResults);
			}
		});
		
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MainActivity parentActivity = (MainActivity) getSherlockActivity();
		boolean drawerIsOpen = parentActivity.isDrawerOpen();
		//If the drawer is closed, show the menu related to the content
		if (!drawerIsOpen) {
			menu.clear();
			parentActivity.getSupportMenuInflater().inflate(R.menu.search_route_fragment, menu);
		}
		super.onPrepareOptionsMenu(menu);
	}
	
}