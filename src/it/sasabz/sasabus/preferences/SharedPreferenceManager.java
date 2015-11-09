package it.sasabz.sasabus.preferences;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.config.ConfigManager;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

	//private Context context;
	private SharedPreferences sharedPreferences;
	private Context context;
	
	private final String SHAREDPREFERENCESNAME = "sasabus";
	private final static String PREF_SURVEY_RECURRING = "PREF_SURVEY_RECURRING";
	private final static String PREF_SURVEY_LASTOCCURRENCE = "PREF_SURVEY_LASTOCCURRENCE";
	private final static String PREF_BEACON_CURRENT_BUS_STOP = "PREF_BEACON_CURRENT_BUS_STOP";
	private final static String PREF_BEACON_CURRENT_BUS_STOP_LAST = "PREF_BEACON_CURRENT_BUS_STOP_LAST";
	private final static String PREF_BEACON_DETECTION_ENALBED = "PREF_BEACON_DETECTION_ENALBED";
	
	
	public SharedPreferenceManager(Context context) {
		this.context = context;
		this.sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCESNAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * Stores the survey recurring time in seconds
	 * @param int surveyReccurring
	 */
	public void setSurveyRecurring(int surveyReccurring) {
		this.sharedPreferences.edit().putInt(PREF_SURVEY_RECURRING, surveyReccurring).commit();
	}
	
	/**
	 * Retrieves the survey recurring time in seconds from the preferences
	 * @return int
	 */
	public int getSurveyRecurring() {
		int defaultValue = context.getResources().getInteger(R.integer.survey_recurring_time_default);
		return this.sharedPreferences.getInt(PREF_SURVEY_RECURRING, defaultValue);
	}
	
	/**
	 * Stores the last occurrence of a survey
	 * @param Date date
	 */
	public void setSurveyLastOccurence(Date date) {
		this.sharedPreferences.edit().putLong(PREF_SURVEY_LASTOCCURRENCE, date.getTime()).commit();
	}
	
	/**
	 * Retrieves if the the busstop detection is enabled
	 * @return boolean
	 */
	public boolean isBusStopDetectionEnabled() {
		
		return this.sharedPreferences.getBoolean(PREF_BEACON_DETECTION_ENALBED, true);
	}
	
	/**
	 * Stores if the the busstop detection is enabled
	 * @param Date date
	 */
	public void setBusStopDetectionEnabled(boolean value) {
		this.sharedPreferences.edit().putBoolean(PREF_BEACON_DETECTION_ENALBED, value).commit();
	}
	
	/**
	 * Retrieves the current busstop set by iBeacon detection
	 * @return int
	 */
	public Integer getCurrentBusStop() {
		Integer currentBusStop = null;
		Long currentBusStopTimeStamp = null;
		if (this.sharedPreferences.getInt(PREF_BEACON_CURRENT_BUS_STOP, -999) != -999) {
			currentBusStop = this.sharedPreferences.getInt(PREF_BEACON_CURRENT_BUS_STOP, -999);
		}
		
		if (this.sharedPreferences.getLong(PREF_BEACON_CURRENT_BUS_STOP_LAST, -999) != -999) {
			currentBusStopTimeStamp = this.sharedPreferences.getLong(PREF_BEACON_CURRENT_BUS_STOP_LAST, -999);
		}
		
		if (currentBusStopTimeStamp != null &&
				currentBusStop != null) {
			Long nowTimeStamp = (new Date()).getTime();
			Long difference = nowTimeStamp - currentBusStopTimeStamp;
			Integer configuredMilisecons = ConfigManager.getInstance(context).getValue("busStopValiditySeconds",30000);;
			if (difference < configuredMilisecons) {
                return currentBusStop;
            } else {
                this.setCurrentBusStop(null);
                return null;
            }
		}
		return null;
	}
	
	/**
	 * Sets the current busstop
	 * @return int
	 */
	public void setCurrentBusStop(Integer busStopId) {
		if (busStopId == null) {
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP).commit();
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP_LAST).commit();
		} else {
			this.sharedPreferences.edit().putInt(PREF_BEACON_CURRENT_BUS_STOP, busStopId).commit();
			this.sharedPreferences.edit().putLong(PREF_BEACON_CURRENT_BUS_STOP_LAST, (new Date()).getTime()).commit();
		}
	}
	
	
	/**
	 * Retrieves the last occurrence of a survey
	 * @return Date
	 */
	public Date getSurveyLastOccurence() {
		Date returnDate = null;
		long lastSurveyTime = this.sharedPreferences.getLong(PREF_SURVEY_LASTOCCURRENCE, 0);
		if (lastSurveyTime != 0) {
			returnDate = new Date(lastSurveyTime);
		}
		return returnDate;
		
	}
}
