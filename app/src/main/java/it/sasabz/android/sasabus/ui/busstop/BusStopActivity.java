package it.sasabz.android.sasabus.ui.busstop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import it.sasabz.android.sasabus.Config;
import it.sasabz.android.sasabus.R;
import it.sasabz.android.sasabus.beacon.BusStopBeacon;
import it.sasabz.android.sasabus.beacon.BusStopBeaconHandler;
import it.sasabz.android.sasabus.model.ClusterMarker;
import it.sasabz.android.sasabus.realm.BusStopRealmHelper;
import it.sasabz.android.sasabus.realm.busstop.BusStop;
import it.sasabz.android.sasabus.realm.user.FavoriteBusStop;
import it.sasabz.android.sasabus.ui.BaseActivity;
import it.sasabz.android.sasabus.ui.widget.NestedSwipeRefreshLayout;
import it.sasabz.android.sasabus.ui.widget.adapter.TabsAdapter;
import it.sasabz.android.sasabus.util.AnalyticsHelper;
import it.sasabz.android.sasabus.util.AnimUtils;
import it.sasabz.android.sasabus.util.DeviceUtils;
import it.sasabz.android.sasabus.util.SettingsUtils;
import it.sasabz.android.sasabus.util.Utils;
import it.sasabz.android.sasabus.util.recycler.BusStopListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Holds the three bus stop fragments in a viewpager. These fragments display all the bus stops in
 * form of a list or on a map. A third fragment allows the user to select certain bus stops and
 * add them to the favorites, allowing easy and fast access to them.
 *
 * @author Alex Lardschneider
 * @author David Dejori
 */
public class BusStopActivity extends BaseActivity {

    private static final String TAG = "BusStopActivity";

    static final int RESULT_DISPLAY_FAVORITES = 22341;
    public static final int INTENT_DISPLAY_FAVORITES = 22342;

    @BindView(R.id.viewpager) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout mTabLayout;
    @BindView(R.id.toolbar_search_text) EditText mSearchText;

    private TabsAdapter mAdapter;

    private FavoritesFragment mFavoritesFragment;
    private ListFragment mListFragment;
    private MapFragment mMapFragment;

    private static boolean isSearchBarShown;
    private static boolean isSearching;

    private final int[] tabIcons = {
            R.drawable.ic_star_white_48dp,
            R.drawable.ic_reorder_white_48dp,
            R.drawable.ic_map_white_48dp
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bus_stops);
        ButterKnife.bind(this);

        AnalyticsHelper.sendScreenView(TAG);

        mAdapter = new TabsAdapter(getSupportFragmentManager(), true);

        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));

        mSearchText = (EditText) findViewById(R.id.toolbar_search_text);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mListFragment != null) {
                    mListFragment.search(s.toString());
                }

                if (mMapFragment != null) {
                    mMapFragment.search(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mFavoritesFragment = (FavoritesFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, FavoritesFragment.class.getName());

            mListFragment = (ListFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, ListFragment.class.getName());

            mMapFragment = (MapFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, MapFragment.class.getName());
        }

        if (mFavoritesFragment == null) {
            mFavoritesFragment = new FavoritesFragment();
        }

        if (mListFragment == null) {
            mListFragment = new ListFragment();
        }

        if (mMapFragment == null) {
            mMapFragment = new MapFragment();
        }

        mAdapter.addFragment(mFavoritesFragment, getString(R.string.favorites));
        mAdapter.addFragment(mListFragment, getString(R.string.station_tab_list));
        mAdapter.addFragment(mMapFragment, getString(R.string.map));

        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            Drawable drawable = ContextCompat.getDrawable(this, tabIcons[i]);
            drawable = drawable.mutate();

            ColorStateList colours = ContextCompat.getColorStateList(this, R.color.selector_tab);

            DrawableCompat.setTintList(drawable, colours);

            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setIcon(drawable);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENT_DISPLAY_FAVORITES:
                if (resultCode == RESULT_DISPLAY_FAVORITES) {
                    mViewPager.post(() -> {
                        mViewPager.setCurrentItem(0);
                        mTabLayout.setupWithViewPager(mViewPager);
                    });
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public int getNavItem() {
        return NAVDRAWER_ITEM_BUS_STOPS;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, FavoritesFragment.class.getName(), mFavoritesFragment);
        getSupportFragmentManager().putFragment(outState, ListFragment.class.getName(), mListFragment);
        getSupportFragmentManager().putFragment(outState, MapFragment.class.getName(), mMapFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        invalidateFavorites();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_bus_stops, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        toggleSearchBar();
        return true;
    }


    /**
     * Reloads the favorite bus stops after the user added/removed some by either long clicking
     * a bus stop on the list or adding it via {@link BusStopDetailActivity}.
     */
    public void invalidateFavorites() {
        if (mFavoritesFragment != null) {
            mFavoritesFragment.parseData();
        }
    }

    private void showSearchBar(boolean show) {
        if (show && !isSearchBarShown) {
            mSearchText.setVisibility(View.VISIBLE);
            mSearchText.requestFocus();

            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(mSearchText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            mSearchText.setVisibility(View.GONE);

            View focus = getCurrentFocus();

            if (focus != null) {
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            }

            if (mListFragment != null) {
                mListFragment.search("");
            }

            if (mMapFragment != null) {
                mMapFragment.search("");
            }

            mSearchText.setText("");
        }

        isSearchBarShown = show;
    }

    private void toggleSearchBar() {
        showSearchBar(!isSearchBarShown);
    }


    /**
     * Holds all the favorite bus stops in a list.
     */
    public static class FavoritesFragment extends RxFragment {

        private RecyclerView mRecyclerView;
        private BusStopListAdapter mAdapter;

        private List<BusStop> mItems;

        final Realm userRealm = Realm.getDefaultInstance();
        final Realm busStopRealm = Realm.getInstance(BusStopRealmHelper.CONFIG);

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bus_stop_favorites, container, false);

            mItems = new ArrayList<>();
            mAdapter = new BusStopListAdapter(getActivity(), mItems);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            mRecyclerView.setHasFixedSize(true);

            if (DeviceUtils.isTablet(getActivity())) {
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            } else {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            mRecyclerView.setAdapter(mAdapter);

            new Handler().postDelayed(this::parseData, Config.BUS_STOP_FRAGMENTS_POST_DELAY);

            return view;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            userRealm.close();
            busStopRealm.close();
        }

        public void parseData() {
            if (getActivity() == null) return;

            mItems.clear();
            mAdapter.notifyDataSetChanged();

            userRealm.where(FavoriteBusStop.class).findAllAsync().asObservable()
                    .compose(bindToLifecycle())
                    .filter(RealmResults::isLoaded)
                    .map(favoriteBusStops -> {
                        List<BusStop> busStops = new ArrayList<>();

                        for (FavoriteBusStop busStop : favoriteBusStops) {
                            busStops.add(busStopRealm.where(BusStop.class).equalTo("family",
                                    busStop.getGroup()).findFirst());
                        }

                        return busStops;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<BusStop>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(List<BusStop> busStops) {
                            mItems.addAll(busStops);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    /**
     * Holds all bus stops in a list. Allows the user to search through the list by using
     * the header search bar.
     */
    public static class ListFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private BusStopListAdapter mAdapter;

        private NestedSwipeRefreshLayout mSwipeRefreshLayout;

        private List<BusStop> mSearchItems;
        private List<BusStop> mItems;

        private static int mBeaconCount;

        final Realm realm = Realm.getInstance(BusStopRealmHelper.CONFIG);

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_bus_stop_list, container, false);

            mItems = new ArrayList<>(900);
            mSearchItems = new ArrayList<>(900);

            mAdapter = new BusStopListAdapter(getActivity(), mSearchItems);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            mRecyclerView.setHasFixedSize(true);

            if (DeviceUtils.isTablet(getActivity())) {
                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

                mRecyclerView.setLayoutManager(layoutManager);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (mBeaconCount > 0 && position < mBeaconCount + 2 && !isSearching) {
                            return 2;
                        }

                        return 1;
                    }
                });
            } else {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            mRecyclerView.setAdapter(mAdapter);

            mSwipeRefreshLayout = (NestedSwipeRefreshLayout) view.findViewById(R.id.refresh);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));

            new Handler().postDelayed(this::parseData, Config.BUS_STOP_FRAGMENTS_POST_DELAY);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            realm.close();
        }

        public void parseData() {
            mItems.clear();

            if (Utils.isBeaconEnabled(getActivity())) {
                BusStopBeaconHandler handler = BusStopBeaconHandler.getInstance(getActivity());

                Collection<BusStopBeacon> beacons = new ArrayList<>(handler.getBeaconList());

                if (!beacons.isEmpty()) {
                    mItems.add(new BusStop(-1, null, null, 0, 0, 0));

                    for (BusStopBeacon beacon : beacons) {
                        mItems.add(realm.where(BusStop.class)
                                .equalTo("id", beacon.getId()).findFirst());

                        mBeaconCount++;
                    }

                    mItems.add(new BusStop(-2, null, null, 0, 0, 0));
                }
            }

            String locale = getResources().getConfiguration().locale.toString();
            String sort = locale.contains("de") ? "nameDe" : "nameIt";

            realm.where(BusStop.class).findAllSortedAsync(sort).asObservable()
                    .map((Func1<RealmResults<BusStop>, List<BusStop>>) busStops -> new ArrayList<>(
                            new LinkedHashSet<>(busStops)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stations -> {
                        mItems.addAll(stations);
                        mSearchItems.addAll(mItems);

                        mAdapter.notifyDataSetChanged();

                        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
                    });
        }

        private void search(String string) {
            string = string.toLowerCase().replace(" ", "");

            mSearchItems.clear();

            if (string.isEmpty()) {
                isSearching = false;
                mSearchItems.addAll(mItems);
            } else {
                for (BusStop station : mItems) {
                    if (station.getNameDe() == null && station.getMunicDe() == null) continue;

                    String nameDe = station.getNameDe().toLowerCase().replace(" ", "");
                    String municDe = station.getMunicDe().toLowerCase().replace(" ", "");

                    String nameIt = station.getNameIt().toLowerCase().replace(" ", "");
                    String municIt = station.getMunicIt().toLowerCase().replace(" ", "");

                    if (nameDe.contains(string) ||
                            municDe.contains(string) ||
                            nameIt.contains(string) ||
                            municIt.contains(string) ||
                            String.valueOf(station.getId()).contains(string)) {
                        mSearchItems.add(station);
                    }
                }

                isSearching = true;
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    public static class MapFragment extends RxFragment implements OnMapReadyCallback,
            ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>,
            ClusterManager.OnClusterItemClickListener<ClusterMarker> {

        private View view;

        private ClusterManager<ClusterMarker> mClusterManager;
        private ClusterMarker mClickedClusterItem;

        private ArrayList<BusStop> mItems = new ArrayList<>();
        private ArrayList<ClusterMarker> mClusterItems = new ArrayList<>();

        private Bundle mSavedInstanceState;

        private GoogleMap mGoogleMap;

        static final String BUNDLE_CLUSTER_LIST = "BUNDLE_CLUSTER_LIST";

        final Realm realm = Realm.getInstance(BusStopRealmHelper.CONFIG);

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mSavedInstanceState = savedInstanceState;

            if (view != null) {
                ViewManager parent = (ViewManager) view.getParent();

                if (parent != null) parent.removeView(view);
            }

            try {
                view = inflater.inflate(R.layout.fragment_bus_stop_map, container, false);
            } catch (InflateException e) {
                Utils.handleException(e);
            }

            mItems = new ArrayList<>();

            return view;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mSavedInstanceState = savedInstanceState;

            new Handler().postDelayed(() -> {
                int mGplayStatus = MapsInitializer.initialize(getActivity());

                if (mGplayStatus == 0) {
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                            .findFragmentById(R.id.googlemap);

                    if (mapFragment != null) {
                        mapFragment.getMapAsync(this);
                    }
                }
            }, 250);
        }

        @Override
        public void onMapReady(GoogleMap map) {
            mGoogleMap = map;

            mClusterManager = new ClusterManager<>(getActivity(), map);
            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
            mClusterManager.setOnClusterItemClickListener(this);

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

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }

            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            map.setOnCameraChangeListener(mClusterManager);
            map.setOnMarkerClickListener(mClusterManager);
            map.setInfoWindowAdapter(mClusterManager.getMarkerManager());
            map.setOnInfoWindowClickListener(mClusterManager);

            if (mSavedInstanceState != null) {
                mClusterItems = mSavedInstanceState.getParcelableArrayList(BUNDLE_CLUSTER_LIST);

                if (mClusterItems != null) {
                    mClusterManager.addItems(mClusterItems);

                    mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ItemAdapter());
                    mClusterManager.cluster();
                }
            } else {
                new Handler().postDelayed(this::parseData, Config.BUS_STOP_FRAGMENTS_POST_DELAY);
            }
        }

        @Override
        public boolean onClusterItemClick(ClusterMarker item) {
            mClickedClusterItem = item;
            return false;
        }

        @Override
        public void onClusterItemInfoWindowClick(ClusterMarker item) {
            Intent intent = new Intent(getActivity(), BusStopDetailActivity.class);
            intent.putExtra(Config.EXTRA_STATION_ID,
                    Integer.parseInt(item.getSnippet().split(":")[1]));
            getActivity().startActivityForResult(intent, INTENT_DISPLAY_FAVORITES);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putParcelableArrayList(BUNDLE_CLUSTER_LIST, mClusterItems);

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }

            if (mGoogleMap != null) {
                outState.putDouble("CAMERA_LAT", mGoogleMap.getCameraPosition().target.latitude);
                outState.putDouble("CAMERA_LNG", mGoogleMap.getCameraPosition().target.longitude);
                outState.putFloat("CAMERA_ZOOM", mGoogleMap.getCameraPosition().zoom);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            realm.close();
        }

        private void parseData() {
            realm.where(BusStop.class).findAllAsync().asObservable()
                    .compose(bindToLifecycle())
                    .filter(RealmResults::isLoaded)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(stations -> {
                        mItems.addAll(stations);

                        for (BusStop station : stations) {
                            if (station.getNameDe() == null && station.getMunicDe() == null)
                                continue;

                            ClusterMarker tempItem = new ClusterMarker(station.getLat(), station.getLng(),
                                    station.getName(getActivity()), station.getMunic(getActivity()) + ":" + station.getId());

                            mClusterItems.add(tempItem);
                            mClusterManager.addItem(tempItem);
                        }

                        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ItemAdapter());
                        mClusterManager.cluster();
                    });
        }

        private void search(String string) {
            string = string.toLowerCase().replace(" ", "");

            mClusterItems.clear();
            mClusterManager.clearItems();

            if (string.isEmpty()) {
                isSearching = false;
                parseData();
            } else {
                for (BusStop station : mItems) {
                    if (station.getNameDe() == null && station.getMunicDe() == null) continue;

                    String nameDe = station.getNameDe().toLowerCase().replace(" ", "");
                    String municDe = station.getMunicDe().toLowerCase().replace(" ", "");

                    String nameIt = station.getNameIt().toLowerCase().replace(" ", "");
                    String municIt = station.getMunicIt().toLowerCase().replace(" ", "");

                    if (nameDe.contains(string) ||
                            municDe.contains(string) ||
                            nameIt.contains(string) ||
                            municIt.contains(string) ||
                            String.valueOf(station.getId()).contains(string)) {
                        ClusterMarker tempItem = new ClusterMarker(station.getLat(), station.getLng(),
                                station.getName(getActivity()), station.getMunic(getActivity()) + ":" + station.getId());

                        mClusterItems.add(tempItem);
                        mClusterManager.addItem(tempItem);
                    }
                }

                mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new ItemAdapter());
                mClusterManager.cluster();

                isSearching = true;
            }
        }

        public class ItemAdapter implements GoogleMap.InfoWindowAdapter {

            private final View view;

            ItemAdapter() {
                view = getActivity().getLayoutInflater().inflate(R.layout.include_bus_stop_infowindow, null);
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
}