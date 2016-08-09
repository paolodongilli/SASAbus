package it.sasabz.android.sasabus.ui.busstop;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.BusStopDetail;
import it.sasabz.android.sasabus.model.line.Lines;
import it.sasabz.android.sasabus.network.NetUtils;
import it.sasabz.android.sasabus.network.rest.Endpoint;
import it.sasabz.android.sasabus.network.rest.RestClient;
import it.sasabz.android.sasabus.network.rest.api.RealtimeApi;
import it.sasabz.android.sasabus.network.rest.model.RealtimeBus;
import it.sasabz.android.sasabus.network.rest.response.RealtimeResponse;
import it.sasabz.android.sasabus.provider.API;
import it.sasabz.android.sasabus.provider.ApiUtils;
import it.sasabz.android.sasabus.provider.PlanData;
import it.sasabz.android.sasabus.provider.model.Trip;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.UserRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.BusStop;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.SettingsUtils;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.BusStopDetailsAdapter;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

/**
 * Displays detailed information about a bus stop in form of a list. This list consists of a top
 * card displaying info about this bus stop like passing lines and municipality.
 * <p>
 * The other cards contain the next departures at this bus stop which are calculated offline
 * by using the integrated {@link API}. If a bus which departs at this bus stop is already in service,
 * the delay will be displayed next to the line.
 *
 * @author Alex Lardschneider
 * @author David Dejori
 */
public class BusStopDetailActivity extends RxAppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "BusStopDetailActivity";
    private static final String SCREEN_LABEL = "BusMarker stop details";

    private static final String BUNDLE_POSITION = "POSITION";
    private static final String BUNDLE_LIST = "LIST";

    private final ArrayList<BusStopDetail> mItems = new ArrayList<>();

    private BusStopDetailsAdapter mAdapter;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.bus_stop_details_favorites) FloatingActionButton mFavoritesFab;
    @BindView(R.id.main_content) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar) AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;

    private boolean mIsInFavorites;

    private int mBusStopId;

    private int mBusStopGroup;

    private BusStop busStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setContentView(R.layout.activity_bus_stop_details);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mBusStopId = intent.getExtras().getInt(Config.EXTRA_STATION_ID);
        mBusStopGroup = BusStopRealmHelper.getBusStopGroup(mBusStopId);

        mCollapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbar);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red, R.color.primary_green, R.color.primary_indigo);
        mSwipeRefreshLayout.setOnRefreshListener(() -> parseData(mBusStopGroup, mBusStopId));

        busStop = BusStopRealmHelper.getBusStop(mBusStopId);

        AnalyticsHelper.sendScreenView(TAG);
        AnalyticsHelper.sendEvent(SCREEN_LABEL, "Bus stop " + mBusStopId);

        mCollapsingToolbar.setTitle(busStop.getName(this));

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.stations_detail_fab);
        floatingActionButton.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + busStop.getLat() + ',' + busStop.getLng())));
            } catch (ActivityNotFoundException e) {
                Utils.handleException(e);
            }
        });

        mFavoritesFab.setOnClickListener(this);
        mFavoritesFab.setOnLongClickListener(this);

        if (UserRealmHelper.hasFavoriteBusStop(mBusStopGroup)) {
            mIsInFavorites = true;
        }

        setFavoritesFabIcon(mIsInFavorites);

        mItems.clear();

        mAdapter = new BusStopDetailsAdapter(this, mBusStopGroup, mItems);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            ArrayList<BusStopDetail> temp = savedInstanceState.getParcelableArrayList(BUNDLE_LIST);

            if (temp != null && !temp.isEmpty()) {
                mItems.addAll(temp);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt(BUNDLE_POSITION));

                return;
            }
        }

        if (PlanData.planDataExists(this)) {
            parseData(mBusStopGroup, mBusStopId);
        } else {
            SettingsUtils.markDataUpdateAvailable(this, true);
            mItems.add(new BusStopDetail(0, 0, null, null, null, 0, "data"));
            mAdapter.notifyDataSetChanged();
        }

        ImageView background = (ImageView) findViewById(R.id.backdrop);

        int fetchImages = SettingsUtils.getFetchImages(this);
        if (fetchImages == 1 || fetchImages == 2 && NetUtils.isWifiConnected(this)) {
            Glide.with(this).load(Endpoint.API + "assets/images/bus_stops/" + mBusStopId)
                    .centerCrop()
                    .into(background);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(BUNDLE_LIST, mItems);
        outState.putInt(BUNDLE_POSITION, ((LinearLayoutManager)
                mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());

        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bus_stop_details_favorites:
                if (mIsInFavorites) {
                    UserRealmHelper.removeFavoriteBusStop(mBusStopId);
                    Snackbar.make(coordinatorLayout, getString(R.string.bus_stop_favorites_remove,
                            busStop.getName(this)), Snackbar.LENGTH_SHORT).show();

                    setFavoritesFabIcon(false);
                    mIsInFavorites = false;
                } else {
                    UserRealmHelper.addFavoriteBusStop(mBusStopId);
                    Snackbar.make(coordinatorLayout, getString(R.string.bus_stop_favorites_add,
                            busStop.getName(this)), Snackbar.LENGTH_SHORT).show();

                    setFavoritesFabIcon(true);
                    mIsInFavorites = true;
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.bus_stop_details_favorites:
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(100);

                setResult(BusStopActivity.RESULT_DISPLAY_FAVORITES);
                finish();

                return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mCollapsingToolbar.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(mCollapsingToolbar)) {
            mSwipeRefreshLayout.setEnabled(false);
        } else {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }


    private void parseData(int family, int stationId) {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        getDepartures(family, stationId)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BusStopDetail>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // Should not happen as the Observable itself never calls onError,
                        // except when a RuntimeException occurs.
                        Utils.handleException(e);

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onNext(List<BusStopDetail> busStopDetails) {
                        mItems.clear();
                        mItems.addAll(busStopDetails);

                        mAdapter.notifyDataSetChanged();

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));

                        if (NetUtils.isOnline(BusStopDetailActivity.this)) {
                            getDelays()
                                    .compose(bindToLifecycle())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(Observers.create(busStopDetails1 -> {
                                        if (mItems.size() > 1) {
                                            mAdapter.notifyItemRangeChanged(1, mItems.size() - 1);
                                        }
                                    }, Utils::handleException));
                        }
                    }
                });
    }

    private void setFavoritesFabIcon(boolean favorite) {
        if (favorite) {
            mFavoritesFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_white_48dp));
        } else {
            mFavoritesFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_48dp));
        }
    }

    private Observable<List<BusStopDetail>> getDepartures(int family, int stationId) {
        return Observable.create(new Observable.OnSubscribe<List<BusStopDetail>>() {
            @Override
            public void call(Subscriber<? super List<BusStopDetail>> subscriber) {
                List<BusStopDetail> items = new ArrayList<>();

                String stop = BusStopRealmHelper.getMunic(stationId);
                String lines = ApiUtils.implode(", ",
                        API.getPassingLines(BusStopDetailActivity.this, mBusStopGroup), getString(R.string.station_no_lines));

                items.add(new BusStopDetail(0, 0, null, null, null, 0, stop + '#' + lines));

                if (!API.todayExists(BusStopDetailActivity.this)) {
                    SettingsUtils.markDataUpdateAvailable(BusStopDetailActivity.this, true);

                    items.add(new BusStopDetail(0, 0, null, null, null, 0, "data"));
                    subscriber.onNext(items);
                    subscriber.onCompleted();

                    return;
                }

                List<Trip> departures = API.getMergedDepartures(BusStopDetailActivity.this, family);

                for (Trip trip : departures) {
                    String line = Lines.lidToName(trip.getLine());
                    String departure = ApiUtils.getTime(trip.getSecondsAtUserStop());

                    String lastStationName = BusStopRealmHelper
                            .getName(trip.getPath().get(trip.getPath().size() - 1).getId());

                    items.add(new BusStopDetail(trip.getLine(), trip.getTrip(), line, departure,
                            lastStationName, Config.BUS_STOP_DETAILS_OPERATION_RUNNING, null));
                }

                subscriber.onNext(items);
                subscriber.onCompleted();
            }
        });
    }

    private Observable<Void> getDelays() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    RealtimeApi realtimeApi = RestClient.ADAPTER.create(RealtimeApi.class);
                    Response<RealtimeResponse> response = realtimeApi.delays().execute();

                    if (response.body() == null) {
                        subscriber.onError(new Throwable(response.errorBody().string()));
                        return;
                    }

                    List<RealtimeBus> list = response.body().buses;

                    for (RealtimeBus bus : list) {
                        for (int j = 1; j < mItems.size(); j++) {
                            BusStopDetail item = mItems.get(j);

                            if (item.getTrip() == bus.trip) {
                                item.setDelay(bus.delayMin);
                                item.setVehicle(bus.vehicle);

                                break;
                            }
                        }
                    }

                    for (int i = 1; i < mItems.size(); i++) {
                        BusStopDetail item = mItems.get(i);

                        if (item.getDelay() == Config.BUS_STOP_DETAILS_OPERATION_RUNNING) {
                            item.setDelay(Config.BUS_STOP_DETAILS_NO_DELAY);
                        }
                    }
                } catch (IOException e) {
                    subscriber.onError(e);
                    return;
                }

                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }
}