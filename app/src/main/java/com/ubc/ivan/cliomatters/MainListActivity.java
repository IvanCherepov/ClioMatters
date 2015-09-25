package com.ubc.ivan.cliomatters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ubc.ivan.cliomatters.Controller.MattersController;
import com.ubc.ivan.cliomatters.Database.MatterDataSource;
import com.ubc.ivan.cliomatters.Model.Matter;
import com.ubc.ivan.cliomatters.View.MainListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainListActivity extends AppCompatActivity {

    public static final String TAG = MainListActivity.class.getSimpleName();

    public static final int NUBMER_OF_MATTERS = 100;
    private final String KEY_DISPLAY_NUMBER = "display_number";
    private final String KEY_DESCRIPTION = "description";
    public MattersController controller;
    protected Matter matter;
    protected Matter[] mMatters;
    protected String[] mMatterNumber;
    protected JSONObject mMattersData;
    protected ProgressBar mProgressBar;
    protected Context context = this;
    private ListView mListView;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mListView = (ListView) findViewById(android.R.id.list);

        controller = new MattersController(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMattersFromDatabase();
    }

    private void logException(Exception e) {
        Log.e(TAG, "Excepption caught! ", e);
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
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.action_refresh) {
/*            GetMattersTask getMattersTask = new GetMattersTask();
            getMattersTask.execute();*/

            NetworkHandler networkHandler = new NetworkHandler(this.context);

            if (networkHandler.isNetworkAvailable()) {
                GetMattersTask getMattersTask = new GetMattersTask();
                getMattersTask.execute();

            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                Toast.makeText(this, getString(R.string.network_unavailable_message),
                        Toast.LENGTH_LONG).show();
                getMattersFromDatabase();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDisplay() throws JSONException {
        if (mMattersData == null) {
            alertUserAboutError();
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
        MatterDataSource dataSource = new MatterDataSource(this);
        dataSource.createMatter(matter, counter);
    }


    private void getMattersFromDatabase() {
        final MatterDataSource dataSource = new MatterDataSource(this);
        ArrayList<Matter> matters = dataSource.readMatters();
        final ListAdapter listAdapter = new MainListAdapter(MainListActivity.this,
                R.layout.main_list_item, matters);
        mListView.setAdapter(listAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MatterDetailsActivity.class);
                intent.putExtra("Matter", dataSource.readMatter(position));

                startActivity(intent);
            }
        });


    }


    private void alertUserAboutError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error_title));
        builder.setMessage(getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView emptyTextView = (TextView) findViewById(R.id.empty);
        emptyTextView.setText(getString(R.string.no_items));
    }

    private class GetMattersTask extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(MainListActivity.this);
            mDialog.setCancelable(false);
            mDialog.setMessage("Download is in progress...");
            mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Object[] params) {
            return controller.getMatters();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mMattersData = result;

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            try {
                updateDisplay();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
