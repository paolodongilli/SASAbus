/*
 * SASAbus - Android app for SASA bus open data
 *
 * AboutFragment.java
 *
 * Created: May 14, 2014 19:24:00 PM
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

import it.sasabz.android.sasabus.R;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class AboutFragment extends SherlockFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		final View view = inflater.inflate(R.layout.fragment_about, container,
				false);
		TextView txt_about = (TextView) view.findViewById(R.id.txt_about);
		String versionName = "";
		try
		{
			PackageInfo pInfo = getActivity().getPackageManager()
					.getPackageInfo(getActivity().getPackageName(), 0);
			versionName = pInfo.versionName;
		}
		catch (Exception e)
		{
			// do nothing
		}
		txt_about.setMovementMethod(LinkMovementMethod.getInstance());
		txt_about.setText(Html.fromHtml(getString(R.string.about_text,
				versionName)));
		return view;
	}
}
