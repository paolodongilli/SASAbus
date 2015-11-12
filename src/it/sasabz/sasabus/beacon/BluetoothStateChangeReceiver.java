package it.sasabz.sasabus.beacon;

import org.altbeacon.beacon.BeaconManager;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import it.sasabz.sasabus.beacon.busstop.BusStopBeaconHandler;
import it.sasabz.sasabus.beacon.survey.SurveyBeaconHandler;
import it.sasabz.sasabus.beacon.survey.action.NotificationAction;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (BeaconManager.getInstanceForApplication(context).checkAvailability()) {
				context.startService(new Intent(context, BeaconScannerService.class));
			} else {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!mBluetoothAdapter.isEnabled()) {
					context.stopService(new Intent(context, BeaconScannerService.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
