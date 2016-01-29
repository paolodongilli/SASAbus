package it.sasabz.sasabus.beacon;

import org.altbeacon.beacon.BeaconManager;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStop;
import it.sasabz.sasabus.data.trips.FinishedTrip;
import it.sasabz.sasabus.data.trips.TripsSQLiteOpenHelper;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class BluetoothStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (BeaconManager.getInstanceForApplication(context).checkAvailability()) {
				context.startService(new Intent(context, BeaconScannerService.class));
			} else {
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!mBluetoothAdapter.isEnabled()) {
					context.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
					try {
						SasaApplication mApplication = BusBeaconHandler.mTripNotificationAction.mSasaApplication;
						SharedPreferenceManager mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
						if (mApplication.getSharedPreferenceManager().getCurrentTrip() != null) {
							CurentTrip curentTrip = mApplication.getSharedPreferenceManager().getCurrentTrip();
							BusBeaconInfo beaconInfo = curentTrip.getBeaconInfo();
							if (beaconInfo.getSeenSeconds() > mApplication.getConfigManager()
									.getValue("beacon_secondsInBus", 120)) {
								if (beaconInfo.getStopBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API)
									if (mSharedPreferenceManager.getCurrentBusStop() != null)
										beaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.BEACON,
												mSharedPreferenceManager.getCurrentBusStop()));
								TripsSQLiteOpenHelper.getInstance(mApplication).addTrip(new FinishedTrip(beaconInfo.getStartBusstation().getBusStopId(), beaconInfo.getStopBusstation().getBusStopId(),
										beaconInfo.getLineId(), beaconInfo.getTripId(), mSharedPreferenceManager.getCurrentTrip().getTagesart_nr(), beaconInfo.getStartDate(),
										beaconInfo.getLastSeen()));
							}
							mSharedPreferenceManager.setCurrentTrip(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(BusBeaconHandler.mBusBeaconMap != null)
						BusBeaconHandler.mBusBeaconMap.clear();
					NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.cancel(2);
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
