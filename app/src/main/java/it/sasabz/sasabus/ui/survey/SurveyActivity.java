/*
 * SASAbus - Android app for SASA bus open data
 *
 * SurveyActivity.java
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
import android.widget.TextView;
import it.sasabz.android.sasabus.R;
import it.sasabz.sasabus.SasaApplication;
import it.sasabz.sasabus.gson.survey.service.SurveyApiService;

public class SurveyActivity extends SherlockActivity implements OnClickListener {

	private String questionText;
	private String infoText;
	private Integer frtId;
	private Integer startBusstopId;
	private Integer stopBusstopId;
	
	private int busId;
	private long tripDuration;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		Intent intent = getIntent();

		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			finish();
		}
		this.infoText = bundle.getString("infoText");
		this.questionText = bundle.getString("questionText");
		this.frtId = bundle.getInt("frtId");
		this.busId = bundle.getInt("busId");
		this.startBusstopId = bundle.getInt("startBusstopId");
		this.stopBusstopId = bundle.getInt("stopBusstopId");
		this.tripDuration = bundle.getLong("tripDuration");
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(0);
		SasaApplication application = (SasaApplication) getApplication();
		application.getTracker().track("Survey");
		TextView question = (TextView) this.findViewById(R.id.survey_question);
		question.setText(questionText);

		((Button) this.findViewById(R.id.survey_answer_no)).setOnClickListener(this);
		((Button) this.findViewById(R.id.survey_answer_yes)).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.survey_answer_no:
			Intent intent = new Intent(this, SurveyContactActivity.class);
			intent.putExtra("infoText", this.infoText);
			intent.putExtra("busId", this.busId);
			intent.putExtra("frtId", this.frtId);
			intent.putExtra("tripDuration", this.tripDuration);
			intent.putExtra("answer", false);
			intent.putExtra("startBusstopId", this.startBusstopId);
			intent.putExtra("stopBusstopId", this.stopBusstopId);
			finish();
			startActivity(intent);
			break;
		case R.id.survey_answer_yes:
			SurveyApiService.getInstance(this).commitSurvey("y", this.busId, this.frtId, this.tripDuration, this.startBusstopId, this.stopBusstopId);
			finish();
			break;
		}
	}
}
