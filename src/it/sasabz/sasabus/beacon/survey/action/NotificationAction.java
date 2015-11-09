/*
 * SASAbus - Android app for SASA bus open data
 *
 * NotificationAction.java
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
package it.sasabz.sasabus.beacon.survey.action;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.survey.ISurveyAction;
import it.sasabz.sasabus.beacon.survey.SurveyBeaconInfo;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.survey.model.SurveyDefinitionResult;
import it.sasabz.sasabus.gson.survey.model.SurveyDefinitionResult.Data;
import it.sasabz.sasabus.gson.survey.model.SurveyResult;
import it.sasabz.sasabus.gson.survey.service.SurveyApiService;
import it.sasabz.sasabus.ui.AbstractSasaActivity;
import it.sasabz.sasabus.ui.survey.SurveyActivity;
import it.sasabz.sasabus.ui.survey.SurveyContactActivity;

public class NotificationAction implements ISurveyAction {

	private SasaApplication mSasaApplication;

	public NotificationAction(SasaApplication sasaApplication) {
		this.mSasaApplication = sasaApplication;
	}

	@Override
	public void triggerSurvey(final SurveyBeaconInfo beaconInfo) {
		SurveyApiService.getInstance(mSasaApplication).getSurveyDefinition(new IApiCallback<SurveyDefinitionResult>() {

			@Override
			public void onFailure(Exception e) {
				Log.d(SasaApplication.TAG, "Failed to get survey definition: " + e.getMessage());
			}

			@Override
			public void onSuccess(SurveyDefinitionResult result) {
				if (result.getStatus().equals(SurveyResult.STATUS_SUCCESS)) {
					if (result.hasData() == true) {
						Data data = result.getFirstData();
						Context context = mSasaApplication.getApplicationContext();
						String firstQuestion = data.getFirstQuestionForDeviceLocale(context);
						String firstQuestionPlaceHolder = data.getFirstQuestionPlaceholderForDeviceLocale(context);
						String infoText = data.getSecondQuestionForDeviceLocale(context);

						if (firstQuestionPlaceHolder != null && !firstQuestionPlaceHolder.equals("")) {
							if (beaconInfo.getLineName() != null && !beaconInfo.getLineName().equals("")) {
								firstQuestion = String.format(firstQuestionPlaceHolder, beaconInfo.getLineName());
							}
						}

						AbstractSasaActivity activity = mSasaApplication.getActivity();

						if (activity != null && activity.isInForeground()) {
							showSurveyAlert(activity, firstQuestion, infoText, beaconInfo);
						} else {
							showNotification(firstQuestion, infoText, beaconInfo);
						}
						mSasaApplication.getSharedPreferenceManager().setSurveyLastOccurence(new Date());
					}
				}
			}
		});
	}

	private Intent getSurveyIntent(SurveyBeaconInfo beaconInfo, String question, String infoText, Class<?> className,
			boolean answer) {
		Intent surveyNoIntent = new Intent(mSasaApplication, className);
		surveyNoIntent.putExtra("infoText", infoText);
		surveyNoIntent.putExtra("questionText", question);
		surveyNoIntent.putExtra("answer", answer);
		surveyNoIntent.putExtra("frtId", beaconInfo.getTripId());
		surveyNoIntent.putExtra("busId", beaconInfo.getMajor());
		surveyNoIntent.putExtra("tripDuration", beaconInfo.getSeenSeconds());
		surveyNoIntent.putExtra("startBusstopId", beaconInfo.getStartBusstationId());
		surveyNoIntent.putExtra("stopBusstopId", beaconInfo.getStopBusstationId());
		return surveyNoIntent;
	}

	private void showSurveyAlert(final AbstractSasaActivity activity, final String firstQuestion, final String infoText,
			final SurveyBeaconInfo beaconInfo) {
		try {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.survey_title))
							.setMessage(firstQuestion).setPositiveButton(activity.getString(R.string.survey_answer_yes),
									new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SurveyApiService.getInstance(activity).commitSurvey("y", beaconInfo.getMajor(),
									beaconInfo.getTripId(), beaconInfo.getSeenSeconds(), beaconInfo.getStartBusstationId(), beaconInfo.getStopBusstationId());
							dialog.dismiss();
						}
					}).setNegativeButton(activity.getString(R.string.survey_answer_no),
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							activity.startActivity(getSurveyIntent(beaconInfo, firstQuestion, infoText,
									SurveyContactActivity.class, false));
							dialog.dismiss();
						}
					}).setIcon(android.R.drawable.ic_dialog_info).setCancelable(false).show();
				}
			});
		} catch (Exception e) {
		}
	}

	private void showNotification(String firstQuestion, String infoText, SurveyBeaconInfo beaconInfo) {

		int now = (int) Calendar.getInstance().getTimeInMillis();
		PendingIntent pIntentNo = PendingIntent.getActivity(mSasaApplication, now,
				this.getSurveyIntent(beaconInfo, firstQuestion, infoText, SurveyContactActivity.class, false), 0);

		PendingIntent pIntentNeutral = PendingIntent.getActivity(mSasaApplication, (int) now,
				this.getSurveyIntent(beaconInfo, firstQuestion, infoText, SurveyActivity.class, true), 0);

		Intent intent = new Intent();
		intent.setAction("it.sasabz.sasabus.beacon.surveybroadcast");
		intent.putExtra("frtId", beaconInfo.getTripId());
		intent.putExtra("busId", beaconInfo.getMajor());
		intent.putExtra("tripDuration", beaconInfo.getSeenSeconds());
		intent.putExtra("startBusstopId", beaconInfo.getStartBusstationId());
		intent.putExtra("stopBusstopId", beaconInfo.getStopBusstationId());
		PendingIntent yesPendingIntent = PendingIntent.getBroadcast(mSasaApplication, 0, intent, Intent.FILL_IN_DATA);

		Notification notification = new NotificationCompat.Builder(mSasaApplication)
				.setContentTitle(mSasaApplication.getString(R.string.survey_title)).setContentText(firstQuestion)
				.setSmallIcon(R.drawable.icon).setContentIntent(pIntentNeutral).setAutoCancel(true)
				.addAction(R.drawable.ic_no, mSasaApplication.getString(R.string.survey_answer_no), pIntentNo)
				.addAction(R.drawable.ic_yes, mSasaApplication.getString(R.string.survey_answer_yes), yesPendingIntent)
				.build();

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.ledARGB = Color.argb(255, 255, 166, 0);
		notification.ledOnMS = 200;
		notification.ledOffMS = 200;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		NotificationManager notificationManager = (NotificationManager) mSasaApplication
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(0, notification);

	}

}
