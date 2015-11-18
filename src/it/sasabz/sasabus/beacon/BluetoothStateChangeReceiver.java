package it.sasabz.sasabus.beacon;

import org.altbeacon.beacon.BeaconManager;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (BeaconManager.getInstanceForApplication(context).checkAvailability()) {
				context.startService(new Intent(context, BeaconScannerService.class));
			} else {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!mBluetoothAdapter.isEnabled()) {
					NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel(1);
					context.stopService(new Intent(context, BeaconScannerService.class));
					SharedPreferenceManager mSharedPreferenceManager = new SharedPreferenceManager(context);
					mSharedPreferenceManager.setCurrentTrip(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
