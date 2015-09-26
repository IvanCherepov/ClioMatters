package com.ubc.ivan.cliomatters.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ivan on 21/09/15.
 */
public class NetworkHandler {

    private static final String TAG = NetworkHandler.class.getSimpleName();
    private final Context mContext;

    public NetworkHandler(Context context) {
        mContext = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailabe = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailabe = true;
        }
        return isAvailabe;
    }

    public JSONObject getJason(String url, String auth) {
        int responseCode;
        JSONObject jsonResponse = null;

        try {
            URL clioURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) clioURL.openConnection();
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                jsonResponse = new JSONObject(response.toString());
                reader.close();
                Log.v(TAG, "Response: " + response);
            } else {
                Log.i(TAG, "Unsuccessful HTTP Response Code: %d" + responseCode);
            }

        } catch (Exception e) {
            logException(e);
        }
        return jsonResponse;
    }

    private void logException(Exception e) {
        Log.e(TAG, "Exception caught! ", e);
    }
}

