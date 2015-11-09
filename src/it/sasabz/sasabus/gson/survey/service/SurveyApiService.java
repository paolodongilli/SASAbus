/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyApiService.java
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
package it.sasabz.sasabus.gson.survey.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.survey.model.SurveyDefinitionResult;
import it.sasabz.sasabus.gson.survey.model.SurveyResult;

public class SurveyApiService extends AbstractSurveyApiService {

	private static final String PATH_GET_SURVEY = "get-survey";
	private static final String PATH_COMMIT_SURVEY = "insert-survey";
	private static SurveyApiService instance;
	private Context context;

	private SurveyApiService(Context context) {
		this.apiUrl = context.getResources().getString(R.string.survey_api_url);
		this.apiUser = context.getResources().getString(R.string.survey_api_user);
		this.apiPassword = context.getResources().getString(R.string.survey_api_password);
		this.context = context;
	}
	
	public static SurveyApiService getInstance(Context context) {
		if (instance == null) {
			instance = new SurveyApiService(context);
		}
		return instance;
	}

	@Override
	public void getSurveyDefinition(IApiCallback<SurveyDefinitionResult> callback) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		SurveyApiTask<SurveyDefinitionResult> task = new SurveyApiTask<SurveyDefinitionResult>(this.apiUrl + PATH_GET_SURVEY,
				this.apiUser, this.apiPassword, params, callback, SurveyDefinitionResult.class);
		callApi(task);
	}

	
	/**
	 * Commits survey data without checking the result via callback
	 * @param result
	 * @param frtId
	 * @param tripDuration 
	 */
	public void commitSurvey(String result, int busId, Integer frtId, long tripDuration, Integer startBusStopId, Integer stopBusStopId) {
		this.commitSurvey(result, null, null, busId, frtId, tripDuration, startBusStopId, stopBusStopId);
	}
	
	/**
	 * Commits survey data without checking the result via callback
	 * @param result
	 * @param email
	 * @param phone
	 * @param frtId
	 * @param tripDuration
	 */
	public void commitSurvey(String result, String email, String phone, int busId, Integer frtId, long tripDuration, Integer startBusStopId, Integer stopBusStopId) {
		this.commitSurvey(result, email, phone, busId, frtId, tripDuration, startBusStopId, stopBusStopId, null);
	}
	
	@Override
	public void commitSurvey(String result, String email, String phone, int busId, Integer frtId, long tripDuration, Integer startBusStopId, Integer stopBusStopId, 
			IApiCallback<SurveyResult> callback) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user_id", SasaApplication.getAndroidId(context)));
		params.add(new BasicNameValuePair("result", result));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("bus_id", busId+""));
		params.add(new BasicNameValuePair("frt_id", frtId+""));
		params.add(new BasicNameValuePair("trip_duration", tripDuration+""));
		params.add(new BasicNameValuePair("start_busstop_id", startBusStopId+""));
		params.add(new BasicNameValuePair("stop_busstop_id", stopBusStopId+""));
		SurveyApiTask<SurveyResult> task = new SurveyApiTask<SurveyResult>(this.apiUrl + PATH_COMMIT_SURVEY,
				this.apiUser, this.apiPassword, params, callback, SurveyResult.class);
		callApi(task);
	}

}
