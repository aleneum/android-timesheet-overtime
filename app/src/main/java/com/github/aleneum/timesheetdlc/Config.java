package com.github.aleneum.timesheetdlc;

import java.text.SimpleDateFormat;

/**
 * Created by alneuman on 04/01/16.
 */
public class Config {
    //create an object of SingleObject
    private static Config instance = new Config();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public final long MS_EACH_HOUR = 3600000;

    //make the constructor private so that this class cannot be
    //instantiated
    private Config(){}

    //Get the only object available
    public static Config getInstance(){
        return instance;
    }

    public SimpleDateFormat getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public static long parseDuration(String durationString) {
        String[] tokens = durationString.split(":");
        int hours = Integer.parseInt(tokens[0]);
        int minutes = Integer.parseInt(tokens[1]);
        int seconds = Integer.parseInt(tokens[2]);
        long duration = 3600000 * hours + 60000 * minutes + seconds;
        return duration;
    }
}
