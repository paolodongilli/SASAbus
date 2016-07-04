package it.sasabz.android.sasabus.ui.route;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.route.RouteLeg;
import it.sasabz.android.sasabus.model.route.RouteLegend;
import it.sasabz.android.sasabus.model.route.RouteResult;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.RouteLegendAdapter;

/**
 * Shows a detailed map with route legs and a small legend.
 *
 * @author Alex Lardschneider
 */
public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteDetailsActivity";

    private SupportMapFragment mSupportMap;
    private GoogleMap mGoogleMap;
    private RouteResult mRouteItem;

    private RouteLegendAdapter mAdapter;
    private List<RouteLegend> mItems;

    private RecyclerView mRecyclerView;
    private CardView mCardView;

    private final String[] mLegColors = {
            "#448AFF", // Light blue
            "#F44336", // Red
            "#4CAF50", // Green
            "#9C27B0", // Purple
            "#00BCD4", // Cyan
            "#009688", // Teal
            "#E91E63", // Pink
            "#3F51B5", // Indigo
            "#CDDC39", // Lime
            "#FF5722", // Deep orange
            "#607D8B", // Blue grey
            "#795548", // Brown
            "#000000", // Black
            "#000000", // Black
            "#000000", // Black
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_route_details);

        AnalyticsHelper.sendScreenView(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mRouteItem = intent.getParcelableExtra(Config.EXTRA_STATION);

        mItems = new ArrayList<>();
        mAdapter = new RouteLegendAdapter(this, mItems);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCardView = (CardView) findViewById(R.id.route_details_card);

        if (MapsInitializer.initialize(getApplicationContext()) == 0) {
            mSupportMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap);

            if (mSupportMap != null) {
                mSupportMap.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.58, 11.25), 10));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }

        map.setOnCameraChangeListener(arg0 -> {
            parseData();

            map.setOnCameraChangeListener(null);
        });
    }

    private void parseData() {
        List<Marker> markers = new ArrayList<>();

        for (int i = 0; i < mRouteItem.getLegs().size(); i++) {
            RouteLeg leg = mRouteItem.getLegs().get(i);

            if (leg.getId() == 3) {
                continue;
            }

            mItems.add(new RouteLegend(leg.getId(), leg.getLegend(), mLegColors[i]));
            mAdapter.notifyItemInserted(i);

            float hue = i == 0 ? BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_CYAN;

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .title(leg.getDeparture().getName())
                    .snippet(leg.getDeparture().getMunic())
                    .position(new LatLng(leg.getDeparture().getLat(), leg.getDeparture().getLng()))
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            markers.add(marker);

            if (i == mRouteItem.getLegs().size() - 1) {
                hue = BitmapDescriptorFactory.HUE_GREEN;
            } else {
                hue = BitmapDescriptorFactory.HUE_CYAN;
            }

            marker = mGoogleMap.addMarker(new MarkerOptions()
                    .title(leg.getArrival().getName())
                    .snippet(leg.getArrival().getMunic())
                    .position(new LatLng(leg.getArrival().getLat(), leg.getArrival().getLng()))
                    .icon(BitmapDescriptorFactory.defaultMarker(hue)));

            markers.add(marker);

            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.parseColor(mLegColors[i]))
                    .width(12);

            if (markers.size() > 1) {
                polylineOptions.add(markers.get(markers.size() - 2).getPosition());
            }

            for (LatLng latLng : leg.getCoords()) {
                polylineOptions.add(latLng);
            }

            if (!markers.isEmpty()) {
                polylineOptions.add(markers.get(markers.size() - 1).getPosition());
            }

            mGoogleMap.addPolyline(polylineOptions);
        }

        if (mItems.size() <= 4) {
            setRecyclerHeight();
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        for (RouteLeg leg : mRouteItem.getLegs()) {
            for (LatLng latLng : leg.getCoords()) {
                builder.include(latLng);
            }
        }

        LatLngBounds bounds = builder.build();

        try {
            View map = mSupportMap.getView();
            if (map != null) {
                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                            map.getHeight() / 8);
                    mGoogleMap.animateCamera(cu);
                } else {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                            map.getWidth() / 4);
                    mGoogleMap.animateCamera(cu);
                }
            }
        } catch (IllegalStateException e) {
            Utils.handleException(e);
        }
    }

    private void setRecyclerHeight() {
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mRecyclerView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.list_item_route_legend, null);

        for (int i = 0; i < mItems.size(); i++) {
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RecyclerView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += view.getMeasuredHeight();
        }

        totalHeight += mRecyclerView.getPaddingTop() + mRecyclerView.getPaddingBottom();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCardView.getLayoutParams();
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();

        params.bottomMargin = totalHeight;
        params1.height = totalHeight;

        mCardView.requestLayout();
        mRecyclerView.requestLayout();
    }
}