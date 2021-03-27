package org.example;

import com.opencsv.CSVWriter;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestManager {

    private final OkHttpClient client;
    private CSVWriter successWriter, errorWriter;
    private final AtomicInteger successCounter, errorCounter;

    public RequestManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        successCounter = new AtomicInteger();
        errorCounter = new AtomicInteger();
    }

    public void run(Request request, int times, int concurrentRequestNumber) {
        successCounter.set(0);
        errorCounter.set(0);

        try {
            successWriter = new CSVWriter(new FileWriter("SuccessOut.csv"));
            errorWriter = new CSVWriter(new FileWriter("ErrorOut.csv"));
        } catch (IOException e) {
            throw new RuntimeException();
        }

        client.dispatcher().setMaxRequests(concurrentRequestNumber);
        client.dispatcher().setMaxRequestsPerHost(concurrentRequestNumber);

        Callback callback = getRequestCallback();
        Call c = client.newCall(request);

        for (int i = 0; i < times; i++) {
            c.clone().enqueue(callback);
        }

        client.dispatcher().setIdleCallback(() -> shutdown(times));
    }

    private Callback getRequestCallback() {
        return new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                errorCounter.incrementAndGet();
                e.printStackTrace();
                errorWriter.writeNext(new String[]{e.getMessage()});
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                successCounter.incrementAndGet();
                long time = (response.receivedResponseAtMillis() - response.sentRequestAtMillis());
                successWriter.writeNext(new String[]{String.valueOf(response.code()), String.valueOf(time)});
                response.close();
            }
        };
    }

    private void shutdown(int times) {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        try {
            successWriter.close();
            errorWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf(
                "------------\n" +
                        "Final result\n" +
                        "------------\n" +
                        "Success: %d/%d (%.2f%%)\n" +
                        "Error  : %d/%d (%.2f%%)",
                successCounter.get(), times, 100 * (double)successCounter.get()/times,
                errorCounter.get(), times, 100 * (double)errorCounter.get()/times
        );
    }
}
