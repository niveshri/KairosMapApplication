package io.kairos.maps.speechsynth;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

import io.kairos.maps.KairosApplication;

public class SpeechNotifier {
    private static final String TAG = SpeechNotifier.class.toString();

    private TextToSpeech textToSpeech;

    private static SpeechNotifier instance = new SpeechNotifier();
    public static SpeechNotifier instance() {
        return instance;
    }

    private SpeechNotifier() {
        textToSpeech = new TextToSpeech(KairosApplication.getAppContext(),
                new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.ERROR){
                    Log.d(TAG, "Error initializing Speech Notifier. " + status);
                }
                textToSpeech.setLanguage(Locale.UK);
            }
        });
    }

    public void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
