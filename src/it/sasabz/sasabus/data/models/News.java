/**
 *
 * Information.java
 * 
 * 
 * Copyright (C) 2012 Markus Windegger
 *
 * This file is part of SasaBus.

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


import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * News object
 */
public class News extends DBObject{
	
	/** Title of the news in German */
	private String title_de;
	
	/** Title of the news in Italian */
	private String title_it;
	
	/** Message of the news in German */
	private String message_de;
	
	/** Message of the news in Italian */
	private String message_it;
	
	/** City affected by news (number) */
	private int city;
	
	/** Array of lines (numbers) which are affected by this news */
	private int[] linesAffected; 
	
	
	public News(int id, String title_de, String title_it, String message_de, String message_it, int city, int[] linesAffected) {
		super(id);
		this.title_de = title_de;
		this.title_it = title_it;
		this.message_de = message_de;
		this.message_it = message_it;
		this.city = city;
		this.linesAffected = linesAffected;
	}
	

	public String getTitle_de() {
		return title_de;
	}
	public void setTitle_de(String title_de) {
		this.title_de = title_de;
	}

	
	public String getTitle_it() {
		return title_it;
	}
	public void setTitle_it(String title_it) {
		this.title_it = title_it;
	}


	public String getMessage_de() {
		return message_de;
	}
	public void setMessage_de(String message_de) {
		this.message_de = message_de;
	}

	
	public String getMessage_it() {
		return message_it;
	}
	public void setMessage_it(String message_it) {
		this.message_it = message_it;
	}

	
	public int getCity() {
		return city;
	}
	public void setCity(int city) {
		this.city = city;
	}

	
	public int[] getLinesAffected() {
		return linesAffected;
	}
	public String getLinesAffectedAsString() {
		String linesAffectedText = "";
		for (int i = 0; i < linesAffected.length; i++){
			linesAffectedText += linesAffected[i];
			if (i < linesAffected.length -1) {
				linesAffectedText += ", ";
			}
		}
		return linesAffectedText;
	}
	public void setLinesAffected(int[] linesAffected) {
		this.linesAffected = linesAffected;
	}

	
	public String getMessage() {
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			return this.getMessage_de().trim();
		}
		return this.getMessage_it().trim();
	}
	
	public String getTitle() {
		if((Locale.getDefault().getLanguage()).indexOf(Locale.GERMAN.toString()) != -1) {
			return this.getTitle_de().trim();
		}
		return this.getTitle_it().trim();
	}
	
	@Override
	public String toString()
	{
		try {
			return (new String(this.getTitle().getBytes(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}