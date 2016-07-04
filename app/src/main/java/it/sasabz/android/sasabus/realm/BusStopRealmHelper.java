package it.sasabz.android.sasabus.realm;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.realm.busstop.BusStop;
import it.sasabz.android.sasabus.realm.busstop.BusStopModule;
import it.sasabz.android.sasabus.realm.busstop.SadBusStop;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.LogUtils;
import it.sasabz.android.sasabus.util.Utils;

public final class BusStopRealmHelper {

    private static final String TAG = "BusStopRealmHelper";

    private static final int DB_VERSION = 2016060801; // YY MM DD Rev.
    private static final String DB_NAME = "busstops.realm";

    public static RealmConfiguration CONFIG;

    /**
     * Reference to hold a realm instance. Will be loaded on app start in a background thread
     * and never closed, so further calls to {@link Realm#getInstance(RealmConfiguration)} are
     * almost instant.
     */
    private static Realm sRealm;

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private BusStopRealmHelper() {
    }

    /**
     * Initializes the bus stop realm instance. This database holds all SASA and SAD bus stops
     * needed throughout the app.
     * <p>
     * As using a {@link io.realm.RealmMigration} in this case makes no sense, a empty
     * {@link io.realm.RealmMigration} is provided and the old database will be deleted if it was
     * upgraded in the new app version. On first app start or after every update which shipped a
     * never database the database is copied from the app assets.
     *
     * @param context Context to build the {@link RealmConfiguration}
     */
    public static void init(Context context) {
        sContext = context;

        CONFIG = new RealmConfiguration.Builder(context)
                .name(DB_NAME)
                .schemaVersion(DB_VERSION)
                .assetFile(context, DB_NAME)
                .modules(new BusStopModule())
                .migration((realm, oldVersion, newVersion) -> {
                    // Provide no migration.
                })
                .build();

        DynamicRealm dynamicRealm = DynamicRealm.getInstance(CONFIG);
        long version = dynamicRealm.getVersion();
        dynamicRealm.close();

        LogUtils.w(TAG, "Realm db version: " + version + ", should be " + DB_VERSION);

        if (version < DB_VERSION || version > DB_VERSION) {
            LogUtils.e(TAG, "Deleting old realm");

            Realm.deleteRealm(CONFIG);
        }

        // Load realm on bg thread and keep it open.
        new Thread(() -> {
            sRealm = Realm.getInstance(CONFIG);
        }).start();
    }

    public static String getNameFromId(int id) {
        String locale = sContext.getResources().getConfiguration().locale.toString();

        Realm realm = Realm.getInstance(CONFIG);
        BusStop busStop = realm.where(BusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            LogUtils.e(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getNameFromId SASA station = 0"));

            return sContext.getString(R.string.unknown);
        }

        String name = locale.contains("de") ? busStop.getNameDe() : busStop.getNameIt();

        realm.close();
        return name;
    }

    public static String getNameFromSadId(int id) {
        String locale = sContext.getResources().getConfiguration().locale.toString();

        Realm realm = Realm.getInstance(CONFIG);
        SadBusStop busStop = realm.where(SadBusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            LogUtils.e(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getNameFromSadId SAD station = 0"));

            return sContext.getString(R.string.unknown);
        }

        String name = locale.contains("de") ? busStop.getNameDe() : busStop.getNameIt();

        realm.close();
        return name;
    }

    public static String getMunicFromId(int id) {
        String locale = sContext.getResources().getConfiguration().locale.toString();

        Realm realm = Realm.getInstance(CONFIG);
        BusStop busStop = realm.where(BusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            LogUtils.e(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getMunicFromId SASA station = 0"));

            return sContext.getString(R.string.unknown);
        }

        String name = locale.contains("de") ? busStop.getMunicDe() : busStop.getMunicIt();

        realm.close();
        return name;
    }

    public static String getMunicFromSadId(int id) {
        String locale = sContext.getResources().getConfiguration().locale.toString();

        Realm realm = Realm.getInstance(CONFIG);
        SadBusStop busStop = realm.where(SadBusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            LogUtils.e(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getMunicFromSadId SAD station = 0"));

            return sContext.getString(R.string.unknown);
        }

        String name = locale.contains("de") ? busStop.getMunicDe() : busStop.getMunicIt();

        realm.close();
        return name;
    }

    public static BusStop getBusStopFromId(int id) {
        Realm realm = Realm.getInstance(CONFIG);
        BusStop busStop = realm.where(BusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            AnalyticsHelper.sendEvent(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getBusStopFromId SASA station = 0"));

            busStop = new BusStop(id, String.valueOf(id), String.valueOf(id), 0, 0, 0);
        } else {
            busStop = realm.copyFromRealm(busStop);
        }

        realm.close();

        return busStop;
    }

    public static SadBusStop getSadBusStopFromId(int id) {
        Realm realm = Realm.getInstance(CONFIG);
        SadBusStop busStop = realm.where(SadBusStop.class).equalTo("id", id).findFirst();

        if (busStop == null) {
            AnalyticsHelper.sendEvent(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getSadBusStopFromId SASA station = 0"));

            busStop = new SadBusStop(id, String.valueOf(id), String.valueOf(id), 0, 0);
        } else {
            busStop = realm.copyFromRealm(busStop);
        }

        realm.close();

        return busStop;
    }

    public static Collection<BusStop> getBusStopsFromGroup(int group) {
        Realm realm = Realm.getInstance(CONFIG);
        RealmResults<BusStop> results = realm.where(BusStop.class)
                .equalTo("family", group).findAll();
        Collection<BusStop> busStops = new ArrayList<>();

        for (BusStop busStop : results) {
            busStops.add(realm.copyFromRealm(busStop));
        }

        realm.close();

        return busStops;
    }

    public static List<Integer> getBusStopIdsFromGroup(int group) {
        Realm realm = Realm.getInstance(CONFIG);
        RealmResults<BusStop> results = realm.where(BusStop.class).equalTo("group", group).findAll();
        realm.close();

        List<Integer> resultIds = new ArrayList<>();
        for (BusStop busStop : results) {
            resultIds.add(busStop.getId());
        }

        return resultIds;
    }

    public static int getBusStopGroupFromId(int id) {
        Realm realm = Realm.getInstance(CONFIG);
        BusStop busStop = realm.where(BusStop.class).equalTo("id", id).findFirst();

        int result;

        if (busStop == null) {
            AnalyticsHelper.sendEvent(TAG, "Missing SASA station: " + id);
            Utils.handleException(new Throwable("getBusStopGroupFromId SASA station = 0"));

            result = 0;
        } else {
            result = busStop.getFamily();
        }

        realm.close();
        return result;
    }

    /**
     * Returns all stations in the same family (having the same name and municipality).
     *
     * @return all stations having the same name and municipality in an {@link ArrayList}
     */
    public static Collection<it.sasabz.android.sasabus.provider.model.BusStop> getBusStopsFromFamily(int family) {
        Realm realm = Realm.getInstance(CONFIG);
        List<BusStop> busStops = realm.where(BusStop.class).equalTo("family", family).findAll();

        if (busStops.isEmpty()) {
            LogUtils.e(TAG, "Invalid family id: " + family);
            Utils.handleException(new Throwable("getBusStopsFromFamily: invalid family id"));
            return new ArrayList<>();
        }

        ArrayList<it.sasabz.android.sasabus.provider.model.BusStop> stops = new ArrayList<>();

        for (BusStop busStop : busStops) {
            stops.add(new it.sasabz.android.sasabus.provider.model.BusStop(busStop.getId()));
        }

        realm.close();

        return stops;
    }
}
