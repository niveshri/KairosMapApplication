package io.kairos.maps.apps.texting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import io.kairos.maps.notifications.NotificationService;
import io.kairos.maps.ui.Notification;

/**
 * Receives SMS messages from Android and notifies the notification service.
 */
public class SMSReceiver extends BroadcastReceiver {
    private final String TAG = SMSReceiver.class.toString();

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);

        Log.d(TAG, smsMessage.toString());
        NotificationService.instance().notify(
                new Notification(TextingApp.TEXTING_APP_ID, smsMessage));
    }
}
