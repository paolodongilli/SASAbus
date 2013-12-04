package it.sasabz.sasabus.ui.routing;

import it.sasabz.sasabus.ui.routing.SearchFragment.DateHasBeenSetListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * Picker for the date
 */
public class DatePicker extends SherlockDialogFragment implements OnDateSetListener{

	/** The format that the date has */
	public static String dateFormat = "dd.MM.yyyy";
	
	/** The date that has already been set before
	 * and gets used for the initial date */
	private String dateAlreadySetString;
	private DateHasBeenSetListener callback;
	

	public void setDateAlreadySetString(String dateAlreadySetString) {
		this.dateAlreadySetString = dateAlreadySetString;
	}

	public void setCallback(DateHasBeenSetListener callback) {
		this.callback = callback;
	}
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		//Convert the date in String format to Date format and set it to the new picker

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePicker.dateFormat, Locale.ITALY);
		if (dateAlreadySetString == null) {
			dateAlreadySetString = simpleDateFormat.format(new Date());
		}
		Date dateAlreadySet = null;
		try
		{
			dateAlreadySet = simpleDateFormat.parse(dateAlreadySetString);
		} catch (ParseException e)
		{
			Log.e("error", "could not parse date");
			e.printStackTrace();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateAlreadySet);
	
		int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        
		return new DatePickerDialog(getSherlockActivity(), this, year, monthOfYear, dayOfMonth);
	}


	@Override
	public void onDateSet(android.widget.DatePicker view, int year,
			int monthOfYear, int dayOfMonth) {
		
		//In Android the month starts with 0, 
		//therefore we have to add 1
		monthOfYear++;
		
		//Add a 0 if the month has only 1 digit
		String actualMonth = Integer.valueOf(monthOfYear).toString();
		if (actualMonth.length() < 2){
			actualMonth = "0" + actualMonth;
		}
		
		//Add a 0 if the day has only 1 digit
		String actualDayOfMonth = Integer.valueOf(dayOfMonth).toString();
		if (actualDayOfMonth.length() < 2){
			actualDayOfMonth = "0" + actualDayOfMonth;
		}
		
		String dateText = actualDayOfMonth+"."+actualMonth+"."+year;

		if (callback != null) {
			callback.dateHasBeenSet(dateText);
		}
	} 
	
}