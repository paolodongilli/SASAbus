package it.sasabz.sasabus.beacon.bus.trip;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nocker on 04.01.16.
 */
public class TripBusStop implements Serializable {
    public enum TripBusStopType{
        BEACON, GPS, REALTIME_API
    }
    TripBusStopType tripBusStopType;
    int busStopId;
    long time;

    public TripBusStop(TripBusStopType tripBusStopType, int busStopId){
        this.tripBusStopType = tripBusStopType;
        this.busStopId = busStopId;
        this.time = System.currentTimeMillis();
    }

    public TripBusStopType getTripBusStopType() {
        return tripBusStopType;
    }

    public void setTripBusStopType(TripBusStopType tripBusStopType) {
        this.tripBusStopType = tripBusStopType;
    }

    public int getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(int busStopId) {
        this.busStopId = busStopId;
    }

    public long getSecondsAgo(){
        return (System.currentTimeMillis() - time) / 1000;
    }

    public String toString(){
        return "{" + tripBusStopType + ": " + busStopId + " (" + new SimpleDateFormat("HH:mm:ss").format(new Date(time)) + ")}";
    }
}
