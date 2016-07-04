package it.sasabz.android.sasabus.ui.line;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.network.NetUtils;
import it.sasabz.android.sasabus.network.rest.RestClient;
import it.sasabz.android.sasabus.network.rest.api.PathsApi;
import it.sasabz.android.sasabus.network.rest.response.PathResponse;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.BusStop;
import it.sasabz.android.sasabus.ui.busstop.BusStopDetailActivity;
import it.sasabz.android.sasabus.ui.widget.NestedSwipeRefreshLayout;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.Utils;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Displays a map with all the bus stops a where the selected line passes by.
 *
 * @author Alex Lardschneider
 */
public class LinePathActivity extends RxAppCompatActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "LinePathActivity";
    private static final String SCREEN_LABEL = "Line path";

    private GoogleMap mGoogleMap;
    private SupportMapFragment mMap;
    private NestedSwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;

    private int mLineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_line_path);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mLineId = intent.getExtras().getInt(Config.EXTRA_LINE_ID);

        AnalyticsHelper.sendScreenView(TAG);
        AnalyticsHelper.sendEvent(SCREEN_LABEL, "Line " + mLineId);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        mSwipeRefreshLayout = (NestedSwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red, R.color.primary_green, R.color.primary_indigo);

        if (MapsInitializer.initialize(this) == 0) {
            mMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);

            if (mMap != null) {
                mMap.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);

        parseData(mLineId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGoogleMap != null) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(false);
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.include_bus_stop_infowindow, null);

        TextView title = (TextView) view.findViewById(R.id.stations_popup_title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.stations_popup_snippet);
        snippet.setText(marker.getSnippet().split(":")[0]);

        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getApplication(), BusStopDetailActivity.class);
        intent.putExtra(Config.EXTRA_STATION_ID, Integer.parseInt(marker.getSnippet().split(":")[1]));
        startActivity(intent);
    }

    private void showErrorSnackbar(int message) {
        Snackbar errorSnackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_INDEFINITE);

        View snackbarView = errorSnackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));

        runOnUiThread(errorSnackbar::show);
    }

    private void parseData(int id) {
        if (!NetUtils.isOnline(this)) {
            showErrorSnackbar(R.string.error_wifi);
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));

            return;
        }

        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        PathsApi pathsApi = RestClient.ADAPTER.create(PathsApi.class);
        pathsApi.getPath(id)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PathResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.handleException(e);

                        showErrorSnackbar(R.string.error_general);

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onNext(PathResponse pathResponse) {
                        List<Integer> list = pathResponse.path;
                        Collection<Marker> markers = new ArrayList<>();

                        for (int i = 0, listSize = list.size(); i < listSize; i++) {
                            Integer integer = list.get(i);

                            BusStop station = BusStopRealmHelper.getBusStopFromId(integer);

                            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                                    .title(station.getName(LinePathActivity.this))
                                    .snippet(station.getMunic(LinePathActivity.this) + ":" + station.getId())
                                    .position(new LatLng(station.getLat(), station.getLng())));

                            markers.add(marker);

                            if (i >= 1) {
                                BusStop station1 = BusStopRealmHelper.getBusStopFromId(list.get(i - 1));

                                mGoogleMap.addPolyline(new PolylineOptions()
                                        .add(new LatLng(station1.getLat(), station1.getLng()))
                                        .add(marker.getPosition())
                                        .color(ContextCompat.getColor(LinePathActivity.this,
                                                R.color.primary_red))
                                        .width(5));
                            }
                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }

                        LatLngBounds bounds = builder.build();

                        View map = mMap.getView();
                        if (map != null) {
                            if (getResources().getConfiguration().orientation ==
                                    Configuration.ORIENTATION_LANDSCAPE) {
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                                        map.getHeight() / 4);
                                mGoogleMap.animateCamera(cu);
                            } else {
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                                        map.getWidth() / 4);
                                mGoogleMap.animateCamera(cu);
                            }
                        }

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
                    }
                });
    }
}