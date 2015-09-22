package com.ubc.ivan.cliomatters;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static final int NUBMER_OF_MATTERS = 100;

    protected String[] mMatters;
    protected String[] mMatterNumber;
    protected JSONObject mMattersData;

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
        //  pass the context from Activity to the non Activity class
        //NetworkHandler networkHandler = new NetworkHandler(this);
        //networkHandler.getJSON(MattersApiConstants.CLIO_URL);
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


    private void updateList() throws JSONException {
        if (mMattersData == null) {
            // TODO: handle error
        } else {
            JSONArray jsonMatters = mMattersData.getJSONArray("matters");
            mMatterNumber = new String[jsonMatters.length()];
            for (int i = 0; i < jsonMatters.length(); i++) {
                JSONObject matter = jsonMatters.getJSONObject(i);
                String display_number = matter.getString("display_number");
                display_number = Html.fromHtml(display_number).toString();
                mMatterNumber[i] = display_number;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, mMatterNumber);

            setListAdapter(adapter);
            Log.d(TAG, mMattersData.toString(2));
        }
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

            try {
                updateList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
