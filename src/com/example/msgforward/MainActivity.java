package com.example.msgforward;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	public final static String sTag = "MainActivity";
	public final static String PREF_NAME = "prefs";
	public final static String SMS_SENT = "SMS_SENT";

	TextView mTextHistory;
	BroadcastReceiver mRecevierSmsSent;
	private String FILE_LOG = "history.txt";
	private final int MAX_HISTORY = 100;
	private int mSentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// cache
		mTextHistory = (TextView) findViewById(R.id.editTextHistory);
		Button btn = (Button)findViewById(R.id.buttonTest);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.d(sTag, "test clicked");
				forwardMsg("15776200", "test msg from darren");
			}
		});

		checkIntentNforwardMsg(getIntent());

		mRecevierSmsSent = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					mTextHistory.append("-> OK\n");
					break;
				default:
					mTextHistory.append("-> Failed\n");
					break;
				}
			}
		};
		registerReceiver(mRecevierSmsSent, new IntentFilter(SMS_SENT));

		loadHistory();
	}
	
	private void loadHistory() {
		FileInputStream fis = null;
		try {
			fis = openFileInput(FILE_LOG);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));
			String line;
			StringBuffer buffer = new StringBuffer();

		    while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			line = buffer.toString();
			mTextHistory.setText(line);
		} catch (FileNotFoundException e) {
			// no log file
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveHistory() {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILE_LOG, Context.MODE_PRIVATE);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fos));
			CharSequence raw = mTextHistory.getText();

			if (raw.length() > MAX_HISTORY) {
				int delta = raw.length() - MAX_HISTORY;
				raw = raw.subSequence(delta, raw.length());
			}
			writer.write(raw.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void forwardMsg(String from, String msg) {
		// get preferences
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String wantedFromNumber = sharedPref.getString(
				getString(R.string.sender), "");
		String toNumber = sharedPref.getString(
				getString(R.string.forward_phone_number), "");


		if (from.equals(wantedFromNumber)) {
			String log = "sending: " + msg + mSentIndex;
			mSentIndex++;
			mTextHistory.append(log);

			PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
					new Intent(SMS_SENT), 0);

			SmsManager mgr = SmsManager.getDefault();
			mgr.sendTextMessage(toNumber, from, msg, sentPI, null);
		}
	}

	private void checkIntentNforwardMsg(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null) {
			return;
		}

		String from = intent.getStringExtra("from");
		String msg = intent.getStringExtra("body");
		forwardMsg(from, msg);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		checkIntentNforwardMsg(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveHistory();
		unregisterReceiver(mRecevierSmsSent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent sub = new Intent(this, PrefActivity.class);
			this.startActivity(sub);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
