package com.example.msgforward;

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

				forwardMsg("15776200", "test");

			}
		});

		checkIntentNforwardMsg(getIntent());

		// mTextTo = (TextView) findViewById(R.id.editTextTo);
		//
		// SharedPreferences pref = getSharedPreferences(PREF_NAME,
		// Context.MODE_PRIVATE);
		//
		// String from = pref.getString("from", "");
		// String to = pref.getString("to", "");
		// Log.d(sTag, "perfs:" + from + "," + to);
		//
		// if (from.length() != 0) {
		// mTextFrom.setText(from);
		// }
		// if (to.length() != 0) {
		// mTextTo.setText(to);
		// }
		//
		// if (address.equals(from)) {
		// Log.d(MainActivity.sTag, "HCard msg arrived");
		// }
		//
		// if (to.length()>0) {
		// String body = getIntent().getStringExtra("body");
		// if (body != null) {
		// String sender = getIntent().getStringExtra("from");
		//
		// if (sender.equals(from)) {

		//
		// }else{
		// Toast.makeText(context, "", duration);
		// }
		// }
		// }
		
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					mTextHistory.append("-> OK \n");
					break;
				default:
					mTextHistory.append("-> Failed \n");
					break;
				}
			}
		}, new IntentFilter(SMS_SENT));
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
			String log = "sending: " + msg;
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
