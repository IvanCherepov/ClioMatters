package com.ubc.ivan.cliomatters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ubc.ivan.cliomatters.Database.MatterSQLHelper;
import com.ubc.ivan.cliomatters.Model.Matter;
import com.ubc.ivan.cliomatters.View.MainListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainListActivity extends AppCompatActivity {

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

    protected Matter matter;
    protected Matter[] mMatters;
    protected String[] mMatterNumber;
    protected JSONObject mMattersData;
    protected ProgressBar mProgressBar;
    protected Context context = this;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView) findViewById(android.R.id.list);

        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            GetMattersTask getMattersTask = new GetMattersTask();
            getMattersTask.execute();
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
            getMattersFromDatabase();
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        //  pass the context from Activity to the non Activity class
        //NetworkHandler networkHandler = new NetworkHandler(this);
        //networkHandler.getJSON(MattersApiConstants.CLIO_URL);
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

    private void updateDisplay() throws JSONException {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mMattersData == null) {
            Toast.makeText(this, getString(R.string.app_difficulties_message), Toast.LENGTH_LONG).show();
        } else {
            JSONArray jsonMatters = mMattersData.getJSONArray("matters");

            for (int i = 0; i < jsonMatters.length(); i++) {
                JSONObject jsonMatter = jsonMatters.getJSONObject(i);
                JSONObject jsonClient = jsonMatter.getJSONObject("client");
                matter = new Matter(jsonMatter.getInt("id"),
                        jsonMatter.getString("display_number"),
                        jsonClient.getString("name"),
                        jsonMatter.getString("description"),
                        jsonMatter.getString("open_date"),
                        jsonMatter.getString("status"),
                        jsonMatter.getBoolean("billable"),
                        jsonMatter.getString("practice_area"));

                addMatterToDatabase(matter, i + 1);
            }

            getMattersFromDatabase();
        }
    }

    private void addMatterToDatabase(Matter matter, int counter) {
        MatterSQLHelper matterSQLHelper = new MatterSQLHelper(context);

        SQLiteDatabase database = matterSQLHelper.getWritableDatabase();

        ContentValues conventValues = new ContentValues();
        conventValues.put(MatterSQLHelper.COLUMN_ID, counter);
        conventValues.put(MatterSQLHelper.COLUMN_MATTER_ID, matter.getId());
        conventValues.put(MatterSQLHelper.COLUMN_DISPLAY_NUMBER, matter.getDisplayName());
        conventValues.put(MatterSQLHelper.COLUMN_CLIENT_NAME, matter.getClientName());
        conventValues.put(MatterSQLHelper.COLUMN_DESCRIPTION, matter.getDescription());
        conventValues.put(MatterSQLHelper.COLUMN_OPEN_DATE, matter.getOpenDate());
        conventValues.put(MatterSQLHelper.COLUMN_OPEN_STATUS, matter.getStatus());
        conventValues.put(MatterSQLHelper.COLUMN_BILLABLE, matter.getBillable());
        conventValues.put(MatterSQLHelper.COLUMN_PRACTICE_AREA, matter.getPracticeArea());

        database.insert(MatterSQLHelper.TABLE_MATTERS, null, conventValues);
        database.close();
    }

    private int getIntFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getInt(columnIndex);
    }

    private String getStringFromColumnName(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return cursor.getString(columnIndex);
    }

    private void getMattersFromDatabase() {
        MatterSQLHelper matterSQLHelper = new MatterSQLHelper(context);

        final SQLiteDatabase database = matterSQLHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + MatterSQLHelper.TABLE_MATTERS, null);

        ArrayList<Matter> matters = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Matter matter = new Matter(getIntFromColumnName(cursor, MatterSQLHelper.COLUMN_MATTER_ID),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_DISPLAY_NUMBER),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_CLIENT_NAME),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_DESCRIPTION),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_OPEN_DATE),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_OPEN_STATUS),
                        Boolean.getBoolean(getStringFromColumnName(cursor,
                                MatterSQLHelper.COLUMN_BILLABLE)),
                        getStringFromColumnName(cursor, MatterSQLHelper.COLUMN_PRACTICE_AREA));
                matters.add(matter);

            } while (cursor.moveToNext());
        }

        ListAdapter listAdapter = new MainListAdapter(MainListActivity.this,
                R.layout.main_list_item, matters);
        mListView.setAdapter(listAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MatterDetailsActivity.class);
                //intent.setData(Uri.parse("http:google.com"));

                Cursor cursor = database.rawQuery("SELECT * FROM " +
                        MatterSQLHelper.TABLE_MATTERS +
                        " WHERE _id = " + Integer.toString(position + 1), null);

                if (cursor.moveToFirst()) {
                    Matter matter = new Matter(cursor.getInt(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_MATTER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_DISPLAY_NUMBER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_CLIENT_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_OPEN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_OPEN_STATUS)),
                            Boolean.getBoolean(cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_BILLABLE))),
                            cursor.getString(cursor.getColumnIndexOrThrow(MatterSQLHelper.COLUMN_PRACTICE_AREA)));

                    intent.putExtra("Matter", matter);

                    startActivity(intent);
                }
            }
        });


    }

/*    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        JSONArray jsonMatters = null;
        try {
            jsonMatters = mMattersData.getJSONArray("matters");
            JSONObject jsonMatter = jsonMatters.getJSONObject(position);
            String matterID = jsonMatter.getString("id");

            Intent intent = new Intent(this, MatterDetailsActivity.class);
            intent.setType(matterID);
            startActivity(intent);

        } catch (JSONException e) {
            logException(e);
        }
    }*/

    private void alertUserAboutError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

/*        TextView emptyTextView = (TextView) findViewById(R.id.empty);
        emptyTextView.setText(getString(R.string.no_items));*/
    }

    private class GetMattersTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object[] params) {
            int responseCode;
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

            } catch (Exception e) {
                logException(e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mMattersData = result;

            try {
                updateDisplay();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
