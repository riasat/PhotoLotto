package com.abir.photolotto;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TermsConditionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_terms_conditions);
		TextView textViewTermsConditions = (TextView) findViewById(R.id.textViewTermsConditions);
		textViewTermsConditions.setText(EventModel.getSelectedEvent().getsTerms());
	}
}