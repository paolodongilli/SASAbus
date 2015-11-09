/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyDefinitionResult.java
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
package it.sasabz.sasabus.gson.survey.model;

import java.util.ArrayList;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;

import android.content.Context;

public class SurveyDefinitionResult extends SurveyResult {
	@SerializedName("data")
	public ArrayList<Data> data;

	public SurveyDefinitionResult() {

	}

	/**
	 * Retrieves the data
	 * 
	 * @return
	 */
	public ArrayList<Data> getData() {
		return this.data;
	}

	/**
	 * Checks if the survey definition has data
	 * 
	 * @return
	 */
	public boolean hasData() {
		return this.data.size() > 0;
	}

	/**
	 * Gets the first data
	 * 
	 * @return
	 */
	public Data getFirstData() {
		Data data = null;
		if (hasData() == true) {
			data = this.data.get(0);
		}
		return data;
	}

	public static class Data {
		@SerializedName("id")
		public int id;
		@SerializedName("first_question_en")
		public String firstQuestionEn;
		@SerializedName("first_question_placeholder_en")
		public String firstQuestionPlaceholderEn;
		@SerializedName("second_question_en")
		public String secondQuestionEn;
		@SerializedName("first_question_de")
		public String firstQuestionDe;
		@SerializedName("first_question_placeholder_de")
		public String firstQuestionPlaceholderDe;
		@SerializedName("second_question_de")
		public String secondQuestionDe;
		@SerializedName("first_question_it")
		public String firstQuestionIt;
		@SerializedName("first_question_placeholder_it")
		public String firstQuestionPlaceholderIt;
		@SerializedName("second_question_it")
		public String secondQuestionIt;
		@SerializedName("enabled")
		public String enabled;

		public int getId() {
			return this.id;
		}

		public String getFirstQuestionForDeviceLocale(Context context) {
			Locale locale = context.getResources().getConfiguration().locale;
			return getFirstQuestionForLocale(locale);
		}

		public String getFirstQuestionForLocale(Locale locale) {
			String language = locale.getLanguage();
			String firstQuestion = this.firstQuestionEn;
			if (language.equals(Locale.GERMAN.getLanguage())) {
				firstQuestion = this.firstQuestionDe;
			} else if (language.equals(Locale.ITALIAN.getLanguage())) {
				firstQuestion = this.firstQuestionIt;
			}
			return firstQuestion;
		}

		public String getFirstQuestionPlaceholderForDeviceLocale(Context context) {
			Locale locale = context.getResources().getConfiguration().locale;
			return getFirstQuestionPlaceholderForLocale(locale);
		}

		public String getFirstQuestionPlaceholderForLocale(Locale locale) {
			String language = locale.getLanguage();
			String firstQuestionPlaceholder = this.firstQuestionPlaceholderEn;
			if (language.equals(Locale.GERMAN.getLanguage())) {
				firstQuestionPlaceholder = this.firstQuestionPlaceholderDe;
			} else if (language.equals(Locale.ITALIAN.getLanguage())) {
				firstQuestionPlaceholder = this.firstQuestionPlaceholderIt;
			}
			return firstQuestionPlaceholder;
		}

		public String getSecondQuestionForDeviceLocale(Context context) {
			Locale locale = context.getResources().getConfiguration().locale;
			return getSecondQuestionForLocale(locale);
		}

		public String getSecondQuestionForLocale(Locale locale) {
			String language = locale.getLanguage();
			String secondQuestion = this.secondQuestionEn;
			if (language.equals(Locale.GERMAN.getLanguage())) {
				secondQuestion = this.secondQuestionDe;
			} else if (language.equals(Locale.ITALIAN.getLanguage())) {
				secondQuestion = this.secondQuestionIt;
			}
			return secondQuestion;
		}

		public boolean isEnabled() {
			return this.enabled.equals("y");
		}
	}
}
