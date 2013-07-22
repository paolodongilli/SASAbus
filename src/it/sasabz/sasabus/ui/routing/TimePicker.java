package it.sasabz.sasabus.ui.routing;

import it.sasabz.sasabus.ui.routing.SearchFragment.DateHasBeenSetListener;
import it.sasabz.sasabus.ui.routing.SearchFragment.TimeHasBeenSetListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TimePicker extends SherlockDialogFragment implements OnTimeSetListener{

public static String timeFormat = "HH:mm";
	
	private String timeAlreadySetString;
	private TimeHasBeenSetListener callback;
	

	public void setTimeAlreadySetString(String timeAlreadySetString) {
		this.timeAlreadySetString = timeAlreadySetString;
	}

	public void setCallback(TimeHasBeenSetListener callback) {
		this.callback = callback;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * gets called when the dialog is being created
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		//Convert the time in String format to Date format and set it to the new picker

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimePicker.timeFormat, Locale.ITALY);
		if (timeAlreadySetString == null) {
			timeAlreadySetString = simpleDateFormat.format(new Date());
		}
		Date timeAlreadySet = null;
		try
		{
			timeAlreadySet = simpleDateFormat.parse(timeAlreadySetString);
		} catch (ParseException e)
		{
			Log.e("error", "could not parse time");
			e.printStackTrace();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeAlreadySet);
	
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
		
        return new TimePickerDialog(getSherlockActivity(), this, hourOfDay, minute, true);
	}

	/**
	 * gets called when the user has set a time and clicked on finish
	 */
	@Override
	public void onTimeSet(android.widget.TimePicker view, int hourOfDay,
			int minute) {
		
		//when an hour or a minute is composed of only one digit,
		//then we need to add a 0 before, because 0s are cut of by int
		String actualHourOfDayString = Integer.toString(hourOfDay);
		if (actualHourOfDayString.length() < 2) {
			actualHourOfDayString = "0"+actualHourOfDayString;
		}
		
		String actualMinuteString = Integer.toString(minute);
		if (actualMinuteString.length() < 2) {
			actualMinuteString = "0"+actualMinuteString;
		}
		
		
		String timeText = actualHourOfDayString+":"+actualMinuteString;

		if (callback != null) {
			callback.timeHasBeenSet(timeText);
		}
	}
	
}