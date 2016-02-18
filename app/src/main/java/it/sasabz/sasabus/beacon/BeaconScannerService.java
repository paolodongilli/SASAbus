package it.sasabz.sasabus.beacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.busstop.BusStopBeaconHandler;
import it.sasabz.sasabus.beacon.survey.action.NotificationAction;
import it.sasabz.sasabus.beacon.bus.trip.TripNotificationAction;
import it.sasabz.sasabus.ui.MainActivity;

public class BeaconScannerService extends Service {

	private BeaconObserver mBeaconObserver;
	public static boolean isAlive = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isAlive = true;
		SasaApplication application = (SasaApplication) getApplication();
		mBeaconObserver = new BeaconObserver(application, new BusBeaconHandler(application, new NotificationAction(application), new TripNotificationAction(application)),
				new BusStopBeaconHandler(application));
		mBeaconObserver.startListening();
	}
	
	@Override
	public void onDestroy(){
		isAlive = false;
		mBeaconObserver.stopListening();
		super.onDestroy();
		sendBroadcast(new Intent(this, BluetoothStateChangeReceiver.class));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
}
