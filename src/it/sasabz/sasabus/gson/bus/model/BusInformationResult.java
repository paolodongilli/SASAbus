/*
 * SASAbus - Android app for SASA bus open data
 *
 * BusInformationResult.java
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
package it.sasabz.sasabus.gson.bus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class BusInformationResult extends AbstractBusResult {

	@SerializedName("features")
	public ArrayList<Feature> features;

	public BusInformationResult() {

	}

	/**
	 * Retrieves the features
	 * 
	 * @return
	 */
	public ArrayList<Feature> getFeatures() {
		return this.features;
	}

	/**
	 * Checks if the bus information contains feature data
	 * 
	 * @return
	 */
	public boolean hasFeatures() {
		return this.features.size() > 0;
	}

	/**
	 * Gets the first feature
	 * @return
	 */
	public Feature getFirstFeature() {
		Feature feature = null;
		if (hasFeatures() == true) {
			Collections.sort(this.features, new Comparator<Feature>() {
				@Override
				public int compare(Feature feature0, Feature feature1) {
					return feature0.getProperties().getGpsDate().compareTo(feature1.getProperties().getGpsDate());
				}
			});
			feature = this.features.get(0);
		}
		return feature;
	}

	public static class Feature {
		@SerializedName("type")
		public String type;
		@SerializedName("geometry")
		public Geometry geometry;
		@SerializedName("properties")
		public Properties properties;

		public String getType() {
			return this.type;
		}

		public Geometry getGeometry() {
			return this.geometry;
		}

		public Properties getProperties() {
			return this.properties;
		}

		public static class Geometry {

			@SerializedName("type")
			public String type;
			@SerializedName("coordinates")
			public ArrayList<Double> coordinates;

			public String getType() {
				return this.type;
			}

			public ArrayList<Double> getCoordinates() {
				return this.coordinates;
			}
		}

		public static class Properties {

			@SerializedName("frt_fid")
			public int frtFid;
			@SerializedName("gps_date")
			public Date gpsDate;
			@SerializedName("delay_sec")
			public int delay;
			@SerializedName("vehiclecode")
			public String vehicleCode;
			@SerializedName("li_nr")
			public int lineNumber;
			@SerializedName("str_li_var")
			public String lineVariant;
			@SerializedName("lidname")
			public String lineName;
			@SerializedName("insert_date")
			public Date insertDate;
			@SerializedName("li_r")
			public int liColorRed;
			@SerializedName("li_g")
			public int liColorGreen;
			@SerializedName("li_b")
			public int lineColorBlue;
			@SerializedName("ort_nr")
			public int nextStopNumber;
			@SerializedName("onr_typ_nr")
			public int nextStopTypeNumber;
			@SerializedName("ort_name")
			public String nextStopName;
			@SerializedName("ort_ref_ort_name")
			public String nextStopFullReferenceName;
			@SerializedName("hexcolor")
			public String lineColorHex;
			@SerializedName("hexcolor2")
			public String lineColorHexWithoutHash;

			public int getFrtFid() {
				return frtFid;
			}

			public Date getGpsDate() {
				return gpsDate;
			}

			public int getDelay() {
				return delay;
			}

			public String getVehicleCode() {
				return vehicleCode;
			}

			public int getLineNumber() {
				return lineNumber;
			}

			public String getLineVariant() {
				return lineVariant;
			}

			public String getLineName() {
				return lineName;
			}

			public Date getInsertDate() {
				return insertDate;
			}

			public int getLiColorRed() {
				return liColorRed;
			}

			public int getLiColorGreen() {
				return liColorGreen;
			}

			public int getLineColorBlue() {
				return lineColorBlue;
			}

			public int getNextStopNumber() {
				return nextStopNumber;
			}

			public int getNextStopTypeNumber() {
				return nextStopTypeNumber;
			}

			public String getNextStopName() {
				return nextStopName;
			}

			public String getNextStopFullReferenceName() {
				return nextStopFullReferenceName;
			}

			public String getLineColorHex() {
				return lineColorHex;
			}

			public String getLineColorHexWithoutHash() {
				return lineColorHexWithoutHash;
			}

		}
	}
}
