/**
 *
 * Itinerary.java
 * 
 * Created: 14.12.2011 16:34:43
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
 *
 * This file is part of SasaBus.
 *
 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.sasabus.data.models;

import android.database.Cursor;
import android.text.format.Time;


/**
 * Itinerary (passaggio) object
 */
public class Itinerary extends DBObject {
	
	/** the id of the bus stop */
	private int busStopId = 0;
	
	/** name of the busstop */
	private String busStopName = "";
	
	/**	the code of the course */
	private int courseCode = 0;
	
	/** progressive course-related number */
	private int progressive = 0;
	
	/** time when the bus passes by */
	private Time time = null;
	
	
	/**
	 * Creates a new {@link Itinerary}
	 */
	public Itinerary() {
		
	}
	
	
	/**
	 * Creates a new {@link Itinerary} object with time as {@link Time} object
	 * @param id is the identifier in the database
	 * @param busStopId is the id-code of the bus stop
	 * @param courseCode is the id-code of the course
	 * @param progressive is the progressive number which are course-related (1-2-3-4....etc)
	 * @param time is the time when the bus starts from this bus stop
	 */
	public Itinerary(int id, int busStopId, int courseCode, int progressive, Time time) {
		super(id);
		this.setBusStopId(busStopId);
		this.setCourseCode(courseCode);
		this.setProgressive(progressive);
		this.setTime(time);
	}
	
	
	/**
	 * Creates a new {@link Itinerary} object with time as {@link String} object
	 * @param id is the identifier in the database
	 * @param busStopId is the id-code of the bus stop
	 * @param courseCode is the id-code of the course
	 * @param progressive is the progressive number which are course-related (1-2-3-4....etc)
	 * @param time is the time when the bus starts from this bus stop
	 */
	public Itinerary(int id, int busStopId, int courseCode, int progressive, String time) {
		super(id);
		this.setBusStopId(busStopId);
		this.setCourseCode(courseCode);
		this.setProgressive(progressive);
		this.setTime(time);
	}
	
	
	/**
	 * Creates a new {@link Itinerary} object from the {@link Cursor} on an object in the database
	 * @param cursor is the cursor to an object of the database
	 */
	public Itinerary(Cursor cursor) {
		super(cursor.getInt(cursor.getColumnIndex("id")));
		this.setBusStopId(cursor.getInt(cursor.getColumnIndex("palinaId")));
		this.setCourseCode(cursor.getInt(cursor.getColumnIndex("corsaId")));
		this.setProgressive(cursor.getInt(cursor.getColumnIndex("progressivo")));
		this.setTime(cursor.getString(cursor.getColumnIndex("orario")));
	}


	/**
	 * @return the id of the bus stop (palina)
	 */
	public int getBusStopId() {
		return busStopId;
	}

	/**
	 * @param busStopId is the id of a bus stop (palina)
	 */
	public void setBusStopId(int busStopId) {
		this.busStopId = busStopId;
	}
	
	/**
	 * @return the name of the bus stop (palina)
	 */
	public String getBusStopName() {
		return busStopName;
	}

	/**
	 * @param busStopName is the id of a bus stop (palina)
	 */
	public void setBusStopName(String busStopName) {
		this.busStopName = busStopName;
	}

	/**
	 * @return the code of the course
	 */
	public int getCourseCode() {
		return courseCode;
	}

	/**
	 * @param courseCode is the code of the course
	 */
	public void setCourseCode(int courseCode) {
		this.courseCode = courseCode;
	}

	/**
	 * @return the progressive course-related number
	 */
	public int getProgressive() {
		return progressive;
	}

	/**
	 * @param progressive is the progressive course-related number
	 */
	public void setProgressive(int progressive) {
		this.progressive = progressive;
	}

	/**
	 * @return the time when the bus passes by
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * @param time is the time when the bus passes by
	 */
	public void setTime(Time time) {
		this.time = time;
	}
	
	/**
	 * @param time the time when the bus passes by
	 */
	public void setTime(String time) {
		this.time = new Time();
		String [] split = time.split(":");
		this.time.setToNow();
		this.time.minute = Integer.parseInt(split[1]);
		this.time.hour = Integer.parseInt(split[0]);
		this.time.second = 0;
	}
	
	/**
	 * Compares an Itinerary to another
	 */
	@Override
	public boolean equals(Object object) {
		if(object instanceof Itinerary) {
			return false;
		}
		Itinerary pas = (Itinerary)object;
		if(pas.getBusStopId() != this.getBusStopId() || pas.getCourseCode() != this.getCourseCode() || pas.getTime().equals(this.getTime())) {
			return false;
		}
		return true;
	}
	
	/**
	 * Handles the string output
	 */
	@Override
	public String toString() {
		return (this.getTime().format("%H:%M")).trim();
	}
	
}