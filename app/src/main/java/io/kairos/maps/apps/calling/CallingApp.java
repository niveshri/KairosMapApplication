package io.kairos.maps.apps.calling;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import io.kairos.maps.KairosApplication;
import io.kairos.maps.R;
import io.kairos.maps.apps.KairosApp;
import io.kairos.maps.speechsynth.SpeechNotifier;
import io.kairos.maps.ui.AppState;
import io.kairos.maps.ui.Notification;
import io.kairos.maps.utils.Utils;

public class CallingApp implements KairosApp {
    public static final String CALLING_APP_ID = "io.kairos.maps.apps.calling.CallingApp";

    @Override
    public String getName() {
        return "Calling";
    }

    @Override
    public Fragment getAppMainFragment() {
        return new CallingFragment();
    }

    @Override
    public View getAppDeckView(Context context, ViewGroup parent) {
        View appDeckView = LayoutInflater.from(context).inflate(
                R.layout.phone_app_tile, parent, false);


        double lastCheckedTime;
        if (AppState.instance().contains("calling_fragment_checked_time")) {
            lastCheckedTime = Double.parseDouble(AppState.instance().get("calling_fragment_checked_time"));
        } else {
            lastCheckedTime = DateTime.now().getMillis() - 15 * 60 * 1000;
        }

        String[] projection = {CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL,
                CallLog.Calls.TYPE};
        String where = CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE + " AND " +
                CallLog.Calls.DATE + " >= " + lastCheckedTime;
        Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, null);
        c.moveToFirst();

        TextView missedCallInfo = (TextView) appDeckView.findViewById(R.id.missedCallInfo);
        missedCallInfo.setText("" + c.getCount());

        return appDeckView;
    }

    @Override
    public View getNotificationView(Context context, ViewGroup parent, Notification notification) {
        return null;
    }
}
