package com.example.msgforward;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	EditTextPreference mSenderPref, mForwardPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		mSenderPref = (EditTextPreference) getPreferenceScreen()
				.findPreference(getString(R.string.sender));
		mForwardPref = (EditTextPreference) getPreferenceScreen()
				.findPreference(getString(R.string.forward_phone_number));

	}

	@Override
	protected void onResume() {
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		updatePreferencesSummary();
	}

	private void updatePreferencesSummary() {
		mSenderPref.setSummary("Current value : " + mSenderPref.getText());
		mForwardPref.setSummary("Current value : " + mForwardPref.getText());
	}

	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreferencesSummary();
	}
}
