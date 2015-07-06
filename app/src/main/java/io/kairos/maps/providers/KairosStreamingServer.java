package io.kairos.maps.providers;

import android.util.Log;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.util.HashMap;
import java.util.Map;

import io.kairos.maps.KairosApplication;

/**
 * Serves as the streaming server for any form of data kairos may want to consume. The goal is to
 * support the following types of use cases.
 *   1. Receive signals for user input (SWIPE SIGNALS + BUTTON PRESSES)
 *   2. Serve as the streaming data server for input data from various sensors
 *      (such as OBD/Camera etc.)
 */
public class KairosStreamingServer {
    private static final String TAG = KairosStreamingServer.class.toString();

    private AsyncHttpServer server;
    private Map<String, KairosStreamingListener> kairosStreamingListenerMap;

    public static final String SWIPE_KEY = "/swipe";

    private static KairosStreamingServer kairosServer;

    private KairosStreamingServer() {
        server = new AsyncHttpServer();
        kairosStreamingListenerMap = new HashMap<String, KairosStreamingListener>();

        initialize();
    }

    public static KairosStreamingServer instance() {
        if (kairosServer != null) return kairosServer;

        kairosServer = new KairosStreamingServer();
        return kairosServer;
    }

    private void initialize() {
        server.get("/hello", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request,
                                  AsyncHttpServerResponse response) {
                response.send("Hello, World!");
            }
        });

        server.directory(KairosApplication.getAppContext(), "(/files/).*", "web/");

        server.websocket(SWIPE_KEY, "input-protocol",
                new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(WebSocket webSocket, AsyncHttpServerRequest request) {
                Log.i(TAG, "WebSocket connected. " + SWIPE_KEY);

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        Log.d(TAG, SWIPE_KEY + ": " + s);

                        if (!kairosStreamingListenerMap.containsKey(SWIPE_KEY)) return;

                        kairosStreamingListenerMap.get(SWIPE_KEY).onStringReceived(s);
                    }
                });
            }
        });

        server.listen(9010);
    }

    public void requestStream(String key, KairosStreamingListener listener) {
        kairosStreamingListenerMap.put(key, listener);
    }
}
