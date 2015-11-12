package it.sasabz.sasabus.beacon;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.busstop.BusStopBeaconHandler;
import it.sasabz.sasabus.beacon.survey.SurveyBeaconHandler;
import it.sasabz.sasabus.beacon.survey.action.NotificationAction;

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
		mBeaconObserver = new BeaconObserver(application, new SurveyBeaconHandler(application, new NotificationAction(application)),
				new BusStopBeaconHandler(application));
		mBeaconObserver.startListening();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mBeaconObserver.stopListening();
		sendBroadcast(new Intent(this, BluetoothStateChangeReceiver.class));
	}

}
