package io.kairos.maps.speechrec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import io.kairos.maps.R;

public class MicButton extends Button implements RecognitionListener, View.OnClickListener {
    private final String TAG = MicButton.class.toString();

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening;

    private SpeechToTextListener speechToTextListener;

    public MicButton(Context context) {
        super(context);

        initialize(context);
    }

    public MicButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public MicButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(context);
    }

    private void initialize(Context context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        this.setOnClickListener(this);

        setBackgroundResource(R.drawable.mic);
    }

    public void setSpeechToTextListener(SpeechToTextListener speechToTextListener) {
        this.speechToTextListener = speechToTextListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        speechRecognizer.destroy();
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.d(TAG, "Rms changed - " + rmsdB);
        // Use to display animation.
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        setListeningState(false);
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "Error - " + error);
        setListeningState(false);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults - " + results);

        ArrayList recognizedData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < recognizedData.size(); i++) {
            Log.d(TAG, "result " + recognizedData.get(i));
        }

        if (speechToTextListener != null) {
            speechToTextListener.onSpeechToText(recognizedData);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    private void setListeningState(boolean isListening) {
        if (this.isListening == isListening) return;

        this.isListening = isListening;

        LayerDrawable layerDrawable = (LayerDrawable) getBackground();
        GradientDrawable background = (GradientDrawable) layerDrawable.getDrawable(0);
        background.setColor(this.isListening ? getResources().getColor(R.color.mic_color2)
                : getResources().getColor(R.color.mic_color));

        if (isListening)
            speechRecognizer.startListening(speechRecognizerIntent);
        else
            speechRecognizer.stopListening();
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "micButtonClicked");

        setListeningState(!isListening);
    }
}
