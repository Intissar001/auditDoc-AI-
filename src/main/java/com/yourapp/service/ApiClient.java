package com.yourapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static <T> T get(String endpoint, Class<T> responseClass) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String jsonResponse = response.body().string();
            return gson.fromJson(jsonResponse, responseClass);
        }
    }

    public static <T> List<T> getList(String endpoint, Type typeToken) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String jsonResponse = response.body().string();
            return gson.fromJson(jsonResponse, typeToken);
        }
    }

    public static <T> T post(String endpoint, Object requestBody, Class<T> responseClass) throws IOException {
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            if (responseClass == Void.class) {
                return null;
            }
            String jsonResponse = response.body().string();
            return gson.fromJson(jsonResponse, responseClass);
        }
    }

    public static <T> T put(String endpoint, Object requestBody, Class<T> responseClass) throws IOException {
        String json = gson.toJson(requestBody);
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            String jsonResponse = response.body().string();
            return gson.fromJson(jsonResponse, responseClass);
        }
    }

    public static boolean delete(String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        }
    }
}
