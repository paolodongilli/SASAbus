package it.sasabz.sasabus.logic;

import it.sasabz.sasabus.data.models.Area;
import it.sasabz.sasabus.data.models.BusLine;
import it.sasabz.sasabus.data.models.BusStop;
import it.sasabz.sasabus.data.models.Itinerary;

import java.util.ArrayList;
import java.util.List;

public class BusSchedulesDatabase {

	
	/**
	 * 
	 * @return all {@link Area}s available in the database
	 */
	public static List<Area> getAllAreas() {
		
		//TODO fetch data from local database about all areas available
		//for Spinner in bus schedules tab
				
		List<Area> fakeList = new ArrayList<Area>();
			fakeList.add(new Area(0, "Bolzano", "Bozen"));;
			fakeList.add(new Area(0, "Merano", "Meran"));
			fakeList.add(new Area(0, "Extraurbano", "‹berland"));
		
		return fakeList;
		
	}
	
	/**
	 * Fetch all bus lines available in the database
	 * for a specific area
	 * @param area where to search for the various bus lines
	 * @return {@link List} of bus lines
	 */
	public static List<BusLine> getAllBusLinesForArea(Area area) {
		
		//TODO fetch data from local database about all bus stops available
		//for Spinner in bus schedules tab
		
		List<BusLine> fakeList = new ArrayList<BusLine>();
			fakeList.add(new BusLine("Linie 1", null, null, null, 0));
			fakeList.add(new BusLine("Linie 2", null, null, null, 0));
			fakeList.add(new BusLine("Linie 3", null, null, null, 0));
			fakeList.add(new BusLine("Linie 4", null, null, null, 0));
			fakeList.add(new BusLine("Linie 5", null, null, null, 0));
			fakeList.add(new BusLine("Linie 6", null, null, null, 0));
			fakeList.add(new BusLine("Linie 7", null, null, null, 0));
			fakeList.add(new BusLine("Linie 7A", null, null, null, 0));
			fakeList.add(new BusLine("Linie 7B", null, null, null, 0));
			fakeList.add(new BusLine("Linie 8", null, null, null, 0));
			
		return fakeList;
	}
	
	
	/**
	 * Fetch the starting and the ending bus stop of a specific busline
	 * @param area where to search for the busline
	 * @param busline the busline to search for
	 * @return a List of directions
	 */
	public static List<String> getStartAndEndBusStopForBusline(Area area, BusLine busline) {
		
		//TODO fetch data from local database about the first about 
		//the starting and ending bus stop of a specific line
		
		List<String> fakeList = new ArrayList<String>();
			fakeList.add("Fagenstraﬂe - Kardaun");
			fakeList.add("Kardaun - Fagenstraﬂe");
		
		return fakeList;
	}
	
	public static List<Itinerary> getItineraryForLine(Area area, BusLine busLine, String direction) {
		
		//TODO fetch data from database
		
		List<Itinerary> fakeList = new ArrayList<Itinerary>();
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:00"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:01"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:02"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:03"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:04"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:05"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:06"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:07"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:08"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:09"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "08:30"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "08:31"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "08:32"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "08:33"));
				
		return fakeList;
	}
	
	
	public static List<Itinerary> getDepartureTimesForBusstop(Area area, BusLine busline, BusStop busstop) {
		
		//TODO fetch data from database
		
		List<Itinerary> fakeList = new ArrayList<Itinerary>();
			fakeList.add(new Itinerary(0, 1234, 0, 0, "08:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "08:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "09:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "09:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "10:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "10:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "11:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "11:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "12:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "12:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "13:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "13:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "14:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "14:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "15:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "15:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "16:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "16:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "17:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "17:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "18:00"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "18:30"));
			fakeList.add(new Itinerary(0, 1234, 0, 0, "19:00"));
			
		return fakeList;
		
	}
	
	public static List<Itinerary> getItineraryForCourse(Area area, BusLine busLine, Itinerary itineraryStart) {
		
		//TODO fetch data from database
		
		List<Itinerary> fakeList = new ArrayList<Itinerary>();
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:00"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:01"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:02"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:03"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:04"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:05"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:06"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:07"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:08"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:09"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:10"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:11"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:12"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "09:13"));
				
		return fakeList;
	}
	
	
	public static List<Itinerary> getNextBusesItineraryForBusstop(BusStop busstop) {
		
		//TODO fetch data from database
		
		List<Itinerary> fakeList = new ArrayList<Itinerary>();
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "00:03"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "00:04"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "00:06"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "00:08"));
			fakeList.add(new Itinerary(0, 1234567891, 0, 0, "00:11"));
				
		return fakeList;
		
	}
	
}
