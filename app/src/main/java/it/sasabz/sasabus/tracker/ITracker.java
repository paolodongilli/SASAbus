package it.sasabz.sasabus.tracker;

public interface ITracker {

    /**
     * Tracks the given ScreenName on Google Analytics
     */
	public void track(String screenName);
}
