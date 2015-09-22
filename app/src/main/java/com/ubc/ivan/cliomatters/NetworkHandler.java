package com.ubc.ivan.cliomatters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ivan on 21/09/15.
 */
public class NetworkHandler {

    public static final String TAG = NetworkHandler.class.getSimpleName();
    protected JSONObject mMattersData;
    Context mContext;

    public NetworkHandler(Context mContext) {
        this.mContext = mContext;
    }

    public void getJSON(String url) {
        if (isNetworkAvailable()) {
            GetMattersTask getMattersTask = new GetMattersTask();
            getMattersTask.execute();
        } else {
            Toast.makeText(mContext, "Network is not available!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailabe = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailabe = true;
        }

        return isAvailabe;
    }

    private class GetMattersTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object[] params) {
            int responseCode = -1;
            JSONObject jsonResponse = null;

            try {
                URL clioURL = new URL(MattersApiConstants.CLIO_URL);
                HttpURLConnection connection = (HttpURLConnection) clioURL.openConnection();
                connection.setRequestProperty("Authorization", MattersApiConstants.CLIO_AUTH);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();


                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    int nextCharacter;
                    String responseData = "";

                    while (true) {
                        nextCharacter = reader.read();
                        if (nextCharacter == -1) {
                            break;
                        }
                        responseData += (char) nextCharacter;
                    }

                    Log.v(TAG, "Response: " + responseData);

                    jsonResponse = new JSONObject(responseData);
//                    String records = jsonResponse.getString("records");
//
//                    Log.v(TAG, "Number of matters: " + records);
//
//                    JSONArray jsonMatters = jsonResponse.getJSONArray("matters");
//                    for (int i = 0; i < jsonMatters.length(); i++) {
//                        JSONObject jsonMatter = jsonMatters.getJSONObject(i);
//                        String description = jsonMatter.getString("description");
//                        Log.v(TAG, "Matter " + i + ": " + description);
//                    }
//
//                    return responseData.toString();

                } else {
                    Log.i(TAG, "Unsuccessful HTTP Response Code: %d" + responseCode);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception caught: ", e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mMattersData = result;
        }
    }
}
