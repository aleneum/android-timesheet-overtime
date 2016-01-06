package com.github.aleneum.timesheetdlc;

import java.text.SimpleDateFormat;

/**
 * Created by alneuman on 04/01/16.
 */
public class Config {
    //create an object of SingleObject
    private static Config instance = new Config();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");

    public final long MS_EACH_HOUR = 3600000;

    //make the constructor private so that this class cannot be
    //instantiated
    private Config(){}

    //Get the only object available
    public static Config getInstance(){
        return instance;
    }

    public SimpleDateFormat getDurationFormat() {
        return this.durationFormat;
    }

    public void setDurationFormat(SimpleDateFormat durationFormat) {
        this.durationFormat = durationFormat;
    }

    public SimpleDateFormat getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}
