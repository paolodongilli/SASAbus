package com.google.android.gms.analytics;

import android.app.Application;

public class GoogleAnalytics extends TrackerHandler {
    private static GoogleAnalytics Bs;
    private static Tracker Tr;

    public static GoogleAnalytics getInstance(Application app) {
        GoogleAnalytics googleAnalytics;
	synchronized (GoogleAnalytics.class) {
            googleAnalytics = Bs;
        }
        return googleAnalytics;
    }

    public static Tracker newTracker(String id) {
        Tracker t;
	synchronized (Tracker.class) {
            t = Tr;
        }
	return t.getInstance();
    }

}
