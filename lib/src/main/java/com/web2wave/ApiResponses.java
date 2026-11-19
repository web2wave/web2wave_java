package com.web2wave;

import org.json.JSONObject;

/**
 * Pure helpers for interpreting web2wave API responses (no Android dependencies, unit-testable on the JVM).
 */
final class ApiResponses {

    static final String KEY_SUCCESS = "success";
    static final String KEY_ERROR_MSG = "error_msg";
    static final String KEY_MESSAGE = "message";

    private ApiResponses() {
    }

    /**
     * The backend reports success as boolean {@code true} (cancel, charge), number {@code 1} (refund)
     * or the string {@code "1"} / {@code "true"}. Anything else, including a missing key, is a failure.
     */
    static boolean isSuccess(JSONObject json) {
        if (json == null) {
            return false;
        }
        Object value = json.opt(KEY_SUCCESS);
        if (value == null || value == JSONObject.NULL) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        String text = value.toString().trim();
        return "1".equals(text) || "true".equalsIgnoreCase(text);
    }

    /**
     * Error text for a failed response: {@code error_msg} first (where the backend puts the reason),
     * then a non-empty {@code message}, otherwise {@code fallback}.
     */
    static String errorMessage(JSONObject json, String fallback) {
        if (json == null) {
            return fallback;
        }
        String errorMsg = nonEmptyString(json, KEY_ERROR_MSG);
        if (errorMsg != null) {
            return errorMsg;
        }
        String message = nonEmptyString(json, KEY_MESSAGE);
        if (message != null) {
            return message;
        }
        return fallback;
    }

    static boolean isSuccessfulStatus(int code) {
        return code >= 200 && code < 300;
    }

    static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String nonEmptyString(JSONObject json, String key) {
        Object value = json.opt(key);
        if (value == null || value == JSONObject.NULL) {
            return null;
        }
        String text = value.toString();
        return text.trim().isEmpty() ? null : text;
    }
}
