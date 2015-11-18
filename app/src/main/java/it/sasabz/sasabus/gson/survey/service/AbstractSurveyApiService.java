/*
 * SASAbus - Android app for SASA bus open data
 *
 * AbstractSurveyApiService.java
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

import it.sasabz.sasabus.gson.IApiCallback;
import it.sasabz.sasabus.gson.survey.model.SurveyDefinitionResult;
import it.sasabz.sasabus.gson.survey.model.SurveyResult;

public abstract class AbstractSurveyApiService {

	protected String apiUrl;
	protected String apiUser;
	protected String apiPassword;

	/**
	 * Retrieves survey definitions
	 * 
	 * @param callback
	 */
	abstract public void getSurveyDefinition(IApiCallback<SurveyDefinitionResult> callback);

	/**
	 * Commits survey data
	 * 
	 * @param userId
	 * @param result
	 * @param email
	 * @param phone
	 * @param frtId
	 * @param callback
	 */
	abstract public void commitSurvey(String result, String email, String phone, int busId, Integer frtId, long tripDuration, Integer startBusStopId, Integer stopBusStopId,
			IApiCallback<SurveyResult> callback);

	protected <T> void callApi(SurveyApiTask<T> task) {
		task.execute();
	}
}
