package com.web2wave;

import java.util.Map;

public interface Web2WaveWebListener {
    void onQuizFinished(Map<String, Object> data);

    void onClose(Map<String, Object> data);

    void onEvent(String event, Map<String, Object> data);
}
