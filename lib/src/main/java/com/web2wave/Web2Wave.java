package com.web2wave;

import static com.web2wave.Utils.jsonToMap;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Web2Wave {

    private static final String BASE_URL = "https://api.web2wave.com";

    private static final String PROFILE_ID_REVENUECAT = "revenuecat_profile_id";
    private static final String PROFILE_ID_ADAPTY = "adapty_profile_id";
    private static final String PROFILE_ID_QONVERSION = "qonversion_profile_id";

    private static final String API_SUBSCRIPTIONS = "api/user/subscriptions";
    private static final String API_USER_PROPERTIES = "api/user/properties";
    private static final String API_SUBSCRIPTION_CANCEL = "api/subscription/cancel";
    private static final String API_SUBSCRIPTION_REFUND = "api/subscription/refund";
    private static final String API_SUBSCRIPTION_CHARGE = "api/subscription/user/charge";

    private static final String KEY_USER = "user";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PRICE_ID = "price_id";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_SUBSCRIPTION = "subscription";
    private static final String KEY_PROPERTIES = "properties";
    private static final String KEY_PROPERTY = "property";
    private static final String KEY_VALUE = "value";
    private static final String KEY_STATUS = "status";
    private static final String KEY_RESULT = "result";
    private static final String KEY_PAY_SYSTEM = "pay_system_id";
    private static final String KEY_INVOICE_ID = "invoice_id";

    private static final String VALUE_ACTIVE = "active";
    private static final String VALUE_TRIAL = "trialing";

    private static final String METHOD_TYPE_POST = "POST";
    private static final String METHOD_TYPE_GET = "GET";
    private static final String METHOD_TYPE_PUT = "PUT";

    private static Web2Wave instance;
    private String apiKey;

    private Web2Wave() {
    }

    public static synchronized Web2Wave getInstance() {
        if (instance == null) {
            instance = new Web2Wave();
        }
        return instance;
    }

    public void initWith(String apiKey) {
        this.apiKey = apiKey;
    }

    private String buildUrl(String path, Map<String, String> queryParams) {
        StringBuilder url = new StringBuilder(BASE_URL).append("/").append(path);
        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url.setLength(url.length() - 1);
        }
        return url.toString();
    }

    public Map<String, Object> fetchSubscriptionStatus(String userID) {
        checkApiKey();
        String url = buildUrl(API_SUBSCRIPTIONS, Collections.singletonMap(KEY_USER, userID));
        String response = makeRequest(url, "GET", null);
        if (response != null) {
            try {
                return jsonToMap(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean hasActiveSubscription(String userID) {
        Map<String, Object> status = fetchSubscriptionStatus(userID);
        if (status == null || !status.containsKey(KEY_SUBSCRIPTION)) return false;
        Object subscriptions = status.get(KEY_SUBSCRIPTION);
        if (subscriptions instanceof List<?>) {
            for (Object sub : (List<?>) subscriptions) {
                if (sub instanceof Map) {
                    String value = (String) ((Map<?, ?>) sub).get(KEY_STATUS);
                    if (VALUE_ACTIVE.equals(value) || VALUE_TRIAL.equals(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Map<String, Object>> fetchSubscriptions(String userID) {
        Map<String, Object> response = fetchSubscriptionStatus(userID);
        if (response != null && response.containsKey(KEY_SUBSCRIPTION)) {
            Object subscriptions = response.get(KEY_SUBSCRIPTION);
            if (subscriptions instanceof List<?>) {
                return (List<Map<String, Object>>) subscriptions;
            }
        }
        return Collections.emptyList();
    }


    public Result<Boolean> cancelSubscription(String paySystemId, String comment) {
        checkApiKey();
        try {
            String url = buildUrl(API_SUBSCRIPTION_CANCEL, null);
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put(KEY_PAY_SYSTEM, paySystemId);

            if (comment != null && !comment.isBlank()) {
                bodyMap.put(KEY_COMMENT, comment);
            }

            String body = new JSONObject(bodyMap).toString();
            String response = makeRequest(url, METHOD_TYPE_PUT, body);
            JSONObject json = response != null ? new JSONObject(response) : null;

            if (json != null && "1".equals(json.optString("success"))) {
                return Result.success(true);
            } else {
                return Result.failure(new Exception(json != null ? json.optString("message", "Unknown error") : "Empty response"));
            }

        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> chargeUser(String web2waveUserId, int priceId) {
        checkApiKey();
        try {
            String url = buildUrl(API_SUBSCRIPTION_CHARGE, null);

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put(KEY_USER_ID, web2waveUserId);
            bodyMap.put(KEY_PRICE_ID, Integer.toString(priceId));

            String body = new JSONObject(bodyMap).toString();
            String response = makeRequest(url, METHOD_TYPE_PUT, body);
            JSONObject json = response != null ? new JSONObject(response) : null;

            if (json != null && "1".equals(json.optString("success"))) {
                return Result.success(true);
            } else {
                return Result.failure(new Exception(json != null ? json.optString("message", "Unknown error") : "Empty response"));
            }

        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> refundSubscription(String paySystemId, String invoiceId, String comment) {
        checkApiKey();
        try {
            String url = buildUrl(API_SUBSCRIPTION_REFUND, null);

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put(KEY_PAY_SYSTEM, paySystemId);
            bodyMap.put(KEY_INVOICE_ID, invoiceId);

            if (comment != null && !comment.isBlank()) {
                bodyMap.put(KEY_COMMENT, comment);
            }

            String body = new JSONObject(bodyMap).toString();
            String response = makeRequest(url, METHOD_TYPE_PUT, body);
            JSONObject json = response != null ? new JSONObject(response) : null;

            if (json != null && "1".equals(json.optString("success"))) {
                return Result.success(true);
            } else {
                return Result.failure(new Exception(json != null ? json.optString("message", "Unknown error") : "Empty response"));
            }

        } catch (Exception e) {
            return Result.failure(e);
        }
    }


    public Map<String, String> fetchUserProperties(String userID) {
        checkApiKey();
        String url = buildUrl(API_USER_PROPERTIES, Collections.singletonMap(KEY_USER, userID));
        String response = makeRequest(url, METHOD_TYPE_GET, null);
        if (response != null) {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray propertiesArray = json.optJSONArray(KEY_PROPERTIES);
                Map<String, String> result = new HashMap<>();
                if (propertiesArray != null) {
                    for (int i = 0; i < propertiesArray.length(); i++) {
                        JSONObject property = propertiesArray.getJSONObject(i);
                        String key = property.optString(KEY_PROPERTY, "");
                        String value = property.optString(KEY_VALUE, "");
                        result.put(key, value);
                    }
                }
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Result<Boolean> updateUserProperty(String userID, String property, String value) {
        checkApiKey();
        String url = buildUrl(API_USER_PROPERTIES, Collections.singletonMap(KEY_USER, userID));
        JSONObject body = new JSONObject();

        try {
            body.put(KEY_PROPERTY, property);
            body.put(KEY_VALUE, value);
        } catch (JSONException e) {
            return Result.failure(e);
        }

        String response = makeRequest(url, METHOD_TYPE_POST, body.toString());
        if (response != null) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                Boolean isSuccessful = jsonResponse.optString(KEY_RESULT, "").equals("1");
                return Result.success(isSuccessful);
            } catch (JSONException e) {
                return Result.failure(e);
            }
        }
        return Result.failure(new Exception("Failed to update properties"));
    }

    public Result<Boolean> setRevenuecatProfileID(String appUserID, String revenueCatProfileID) {
        return updateUserProperty(appUserID, PROFILE_ID_REVENUECAT, revenueCatProfileID);
    }

    public Result<Boolean> setAdaptyProfileID(String appUserID, String adaptyProfileID) {
        return updateUserProperty(appUserID, PROFILE_ID_ADAPTY, adaptyProfileID);
    }

    public Result<Boolean> setQonversionProfileID(String appUserID, String qonversionProfileID) {
        return updateUserProperty(appUserID, PROFILE_ID_QONVERSION, qonversionProfileID);
    }

    private void checkApiKey() {
        if (apiKey == null) {
            throw new IllegalStateException("You have to initialize apiKey before use");
        }
    }

    private String makeRequest(String url, String method, String body) {
        try {
            HttpURLConnection connection = getHttpURLConnection(url, method, body);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                System.err.println("Unexpected response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
        }
        return null;
    }

    private HttpURLConnection getHttpURLConnection(String url, String method, String body) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("api-key", apiKey);
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Pragma", "no-cache");

        if ("POST".equals(method)) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        return connection;
    }


    public static void showWebView(
            @NonNull FragmentManager fragmentManager,
            @NonNull String url,
            @NonNull Web2WaveWebListener listener,
            int topOffset,
            int bottomOffset
    ) {
        if (Web2Wave.getInstance().apiKey == null) {
            throw new IllegalStateException("You must initialize apiKey before use");
        }

        if (!URLUtil.isValidUrl(url)) {
            throw new IllegalArgumentException("You must provide valid url");
        }

        Web2WaveDialog dialog = Web2WaveDialog.create(url, listener, topOffset, bottomOffset);
        dialog.show(fragmentManager, "web2wave_tag");
    }

    public static void closeWebView(@NonNull FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag("web2wave_tag");
        if (fragment instanceof Web2WaveDialog) {
            ((Web2WaveDialog) fragment).dismissAllowingStateLoss();
        }
    }
}




