/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyBroadcastReceiver.java
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
package it.sasabz.sasabus.beacon.survey;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import it.sasabz.sasabus.gson.survey.service.SurveyApiService;

public class SurveyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		int busId = 0;
		int frtId = 0;
		long tripDuration = 0;
		int startBusstopId = 0;
		int stopBusstopId = 0;
		if (extras != null) {
			busId = extras.getInt("busId");
			frtId = extras.getInt("frtId");
			tripDuration = extras.getLong("tripDuration");
			startBusstopId = extras.getInt("startBusstopId");
			stopBusstopId = extras.getInt("stopBusstopId");
		}
		SurveyApiService.getInstance(context).commitSurvey("y", busId, frtId, tripDuration, startBusstopId, stopBusstopId);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(0);
	}

}
