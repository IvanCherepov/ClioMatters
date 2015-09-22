package com.ubc.ivan.cliomatters;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;

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
    private final String KEY_DISPLAY_NUMBER = "display_number";
    private final String KEY_DESCRIPTION = "description";
    protected String[] mMatters;
    protected String[] mMatterNumber;
    protected JSONObject mMattersData;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetMattersTask getMattersTask = new GetMattersTask();
            getMattersTask.execute();
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
        }
        //  pass the context from Activity to the non Activity class
        //NetworkHandler networkHandler = new NetworkHandler(this);
        //networkHandler.getJSON(MattersApiConstants.CLIO_URL);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        JSONArray jsonMatters = null;
        try {
            jsonMatters = mMattersData.getJSONArray("matters");
            JSONObject jsonMatter = jsonMatters.getJSONObject(position);
            String matterID = jsonMatter.getString("id");

            Intent intent = new Intent(this, MatterDetailsActivity.class);
            //intent.setData(Uri.parse("http:google.com"));
            intent.setType(matterID);
            startActivity(intent);

        } catch (JSONException e) {
            logException(e);
        }
    }

    private void logException(Exception e) {
        Log.e(TAG, "Excepption caught! ", e);
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

    private void updateDisplayforError() throws JSONException {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mMattersData == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_title));
            builder.setMessage(getString(R.string.error_message));
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();

            TextView emptyTextView = (TextView) getListView().getEmptyView();
            emptyTextView.setText(getString(R.string.no_items));
        } else {
            JSONArray jsonMatters = mMattersData.getJSONArray("matters");
            ArrayList<HashMap<String, String>> matters =
                    new ArrayList<HashMap<String, String>>();

            mMatterNumber = new String[jsonMatters.length()];
            for (int i = 0; i < jsonMatters.length(); i++) {
                JSONObject m = jsonMatters.getJSONObject(i);
                String display_number = m.getString(KEY_DISPLAY_NUMBER);
                display_number = Html.fromHtml(display_number).toString();
                String description = m.getString(KEY_DESCRIPTION);
                description = Html.fromHtml(description).toString();
                mMatterNumber[i] = display_number;

                HashMap<String, String> matter = new HashMap<String, String>();
                matter.put(KEY_DISPLAY_NUMBER, display_number);
                matter.put(KEY_DESCRIPTION, description);

                matters.add(matter);

            }

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            //        android.R.layout.simple_list_item_1, mMatterNumber);

            String[] keys = {KEY_DISPLAY_NUMBER, KEY_DESCRIPTION};
            int[] ids = {android.R.id.text1, android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter(this, matters,
                    android.R.layout.simple_list_item_2, keys, ids);
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
                logException(e);
            } catch (IOException e) {
                logException(e);
            } catch (Exception e) {
                logException(e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mMattersData = result;

            try {
                updateDisplayforError();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
