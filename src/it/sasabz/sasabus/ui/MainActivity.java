/*
 * SASAbus - Android app for SASA bus open data
 *
 * MainActivity.java
 *
 * Created: Mar 15, 2012 22:40:06 PM
 *
 * Copyright (C) 2011-2014 Paolo Dongilli, Markus Windegger, Davide Montesin
 *
 * This file is part of SASAbus.
 *
 * SASAbus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SASAbus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SASAbus.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.sasabz.sasabus.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import bz.davide.dmxmljson.json.HTTPAsyncJSONDownloader;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.data.AndroidOpenDataLocalStorage;
import it.sasabz.sasabus.opendata.client.RemoteVersionDateReady;
import it.sasabz.sasabus.opendata.client.SASAbusOpenDataDownloadCallback;
import it.sasabz.sasabus.opendata.client.model.BusStation;
import it.sasabz.sasabus.ui.survey.SurveyActivity;

/**
 * Main Activity that holds all the tabs
 */
public class MainActivity extends AbstractSasaActivity {

	final String OSM_ZIP_NAME = "osm-tiles.zip";

	private static final String HOME_FRAGMENT = "HOME_FRAGMENT";

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private String[] mNavigationTitles;
	private TypedArray mNavigationIcons;
	private String[] mFragments;

	AlertDialog firstTimeDialog;

	AndroidOpenDataLocalStorage opendataStorage;

	final static String FORCE_UPDATE_FOREGROUND = "FORCE_UPDATE_FOREGROUND";

	Thread pregps;

	MainLocationManager mainLocationManager;

	private CharSequence mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);
			this.addNavigationDrawer(savedInstanceState);
			this.opendataStorage = new AndroidOpenDataLocalStorage(this);
			this.mainLocationManager = new MainLocationManager(this);
			this.checkFirstTime();
		} catch (Exception ioxxx) {
			this.handleApplicationException(ioxxx);
			throw new RuntimeException(ioxxx);
		}
	}

	public MainLocationManager getMainLocationManager() {
		return this.mainLocationManager;
	}

	private void checkFirstTime() {
		try {
			final String versionDate = this.opendataStorage.getVersionDateIfExists();

			Intent intent = this.getIntent();
			final boolean forceUpdate = intent != null && intent.getExtras() != null
					&& intent.getExtras().getBoolean(FORCE_UPDATE_FOREGROUND, false);

			if (forceUpdate || versionDate == null) {
				// Sync update required, the app is empty!
				AlertDialog.Builder firstTimeDialogBuilder = new AlertDialog.Builder(this);

				if (versionDate == null) {
					firstTimeDialogBuilder.setTitle(this.getString(R.string.first_time_opendata_dialog_title));
					firstTimeDialogBuilder.setMessage(this.getString(R.string.first_time_opendata_dialog_message));
				} else {
					firstTimeDialogBuilder.setTitle(this.getString(R.string.update_opendata_dialog_title));
					firstTimeDialogBuilder.setMessage(this.getString(R.string.update_opendata_dialog_message));
				}
				firstTimeDialogBuilder.setCancelable(false);
				OnClickListener yes = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.downloadSASAbusOpenDataToLocalStore();
					}
				};
				firstTimeDialogBuilder.setPositiveButton(this.getString(R.string.first_time_opendata_dialog_yes), yes);
				OnClickListener no = new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (versionDate == null) {
							MainActivity.this.finish(); // Close the app: no
														// data can be used!
						} else {
							try {
								if (forceUpdate) {
									MainActivity.this.notifyUserForUpdate();
								}
								MainActivity.this.checkMapFirstTime();
							} catch (IOException e) {
								MainActivity.this.handleApplicationException(e);
							}
						}
					}
				};
				firstTimeDialogBuilder.setNegativeButton(this.getString(R.string.first_time_opendata_dialog_no), no);
				this.firstTimeDialog = firstTimeDialogBuilder.create();
				this.firstTimeDialog.show();

			} else {
				this.checkMapFirstTime();
			}
		} catch (IOException ioxxx) {
			this.handleApplicationException(ioxxx);
		}
	}

	private void notifyUserForUpdate() {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.icon);
		mBuilder.setContentTitle(this.getString(R.string.update_opendata_dialog_title));
		mBuilder.setContentText(this.getString(R.string.update_opendata_notification_message));
		mBuilder.setAutoCancel(true);

		Intent resultIntent = new Intent(this, MainActivity.class);
		resultIntent.putExtra(FORCE_UPDATE_FOREGROUND, true);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(0, notification);

	}

	void downloadSASAbusOpenDataToLocalStore() {
		try {
			final ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle(this.getString(R.string.download_opendata_map_progress_dialog_title));
			progressDialog.setMessage("");
			progressDialog.show();

			SASAbusOpenDataDownloadCallback downloadCallback = new SASAbusOpenDataDownloadCallback() {
				@Override
				public void progress(final int n, final int total, final String resourceName) {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressDialog.setMax(total);
							progressDialog.setProgress(n);
							progressDialog.setMessage("Data: " + resourceName);
						}
					});
				}

				@Override
				public void complete() {
					MainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								progressDialog.dismiss();
								MainActivity.this.checkMapFirstTime();
							} catch (IOException e) {
								MainActivity.this.handleApplicationException(e);
							}

						}
					});

				}

				@Override
				public void exception(IOException ioxxx) {
					MainActivity.this.handleApplicationException(ioxxx);
				}
			};

			this.opendataStorage.asyncDownloadSASAbusOpenDataToLocalStore(this.getString(R.string.opendata_server_url),
					new HTTPAsyncJSONDownloader(), downloadCallback);

		} catch (IOException e) {
			this.handleApplicationException(e);
		}
	}

	private void checkMapFirstTime() throws IOException {
		this.opendataStorage.preloadData();

		File mapTilesRootFolder = MainActivity.this.opendataStorage.getMapTilesRootFolder();

		if (mapTilesRootFolder.listFiles().length == 0) // Map don't already
														// downloaded!
		{
			// Sync update required, the app is empty!
			AlertDialog.Builder firstTimeDialogBuilder = new AlertDialog.Builder(this);
			firstTimeDialogBuilder.setTitle(this.getString(R.string.first_time_map_dialog_title));
			firstTimeDialogBuilder.setMessage(this.getString(R.string.first_time_map_dialog_message));
			firstTimeDialogBuilder.setCancelable(false);
			OnClickListener yes = new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.downloadOSMTiles();
					MainActivity.this.initUI();
				}
			};
			firstTimeDialogBuilder.setPositiveButton(this.getString(R.string.first_time_opendata_dialog_yes), yes);
			OnClickListener no = new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.initUI();
				}
			};
			firstTimeDialogBuilder.setNegativeButton(this.getString(R.string.first_time_opendata_dialog_no), no);
			this.firstTimeDialog = firstTimeDialogBuilder.create();
			this.firstTimeDialog.show();
		} else {
			MainActivity.this.initUI();
		}
	}

	@SuppressLint("NewApi")
	private void downloadOSMTiles() {

		final String downloadzip = MainActivity.this.getString(R.string.maptiles_server_url) + "/" + this.OSM_ZIP_NAME;
		final File destination = new File(MainActivity.this.opendataStorage.getMapTilesRootFolder(), this.OSM_ZIP_NAME);

		destination.getParentFile().mkdirs();

		if (android.os.Build.VERSION.SDK_INT >= 9) // DownloadManager requires
													// at least api 9
		{
			DownloadManager dm = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
			Uri downloadzipUri = Uri.parse(downloadzip);
			Request request = new Request(downloadzipUri);
			request.setDestinationUri(Uri.fromFile(destination));
			long downloadId = dm.enqueue(request);

			this.registerReceiver(new OSMZipDownloadComplete(this, downloadId, destination),
					new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
						mBuilder.setSmallIcon(R.drawable.icon);
						mBuilder.setContentTitle("SASAbus download map");
						mBuilder.setAutoCancel(true);
						PendingIntent pintent = PendingIntent.getActivity(MainActivity.this.getApplicationContext(), 0,
								new Intent(), 0);
						mBuilder.setContentIntent(pintent);

						NotificationManager mNotificationManager = (NotificationManager) MainActivity.this
								.getSystemService(Context.NOTIFICATION_SERVICE);

						int len;
						byte[] buf = new byte[100000];

						URL osmUrl = new URL(downloadzip);

						InputStream in = osmUrl.openStream();
						FileOutputStream out = new FileOutputStream(destination);
						int lastNotificationBytes = 0;
						int countBytes = 0;
						while ((len = in.read(buf)) > 0) {
							if (lastNotificationBytes < countBytes - 400000) {
								mBuilder.setContentText(String.format("%.1f MBytes", countBytes / (1024d * 1024d)));
								Notification notification = mBuilder.build();
								notification.flags |= Notification.FLAG_AUTO_CANCEL;
								mNotificationManager.notify(1, notification);
								lastNotificationBytes = countBytes;
							}

							out.write(buf, 0, len);
							countBytes += len;

						}
						out.close();
						in.close();

						mBuilder.setContentText("Extracting ...");
						Notification notification = mBuilder.build();
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						mNotificationManager.notify(1, notification);

						OSMZipDownloadComplete.extractZipContent(destination);

						mBuilder.setContentText("Complete!");
						notification = mBuilder.build();
						notification.flags |= Notification.FLAG_AUTO_CANCEL;
						mNotificationManager.notify(1, notification);

					} catch (IOException ioxxx) {
						MainActivity.this.handleApplicationException(ioxxx);
					}
				}
			}).start();
		}

	}

	public void handleApplicationException(final Exception exxx) {
		exxx.printStackTrace();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder exceptionDialogBuilder = new AlertDialog.Builder(MainActivity.this);
				exceptionDialogBuilder.setTitle("Exception: " + exxx.getClass().getName());
				exceptionDialogBuilder.setMessage(exxx.getMessage());
				exceptionDialogBuilder.setCancelable(false);
				exceptionDialogBuilder.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// MainActivity.this.finish();
					}
				});
				exceptionDialogBuilder.create().show();
			}
		});

	}

	void initUI() {
		this.showFragment(0, true);
		this.mDrawerLayout.openDrawer(this.mDrawerList);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					// Nothing to do
				}
				MainActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainActivity.this.mDrawerLayout.closeDrawer(MainActivity.this.mDrawerList);
					}
				});
			}
		}).start();
		try {
			this.opendataStorage.asyncReadRemoteVersionDate(this.getString(R.string.opendata_server_url),
					new HTTPAsyncJSONDownloader(), new RemoteVersionDateReady() {

						@Override
						public void ready(String remoteDate) {
							try {
								String versionDate = MainActivity.this.opendataStorage.getVersionDateIfExists();
								if (!versionDate.equals(remoteDate)) {
									MainActivity.this.notifyUserForUpdate();
								}
							} catch (IOException x) {
								// don't report to user the problem, it is a
								// background check.
							}
						}

						@Override
						public void exception(IOException ioxxx) {
							// don't report to user the problem, it is a
							// background check.
						}
					});
		} catch (IOException ioException) {
			// don't report to user the problem, it is a background check.
		}

	}

	public AndroidOpenDataLocalStorage getOpenDataStorage() {
		return this.opendataStorage;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.mainLocationManager.resume();

		final LocationListener locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
			}
		};
		this.mainLocationManager.requestLocationUpdates(locationListener);
		this.pregps = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(20 * 1000);
				} catch (InterruptedException e) {
					// We interrupt to remove onPause
				}
				MainActivity.this.mainLocationManager.removeUpdates(locationListener);
			};
		};
		this.pregps.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		this.mainLocationManager.pause();

		this.pregps.interrupt(); // does nothing when thread is dead
		try {
			this.pregps.join();
		} catch (InterruptedException e) {
			// Nothing to do!
		}
		this.pregps = null;
	}

	/** Add the navigation drawer on the left side of the screen */
	private void addNavigationDrawer(Bundle savedInstanceState) {
		this.setContentView(R.layout.navigation_drawer);

		this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.naviagion_drawer);
		this.mDrawerList = (ListView) this.findViewById(R.id.left_drawer);

		// Get Resources for the list in the drawer
		this.mNavigationTitles = this.getResources().getStringArray(R.array.navigation_drawer_entries);
		this.mNavigationIcons = this.getResources().obtainTypedArray(R.array.navigation_drawer_icons);
		this.mFragments = this.getResources().getStringArray(R.array.navigation_drawer_fragments);

		// Populate list items
		List<DrawerItem> drawerItems = new ArrayList<MainActivity.DrawerItem>();
		for (int i = 0; i < this.mNavigationTitles.length; i++) {
			drawerItems.add(new DrawerItem(this.mNavigationTitles[i], this.mNavigationIcons.getDrawable(i),
					this.mFragments[i]));
		}
		this.mNavigationIcons.recycle();

		// Add adater to navigation drawer
		Adapter drawerAdapter = new NavigationDrawerAdapter(this, R.layout.drawer_layout_item, drawerItems);
		this.mDrawerList.setAdapter((ListAdapter) drawerAdapter);
		this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Add toggle to actionbar
		this.mDrawerToggle = new ActionBarDrawerToggle(this, this.mDrawerLayout, R.drawable.ic_drawer,
				R.string.drawer_open_close, R.string.drawer_open_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				MainActivity.this.getSupportActionBar().setTitle(MainActivity.this.mTitle);
				MainActivity.this.supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				MainActivity.this.getSupportActionBar().setTitle(R.string.app_name);
				MainActivity.this.supportInvalidateOptionsMenu();
			}
		};
		// Set the drawer toggle as the DrawerListener
		this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		this.getSupportActionBar().setHomeButtonEnabled(true);

		this.mDrawerList.setItemChecked(0, true);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred
		this.mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.mDrawerToggle.onConfigurationChanged(newConfig);
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
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			MainActivity.this.selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(final int position) {
		/*
		 * if (position == 5) {
		 * this.mDrawerLayout.closeDrawer(this.mDrawerList);
		 * this.notifyUserForUpdate(); return; }
		 */

		if (position == 8) {
			Intent intent = new Intent(this, SurveyActivity.class);
			startActivity(intent);
		} else {

			this.mDrawerLayout.closeDrawer(this.mDrawerList);
			this.mDrawerList.setItemChecked(position, true);

			// if (position != mPosition)
			{
				// mDrawerLayout.setDrawerListener(new DrawerListener() {
				// @Override public void onDrawerStateChanged(int arg0) { }
				// @Override public void onDrawerSlide(View arg0, float arg1) {
				// }
				// @Override public void onDrawerOpened(View arg0) { }
				//
				// @Override
				// public void onDrawerClosed(View arg0) {
				// // Show the actual fragment only now to prevent lag
				// this.mPosition = position;
				this.showFragment(position, false);
				// mDrawerLayout.setDrawerListener(mDrawerToggle);
				// }
				// });
			}
		}
	}

	private void showFragment(final int position, boolean firstTime) {

		SherlockFragment fragmentToShow = (SherlockFragment) SherlockFragment.instantiate(MainActivity.this,
				this.mFragments[position]);

		FragmentManager fragmentManager = this.getSupportFragmentManager();
		if (firstTime) {
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragmentToShow).commit();
		} else {
			fragmentManager.popBackStack(HOME_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			fragmentManager.beginTransaction().add(R.id.content_frame, fragmentToShow).addToBackStack(HOME_FRAGMENT)
					.commit();
		}

		this.mTitle = this.mNavigationTitles[position];

		this.getSupportActionBar().setTitle(this.mTitle);
	}

	/* Called whenever invalidateOptionsMenu is called */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the drawer is open, hide action items related to the content view
		// and show only generic ones
		if (this.isDrawerOpen()) {
			menu.clear();
			this.getSupportMenuInflater().inflate(R.menu.main, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
				this.mDrawerLayout.closeDrawer(this.mDrawerList);
			} else {
				this.mDrawerLayout.openDrawer(this.mDrawerList);
			}
			return true;
		/*
		 * case R.id.menu_settings: Intent intentPreferences = new Intent(this,
		 * PreferencesActivity.class); startActivity(intentPreferences); return
		 * true; case R.id.menu_about: Intent intentAbout = new Intent(this,
		 * AboutActivity.class); startActivity(intentAbout); return true;
		 */
		default:
			return false;
		}

	}

	public boolean isDrawerOpen() {
		return this.mDrawerLayout.isDrawerOpen(this.mDrawerList);
	}

	public String getBusStationNameUsingAppLanguage(BusStation busStation) {
		if (this.getString(R.string.bus_station_name_language).equals("de")) {
			return busStation.findName_de();
		} else {
			return busStation.findName_it();
		}
	}

	public static String getHomeFragment() {
		return MainActivity.HOME_FRAGMENT;
	}
}