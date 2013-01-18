package it.sasabz.android.sasabus;

import it.sasabz.android.sasabus.fragments.OnlineSearchFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class OnlineActivity extends FragmentActivity{
	
	
	protected void onCreate(Bundle savedInstaceState)
	{
		super.onCreate(savedInstaceState);
		setContentView(R.layout.online_fragment_container);
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
}
