package it.sasabz.sasabus.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Random;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.beacon.bus.trip.TripNotificationAction;
import it.sasabz.sasabus.gson.bus.model.BusInformationResult;
import it.sasabz.sasabus.gson.serializer.DateSerializer;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;
import it.sasabz.sasabus.ui.MainActivity;

public class GCMIntentService extends IntentService {

    private static final String SERVICE_NAME = "it.sasabz.sasabus.gcm.GCMIntentService";

    public GCMIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        try {

            if (!extras.isEmpty()) {
                Log.d("Extras", extras.toString());
                String json = extras.getString("feature");
                int vehiclecode = Integer.parseInt(extras.getString("vehiclecode"));
                long serverTime = Long.parseLong(extras.getString("timestamp"));
                synchronized (SERVICE_NAME) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create();
                    BusInformationResult.Feature feature = gson.fromJson(json, BusInformationResult.Feature.class);
                    SasaApplication mApplication = (SasaApplication) getApplication();
                    CurentTrip curentTrip = mApplication.getSharedPreferenceManager().getCurrentTrip();
                    if(curentTrip == null || curentTrip.getBeaconInfo().getMajor() != vehiclecode){
                        BufferedReader br = null;
                        try {
                            URL url = new URL(mApplication.getConfigManager().getValue("gcm_service_cancel_url","http://gcmtest.opensasa.info/unregistration.php") + "?gcmregid=" + URLEncoder.encode(mApplication.getSharedPreferenceManager().getGcmRegId()) + "&vehiclecode=" + vehiclecode);
                            br = new BufferedReader(new InputStreamReader(url.openStream()));
                            br.readLine();
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally {
                            try {
                                br.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        if(curentTrip.getBeaconInfo().getLastFeature().getProperties().getGpsDate().getTime() < feature.getProperties().getGpsDate().getTime())
                            curentTrip.setLastFeatures(feature, mApplication);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
