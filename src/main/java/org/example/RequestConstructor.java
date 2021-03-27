package org.example;

import lombok.experimental.UtilityClass;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Collections;
import java.util.Map;

@UtilityClass
public class RequestConstructor {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    public Request constructGetRequest(String targetUrl, Map<String, String> headers) {
        return new Request.Builder()
                .url(targetUrl)
                .headers(Headers.of(headers))
                .get()
                .build();
    }

    public Request constructGetRequest(String targetUrl) {
        return constructGetRequest(targetUrl, Collections.emptyMap());
    }

    public Request constructPostRequest(String targetUrl, String jsonBody, Map<String, String> headers) {
        RequestBody body = RequestBody.create(jsonBody, JSON_MEDIA_TYPE);
        return new Request.Builder()
                .url(targetUrl)
                .headers(Headers.of(headers))
                .post(body)
                .build();
    }

    public Request constructPostRequest(String targetUrl, String jsonBody) {
        return constructPostRequest(targetUrl, jsonBody, Collections.emptyMap());
    }
}
