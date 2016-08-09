package it.sasabz.android.sasabus.ui.line;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.model.line.LineDetail;
import it.sasabz.android.sasabus.model.line.Lines;
import it.sasabz.android.sasabus.network.NetUtils;
import it.sasabz.android.sasabus.network.rest.RestClient;
import it.sasabz.android.sasabus.network.rest.api.LinesApi;
import it.sasabz.android.sasabus.network.rest.api.RealtimeApi;
import it.sasabz.android.sasabus.network.rest.model.Line;
import it.sasabz.android.sasabus.network.rest.model.RealtimeBus;
import it.sasabz.android.sasabus.network.rest.response.RealtimeResponse;
import it.sasabz.android.sasabus.provider.apis.Trips;
import it.sasabz.android.sasabus.provider.model.BusStop;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.UserRealmHelper;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.LogUtils;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.LineDetailsAdapter;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Displays information about this line and all the vehicles currently driving this line.
 * <p>
 * The list consists of a card at the top with information about this line, e.g. origin, destination
 * and days where this line drives. The other cards hold the vehicles which currently drive this
 * line, including their destination and delay.
 *
 * @author Alex Lardschneider
 * @author David Dejori
 */
public class LineDetailActivity extends RxAppCompatActivity implements OnClickListener,
        OnLongClickListener {

    private static final String TAG = "LineDetailActivity";
    private static final String SCREEN_LABEL = "Line details";

    private final ArrayList<LineDetail> mItems = new ArrayList<>();

    private LineDetailsAdapter mAdapter;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.lines_detail_favorites) FloatingActionButton mFavoritesFab;
    @BindView(R.id.main_content) CoordinatorLayout coordinatorLayout;

    private boolean mErrorGeneral;
    private boolean mIsInFavorites;

    private int lineId;
    private String lineName;

    private Line line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.changeLanguage(this);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_line_details);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        lineId = intent.getIntExtra(Config.EXTRA_LINE_ID, 0);
        line = intent.getParcelableExtra(Config.EXTRA_LINE);
        lineName = Lines.lidToName(lineId);

        int vehicle = intent.getExtras().getInt(Config.EXTRA_VEHICLE);

        AnalyticsHelper.sendScreenView(TAG);
        AnalyticsHelper.sendEvent(SCREEN_LABEL, "Line " + lineId);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.line) + ' ' + lineName);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbar);

        FloatingActionButton pathFab = (FloatingActionButton) findViewById(R.id.lines_detail_path);
        pathFab.setOnClickListener(this);

        mFavoritesFab.setOnClickListener(this);
        mFavoritesFab.setOnLongClickListener(this);

        if (UserRealmHelper.hasFavoriteLine(lineId)) {
            mIsInFavorites = true;
        }

        setFavoritesFabIcon(mIsInFavorites);

        mSwipeRefreshLayout.setOnRefreshListener(() -> parseData(lineId, vehicle));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red,
                R.color.primary_green, R.color.primary_indigo);

        mItems.clear();
        mAdapter = new LineDetailsAdapter(this, mItems);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            ArrayList<LineDetail> temp = savedInstanceState.getParcelableArrayList("LIST");

            mItems.addAll(temp);
            mAdapter.notifyDataSetChanged();

            mRecyclerView.getLayoutManager().scrollToPosition(savedInstanceState.getInt("POSITION"));

            return;
        }

        parseData(lineId, vehicle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("LIST", mItems);
        outState.putInt("POSITION", ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lines_detail_path:
                Intent intent = new Intent(getApplicationContext(), LinePathActivity.class);
                intent.putExtra(Config.EXTRA_LINE_ID, lineId);
                startActivity(intent);
                break;
            case R.id.lines_detail_favorites:
                if (mIsInFavorites) {
                    UserRealmHelper.removeFavoriteLine(lineId);
                    Snackbar.make(coordinatorLayout, getString(R.string.line_favorites_remove, lineName),
                            Snackbar.LENGTH_SHORT).show();

                    setFavoritesFabIcon(false);
                    mIsInFavorites = false;
                } else {
                    UserRealmHelper.addFavoriteLine(lineId);
                    Snackbar.make(coordinatorLayout, getString(R.string.line_favorites_add, lineName),
                            Snackbar.LENGTH_SHORT).show();

                    setFavoritesFabIcon(true);
                    mIsInFavorites = true;
                }

                setResult(RESULT_OK);

                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.lines_detail_favorites:
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(100);

                setResult(LinesActivity.RESULT_DISPLAY_FAVORITES);
                finish();

                return true;
        }
        return false;
    }


    private void parseData(int lineId, int busId) {
        if (!NetUtils.isOnline(this) && line == null) {
            mItems.clear();
            mItems.add(new LineDetail(null, 0, null, null, "nointernet", 0, false));

            mAdapter.notifyDataSetChanged();

            return;
        }

        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

        Observable.zip(getLine(), getTraveling(lineId, busId), zipFunction())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<LineDetail>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.handleException(e);
                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
                    }

                    @Override
                    public void onNext(List<LineDetail> lineDetails) {
                        mItems.clear();
                        mItems.addAll(lineDetails);

                        mAdapter.notifyDataSetChanged();

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));

                        for (int i = 0; i < mItems.size(); i++) {
                            if (mItems.get(i).isColor()) {
                                int finalI = i;
                                mRecyclerView.post(() -> mRecyclerView.smoothScrollToPosition(finalI));
                                break;
                            }
                        }
                    }
                });
    }

    private Observable<LineDetail> getLine() {
        if (line != null) {
            return Observable.create(new Observable.OnSubscribe<LineDetail>() {
                @Override
                public void call(Subscriber<? super LineDetail> subscriber) {
                    String data = line.getOrigin() + "#" +
                            line.getDestination() + '#' +
                            line.getCity() + '#' +
                            getDateString(line.getDays()) + '#' +
                            line.getInfo();

                    subscriber.onNext(new LineDetail(null, 0, null, null, data, 0, false));
                    subscriber.onCompleted();
                }
            });
        } else {
            String locale = getResources().getConfiguration().locale.toString();
            LinesApi linesApi = RestClient.ADAPTER.create(LinesApi.class);

            return linesApi.line(locale, lineId)
                    .map(linesAllResponse -> {
                        List<Line> lines = linesAllResponse.lines;

                        if (!lines.isEmpty()) {
                            Line line = lines.get(0);
                            String data = line.getOrigin() + "#" +
                                    line.getDestination() + '#' +
                                    line.getCity() + '#' +
                                    getDateString(line.getDays()) + '#' +
                                    line.getInfo();

                            return new LineDetail(null, 0, null, null, data, 0, false);
                        }

                        mErrorGeneral = true;
                        return new LineDetail(null, 0, null, null, "error", 0, false);
                    });
        }
    }

    private Observable<List<LineDetail>> getTraveling(int line, int busId) {
        return Observable.create(new Observable.OnSubscribe<List<LineDetail>>() {
            @Override
            public void call(Subscriber<? super List<LineDetail>> subscriber) {
                List<LineDetail> items = new ArrayList<>();

                if (contains(lineId)) {
                    items.add(new LineDetail(null, 0, null, null, "track", 0, false));

                    subscriber.onNext(items);
                    subscriber.onCompleted();
                    return;
                }

                if (!NetUtils.isOnline(LineDetailActivity.this) && LineDetailActivity.this.line != null) {
                    items.add(new LineDetail(null, 0, null, null, "nointernet", 0, false));

                    subscriber.onNext(items);
                    subscriber.onCompleted();
                    return;
                }

                try {
                    RealtimeApi realtimeApi = RestClient.ADAPTER.create(RealtimeApi.class);
                    Response<RealtimeResponse> response = realtimeApi.line(line).execute();

                    if (response.body() != null) {
                        List<RealtimeBus> list = response.body().buses;

                        for (RealtimeBus bus : list) {
                            int lastStationID = bus.destination;

                            String lastStationName = BusStopRealmHelper.getName(lastStationID);

                            List<BusStop> path = Trips.getPath(LineDetailActivity.this, bus.trip);

                            if (path != null && !path.isEmpty()) {
                                String lastTime = path.get(path.size() - 1).getTime();

                                String stationName = BusStopRealmHelper.getName(bus.busStop);

                                items.add(new LineDetail(stationName, bus.delayMin, lastStationName,
                                        lastTime, null, bus.vehicle, busId == bus.trip));
                            } else {
                                items.add(new LineDetail(null, 0, null, null, "error", 0, false));
                                mErrorGeneral = true;
                            }
                        }
                    } else if (!mErrorGeneral) {
                        items.add(new LineDetail(null, 0, null, null, "error", 0, false));
                        mErrorGeneral = true;
                    }
                } catch (IOException e) {
                    if (!mErrorGeneral) {
                        items.add(new LineDetail(null, 0, null, null, "error", 0, false));
                        mErrorGeneral = true;
                    }
                }

                subscriber.onNext(items);
                subscriber.onCompleted();
            }
        });
    }

    private Func2<LineDetail, List<LineDetail>, List<LineDetail>> zipFunction() {
        return (lineDetail, lineDetails) -> {
            List<LineDetail> list = new ArrayList<>();
            list.add(lineDetail);
            list.addAll(lineDetails);

            return list;
        };
    }

    private void setFavoritesFabIcon(boolean favorite) {
        mFavoritesFab.setImageDrawable(ContextCompat.getDrawable(this,
                favorite ? R.drawable.ic_star_white_48dp : R.drawable.ic_star_border_white_48dp));
    }

    private static boolean contains(int v) {
        for (int e : Lines.notTracked) {
            if (e == v) {
                return true;
            }
        }

        return false;
    }

    private String getDateString(int date) {
        switch (date) {
            case 1:
                return getString(R.string.sunday_long);
            case 5:
                return getString(R.string.monday_long) + " - " + getString(R.string.friday_long);
            case 6:
                return getString(R.string.monday_long) + " - " + getString(R.string.saturday_long);
            case 7:
                return getString(R.string.monday_long) + " - " + getString(R.string.sunday_long);
            default:
                LogUtils.e(TAG, "Unknown day: " + date);
                return "Unknown";
        }
    }
}