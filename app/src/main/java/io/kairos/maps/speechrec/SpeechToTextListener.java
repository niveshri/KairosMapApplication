package io.kairos.maps.speechrec;

import java.util.List;

public interface SpeechToTextListener {
    void onSpeechToText(List<String> textList);
}
