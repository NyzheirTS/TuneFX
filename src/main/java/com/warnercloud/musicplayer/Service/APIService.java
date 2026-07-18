package com.warnercloud.musicplayer.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class APIService {

    private static APIService instance;
    private static final OkHttpClient CLIENT = new OkHttpClient();

    private APIService() {
    }

    public static APIService getInstance() {
        if (instance == null) {
            instance = new APIService();
        }
        return instance;
    }

    public String apiCall(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException(
                        "Server responded with code "
                                + response.code()
                                + " "
                                + response.message()
                );
            }

            ResponseBody body = response.body();

            if (body == null) {
                throw new IOException("Response body was empty.");
            }

            return body.string();
        }
    }
}
