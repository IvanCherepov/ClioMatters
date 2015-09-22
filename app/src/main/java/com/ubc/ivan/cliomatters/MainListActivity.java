package com.ubc.ivan.cliomatters;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainListActivity extends ListActivity {

/*    ###Credentials
    Use the following API method and the following API token to complete this task.
            Documentation:​
            ​
    http://api­docs.clio.com/v2/index.html#get­all­matters​
    <br>
    The following HTTP headers should be set for each request:
            "Authorization" => "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a"
            "Content­Type" => "application/json"
            "Accept" => "application/json"*/

    public static final String TAG = MainListActivity.class.getSimpleName();
    public static final int NUBMER_OF_MATTERS = 10;
    private static final String clioUrlString = "https://app.goclio.com/api/v2/matters";
    private static final String clioAUTH = "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a";
    protected String[] mMatters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        if (isNetworkAvailable()) {
            GetMattersTask getMattersTask = new GetMattersTask();
            getMattersTask.execute();
        } else {
            Toast.makeText(this, "Network is not available!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailabe = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailabe = true;
        }

        return isAvailabe;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetMattersTask extends AsyncTask {

        @Override
        protected String doInBackground(Object[] params) {
            int responseCode = -1;

            try {
                URL clioURL = new URL(clioUrlString);
                HttpURLConnection connection = (HttpURLConnection) clioURL.openConnection();
                connection.setRequestProperty("Authorization", clioAUTH);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();


                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Reader reader = new InputStreamReader(inputStream);
                    /*int contentLength = connection.getContentLength();
                    char[] charArray = new char[contentLength];
                    reader.read(charArray);
                    String responseData = new String(charArray);
                    Log.v(TAG, responseData);*/

                    //JSONObject jsonObject = new JSONObject(responseData);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String nextLine = "";
                    String result = "";

                    while ((nextLine = bufferedReader.readLine()) != null) {
                        result += nextLine;
                    }
                    Log.v(TAG, "Result: " + result);
                    inputStream.close();

                } else {
                    Log.i(TAG, "Unsuccusefull HTTP Response Code: " + responseCode);
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (IOException e) {
                Log.e(TAG, "Exception caught: ", e);
            } catch (Exception e) {
                Log.e(TAG, "Exception caught: ", e);
            }

            return "Code: " + responseCode;
        }
    }
}
