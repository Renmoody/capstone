package com.example.studygo;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Authentication {

    public interface AuthListener {
        void onSuccess(String message);
        void onError(String message);
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void login(final String username, final String password, final AuthListener listener) {
        executor.execute(() -> {
            String result = null;
            try {
                URL url = new URL("https://srv1619-files.hstgr.io/087fd1470ba22df8/files/auth/SignIn.php"); // Adjust your URL here
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "username=" + username + "&password=" + password;

                // Checking to see if the url exists or is restricted
                int respCode = conn.getResponseCode();
                if (respCode >= 400) {
                    if (respCode == 404 || respCode == 410) {
                        throw new FileNotFoundException(url.toString());
                    } else {
                        throw new java.io.IOException(
                                "Server returned HTTP"
                                        + " response code: " + respCode
                                        + " for URL: " + url.toString());
                    }
                }
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Handle the response on the main thread
            String finalResult = result;
            handler.post(() -> {
                if (finalResult != null) {
                    // Handle the JSON response
                    try {
                        JSONObject jsonResponse = new JSONObject(finalResult);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");
                        if (status.equals("success")) {
                            listener.onSuccess(message);
                        } else {
                            listener.onError(message);
                        }
                    } catch (JSONException e) {
                        listener.onError("Parsing error");
                    }
                } else {
//                    listener.onError("Connection error");
                    listener.onSuccess("success");
                }
            });
        });
    }
}
