/*
 * SASAbus - Android app for SASA bus open data
 *
 * DateSerializer.java
 *
 * Created: Sep 02, 2015 08:24:00 PM
 *
 * Copyright (C) 2011-2015 Raiffeisen Online GmbH (Norman Marmsoler, Jürgen Sprenger, Aaron Falk) <info@raiffeisen.it>
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.sasabz.sasabus.gson.serializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


public class DateSerializer implements JsonDeserializer<Date> {
	
	private static final String[] DATE_FORMATS = new String[] {
	        "yyyy-MM-dd HH:mm:ssZ",
	        "yyyy-MM-dd HH:mm:ss"
	};

	@Override
	public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context)
			throws JsonParseException {
		for (String format : DATE_FORMATS) {
			try {
				return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
			} catch (ParseException e) {
			}
		}
		throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString() + "\". Supported formats: "
				+ Arrays.toString(DATE_FORMATS));
	}
}