package com.web2wave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

public class ApiResponsesTest {

    @Test
    public void successAcceptsBooleanNumberAndStringForms() throws Exception {
        assertTrue(ApiResponses.isSuccess(new JSONObject("{\"success\": true, \"error\": 0, \"message\": \"ok\"}")));
        assertTrue(ApiResponses.isSuccess(new JSONObject("{\"success\": 1, \"error\": 0, \"message\": \"Request accepted\"}")));
        assertTrue(ApiResponses.isSuccess(new JSONObject("{\"success\": \"1\"}")));
        assertTrue(ApiResponses.isSuccess(new JSONObject("{\"success\": \"true\"}")));
    }

    @Test
    public void successRejectsFailuresAndMissingFlag() throws Exception {
        assertFalse(ApiResponses.isSuccess(null));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{}")));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{\"success\": false, \"message\": \"no\"}")));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{\"success\": 0}")));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{\"success\": \"0\"}")));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{\"success\": null}")));
        assertFalse(ApiResponses.isSuccess(new JSONObject("{\"error\": 1, \"error_msg\": \"bad\"}")));
    }

    @Test
    public void errorMessagePrefersErrorMsg() throws Exception {
        assertEquals("Subscription not found", ApiResponses.errorMessage(
                new JSONObject("{\"error\":1,\"error_msg\":\"Subscription not found\",\"message\":\"\"}"), "fallback"));
        assertEquals("This API key does not have the 'subscriptions:write' scope", ApiResponses.errorMessage(
                new JSONObject("{\"error\":1,\"error_msg\":\"This API key does not have the 'subscriptions:write' scope\","
                        + "\"required_scope\":\"subscriptions:write\"}"), "fallback"));
    }

    @Test
    public void errorMessageFallsBackToMessageThenFallback() throws Exception {
        assertEquals("Card declined", ApiResponses.errorMessage(
                new JSONObject("{\"success\": false, \"message\": \"Card declined\"}"), "fallback"));
        assertEquals("fallback", ApiResponses.errorMessage(new JSONObject("{\"error\":1,\"message\":\"\"}"), "fallback"));
        assertEquals("fallback", ApiResponses.errorMessage(new JSONObject("{\"error_msg\": null}"), "fallback"));
        assertEquals("fallback", ApiResponses.errorMessage(null, "fallback"));
    }

    @Test
    public void statusAndBlankHelpers() {
        assertTrue(ApiResponses.isSuccessfulStatus(200));
        assertTrue(ApiResponses.isSuccessfulStatus(204));
        assertFalse(ApiResponses.isSuccessfulStatus(403));
        assertFalse(ApiResponses.isSuccessfulStatus(422));
        assertTrue(ApiResponses.isBlank(null));
        assertTrue(ApiResponses.isBlank("   "));
        assertFalse(ApiResponses.isBlank(" x "));
    }
}
