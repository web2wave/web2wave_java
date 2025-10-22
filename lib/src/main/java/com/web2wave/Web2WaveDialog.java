package com.web2wave;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Web2WaveDialog extends DialogFragment {

    private static final String URL_KEY = "url_key";
    private static final String TOP_OFFSET_KEY = "top_offset";
    private static final String BOTTOM_OFFSET_KEY = "bottom_offset";
    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private static final String EVENT_QUIZ_FINISHED = "Quiz finished";
    private static final String EVENT_CLOSE_WEB_VIEW = "Close webview";

    private Web2WaveWebListener listener;

    public void setListener(Web2WaveWebListener listener) {
        this.listener = listener;
    }

    public static Web2WaveDialog create(String url, Web2WaveWebListener listener, int topOffset, int bottomOffset, int backgroundColor) {
        Web2WaveDialog dialog = new Web2WaveDialog();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        args.putInt(TOP_OFFSET_KEY, topOffset);
        args.putInt(BOTTOM_OFFSET_KEY, bottomOffset);
        args.putInt(BACKGROUND_COLOR_KEY, backgroundColor);
        dialog.setArguments(args);
        dialog.setListener(listener);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_view, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();

        int topOffset = 0;
        int bottomOffset = 0;
        int backgroundColor = 0;
        String url = null;
        if (args != null) {
            url = args.getString(URL_KEY);
            topOffset = args.getInt(TOP_OFFSET_KEY, 0);
            bottomOffset = args.getInt(BOTTOM_OFFSET_KEY, 0);
            backgroundColor = args.getInt(BACKGROUND_COLOR_KEY, 0);
        } else {
            return;
        }


        WebView webView = (WebView) view;

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.setBackgroundColor(backgroundColor);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        WebView.setWebContentsDebuggingEnabled(true);

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void postMessage(String message) {
                try {
                    JSONObject json = new JSONObject(message);
                    handleJsEvent(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "Android");

        String newUrl = prepareUrl(url, topOffset, bottomOffset);
        webView.loadUrl(newUrl);
    }

    private String prepareUrl(String url, int topOffset, int bottomOffset) {
        return Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("webview_android", "1")
                .appendQueryParameter("top_padding", String.valueOf(topOffset))
                .appendQueryParameter("bottom_padding", String.valueOf(bottomOffset))
                .build()
                .toString();
    }

    private void handleJsEvent(JSONObject json) throws JSONException {
        String event = json.optString("event");
        JSONObject dataJson = json.optJSONObject("data");
        Map<String, Object> data = dataJson != null ? Utils.jsonToMap(dataJson) : null;

        if (event.isEmpty()) return;

        switch (event) {
            case EVENT_QUIZ_FINISHED:
                if (listener != null) listener.onQuizFinished(data);
                break;
            case EVENT_CLOSE_WEB_VIEW:
                if (listener != null) listener.onClose(data);
                break;
            default:
                if (listener != null) listener.onEvent(event, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }
}
