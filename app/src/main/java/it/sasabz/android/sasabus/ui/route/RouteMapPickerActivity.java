package it.sasabz.android.sasabus.ui.route;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import io.realm.Realm;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.BusStop;
import it.sasabz.android.sasabus.model.ClusterMarker;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.SadBusStop;
import it.sasabz.android.sasabus.ui.widget.NestedSwipeRefreshLayout;
import it.sasabz.android.sasabus.util.Utils;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Allows the user to pick a departure/arrival bus stop from a map by clicking the marker.
 *
 * @author Alex Lardschneider
 */
public class RouteMapPickerActivity extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private ClusterManager<ClusterMarker> mClusterManager;
    private ClusterMarker mClickedClusterItem;

    private ArrayList<ClusterMarker> mItems = new ArrayList<>();

    private NestedSwipeRefreshLayout swipeRefreshLayout;

    private GoogleMap mGoogleMap;

    private Bundle mSavedInstanceState;

    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSavedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_bus_stop_picker_map);

        Utils.changeLanguage(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (NestedSwipeRefreshLayout) findViewById(R.id.station_picker_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red,
                R.color.primary_green, R.color.primary_indigo);

        mRealm = Realm.getInstance(BusStopRealmHelper.CONFIG);

        int googleStatus = MapsInitializer.initialize(getApplicationContext());

        if (googleStatus == 0) {
            SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);

            if (map != null) {
                map.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        mClusterManager = new ClusterManager<>(this, map);

        if (mSavedInstanceState != null) {
            double lat = mSavedInstanceState.getDouble("CAMERA_LAT");
            double lng = mSavedInstanceState.getDouble("CAMERA_LNG");
            float zoom = mSavedInstanceState.getFloat("CAMERA_ZOOM");

            if (lat == 0 && lng == 0 && zoom == 0) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10));
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), zoom));
            }
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10));
        }

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        map.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(mClusterManager);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        if (mSavedInstanceState != null) {
            mItems = mSavedInstanceState.getParcelableArrayList(Config.BUNDLE_LIST);

            if (mItems != null) {
                for (ClusterMarker mapItem : mItems) {
                    mClusterManager.addItem(mapItem);
                }

                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ItemAdapter());
                mClusterManager.cluster();
            }
        } else {
            parseData();
        }
    }

    @Override
    public boolean onClusterItemClick(ClusterMarker item) {
        mClickedClusterItem = item;
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        SadBusStop station = BusStopRealmHelper
                .getSadBusStopFromId(Integer.parseInt(item.getSnippet().split(":")[1]));

        Parcelable busStop = new BusStop(station);
        Intent intent = new Intent();
        intent.putExtra(Config.EXTRA_STATION, busStop);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Config.BUNDLE_LIST, mItems);

        if (mGoogleMap != null) {
            outState.putDouble("CAMERA_LAT", mGoogleMap.getCameraPosition().target.latitude);
            outState.putDouble("CAMERA_LNG", mGoogleMap.getCameraPosition().target.longitude);
            outState.putFloat("CAMERA_ZOOM", mGoogleMap.getCameraPosition().zoom);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void parseData() {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));

        mRealm.where(SadBusStop.class).findAllAsync().asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(busStops -> {
                    for (SadBusStop busStop : busStops) {
                        ClusterMarker tempItem = new ClusterMarker(busStop.getLat(), busStop.getLng(),
                                busStop.getName(this), busStop.getMunic(this) + ':' + busStop.getId());

                        mItems.add(tempItem);
                        mClusterManager.addItem(tempItem);
                    }

                    mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ItemAdapter());
                    mClusterManager.cluster();

                    swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
                });
    }

    public class ItemAdapter implements GoogleMap.InfoWindowAdapter {

        private final View view;

        ItemAdapter() {
            view = getLayoutInflater().inflate(R.layout.include_stations_select_popup, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView title = (TextView) view.findViewById(R.id.stations_popup_title);
            title.setText(mClickedClusterItem.getTitle());

            TextView snippet = (TextView) view.findViewById(R.id.stations_popup_snippet);
            snippet.setText(mClickedClusterItem.getSnippet().split(":")[0]);

            return view;
        }
    }
}