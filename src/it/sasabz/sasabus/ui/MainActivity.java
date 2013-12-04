/**
 *
 * TabHostActivity.java
 *
 * Created: Mar 15, 2012 22:40:06 PM
 *
 * Copyright (C) 2012 Paolo Dongilli and Markus Windegger
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus. If not, see <http://www.gnu.org/licenses/>.
 *
 * This activity provides a map an the possibility to show a list of
 * bus stops which were contained in a "journey" (from - to)
 *
 * This activity is the "main" activity of this application. It holds the tabhostwidget, which
 * controls the activities added to the tabs (like online-offlineActivity etc 
 *
 */
package it.sasabz.sasabus.ui;

import android.R.anim;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.logic.Utility;
import it.sasabz.sasabus.ui.about.AboutActivity;
import it.sasabz.sasabus.ui.busschedules.BusSchedulesFragment;
import it.sasabz.sasabus.ui.busstop.NextBusFragment;
import it.sasabz.sasabus.ui.dialogs.About;
import it.sasabz.sasabus.ui.dialogs.Credits;
import it.sasabz.sasabus.ui.map.MapViewActivity;
import it.sasabz.sasabus.ui.news.NewsFragment;
import it.sasabz.sasabus.ui.news.InfoFragment;
import it.sasabz.sasabus.ui.preferences.PreferencesActivity;
import it.sasabz.sasabus.ui.routing.SearchFragment;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
/**
 *Main Activity that holds all the tabs
 */
public class MainActivity extends SherlockFragmentActivity {

	private ActionBar mActionBar;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	
	private String[] mNavigationTitles;
	private TypedArray mNavigationIcons;
	private String[] mFragments;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActionBar = getSupportActionBar();
		
		addNavigationDrawer(savedInstanceState);
		
		showFragment(0);
	}
	

	/** Add the navigation drawer on the left side of the screen */
	private void addNavigationDrawer(Bundle savedInstanceState) {
		setContentView(R.layout.navigation_drawer);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.naviagion_drawer);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		// Get Resources for the list in the drawer
		mNavigationTitles = getResources().getStringArray(R.array.navigation_drawer_entries);
		mNavigationIcons = getResources().obtainTypedArray(R.array.navigation_drawer_icons);
		mFragments = getResources().getStringArray(R.array.navigation_drawer_fragments);
		
		// Populate list items
		List<DrawerItem> drawerItems = new ArrayList<MainActivity.DrawerItem>();
		for (int i = 0; i < mNavigationTitles.length; i++){
			drawerItems.add(new DrawerItem(mNavigationTitles[i], mNavigationIcons.getDrawable(i), mFragments[i]));
		}
		mNavigationIcons.recycle();
		
		// Add adater to navigation drawer
		Adapter drawerAdapter = (Adapter) new NavigationDrawerAdapter(this, R.layout.drawer_layout_item, drawerItems);
		mDrawerList.setAdapter((ListAdapter) drawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		// Add toggle to actionbar
		mDrawerToggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(R.string.app_name);
				supportInvalidateOptionsMenu();
			}
		};
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		
		mDrawerList.setItemChecked(0, true);
	
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//Sync the toggle state after onRestoreInstanceState has occurred
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	class DrawerItem {
		String navigationTitle;
		Drawable navigationIcon;
		String associatedFragment;
		
		public DrawerItem(String navigationTitle, Drawable navigationIcon, String associatedFragment) {
			this.navigationTitle = navigationTitle;
			this.navigationIcon = navigationIcon;
			this.associatedFragment = associatedFragment;
		}
	}
	
	private class DrawerItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}
	
	
	private int mPosition = 0;
		
	/** Swaps fragments in the main content view */
	private void selectItem(final int position) {

		mDrawerLayout.closeDrawer(mDrawerList);
		mDrawerList.setItemChecked(position, true);
	
		if (position != mPosition) {
//			mDrawerLayout.setDrawerListener(new DrawerListener() {
//				@Override public void onDrawerStateChanged(int arg0) { }
//				@Override public void onDrawerSlide(View arg0, float arg1) { }
//				@Override public void onDrawerOpened(View arg0) { }
//	
//				@Override
//				public void onDrawerClosed(View arg0) {
//					// Show the actual fragment only now to prevent lag
					mPosition = position;
					showFragment(position);
//					mDrawerLayout.setDrawerListener(mDrawerToggle);
//				}
//			});
		}
		
	}
	
	private void showFragment(final int position) {
		
		SherlockFragment fragmentToShow = (SherlockFragment) SherlockFragment
				.instantiate(MainActivity.this, mFragments[position]);
		 
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
			.beginTransaction()
			.replace(R.id.content_frame, fragmentToShow)
			.commit();
		
		mTitle = mNavigationTitles[position];
		
		getSupportActionBar().setTitle(mTitle);
	}
		
	
	/* Called whenever invalidateOptionsMenu is called */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view 
		// and show only generic ones
		if (isDrawerOpen()) {
			menu.clear();
			getSupportMenuInflater().inflate(R.menu.main, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		case R.id.menu_settings:
			Intent intentPreferences = new Intent(this, PreferencesActivity.class);
			startActivity(intentPreferences);
			return true;
		case R.id.menu_about:
			Intent intentAbout = new Intent(this, AboutActivity.class);
			startActivity(intentAbout);
			return true;
		default:
			return false;
		}
	}
	
	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mDrawerList);
	}
	
}