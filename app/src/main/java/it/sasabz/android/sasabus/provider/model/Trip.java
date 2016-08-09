package it.sasabz.android.sasabus.provider.model;

import it.sasabz.android.sasabus.provider.apis.HaltTimes;
import it.sasabz.android.sasabus.provider.apis.Intervals;
import it.sasabz.android.sasabus.provider.apis.Paths;
import it.sasabz.android.sasabus.provider.apis.StopTimes;

import java.util.List;

/**
 * Represents a trip identifiable by an unique ID, in JSON format described with the parameter
 * 'FRT_FID'. This parameter is only unique for one day. It might repeat on another day.
 *
 * @author David Dejori
 */
public class Trip {

    private final int line;
    private final int variant;
    private final int day;
    private final int time;
    private final int fgr;
    private final int trip;
    private int secondsAtUserStop;

    public Trip(int line, int variant, int day, int time, int fgr, int trip) {
        this.line = line;
        this.variant = variant;
        this.day = day;
        this.time = time;
        this.fgr = fgr;
        this.trip = trip;
    }

    public List<BusStop> getPath() {
        List<BusStop> path = Paths.getPath(line, variant);
        path.get(0).setSeconds(time);

        for (int i = 1; i < path.size(); i++) {
            path.get(i).setSeconds(path.get(i - 1).getSeconds() + Intervals.getInterval(fgr, path.get(i - 1).getId(),
                    path.get(i).getId()) + StopTimes.getStopSeconds(fgr, path.get(i).getId() +
                    HaltTimes.getStopSeconds(trip, path.get(i).getId())));
        }
        return path;
    }

    public int getSecondsAtStation(int busStopId) {
        for (BusStop busStop : getPath()) {
            if (busStop.getId() == busStopId) {
                secondsAtUserStop = busStop.getSeconds();
                return secondsAtUserStop;
            }
        }
        return -1;
    }

    public int getLine() {
        return line;
    }

    public int getVariant() {
        return variant;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public int getTrip() {
        return trip;
    }

    public int getSecondsAtUserStop() {
        return secondsAtUserStop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trip trip1 = (Trip) o;

        return trip == trip1.trip;

    }

    @Override
    public int hashCode() {
        return trip;
    }
}
