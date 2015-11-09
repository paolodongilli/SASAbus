/*
 * SASAbus - Android app for SASA bus open data
 *
 * SettingsFragment.java
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

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.preferences.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;

public class SettingsFragment extends SherlockFragment {

	public static final String PREF_SURVEY_RECURRING = "pref_surveyRecurring";

	private View view;
	private Spinner surveyRecurringOption;
	private List<Integer> surveyRecurringTimes;
	private SharedPreferenceManager sharedPreferenceManager;
	private CheckBox busDetectionCheckbox;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_settings, container, false);
		SasaApplication application = (SasaApplication) this.getActivity().getApplication();
		application.getTracker().track("Settings");
		sharedPreferenceManager = application.getSharedPreferenceManager();
		initSurveyRecurring();
		initBusDetection();
		return view;
	}

	private void initBusDetection() {
		busDetectionCheckbox = (CheckBox) view.findViewById(R.id.settings_busstop_iBeacon_guessing);
		busDetectionCheckbox.setChecked(this.sharedPreferenceManager.isBusStopDetectionEnabled());
		busDetectionCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		    	sharedPreferenceManager.setBusStopDetectionEnabled(isChecked);
		    }
		});
	}
	
	/**
	 * Initializes the survey recurring preference
	 */
	private void initSurveyRecurring() {
		initSurveyRecurringTimes();
		surveyRecurringOption = (Spinner) view.findViewById(R.id.settings_survey_recurring);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
				R.array.survey_recurring_descriptions, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		surveyRecurringOption.setAdapter(adapter);
		fillSurveyRecurring();
	}

	/**
	 * Loads the stored survey recurring time and sets it as selected
	 */
	private void fillSurveyRecurring() {
		int prefSurveyRecurring = sharedPreferenceManager.getSurveyRecurring();
		surveyRecurringOption.setSelection(surveyRecurringTimes.indexOf(prefSurveyRecurring));
		surveyRecurringOption.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int surveyRecurringTime = surveyRecurringTimes.get(surveyRecurringOption.getSelectedItemPosition());
				sharedPreferenceManager.setSurveyRecurring(surveyRecurringTime);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	/**
	 * Loads the survey recurring times from the integer-array resource to a
	 * List<Integer>
	 */
	private void initSurveyRecurringTimes() {
		int[] surveyRecurringTimesArray = getResources().getIntArray(R.array.survey_recurring_times);
		surveyRecurringTimes = new ArrayList<Integer>();
		for (int index = 0; index < surveyRecurringTimesArray.length; index++) {
			surveyRecurringTimes.add(surveyRecurringTimesArray[index]);
		}
	}
}
