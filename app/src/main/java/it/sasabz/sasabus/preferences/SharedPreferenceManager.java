package it.sasabz.sasabus.preferences;

import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.beacon.bus.BusBeaconHandler;
import it.sasabz.sasabus.beacon.bus.BusBeaconInfo;
import it.sasabz.sasabus.beacon.bus.trip.CurentTrip;
import it.sasabz.sasabus.config.ConfigManager;

import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;


public class SharedPreferenceManager {

	// private Context context;
	private SharedPreferences sharedPreferences;
	private Context context;
	
	private static CurentTrip curentTrip = null;

	private final String SHAREDPREFERENCESNAME = "sasabus";
	private final static String PREF_SURVEY_RECURRING = "PREF_SURVEY_RECURRING";
	private final static String PREF_SURVEY_LASTOCCURRENCE = "PREF_SURVEY_LASTOCCURRENCE";
	private final static String PREF_BEACON_CURRENT_BUS_STOP = "PREF_BEACON_CURRENT_BUS_STOP";
	private final static String PREF_BEACON_CURRENT_BUS_STOP_SEEN = "PREF_BEACON_CURRENT_BUS_STOP_SEEN";
	private final static String PREF_BEACON_CURRENT_BUS_STOP_START = "PREF_BEACON_CURRENT_BUS_STOP_START";
	private final static String PREF_BEACON_CURRENT_BUS_STOP_LAST = "PREF_BEACON_CURRENT_BUS_STOP_LAST";
	private final static String PREF_BEACON_CURRENT_TRIP_LAST = "PREF_BEACON_CURRENT_TRIP_LAST";
	private final static String PREF_BEACON_CURRENT_TRIP = "PREF_BEACON_CURRENT_TRIP";
	private final static String PREF_BUS_BEACON_MAP = "PREF_BUS_BEACON_MAP";
	private final static String PREF_BUS_BEACON_MAP_LAST = "PREF_BUS_BEACON_MAP_LAST";
	private final static String PREF_BEACON_DETECTION_ENALBED = "PREF_BEACON_DETECTION_ENALBED";

	public SharedPreferenceManager(Context context) {
		this.context = context;
		this.sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCESNAME, Context.MODE_PRIVATE);
	}

	/**
	 * Stores the survey recurring time in seconds
	 * 
	 * @param int
	 *            surveyReccurring
	 */
	public void setSurveyRecurring(int surveyReccurring) {
		this.sharedPreferences.edit().putInt(PREF_SURVEY_RECURRING, surveyReccurring).commit();
	}

	/**
	 * Retrieves the survey recurring time in seconds from the preferences
	 * 
	 * @return int
	 */
	public int getSurveyRecurring() {
		int defaultValue = context.getResources().getInteger(R.integer.survey_recurring_time_default);
		return this.sharedPreferences.getInt(PREF_SURVEY_RECURRING, defaultValue);
	}

	/**
	 * Stores the last occurrence of a survey
	 * 
	 * @param Date
	 *            date
	 */
	public void setSurveyLastOccurence(Date date) {
		this.sharedPreferences.edit().putLong(PREF_SURVEY_LASTOCCURRENCE, date.getTime()).commit();
	}

	/**
	 * Retrieves if the the busstop detection is enabled
	 * 
	 * @return boolean
	 */
	public boolean isBusStopDetectionEnabled() {

		return this.sharedPreferences.getBoolean(PREF_BEACON_DETECTION_ENALBED, true);
	}

	/**
	 * Stores if the the busstop detection is enabled
	 * 
	 * @param Date
	 *            date
	 */
	public void setBusStopDetectionEnabled(boolean value) {
		this.sharedPreferences.edit().putBoolean(PREF_BEACON_DETECTION_ENALBED, value).commit();
	}

	/**
	 * Retrieves the current busstop set by iBeacon detection
	 * 
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

		if (currentBusStopTimeStamp != null && currentBusStop != null) {
			Long nowTimeStamp = (new Date()).getTime();
			Long difference = nowTimeStamp - currentBusStopTimeStamp;
			Integer configuredMilisecons = ConfigManager.getInstance(context).getValue("busStopValiditySeconds", 30000);
			;
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
	 * 
	 * @return int
	 */
	public void setCurrentBusStop(Integer busStopId) {
		if (busStopId == null) {
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP).commit();
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP_LAST).commit();
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP_SEEN).commit();
			this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_BUS_STOP_START).commit();
		} else {
			if(!busStopId.equals(getCurrentBusStop())){
				this.sharedPreferences.edit().putBoolean(PREF_BEACON_CURRENT_BUS_STOP_SEEN, false).commit();
				this.sharedPreferences.edit().putLong(PREF_BEACON_CURRENT_BUS_STOP_START, (new Date()).getTime()).commit();
			}
			this.sharedPreferences.edit().putInt(PREF_BEACON_CURRENT_BUS_STOP, busStopId).commit();
			this.sharedPreferences.edit().putLong(PREF_BEACON_CURRENT_BUS_STOP_LAST, (new Date()).getTime()).commit();
		}
	}

	public void setCurrentBusStopSeen() {
		this.sharedPreferences.edit().putBoolean(PREF_BEACON_CURRENT_BUS_STOP_SEEN, true).commit();
	}

	public boolean itsCurrentBusStopSeen(){
		return getCurrentBusStop() != null && this.sharedPreferences.getBoolean(PREF_BEACON_CURRENT_BUS_STOP_SEEN, false);
	}

	public long getCurrentBusStopDetectStart(){
		return this.sharedPreferences.getLong(PREF_BEACON_CURRENT_BUS_STOP_START,Long.MAX_VALUE);
	}

	/**
	 * Retrieves the last occurrence of a survey
	 * 
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

	public void setCurrentTrip(CurentTrip curentTrip) {
		CurentTrip preTrip = getCurrentTrip();
		boolean update = false;
		if (preTrip == null) {
			if (curentTrip != null)
				update = true;
		} else if (curentTrip == null)
			update = true;
		else
			update = preTrip.checkUpdate(curentTrip);
		if (update) {
			SharedPreferenceManager.curentTrip = curentTrip;
			if (curentTrip == null) {
				this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_TRIP).commit();
				this.sharedPreferences.edit().remove(PREF_BEACON_CURRENT_TRIP_LAST).commit();
			} else {
				this.sharedPreferences.edit().putString(PREF_BEACON_CURRENT_TRIP, new Gson().toJson(curentTrip)).commit();
				this.sharedPreferences.edit().putLong(PREF_BEACON_CURRENT_TRIP_LAST, new Date().getTime()).commit();
			}
			if (curentTrip != null) {
				BusBeaconHandler.mTripNotificationAction.showNotification();
			} else{
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.cancel(2);
			}
		}
	}

	public CurentTrip getCurrentTrip() {
		if(curentTrip != null)
			return curentTrip;
		CurentTrip currentTrip = null;
		Long currentTripTimeStamp = null;
		if (this.sharedPreferences.getString(PREF_BEACON_CURRENT_TRIP, null) != null) {
			currentTrip = new Gson().fromJson(sharedPreferences.getString(PREF_BEACON_CURRENT_TRIP, null), CurentTrip.class);
		}

		if (this.sharedPreferences.getLong(PREF_BEACON_CURRENT_TRIP_LAST, -999) != -999) {
			currentTripTimeStamp = this.sharedPreferences.getLong(PREF_BEACON_CURRENT_TRIP_LAST, -999);
		}

		if (currentTripTimeStamp != null && currentTrip != null) {
			Long nowTimeStamp = (new Date()).getTime();
			Long difference = nowTimeStamp - currentTripTimeStamp;
			Integer configuredMilisecons = ConfigManager.getInstance(context).getValue("busStopValiditySeconds", 30000);
			;
			if (difference < configuredMilisecons) {
				return currentTrip;
			} else {
				return null;
			}
		}
		return null;
	}

	public void setBusBeaconMap(HashMap<String, BusBeaconInfo> mBusBeaconMap) {
		if (mBusBeaconMap == null) {
			this.sharedPreferences.edit().remove(PREF_BUS_BEACON_MAP).commit();
			this.sharedPreferences.edit().remove(PREF_BUS_BEACON_MAP_LAST).commit();
		} else {
			this.sharedPreferences.edit().putString(PREF_BUS_BEACON_MAP, new Gson().toJson(mBusBeaconMap)).commit();
			this.sharedPreferences.edit().putLong(PREF_BUS_BEACON_MAP_LAST, (new Date()).getTime()).commit();
		}
	}

	public HashMap<String, BusBeaconInfo> getBusBeaconMap() {
		HashMap<String, BusBeaconInfo> mBusBeaconMap = null;
		Long currentTripTimeStamp = null;
		if (this.sharedPreferences.getString(PREF_BUS_BEACON_MAP, null) != null) {
			Type type = new TypeToken<HashMap<String, BusBeaconInfo>>() {
			}.getType();
			try {
				mBusBeaconMap = new Gson().fromJson(sharedPreferences.getString(PREF_BUS_BEACON_MAP, null), type);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		if (this.sharedPreferences.getLong(PREF_BUS_BEACON_MAP_LAST, -999) != -999) {
			currentTripTimeStamp = this.sharedPreferences.getLong(PREF_BUS_BEACON_MAP_LAST, -999);
		}

		if (currentTripTimeStamp != null && mBusBeaconMap != null) {
			Long nowTimeStamp = (new Date()).getTime();
			Long difference = nowTimeStamp - currentTripTimeStamp;
			Integer configuredMilisecons = ConfigManager.getInstance(context).getValue("busStopValiditySeconds", 30000);
			;
			if (difference < configuredMilisecons) {
				return mBusBeaconMap;
			} else {
				this.setCurrentTrip(null);
				return new HashMap<String, BusBeaconInfo>();
			}
		}
		return new HashMap<String, BusBeaconInfo>();
	}

	public boolean hasCurrentTrip() {
		return getCurrentTrip() != null;
	}
}
