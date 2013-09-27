package com.example.msgforward;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	public static final String SMS_EXTRA_NAME = "pdus";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
        
        if ( extras != null )
        {
            // Get received SMS array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
                 
                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();

				Intent mainIntent = new Intent(context, MainActivity.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mainIntent.putExtra("from", address);
				mainIntent.putExtra("body", body);
				context.startActivity(mainIntent);
            }
            // Display SMS message
			// Toast.makeText( context, messages, Toast.LENGTH_SHORT ).show();
		}
        // WARNING!!! 
        // If you uncomment the next line then received SMS will not be put to incoming.
        // Be careful!
        // this.abortBroadcast(); 
	}
}
