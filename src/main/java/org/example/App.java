package org.example;

import okhttp3.Request;

import java.io.IOException;
import java.util.Map;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        RequestManager requestManager = new RequestManager();

        Request request = RequestConstructor.constructGetRequest(
                "http://localhost:8080/urls",
                Map.of("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiIiwibmJmIjoxNjE2Nzc4OTQyLCJyb2xlcyI6WyJVU0VSIl0sImlzcyI6InRlc3QtY291cnNlLXRlbXBsYXRlIiwiZXhwIjoxNjE2ODY1MzQyLCJpYXQiOjE2MTY3Nzg5NDJ9.tO_2xefV0eFRX8VjmwLNeqmpvIA070UQFAvO7rPx9vQ")
        );

        requestManager.run(request, 1000, 1000);
    }
}
