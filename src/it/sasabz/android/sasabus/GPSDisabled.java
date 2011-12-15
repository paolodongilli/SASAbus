/**
 * 
 *
 * GPSDisabled.java
 * 
 * Created: 15.12.2011 11:27:21
 * 
 * Copyright (C) 2011 Paolo Dongilli & Markus Windegger
 * 
 *
 * This file is part of SasaBus.

 * SasaBus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SasaBus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SasaBus.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package it.sasabz.android.sasabus;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author Markus Windegger (markus@mowiso.com)
 *
 */
public class GPSDisabled {
	private Activity act;

	public GPSDisabled(Activity act) {
		this.act = act;
	}


	public void show() {
		AlertDialog.Builder builder = new AlertDialog.Builder(act)
				.setTitle(R.string.gps_disabled_title).setMessage(R.string.gps_disabled).setPositiveButton(
						android.R.string.ok, new Dialog.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								dialogInterface.dismiss();
							}
						});
		builder.create().show();
	}
}
