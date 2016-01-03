package com.github.aleneum.timesheetphd;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVFormat;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(2015, Calendar.JULY, 1);
        Calendar endCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");
        int expectedHours = 8;
        long expectedTimeS = 0;
        long workedTimeS = 0;
        double expectedOvertimePercentage = 0.0;

        try {
            InputStreamReader in = new InputStreamReader(this.getAssets().open("timesheet2.csv"));
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
            for (CSVRecord record : records) {
                //System.out.println(record.toString());
                Date date = dateFormat.parse(record.get("Date"));
                if (date.getTime() - startCalendar.getTime().getTime() > -60000 * 60 * 24) {
                    endCalendar.setTime(date);
                    String stringDuration = record.get("rel. Duration");
                    workedTimeS += durationFormat.parse(stringDuration).getTime() / 1000;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        int workingDays = Holidays.GERMANY_NRW.getBusinessDayCount(startCalendar.getTime(),
        endCalendar.getTime());
        expectedTimeS = workingDays * expectedHours * 60 * 60;
        long forfeitOvertimeS =
                (long) Math.floor(workingDays * expectedOvertimePercentage * expectedHours * 60 * 60);
        long overtimeS = workedTimeS - expectedTimeS;
        if (overtimeS > 0) {
            overtimeS = Math.max(0, overtimeS - forfeitOvertimeS);
        }

        System.out.println("Worked " + workedTimeS / 60 / 60);
        System.out.println("Expected " + expectedTimeS / 60 / 60);
        System.out.println("Overtime " + (workedTimeS - expectedTimeS) / 60 / 60);
        System.out.println("Rel. Overtime " + (overtimeS) / 60 / 60);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        TextView overtime = (TextView) findViewById(R.id.textBalance);
        System.out.println("HOURS: " + overtimeS / 60 / 60);
        System.out.println("MINUTES: " + overtimeS % 3600 / 60);
        System.out.println("SECONDS: " + overtimeS);

        overtime.setText(String.format("%d:%02d", overtimeS / 60 / 60, overtimeS % 3600 / 60));
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
                Uri.parse("android-app://com.github.aleneum.timesheetphd/http/host/path")
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
                Uri.parse("android-app://com.github.aleneum.timesheetphd/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}