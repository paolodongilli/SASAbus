package it.sasabz.sasabus.logic;

import it.sasabz.sasabus.ui.routing.SearchResultsActivity.SearchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

public class SearchConnection {

	
	public void searchForConnection(String departure, String arrival, String date, String time,
			final Activity activity, final SearchCallback callback) {
		
		//TODO make a new request and generate necessary object, 
		//when everything downloaded and splitted...
		
		//when finished return results
		
		
		Thread thread = new Thread(){
			@Override
			public void run() {
				try {
					synchronized(this) {
						wait(2000);
					}
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							callback.searchIsFinished(null);
						}
					});
				}
				
			}
		};
		thread.start();
	}
	
	public List<String> getConncections() {
		
		
		//TODO fetch the list of connections found, after they 
		//have been downloaded
		List<String> fakeList = new ArrayList<String>();
			fakeList.add("Test");
			fakeList.add("Test");
			fakeList.add("Test");
			fakeList.add("Test");
		
		return fakeList;
	}
	
	public Map<Integer, List<String>> getConnectionDetails() {
		
		//TODO fetch list of the details of the connections found
		
		
		Map<Integer, List<String>> fakeList = new LinkedHashMap<Integer, List<String>>();
			fakeList.put(0, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(0, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(0, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(1, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(1, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(2, new LinkedList<String>(Arrays.asList("Test", "Test")));
			fakeList.put(3, new LinkedList<String>(Arrays.asList("Test", "Test")));
	
		return fakeList;
	}
	
}