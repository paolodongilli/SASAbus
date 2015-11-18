package it.sasabz.sasabus.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.busstop.BusStopBeaconHandler;
import it.sasabz.sasabus.beacon.survey.action.NotificationAction;
import it.sasabz.sasabus.bus.trip.TripNotificationAction;

public class BeaconScannerService extends Service {

	private BeaconObserver mBeaconObserver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SasaApplication application = (SasaApplication) getApplication();
		mBeaconObserver = new BeaconObserver(application, new BusBeaconHandler(application, new NotificationAction(application), new TripNotificationAction(application)),
				new BusStopBeaconHandler(application));
		mBeaconObserver.startListening();
	}
	
	@Override
	public void onDestroy(){
		mBeaconObserver.stopListening();
		super.onDestroy();
		sendBroadcast(new Intent(this, BluetoothStateChangeReceiver.class));
	}

}
