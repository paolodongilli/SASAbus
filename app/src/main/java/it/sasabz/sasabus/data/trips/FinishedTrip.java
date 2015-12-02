package it.sasabz.sasabus.data.trips;

import java.util.Date;

/**
 * Created by nocker on 01.12.15.
 */
public class FinishedTrip {
    private int startOrt;
    private int finishOrt;
    private int lineId;
    private int tripId;
    private int tagesart;
    private Date startTime;
    private Date finishTime;

    public FinishedTrip(int startOrt, int finishOrt, int lineId, int tripId, int tagesart, Date startTime, Date finishTime) {
        this.startOrt = startOrt;
        this.finishOrt = finishOrt;
        this.lineId = lineId;
        this.tripId = tripId;
        this.tagesart = tagesart;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public int getStartOrt() {
        return startOrt;
    }

    public void setStartOrt(int startOrt) {
        this.startOrt = startOrt;
    }

    public int getFinishOrt() {
        return finishOrt;
    }

    public void setFinishOrt(int finishOrt) {
        this.finishOrt = finishOrt;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTagesart() {
        return tagesart;
    }

    public void setTagesart(int tagesart) {
        this.tagesart = tagesart;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String toString(){
        return startOrt + ";" + finishOrt + ";" + lineId + ";" + tripId + ";" + tagesart + ";"
                +startTime + ";"+ finishTime;
    }
}
