/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyContactActivity.java
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

package it.sasabz.sasabus.ui.survey;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.gson.survey.service.SurveyApiService;

public class SurveyContactActivity extends SherlockActivity implements OnClickListener {

	private String infoText;
	private String answer;
	private Integer frtId;
	private int busId;
	private long tripDuration;
	private Integer startBusstopId;
	private Integer stopBusstopId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surveycontact);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			finish();
		}
		this.infoText = bundle.getString("infoText");
		this.frtId = bundle.getInt("frtId");
		this.busId = bundle.getInt("busId");
		this.startBusstopId = bundle.getInt("startBusstopId");
		this.stopBusstopId = bundle.getInt("stopBusstopId");
		this.tripDuration = bundle.getLong("tripDuration");
		if (bundle.getBoolean("answer")) {
			this.answer = "y";
		} else {
			this.answer = "n";
		}
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(0);
		SasaApplication application = (SasaApplication) getApplication();
		application.getTracker().track("SurveyContact");
		TextView info = (TextView) this.findViewById(R.id.survey_contact_information);
		info.setText(this.infoText);
		((Button) this.findViewById(R.id.survey_contact_information_send)).setOnClickListener(this);
		((Button) this.findViewById(R.id.survey_contact_information_cancel)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.survey_contact_information_send:
			EditText emailText = (EditText) this.findViewById(R.id.survey_contact_email_address);
			EditText phoneText = (EditText) this.findViewById(R.id.survey_contact_phone);
			String email = emailText.getText().toString().trim();
			String phone = phoneText.getText().toString().trim();
			Boolean contactFormValid = true;
			if (phone.equals("") && email.equals("")) {
				emailText.setError(getResources().getString(R.string.survey_required_fields));
				phoneText.setError(getResources().getString(R.string.survey_required_fields));
				contactFormValid = false;
			}
			if (!email.equals("")) {
				if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
					emailText.setError(getResources().getString(R.string.email_invalid));
					contactFormValid = false;
				}
			}
			if (!phone.equals("")) {
				if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
					phoneText.setError(getResources().getString(R.string.phone_invalid));
					contactFormValid = false;
				}
			}
			if (contactFormValid == true) {
				SurveyApiService.getInstance(this).commitSurvey(this.answer, email, phone, this.busId, this.frtId, this.tripDuration, this.startBusstopId, this.stopBusstopId);
				finish();
			}
			break;
		case R.id.survey_contact_information_cancel:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		SurveyApiService.getInstance(this).commitSurvey(this.answer, this.busId, this.frtId, this.tripDuration, this.startBusstopId, this.stopBusstopId);
		super.onBackPressed();
	}
}
