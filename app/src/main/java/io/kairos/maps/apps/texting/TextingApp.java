package io.kairos.maps.apps.texting;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.notifications.NotificationService;
import io.kairos.maps.speechsynth.SpeechNotifier;
import io.kairos.maps.ui.Notification;
import io.kairos.maps.utils.Utils;

public class TextingApp implements KairosApp {
    public static final String TEXTING_APP_ID = "io.kairos.maps.apps.texting.TextingApp";

    @Override
    public String getName() {
        return "Texting";
    }

    @Override
    public Fragment getAppMainFragment() {
        return new TextingFragment();
    }

    @Override
    public View getAppDeckView(Context context, ViewGroup parent) {
        View appDeckView = LayoutInflater.from(context).inflate(
                R.layout.texting_app_tile, parent, false);

        TextView unreadTextCountView = (TextView) appDeckView.findViewById(R.id.unreadTextCount);
        Map<String, Integer> notificationCounts = NotificationService.instance().getNotificationCounts();
        unreadTextCountView.setText(
                notificationCounts.containsKey(TEXTING_APP_ID)
                        ? "" + notificationCounts.get(TEXTING_APP_ID) : "" +  0);

        return appDeckView;
    }

    @Override
    public View getNotificationView(Context context, ViewGroup parent, Notification notification) {
        View smsView = LayoutInflater.from(context).inflate(
                R.layout.sms_notification_item, parent, false);

        SmsMessage smsMessage = (SmsMessage)notification.getValue();
        smsView.setTag(smsMessage);

        TextView smsSender = (TextView) smsView.findViewById(R.id.smsSender);
        smsSender.setText(Utils.trimString(smsMessage.getOriginatingAddress(), 8));

        TextView smsTextView = (TextView) smsView.findViewById(R.id.smsTextView);
        smsTextView.setText(Utils.trimString(
                smsMessage.getMessageBody(), 30));

        smsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsMessage message = (SmsMessage) v.getTag();
                String messageBody = message.getMessageBody();
                SpeechNotifier.instance().speakText(messageBody);
            }
        });

        return smsView;
    }
}
