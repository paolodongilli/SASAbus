package it.sasabz.sasabus.ui.news;

import it.sasabz.sasabus.data.models.News;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * This is a helper class that implements the management of tabs and all
 * details of connecting a ViewPager with associated {@link PagerSlidingTabStrip}.
 */
public class NewsPagerAdapter extends FragmentStatePagerAdapter {
	private final SherlockFragmentActivity mActivity;
	private final ViewPager mViewPager;
	private final FragmentManager mFragmentManager;
	private final List<TabInfo> mTabs = new ArrayList<TabInfo>();
	
	static final class TabInfo {
		private final Bundle args;
		private final String title;
		private List<News> news;
		
		public TabInfo(Bundle args, String title, List<News> news) {
			this.args = args;
			this.title = title;
			this.news = news;
		}
	}
	
	public NewsPagerAdapter(ViewPager viewPager, FragmentManager fragmentManager, SherlockFragmentActivity activity) {
		super(fragmentManager);
		mViewPager = viewPager;
		mFragmentManager = fragmentManager;
		mActivity = activity;
	}
	
	public void addTab(Bundle args, String title, List<News> news) {
		TabInfo info = new TabInfo(args, title, news);
		mTabs.add(info);
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
//		return super.getPageTitle(position);
		TabInfo info = mTabs.get(position);
		return info.title;
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		CityNewsFragment newCityNewsFragment = CityNewsFragment.newInstance(position);
		if (info.news != null) {
			newCityNewsFragment.setListAdapter(info.news, mActivity);
		}
		
		return newCityNewsFragment;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	

	@Override
	public int getCount() {
		return mTabs.size();
	}
	
	public void setData(List<List<News>> news) {
		for (int i = 0; i < mTabs.size(); i++) {
			TabInfo info = mTabs.get(i);
			info.news = news.get(i);
		}
		notifyDataSetChanged();
	}

}