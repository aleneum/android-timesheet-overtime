package com.github.aleneum.timesheetdlc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "SettingsActivity";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private String currentProject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> projects = (ArrayList<String>) getIntent().getSerializableExtra("projects");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, projects);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerProjects);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_save) {
            saveAction();
            editor.commit();
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAction() {
        EditText hours = (EditText) findViewById(R.id.editExpectedHours);
        if (hours.getText().toString().length() < 1) return;

        EditText overtime = (EditText) findViewById(R.id.editExpectedOvertime);
        CheckBox workingDaysOnly = (CheckBox) findViewById(R.id.checkboxTurnus);
        editor.putInt(String.format("%s.hours", this.currentProject),
                Integer.parseInt(hours.getText().toString()));
        editor.putFloat(String.format("%s.overtime", this.currentProject),
                Float.parseFloat(overtime.getText().toString()));
        editor.putBoolean(String.format("%s.workingDaysOnly", this.currentProject),
                workingDaysOnly.isChecked());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        saveAction();
        String projectName = ((Spinner) findViewById(R.id.spinnerProjects))
                .getSelectedItem().toString();
        this.currentProject = projectName;
        ((EditText) findViewById(R.id.editExpectedHours)).setText(
                String.valueOf(sharedPref.getInt(String.format("%s.hours", projectName), 8)));
        ((EditText) findViewById(R.id.editExpectedOvertime)).setText(
                String.valueOf(sharedPref.getFloat(String.format("%s.overtime", projectName), 0.0f)));
        ((CheckBox) findViewById(R.id.checkboxTurnus)).setChecked(
                sharedPref.getBoolean(String.format("%s.workingDaysOnly", projectName), true));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i(TAG,"Nothing selected");
    }
}
