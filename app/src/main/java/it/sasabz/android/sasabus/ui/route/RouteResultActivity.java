package it.sasabz.android.sasabus.ui.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.BusStop;
import it.sasabz.android.sasabus.model.route.RouteLeg;
import it.sasabz.android.sasabus.model.route.RouteResult;
import it.sasabz.android.sasabus.network.NetUtils;
import it.sasabz.android.sasabus.network.rest.RestClient;
import it.sasabz.android.sasabus.network.rest.api.RouteApi;
import it.sasabz.android.sasabus.network.rest.model.Leg;
import it.sasabz.android.sasabus.network.rest.model.Route;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.list.RouteResultsAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Displays all the route results in form of a list.
 *
 * @author Alex Lardschneider
 */
public class RouteResultActivity extends RxAppCompatActivity {

    private static final String TAG = "RouteResultActivity";

    @BindView(R.id.refresh) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.error_wifi) RelativeLayout errorWifi;
    @BindView(R.id.error_general) RelativeLayout errorGeneral;
    @BindView(R.id.error_route) RelativeLayout errorResults;

    private final ArrayList<RouteResult> mItems = new ArrayList<>();
    private RouteResultsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_route_results);

        ButterKnife.bind(this);

        AnalyticsHelper.sendScreenView(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int departureId = intent.getIntExtra(Config.EXTRA_DEPARTURE_ID, 0);
        int arrivalId = intent.getIntExtra(Config.EXTRA_ARRIVAL_ID, 0);

        String fromPlace = intent.getStringExtra("fromPlace");
        String toPlace = intent.getStringExtra("toPlace");
        String time = intent.getStringExtra("time");

        Date date = (Date) intent.getExtras().getSerializable("date");

        int results = intent.getIntExtra("results", 5);
        int walk = intent.getIntExtra("walk", 10);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String date1 = calendar.get(Calendar.YEAR) + "-" +
                (calendar.get(Calendar.MONTH) + 1) + '-' +
                calendar.get(Calendar.DAY_OF_MONTH);

        mAdapter = new RouteResultsAdapter(this, mItems);

        ListView listview = (ListView) findViewById(R.id.route_listView);
        listview.setAdapter(mAdapter);

        String departure = departureId != 0 ? String.valueOf(departureId) : fromPlace;
        String arrival = arrivalId != 0 ? String.valueOf(arrivalId) : toPlace;

        swipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red, R.color.primary_green, R.color.primary_indigo);
        swipeRefreshLayout.setOnRefreshListener(() -> parseData(departure, arrival, date1, time, walk, results));

        if (savedInstanceState != null) {
            int errorWifiVisibility = savedInstanceState.getInt(Config.BUNDLE_ERROR_WIFI);
            int errorGeneralVisibility = savedInstanceState.getInt(Config.BUNDLE_ERROR_GENERAL);
            int errorResultVisibility = savedInstanceState.getInt("ERROR_RESULT");

            if (errorWifiVisibility != 8 || errorGeneralVisibility != 8 ||
                    errorResultVisibility != 8) {

                //noinspection ResourceType
                errorGeneral.setVisibility(errorGeneralVisibility);
                //noinspection ResourceType
                errorWifi.setVisibility(errorWifiVisibility);
                //noinspection ResourceType
                errorResults.setVisibility(errorResultVisibility);

                return;
            } else {
                ArrayList<RouteResult> rowItemsTemp = savedInstanceState.getParcelableArrayList(Config.BUNDLE_LIST);

                if (rowItemsTemp != null && !rowItemsTemp.isEmpty()) {
                    mItems.addAll(rowItemsTemp);

                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }

                    return;
                }
            }
        }

        parseData(departure, arrival, date1, time, walk, results);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(Config.BUNDLE_LIST, mItems);

        outState.putInt("ERROR_RESULT", errorResults.getVisibility());
        outState.putInt(Config.BUNDLE_ERROR_WIFI, errorWifi.getVisibility());
        outState.putInt(Config.BUNDLE_ERROR_GENERAL, errorGeneral.getVisibility());
    }

    private void parseData(String from, String to, String date, String time, int walk, int results) {
        if (!NetUtils.isOnline(this)) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }

            errorWifi.setVisibility(View.VISIBLE);

            errorResults.setVisibility(View.GONE);
            errorGeneral.setVisibility(View.GONE);

            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));

            return;
        }

        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));

        String locale = getResources().getConfiguration().locale.toString();

        RouteApi routeApi = RestClient.ADAPTER.create(RouteApi.class);
        routeApi.route(locale, from, to, date, time, walk, results)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .map(routeResponse -> {
                    List<Route> list = routeResponse.routes;
                    List<RouteResult> routes = new ArrayList<>();

                    if (list.isEmpty()) {
                        return routes;
                    }

                    for (Route route : list) {
                        List<RouteLeg> legs = new ArrayList<>();

                        for (Leg leg : route.legs) {
                            if (leg.app.id == 3) {
                                legs.add(new RouteLeg(-1, 3, leg.app.vehicle, null, null, null,
                                        null, null, null));

                                continue;
                            }

                            BusStop departure = new BusStop(0, leg.departure.name,
                                    leg.departure.municipality, leg.departure.lat, leg.departure.lng, 0);

                            BusStop arrival = new BusStop(0, leg.arrival.name,
                                    leg.arrival.municipality, leg.arrival.lat, leg.arrival.lng, 0);

                            legs.add(new RouteLeg(leg.duration, leg.app.id, leg.app.vehicle,
                                    leg.app.id == 4 ? null : leg.app.line, leg.app.legend,
                                    departure, leg.departure.time,
                                    arrival, leg.arrival.time));
                        }

                        routes.add(new RouteResult(route.changes, route.departure, route.arrival,
                                route.duration, legs));
                    }

                    return routes;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<RouteResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }

                        errorGeneral.setVisibility(View.VISIBLE);

                        errorWifi.setVisibility(View.GONE);
                        errorResults.setVisibility(View.GONE);

                        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onNext(List<RouteResult> routeResults) {
                        if (routeResults.isEmpty()) {
                            mItems.clear();

                            errorResults.setVisibility(View.VISIBLE);
                        } else {
                            mItems.clear();
                            mItems.addAll(routeResults);

                            errorResults.setVisibility(View.GONE);
                        }

                        mAdapter.notifyDataSetChanged();

                        errorGeneral.setVisibility(View.GONE);
                        errorWifi.setVisibility(View.GONE);

                        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
                    }
                });
    }
}