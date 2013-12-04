package it.sasabz.sasabus.ui.news;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.models.News;
import it.sasabz.sasabus.logic.DownloadNews;
import it.sasabz.sasabus.ui.MainActivity;
import it.sasabz.sasabus.ui.Utility;

import java.util.ArrayList;
import java.util.List;

import android.R.anim;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

/**
 * Display general information about changes of routes ecc.
 */
public class NewsFragment extends SherlockFragment {

	private ViewPager mViewPager;
	private NewsPagerAdapter mPagerAdapter;
	
	private MenuItem mOptionsMenuitemRefresh;
	
	private String[] mCities;
	
	private boolean loaded = false, loading = false;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_news, container, false);
		
		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		initializeViews(view);
		
		addTabs(view);
		
		getInfosFromCache();
		
		setHasOptionsMenu(true);
		
		return view;
	}
	
	
	private void initializeViews(View view) {
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		
		mCities = getResources().getStringArray(R.array.cities);
	}
	
	private void addTabs(View view) {
		 // Instantiate the PagerAdapter
		 mPagerAdapter = new NewsPagerAdapter(mViewPager, getChildFragmentManager(), getSherlockActivity());
		 
		 // Add Tabs to the Adapter
		 mPagerAdapter.addTab(null, mCities[0], null);
		 mPagerAdapter.addTab(null, mCities[1], null);
		 
		 mViewPager.setAdapter(mPagerAdapter);
		 
		 // Bind the widget to the adapter
		 PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		 tabs.setViewPager(mViewPager);
		 
		 //Set the color for the Tab strips
		 tabs.setIndicatorColorResource(R.color.orange_pressed);
		 tabs.setBackgroundResource(R.drawable.ab_solid_sasabus);
		 tabs.setTextColorResource(android.R.color.white);
		 tabs.setTabBackground(R.drawable.background_tab);
	}
	
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MainActivity parentActivity = (MainActivity) getSherlockActivity();
		boolean drawerIsOpen = parentActivity.isDrawerOpen();
		//If the drawer is closed, show the menu related to the content
		if (!drawerIsOpen) {
			menu.clear();
			parentActivity.getSupportMenuInflater().inflate(R.menu.news_fragment, menu);
			mOptionsMenuitemRefresh = menu.findItem(R.id.menu_refresh);
			setRefreshActionButtonState();
			if (!loaded) {
				loadInfos();
			}
		}
		super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			loadInfos();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setRefreshActionButtonState() {
		if(loading) {
			mOptionsMenuitemRefresh.setActionView(R.layout.actionbar_indeterminate_progress);
		} else {
			mOptionsMenuitemRefresh.setActionView(null);
		}
	}
	
	
	public interface NewsCallback {
		void newsDownloaded(List<News> infos);
	}
	
	
	private void getInfosFromCache() {
		// TODO get the infos from cache and display them 
		// meanwhile new infos are being downloaded and 
		// eventually the view will get refreshed
		addAdapterToListViews(DownloadNews.getInfosFromCache());
	}
	
	private void loadInfos() {
		setLoading(true);
		DownloadNews.downloadInfos(this, new NewsCallback() {
			@Override
			public void newsDownloaded(List<News> infos) {
				setLoading(false);
				Log.i("infos", ""+infos);
				if (isAdded()) {
					if (infos != null) {
						addAdapterToListViews(infos);
					} else {
						Utility.showNetworkErrorDialog(NewsFragment.this);
					}
				}
				
			}
		});
	}
	
	private void addAdapterToListViews(List<News> infos) {
		List<List<News>> cityInfos = new ArrayList<List<News>>();
		cityInfos.add(DownloadNews.getInfosForArea(infos, DownloadNews.BOLZANO));
		cityInfos.add(DownloadNews.getInfosForArea(infos, DownloadNews.MERANO));
//		for (int i = 1; i <= mCities.length; i++) {
//			cityInfos.add(DownloadNews.getInfosForArea(infos, i));
//		}
		mPagerAdapter.setData(cityInfos);
	}
	
	private void setLoading(boolean loading) {
		this.loading = loading;
		if (!loading) {
			loaded = true;
		}
		setRefreshActionButtonState();
	}
	
}