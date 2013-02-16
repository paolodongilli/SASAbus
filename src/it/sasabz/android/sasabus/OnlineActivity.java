package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.classes.dialogs.About;
import it.sasabz.android.sasabus.classes.dialogs.Credits;
import it.sasabz.android.sasabus.fragments.OnlineSearchFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OnlineActivity extends FragmentActivity{
	
	
	@Override
	protected void onCreate(Bundle savedInstaceState)
	{
		super.onCreate(savedInstaceState);
		setContentView(R.layout.fragment_container);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		Fragment fragment;
		fragment = fragmentManager.findFragmentById(R.id.onlinefragment);
		if(fragment != null)
		{
			ft.remove(fragment);
		}
		fragment = new OnlineSearchFragment();
		ft.add(R.id.onlinefragment, fragment);
		ft.commit();
		fragmentManager.executePendingTransactions();
	}
	
	protected void onResume()
	{
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionmenu, menu);
   	 	return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
	        {
	            this.finish();
	            return true;
	        }
	        else
	        {
	            getSupportFragmentManager().popBackStack();
	            removeCurrentFragment();
	            return true;
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}


	public void removeCurrentFragment()
	{
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    Fragment currentFrag =  getSupportFragmentManager().findFragmentById(R.id.onlinefragment);

	    if (currentFrag != null)
	        transaction.remove(currentFrag);

	    transaction.commit();

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) 
		{
			case R.id.menu_about: {
				new About(this).show();
				return true;
			}
			case R.id.menu_credits: {
				new Credits(this).show();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this method checks if a networkconnection is active or not
	 * @return boolean if the network is reachable or not
	 */
	private boolean haveNetworkConnection() 
	{
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) (this.getSystemService(Context.CONNECTIVITY_SERVICE));
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			//testing WIFI connection
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			//testing GPRS/EDGE/UMTS/HDSPA/HUSPA/LTE connection
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
	
}
