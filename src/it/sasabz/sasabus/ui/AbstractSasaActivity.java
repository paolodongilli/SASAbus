/*
 * SASAbus - Android app for SASA bus open data
 *
 * AbstractSasaActivity.java
 *
 * Created: Sep 02, 2015 08:24:00 PM
 *
 * Copyright (C) 2011-2015 Raiffeisen Online GmbH (Norman Marmsoler, Jürgen Sprenger, Aaron Falk) <info@raiffeisen.it>
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

import it.sasabz.sasabus.SasaApplication;
import android.app.Activity;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

abstract public  class AbstractSasaActivity extends SherlockFragmentActivity{
	
	private boolean	isInForeGround = false;
	private SasaApplication sasaApplication = null;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		this.sasaApplication = (SasaApplication)this.getApplication();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sasaApplication.setActivity((MainActivity)this);
		this.isInForeGround = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.clearReference();
		this.isInForeGround = false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.clearReference();
		this.isInForeGround = false;
	}
	
	public boolean isInForeground() {
		return this.isInForeGround;
	}
	
	private void clearReference() {
		Activity currentActivity = this.sasaApplication.getActivity();
		if (currentActivity != null && currentActivity.equals(this)) {
			sasaApplication.setActivity(null);
		}
	}
}
