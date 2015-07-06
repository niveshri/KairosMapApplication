package io.kairos.maps;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.LinkedHashMap;

import io.kairos.maps.apps.texting.TextingApp;
import io.kairos.maps.context.CarContextProvider;
import io.kairos.maps.notifications.NotificationService;
import io.kairos.maps.providers.KairosStreamingServer;
import io.kairos.maps.providers.TouchpadInputProvider;
import io.kairos.maps.sensing.SensingReplayer;
import io.kairos.maps.speechsynth.SpeechNotifier;
import io.kairos.maps.ui.Notification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KairosApplication extends Application {
    private String TAG = KairosApplication.class.toString();

    private static Context appContext;
    private PowerManager pm;
    private PowerManager.WakeLock wl;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        // Ensures LocationMocker initializes its connection.
        Log.i(TAG, "Initializing Location Mocker.");
        new LocationMocker();

        Log.i(TAG, "Starting streaming server");
        KairosStreamingServer.instance();
        TouchpadInputProvider.instance();

        Log.i(TAG, "Initializing Speech Notifier");
        SpeechNotifier.instance();

        Log.i(TAG, "Initializing Car Context Provider");
        final CarContextProvider carContextProvider = CarContextProvider.instance();

        //Log.i(TAG, "Initializing Sensing Replayer");
        //SensingReplayer sensingReplayer = SensingReplayer.instance();
        //sensingReplayer.requestSensingListener(carContextProvider);
        //sensingReplayer.start();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                carContextProvider.onSense(new LinkedHashMap<String, Double>());
//                handler.postDelayed(this, 1000);
//            }
//        }, 1000);

        // Odd bug that shows up on the OnePlus for the dexmaker lib required for mockito.
        if (System.getProperty("dexmaker.dexcache") == null) {
            System.setProperty("dexmaker.dexcache", getApplicationContext().getCacheDir().getPath());
        }

        //Creating dummy notification for testing.
        SmsMessage smsMessage = mock(SmsMessage.class);
        when(smsMessage.getMessageBody()).thenReturn("When are you graduating?");
        when(smsMessage.getOriginatingAddress()).thenReturn("Leslie");
        NotificationService.instance().notify(
                new Notification(TextingApp.TEXTING_APP_ID, smsMessage));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        wl.release();
    }

    public static Context getAppContext() {
        return appContext;
    }
}
