package it.sasabz.sasabus.beacon;

import org.altbeacon.beacon.BeaconManager;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.beacon.bus.trip.TripBusStop;
import it.sasabz.sasabus.data.trips.FinishedTrip;
import it.sasabz.sasabus.data.trips.TripsSQLiteOpenHelper;
import it.sasabz.sasabus.gcm.GCMIntentService;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.busschedules.BusDepartureItem;

public class BluetoothStateChangeReceiver extends WakefulBroadcastReceiver {

	private static final String TAG = "android.sasabus";

	@Override
	public void onReceive(Context context, Intent intent) {
		synchronized (TAG) {
			try {
				if (BeaconManager.getInstanceForApplication(context).checkAvailability()) {
					if(intent.getAction() != null && intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
						Intent checkLocation = new Intent(context, LocationServicesStateChangeReceiver.class);
						context.sendBroadcast(checkLocation);
					}
					ComponentName comp = new ComponentName(context.getPackageName(),
							BeaconScannerService.class.getName());
					startWakefulService(context, (intent.setComponent(comp)));
				} else {
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					if (!mBluetoothAdapter.isEnabled()) {
						context.sendBroadcast(new Intent(BusDepartureItem.class.getName()));
						try {
							SasaApplication mApplication = BusBeaconHandler.mTripNotificationAction.mSasaApplication;
							SharedPreferenceManager mSharedPreferenceManager = mApplication.getSharedPreferenceManager();
							CurentTrip curentTrip = mApplication.getSharedPreferenceManager().getCurrentTrip();
							if (curentTrip != null) {
								mSharedPreferenceManager.setCurrentTrip(null);
								BusBeaconInfo beaconInfo = curentTrip.getBeaconInfo();
								if (beaconInfo.getSeenSeconds() > mApplication.getConfigManager()
										.getValue("beacon_secondsInBus", 120)) {
									if (beaconInfo.getStopBusstation().getTripBusStopType() == TripBusStop.TripBusStopType.REALTIME_API)
										if (mSharedPreferenceManager.getCurrentBusStop() != null)
											beaconInfo.setStopBusstation(new TripBusStop(TripBusStop.TripBusStopType.BEACON,
													mSharedPreferenceManager.getCurrentBusStop()));
									TripsSQLiteOpenHelper.getInstance(mApplication).addTrip(new FinishedTrip(beaconInfo.getStartBusstation().getBusStopId(), beaconInfo.getStopBusstation().getBusStopId(),
											beaconInfo.getLineId(), beaconInfo.getTripId(), curentTrip.getTagesart_nr(), beaconInfo.getStartDate(),
											beaconInfo.getLastSeen()));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						context.stopService(new Intent(context, BeaconScannerService.class));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
