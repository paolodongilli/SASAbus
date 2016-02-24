package it.sasabz.sasabus.beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import it.sasabz.android.sasabus.R;

public class LocationServicesStateChangeReceiver extends BroadcastReceiver {
    public LocationServicesStateChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handleLocationServiceState(context);
    }
    public void handleLocationServiceState(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled() && android.os.Build.VERSION.SDK_INT >= 23) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            ;
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Intent notificationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, Intent.FILL_IN_DATA);
                Notification notification = new NotificationCompat.Builder(context)
                        .setContentTitle(context.getString(R.string.location_service_required)).setContentText(context.getText(R.string.for_beacon_background_scan))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon))
                        .setAutoCancel(true).setContentIntent(pendingIntent)
                        .build();

                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notification.ledARGB = Color.argb(255, 255, 166, 0);
                notification.ledOnMS = 200;
                notification.ledOffMS = 200;
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;

                notificationManager.notify(5, notification);
            } else
                notificationManager.cancel(5);
        }

    }
}
