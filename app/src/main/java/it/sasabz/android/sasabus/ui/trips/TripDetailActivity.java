package it.sasabz.android.sasabus.ui.trips;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.Buses;
import it.sasabz.android.sasabus.model.Vehicle;
import it.sasabz.android.sasabus.model.line.Lines;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.BusStop;
import it.sasabz.android.sasabus.realm.user.Trip;
import it.sasabz.android.sasabus.ui.bus.BusDetailActivity;
import it.sasabz.android.sasabus.ui.busstop.BusStopDetailActivity;
import it.sasabz.android.sasabus.ui.widget.NestedMapFragment;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.SettingsUtils;
import it.sasabz.android.sasabus.util.Utils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import it.sasabz.android.sasabus.util.recycler.TripAdapter;

/**
 * Shows detailed information about a trip like origin, destination, map which shows all the
 * bus stops the user passed by, and some info about fuel consumption.
 * <p>
 * As the {@link TripAdapter} starts this activity by using
 * a reveal animation it will slide up the toolbar and cards to display a nice animation
 * sequence.
 *
 * @author Alex Lardschneider
 */
public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener {

    private static final String TAG = "TripDetailActivity";

    private NestedMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    @BindView(R.id.trip_detail_start_station) TextView mStartStation;
    @BindView(R.id.trip_detail_start_time) TextView mStartTime;
    @BindView(R.id.trip_detail_stop_station) TextView mStopStation;
    @BindView(R.id.trip_detail_stop_time) TextView mStopTime;
    @BindView(R.id.trip_detail_duration) TextView mDuration;
    @BindView(R.id.trip_detail_line) TextView mLine;
    @BindView(R.id.trip_detail_distance) TextView mDistanceText;
    @BindView(R.id.trip_detail_vehicle_brand) TextView mVehicleBrand;
    @BindView(R.id.trip_detail_vehicle_name) TextView mVehicleFuel;
    @BindView(R.id.trip_detail_vehicle_info) LinearLayout mVehicleInfo;
    @BindView(R.id.trip_detail_vehicle_error) LinearLayout mVehicleError;
    @BindView(R.id.trip_detail_scrollview) ScrollView mScrollView;
    @BindView(R.id.trip_detail_vehicle_loading) ProgressBar mVehicleLoading;

    @BindView(R.id.trip_details_card_1) CardView cardView1;
    @BindView(R.id.trip_details_card_2) CardView cardView2;
    @BindView(R.id.trip_details_card_3) CardView cardView3;
    @BindView(R.id.trip_details_card_4) CardView cardView4;

    private String mTripHash;
    private Trip mTrip;

    private float mDistance;

    private final Realm realm = Realm.getDefaultInstance();

    private final short[] mEmissions = {
            0,   // ignore
            0,   // H2
            89,  // 2
            89,  // 3
            110, // 4
            89,  // 5
            110, // 6
            110, // 7
            89,  // 8
            89,  // 9
            89,  // 10
            110, // 11
            110, // 12
            110, // 13
            110, // 14
            160, // 15
            160, // 16
            160, // 17
            160, // 18
            160, // 19
            160  // 99
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_trip_details);

        ButterKnife.bind(this);

        AnalyticsHelper.sendScreenView(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mTripHash = intent.getStringExtra(Config.EXTRA_TRIP_HASH);

        cardView3.setOnClickListener(this);

        mVehicleLoading.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.primary_amber),
                PorterDuff.Mode.SRC_ATOP);

        int googleStatus = MapsInitializer.initialize(getApplicationContext());
        if (googleStatus == 0) {
            mMapFragment = (NestedMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);
            mMapFragment.getMapAsync(this);
            mMapFragment.setListener(() -> mScrollView.requestDisallowInterceptTouchEvent(true));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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
        });

        mGoogleMap.setOnInfoWindowClickListener(marker -> {
            Intent intent = new Intent(getApplication(), BusStopDetailActivity.class);
            intent.putExtra(Config.EXTRA_STATION_ID, Integer.parseInt(marker.getSnippet().split(":")[1]));
            startActivity(intent);
        });

        mGoogleMap.setOnMarkerClickListener(marker -> {
            Projection projection = mGoogleMap.getProjection();
            Point markerPoint = projection.toScreenLocation(marker.getPosition());

            if (mMapFragment.getView() == null) return false;
            markerPoint.offset(0, -(mMapFragment.getView().getHeight() / 4));
            LatLng newLatLng = projection.fromScreenLocation(markerPoint);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(newLatLng), 350, null);
            marker.showInfoWindow();

            return true;
        });

        parseData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_details_card_3:
                Intent intent = new Intent(this, BusDetailActivity.class);
                intent.putExtra(Config.EXTRA_VEHICLE, mTrip.getVehicle());
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }


    private void parseData() {
        mTrip = realm.where(Trip.class).equalTo("hash", mTripHash).findFirst();

        if (mTrip == null) {
            mScrollView.setVisibility(View.GONE);

            return;
        }

        parseVehicleData();

        Date startDate = new Date(mTrip.getDeparture() * 1000L);
        Date stopDate = new Date(mTrip.getArrival() * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALY);

        mStartStation.setText(BusStopRealmHelper
                .getNameFromId(mTrip.getOrigin()));
        mStopStation.setText(BusStopRealmHelper
                .getNameFromId(mTrip.getDestination()));

        mStartTime.setText(sdf.format(startDate));
        mStopTime.setText(sdf.format(stopDate));

        mLine.setText(Lines.lidToName(mTrip.getLine()));

        int timeDifference = (int) (stopDate.getTime() / 60000 - startDate.getTime() / 60000);

        if (timeDifference > 59) {
            int hours = timeDifference % 60;
            int minutes = timeDifference % 60;

            mDuration.setText(hours + "h " + minutes + '\'');
        } else {
            mDuration.setText(timeDifference + "'");
        }

        mGoogleMap.setOnMapLoadedCallback(() -> {
            mDistance = parseMapDataAndDistance(mTrip);
            parseVehicleData();
        });
    }

    private float parseMapDataAndDistance(Trip trip) {
        List<Marker> markers = new ArrayList<>();

        String[] tripList = trip.getPath().split(",");

        float distance = 0F;
        for (int i = 0; i < tripList.length; i++) {
            BusStop station = BusStopRealmHelper.getBusStopFromId(Integer.parseInt(tripList[i]));

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .title(station.getName(this))
                    .snippet(station.getMunic(this) + ":" + station.getId())
                    .position(new LatLng(station.getLat(), station.getLng())));

            markers.add(marker);

            if (i >= 1) {
                BusStop station1 = BusStopRealmHelper.getBusStopFromId(Integer.parseInt(tripList[i - 1]));

                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(station1.getLat(), station1.getLng()))
                        .add(marker.getPosition())
                        .color(Color.parseColor("#F44336"))
                        .width(5));


                distance += getDistance(marker.getPosition(), new LatLng(station1.getLat(), station1.getLng()));
            }
        }

        runOnUiThread(() -> {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < markers.size(); i++) {
                builder.include(markers.get(i).getPosition());
            }

            LatLngBounds bounds = builder.build();

            try {
                assert mMapFragment.getView() != null;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, mMapFragment.getView().getHeight() / 4);
                mGoogleMap.animateCamera(cu);
            } catch (IllegalStateException | NullPointerException e) {
                Utils.handleException(e);
            }
        });

        if (distance < 1000) {
            mDistanceText.setText(Math.round(distance) + " m");
        } else {
            mDistanceText.setText(String.valueOf(round(distance / 1000)).replace(".", ",") + " km");
        }

        return distance;
    }

    private void parseVehicleData() {
        Vehicle vehicle = Buses.getBus(this, mTrip.getVehicle());

        if (vehicle != null) {
            loadBackdrop(vehicle.getGroup());

            mVehicleBrand.setText(vehicle.getVendor());
            mVehicleFuel.setText(vehicle.getFuel());

            mVehicleInfo.setVisibility(View.VISIBLE);
            mVehicleError.setVisibility(View.GONE);
            mVehicleLoading.setVisibility(View.GONE);

            TextView co2Emissions = (TextView) findViewById(R.id.trip_detail_co2_emission);
            TextView co2EmissionsCar = (TextView) findViewById(R.id.trip_detail_co2_emission_car);
            TextView fuelPrice = (TextView) findViewById(R.id.trip_detail_fuel_price);

            float co2 = mEmissions[vehicle.getGroup()] * mDistance / 1000;
            co2Emissions.setText(Math.round(co2) + " g");

            float co2Car = 120 * mDistance / 1000;
            co2EmissionsCar.setText(Math.round(co2Car) + " g");

            float fuelConsumption = 0.119F;
            float fuelPriceValue = fuelConsumption * mDistance / 1000F * mTrip.getFuelPrice();
            fuelPrice.setText(String.format(Locale.ITALY, "%.2f €", fuelPriceValue));
        } else {
            mVehicleError.setVisibility(View.VISIBLE);
            mVehicleInfo.setVisibility(View.GONE);
            mVehicleLoading.setVisibility(View.GONE);
        }
    }

    /**
     * Loads the background image into the {@link CollapsingToolbarLayout} and sets the content
     * scrim and the status bar scrim color. Also colors the card icons.
     *
     * @param id the vehicle class id.
     */
    private void loadBackdrop(int id) {
        ImageView imageView = (ImageView) findViewById(R.id.trip_detail_vehicle_image);

        if (imageView != null) {
            Glide.with(this).load(Uri.parse("file:///android_asset/images/bus_" + id + ".jpg"))
                    .animate(R.anim.fade_in_short)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);
        }
    }

    private float getDistance(LatLng start, LatLng stop) {
        Location l1 = new Location("start");
        l1.setLatitude(start.latitude);
        l1.setLongitude(start.longitude);

        Location l2 = new Location("stop");
        l2.setLatitude(stop.latitude);
        l2.setLongitude(stop.longitude);

        return l1.distanceTo(l2);
    }

    private double round(float number) {
        return Math.round(number * 100) / 100.0;
    }
}