/*
 * SASAbus - Android app for SASA bus open data
 *
 * GoogleTracker.java
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
package it.sasabz.sasabus.tracker.googleanalytics;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.tracker.ITracker;

public class GoogleTracker implements ITracker {

	private Tracker mTracker;
	private SasaApplication mApplication;
	
	public GoogleTracker(SasaApplication application) {
		this.mApplication = application;
		this.getDefaultTracker();
	}
	
	@Override
	public void track(String screenName) {
    	this.mTracker.setScreenName(screenName);
        this.mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
    
    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * 
     * @return mTracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (this.mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
            this.mTracker = analytics.newTracker(mApplication.getConfigManager().getValue("google_tracking_id", ""));
            //this.mTracker.set("&uid", mApplication.getAndroidId());
            //this.mTracker.send(new HitBuilders.EventBuilder().setCategory("default").setAction("click").build());
        }
        return this.mTracker;
    }

}
