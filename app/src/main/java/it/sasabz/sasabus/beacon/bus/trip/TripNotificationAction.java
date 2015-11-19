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
package it.sasabz.sasabus.beacon.bus.trip;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.logic.DeparturesThread;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.opendata.client.model.BusTripBusStopTime;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class TripNotificationAction {

	private SasaApplication mSasaApplication;
	private static final int[] BIG_VIEW_ROW_IDS = { R.id.notification_busstop_row0, R.id.notification_busstop_row1,
			R.id.notification_busstop_row2, R.id.notification_busstop_row3, R.id.notification_busstop_row4,
			R.id.notification_busstop_row5, R.id.notification_busstop_row6,R.id.notification_busstop_row7 };
	private static final int[] BIG_VIEW_ROUTE_IMAGE_IDS = { R.id.image_route0, R.id.image_route1, R.id.image_route2,
			R.id.image_route3, R.id.image_route4, R.id.image_route5, R.id.image_route6, R.id.image_route7 };
	private static final int[] BIG_VIEW_TIME_TEXT_IDS = { R.id.txt_time0, R.id.txt_time1, R.id.txt_time2,
			R.id.txt_time3, R.id.txt_time4, R.id.txt_time5, R.id.txt_time6, R.id.txt_time7 };
	private static final int[] BIG_VIEW_BUS_STOP_TEXT_IDS = { R.id.txt_busstopname0, R.id.txt_busstopname1,
			R.id.txt_busstopname2, R.id.txt_busstopname3, R.id.txt_busstopname4, R.id.txt_busstopname5,
			R.id.txt_busstopname6, R.id.txt_busstopname7 };

	public TripNotificationAction(SasaApplication sasaApplication) {
		this.mSasaApplication = sasaApplication;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void showNotification() {
	
		try{
		CurentTrip curentTrip = mSasaApplication.getSharedPreferenceManager().getCurrentTrip();
		
		Intent intent = new Intent(mSasaApplication, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(mSasaApplication, 0, intent, Intent.FILL_IN_DATA);

		Notification notification = new NotificationCompat.Builder(mSasaApplication)
				.setContent(getBaseNotificationView(curentTrip)).setSmallIcon(R.drawable.ic_notification).setContentIntent(pendingIntent)
				.setOngoing(true).build();
		NotificationManager notificationManager = (NotificationManager) mSasaApplication
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notification.bigContentView = getBigNotificationView(curentTrip);

		notificationManager.notify(2, notification);

		mSasaApplication.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private RemoteViews getBaseNotificationView(CurentTrip curentTrip) {
		BusDepartureItem busDepartureItem = curentTrip.getBusDepartureItem();
		RemoteViews remoteViews = new RemoteViews(mSasaApplication.getPackageName(),
				R.layout.notification_curent_trip_base);
		try {
			remoteViews.setTextViewText(R.id.txt_line_name, busDepartureItem.getBusStopOrLineName().split(" ")[0]);
			DisplayMetrics displayMetrics = mSasaApplication.getResources().getDisplayMetrics();
			GradientDrawable circularImage = (GradientDrawable) mSasaApplication.getResources()
					.getDrawable(R.drawable.circle_image);
			circularImage.setStroke(Math.round(4 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),
					curentTrip.getColor());

			int size = Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
			Bitmap circularBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
			Canvas canvas = new Canvas(circularBitmap);
			circularImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			circularImage.draw(canvas);
			remoteViews.setImageViewBitmap(R.id.image_notification_line, circularBitmap);
			remoteViews.setTextColor(R.id.txt_line_name, curentTrip.getColor());
			remoteViews.setTextViewText(R.id.txt_delay, busDepartureItem.getDelay());
			int delay = busDepartureItem.getDelayNumber();
			if (delay == 0) {
				remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.in_time));
			} else if (delay < 0) {
				if (delay < -2) {
					remoteViews.setTextColor(R.id.txt_delay, Color.CYAN);
				} else {
					remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				}
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.advance));
			} else {
				if (delay < 2) {
					remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				} else if (delay < 4) {
					remoteViews.setTextColor(R.id.txt_delay,
							mSasaApplication.getResources().getColor(R.color.sasa_orange));
				} else {
					remoteViews.setTextColor(R.id.txt_delay, Color.RED);
				}
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.delay));

			}

			int index = busDepartureItem.getDeparture_index();
			if (index == 9999) {
				index = busDepartureItem.getStopTimes().length - 1;
			}
			BusTripBusStopTime element = busDepartureItem.getStopTimes()[index];

			remoteViews.setTextViewText(R.id.txt_time,
					DeparturesThread.formatSeconds(element.getSeconds() + busDepartureItem.getDelayNumber() * 60));

			String busStationName = "";
			try {
				busStationName = getBusStationNameUsingAppLanguage(mSasaApplication.getOpenDataStorage()
						.getBusStations().findBusStop(element.getBusStop()).getBusStation());
			} catch (Exception exxooo) {
				System.out.println("Do nothing");
			}
			remoteViews.setTextViewText(R.id.txt_busstopname, busStationName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remoteViews;
	}

	private RemoteViews getBigNotificationView(CurentTrip curentTrip) {
		BusDepartureItem busDepartureItem = curentTrip.getBusDepartureItem();
		RemoteViews remoteViews = new RemoteViews(mSasaApplication.getPackageName(),
				R.layout.notification_curent_trip_big);
		try {
			remoteViews.setTextViewText(R.id.txt_line_name, busDepartureItem.getBusStopOrLineName().split(" ")[0]);
			DisplayMetrics displayMetrics = mSasaApplication.getResources().getDisplayMetrics();
			GradientDrawable circularImage = (GradientDrawable) mSasaApplication.getResources()
					.getDrawable(R.drawable.circle_image);
			circularImage.setStroke(Math.round(4 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),
					curentTrip.getColor());

			int size = Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
			Bitmap circularBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
			Canvas canvas = new Canvas(circularBitmap);
			circularImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			circularImage.draw(canvas);
			remoteViews.setImageViewBitmap(R.id.image_notification_line, circularBitmap);
			remoteViews.setTextColor(R.id.txt_line_name, curentTrip.getColor());
			remoteViews.setTextViewText(R.id.txt_delay, busDepartureItem.getDelay());
			int delay = busDepartureItem.getDelayNumber();
			if (delay == 0) {
				remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.in_time));
			} else if (delay < 0) {
				if (delay < -2) {
					remoteViews.setTextColor(R.id.txt_delay, Color.CYAN);
				} else {
					remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				}
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.advance));
			} else {
				if (delay < 2) {
					remoteViews.setTextColor(R.id.txt_delay, Color.GREEN);
				} else if (delay < 4) {
					remoteViews.setTextColor(R.id.txt_delay,
							mSasaApplication.getResources().getColor(R.color.sasa_orange));
				} else {
					remoteViews.setTextColor(R.id.txt_delay, Color.RED);
				}
				remoteViews.setTextViewText(R.id.txt_delay_text,
						mSasaApplication.getResources().getString(R.string.delay));

			}

			int departureIndex = busDepartureItem.getDelay_index();
			if (departureIndex == 9999) {
				departureIndex = busDepartureItem.getStopTimes().length - 1;
			}
			if (departureIndex > 0)
				departureIndex--;
			remoteViews.setViewVisibility(R.id.image_route_points, View.VISIBLE);
			for (int i = 0; i < BIG_VIEW_ROW_IDS.length - 1; i++)
				if (departureIndex + i < busDepartureItem.getStopTimes().length - 1) {
					remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[i], View.VISIBLE);

					if (i == BIG_VIEW_ROW_IDS.length - 2 && departureIndex + i == busDepartureItem.getStopTimes().length - 2)
						remoteViews.setViewVisibility(R.id.image_route_points, View.GONE);

					BusTripBusStopTime element = busDepartureItem.getStopTimes()[departureIndex + i];
					if (departureIndex + i == 0)
						remoteViews.setImageViewResource(BIG_VIEW_ROUTE_IMAGE_IDS[i], R.drawable.ab_punkt);
					else if (busDepartureItem.getDelay_index() == departureIndex + i) {
						remoteViews.setImageViewResource(BIG_VIEW_ROUTE_IMAGE_IDS[i], R.drawable.middle_bus);
					} else {
						if (busDepartureItem.getDelay_index() > departureIndex + i) {
							remoteViews.setTextColor(BIG_VIEW_BUS_STOP_TEXT_IDS[i], Color.GRAY);
							remoteViews.setTextColor(BIG_VIEW_TIME_TEXT_IDS[i], Color.GRAY);
						}
						remoteViews.setImageViewResource(BIG_VIEW_ROUTE_IMAGE_IDS[i], R.drawable.middle_punkt);
					}

					remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[i], DeparturesThread
							.formatSeconds(element.getSeconds() + busDepartureItem.getDelayNumber() * 60));

					String busStationName = "";
					try {
						busStationName = getBusStationNameUsingAppLanguage(mSasaApplication.getOpenDataStorage()
								.getBusStations().findBusStop(element.getBusStop()).getBusStation());
					} catch (Exception exxooo) {
						System.out.println("Do nothing");
					}
					remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[i], busStationName);
				} else {
					remoteViews.setViewVisibility(R.id.image_route_points, View.GONE);
					remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[i], View.GONE);
				}
			BusTripBusStopTime element = busDepartureItem.getStopTimes()[busDepartureItem.getStopTimes().length - 1];

			remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[BIG_VIEW_TIME_TEXT_IDS.length - 1], DeparturesThread
					.formatSeconds(element.getSeconds() + busDepartureItem.getDelayNumber() * 60));

			String busStationName = "";
			try {
				busStationName = getBusStationNameUsingAppLanguage(mSasaApplication.getOpenDataStorage()
						.getBusStations().findBusStop(element.getBusStop()).getBusStation());
			} catch (Exception exxooo) {
				System.out.println("Do nothing");
			}
			remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[BIG_VIEW_BUS_STOP_TEXT_IDS.length - 1], busStationName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return remoteViews;
	}
	
	

	public String getBusStationNameUsingAppLanguage(BusStation busStation) {
		if (mSasaApplication.getString(R.string.bus_station_name_language).equals("de"))
			return busStation.findName_de();
		return busStation.findName_it();
	}

}
