package it.sasabz.android.sasabus.ui.ecopoints;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.network.NetUtils;
import it.sasabz.android.sasabus.network.auth.AuthHelper;
import it.sasabz.android.sasabus.network.rest.RestClient;
import it.sasabz.android.sasabus.network.rest.api.EcoPointsApi;
import it.sasabz.android.sasabus.network.rest.model.Badge;
import it.sasabz.android.sasabus.network.rest.response.BadgesResponse;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.LogUtils;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.BadgeAdapter;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EcoPointsBadgesActivity extends AppCompatActivity {

    private static final String TAG = "EcoPointsBadgesActivity";

    @BindView(R.id.recycler) RecyclerView recyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.error_general) RelativeLayout errorGeneral;
    @BindView(R.id.error_wifi) RelativeLayout errorWifi;

    private ArrayList<Badge> mItems;
    private BadgeAdapter mAdapter;

    private BroadcastReceiver logoutReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AuthHelper.isTokenValid()) {
            LogUtils.e(TAG, "Token is null, showing login activity");
            finish();
            startActivity(new Intent(this, LoginActivity.class));

            return;
        }

        logoutReceiver = AuthHelper.registerLogoutReceiver(this);

        setContentView(R.layout.activity_eco_points_badges);
        ButterKnife.bind(this);

        AnalyticsHelper.sendScreenView(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_amber, R.color.primary_red,
                R.color.primary_green, R.color.primary_indigo);
        mSwipeRefreshLayout.setOnRefreshListener(this::parseData);

        mItems = new ArrayList<>();
        mAdapter = new BadgeAdapter(this, mItems);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });

        parseData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AuthHelper.unregisterLogoutReceiver(this, logoutReceiver);
    }

    private void parseData() {
        if (!NetUtils.isOnline(this)) {
            errorWifi.setVisibility(View.VISIBLE);
            errorGeneral.setVisibility(View.GONE);

            mItems.clear();
            mAdapter.notifyDataSetChanged();

            mSwipeRefreshLayout.setRefreshing(false);

            return;
        }

        mSwipeRefreshLayout.setRefreshing(true);

        EcoPointsApi ecoPointsApi = RestClient.ADAPTER.create(EcoPointsApi.class);
        ecoPointsApi.getAllBadges()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BadgesResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.handleException(e);

                        AuthHelper.checkIfUnauthorized(EcoPointsBadgesActivity.this, e);

                        mItems.clear();
                        mAdapter.notifyDataSetChanged();

                        errorGeneral.setVisibility(View.VISIBLE);
                        errorWifi.setVisibility(View.GONE);

                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(BadgesResponse badgesResponse) {
                        mItems.clear();
                        mItems.addAll(badgesResponse.badges);

                        mAdapter.notifyDataSetChanged();

                        errorGeneral.setVisibility(View.GONE);
                        errorWifi.setVisibility(View.GONE);

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
