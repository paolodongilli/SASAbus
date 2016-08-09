package it.sasabz.android.sasabus.ui.route;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.BusStop;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.SadBusStop;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.map.RoutePickerMapView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Allows the user to pick a departure/arrival bus stop from a map by clicking the marker.
 *
 * @author Alex Lardschneider
 */
public class RouteMapPickerActivity extends AppCompatActivity {

    private Realm mRealm;

    private RoutePickerMapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_stop_picker_map);

        Utils.changeLanguage(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.googlemap);

        mRealm = Realm.getInstance(BusStopRealmHelper.CONFIG);
        mapView = new RoutePickerMapView(this, webView);

        parseData();
    }

    public void selectBusStop(int id) {
        SadBusStop station = BusStopRealmHelper
                .getSadBusStop(id);

        Parcelable busStop = new BusStop(station);
        Intent intent = new Intent();
        intent.putExtra(Config.EXTRA_STATION, busStop);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void parseData() {
        mRealm.where(SadBusStop.class).findAllAsync().asObservable()
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busStops -> {
                    mapView.setMarkers(busStops);
                });
    }
}