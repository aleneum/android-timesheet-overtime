package com.github.aleneum.timesheetdlc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "MainActivity";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private CSVInfoHolder info = new CSVInfoHolder();
    private OvertimeCalculator calculator = new OvertimeCalculator(info);
    private SharedPreferences sharedPref;
    private TableRow.LayoutParams col1, col2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        col1 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        col1.column = 1;
        col2 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        col2.setMargins(10, 0, 0, 0);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/csv".equals(type)) {
                Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                try {
                    File f = new File(uri.getEncodedPath());
                    FileInputStream inStream = new FileInputStream(f);
                    FileOutputStream outStream = openFileOutput("timesheetdata.csv", MODE_PRIVATE);
                    FileChannel inChannel = inStream.getChannel();
                    FileChannel outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inStream.close();
                    outStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        try {
            File last = new File(getFilesDir(), "timesheetdata.csv");
            InputStream in;
            if (last.exists()) {
                in = new FileInputStream(last);
            } else {
                in = this.getAssets().open("timesheet.csv");
            }
            info = new CSVInfoHolder();
            info.parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerRange);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(sharedPref.getInt("spinnerPos", 0));

    }

    public void updateBalance(String start, String end) {
        double ms = Config.getInstance().MS_EACH_HOUR;

        CSVInfoHolder holder = new CSVInfoHolder(this.info.getPeriod(start, end));
        calculator.setHolder(holder);
        TableLayout table = (TableLayout) findViewById(R.id.tableOverview);
        table.removeAllViews();

        for (String project: holder.getProjects()) {
            Log.i(TAG, "project: " + project);
            holder.setCurrentProject(project);
            calculator.setExpectedHours(sharedPref.getInt(String.format("%s.hours", project), 8));
            calculator.setExpectedOvertimePercentage(sharedPref.getFloat(
                    String.format("%s.overtime", project), 1));
            calculator.setWorkingDaysOnly(sharedPref.getBoolean(
                    String.format("%s.workingDaysOnly", project), true));
            calculator.process(start, end);
            Log.i(TAG, "overtime: " + calculator.getOvertime());

            TableRow row = new TableRow(this);
            TextView projectName = new TextView(this);
            projectName.setTextSize(20);
            TextView balance = new TextView(this);
            balance.setTextSize(20);

            projectName.setText(project);

            String sign = "";
            if (calculator.getOvertime() < 0) {
                balance.setTextColor(Color.RED);
                sign += "-";
            } else {
                balance.setTextColor(Color.BLACK);
                sign += "  ";
            }
            int hours = (int) Math.abs(calculator.getOvertime() / ms);
            int minutes = (int) Math.abs(calculator.getOvertime() % 3600000 / 60000);
            balance.setText(String.format("%s%d:%02d", sign, hours, minutes));

            row.addView(projectName, col1);
            row.addView(balance, col2);
            table.addView(row);
        }

//        Log.i(TAG, "Balance from " + start + " to " + end);
//        calculator.process(start, end);
//
//
//        Log.i(TAG, "Worked: " + calculator.getWorkingTime() / ms);
//        Log.i(TAG, "Expected: " + calculator.getExpectedTime() / ms);
//        Log.i(TAG, "Overtime: " + calculator.getOvertime() / ms);
//
//        TextView balance = (TextView) findViewById(R.id.textBalance);
//        String sign = "";
//
//        if (calculator.getOvertime() < 0) {
//            balance.setTextColor(Color.RED);
//            sign += "-";
//        } else {
//            balance.setTextColor(Color.BLACK);
//        }
//
//        int hours = (int) Math.abs(calculator.getOvertime() / ms);
//        int minutes = (int) Math.abs(calculator.getOvertime() % 3600000 / 60000);
//
//        balance.setText(String.format("%s%d:%02d", sign, hours, minutes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("projects", this.info.getProjects());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.github.aleneum.timesheetdlc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.github.aleneum.timesheetdlc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String start = "";
        String end = Config.getInstance().getDateFormat().format(new Date()).toString();
        Calendar cal = Calendar.getInstance();
        switch (position) {
            case 0: // This Week
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                start = Config.getInstance().getDateFormat().format(cal.getTime());
                break;
            case 1: // This Month
                cal.set(Calendar.DAY_OF_MONTH, 1);
                start = Config.getInstance().getDateFormat().format(cal.getTime());
                break;
            case 2: // This Year
                cal.set(Calendar.DAY_OF_YEAR, 1);
                start = Config.getInstance().getDateFormat().format(cal.getTime());
                break;
            case 3: // Last Week
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                end = Config.getInstance().getDateFormat().format(cal.getTime());
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.add(Calendar.WEEK_OF_YEAR, -1);
                start = Config.getInstance().getDateFormat().format(cal.getTime());
                break;
            case 4: // Last Month
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                start = Config.getInstance().getDateFormat().format(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                end = Config.getInstance().getDateFormat().format(cal.getTime());
            default: // Everything
                break;
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("spinnerPos", position);
        editor.commit();
        updateBalance(start, end);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}