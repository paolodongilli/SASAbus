package it.sasabz.sasabus.ui;

import android.location.Location;

public class ParkingData {
	String name;
	int free;
	int tot;
	Location location = new Location("null");
	String phonenumber;
	String adress;
	String description;

	public void setLongitude(double longitude) {
		location.setLongitude((double) Math.round(longitude * 1000000) / 1000000);
	}

	public void setLatitude(double latitude) {
		location.setLatitude((double) Math.round(latitude * 1000000) / 1000000);
	}
}
