package com.github.aleneum.timesheetphd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by alneuman on 03/01/16.
 */
public class CSVInfoHolder {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");

    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();
    private List<TimesheetTask> tasks = new ArrayList<TimesheetTask>();

    public CSVInfoHolder() throws ParseException {
        this("01.01.2000");
    }

    public CSVInfoHolder(String startDate) throws ParseException {
        this.startCalendar.setTime(dateFormat.parse(startDate));
    }

    public CSVInfoHolder(List<TimesheetTask> tasks) throws IndexOutOfBoundsException {
        this.tasks = tasks;
        this.startCalendar.setTime(tasks.get(0).getDate());
        this.endCalendar.setTime(tasks.get(tasks.size() - 1).getDate());
    }

    public void parse(InputStream in) {
        if (! tasks.isEmpty()) return;
        try {
            InputStreamReader inReader = new InputStreamReader(in);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(inReader);
            for (CSVRecord record : records) {
                //System.out.println(record.toString());
                Date date = dateFormat.parse(record.get("Date"));
                if (date.getTime() - startCalendar.getTime().getTime() > -60000 * 60 * 24) {
                    String stringDuration = record.get("rel. Duration");
                    long duration = durationFormat.parse(stringDuration).getTime();
                    String tags = record.get("Tags");
                    this.tasks.add(new TimesheetTask(date, duration, tags));
                }
            }
            if (this.tasks.isEmpty()) {
                endCalendar.setTime(startCalendar.getTime());
            } else {
                endCalendar.setTime(this.tasks.get(this.tasks.size()-1).getDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TimesheetTask> getPeriod(String begin, String end) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();

        Date startDate = null;
        try {
            startDate = dateFormat.parse(begin);
            Date endDate = dateFormat.parse(end);
            for (TimesheetTask t: this.tasks) {
                Date d = t.getDate();
                if (startDate.getTime() <= d.getTime() &&
                        d.getTime() <= endDate.getTime()) {
                    result.add(t);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<TimesheetTask> getTags(String[] tags) {
        return this.getTags(this.tasks, tags);
    }

    public static List<TimesheetTask> getTags(List<TimesheetTask> tasks, String[] tags) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        List<String> tagList = Arrays.asList(tags);
        for (TimesheetTask t: tasks) {
            if (!Collections.disjoint(t.getTags(), tagList)) {
                result.add(t);
            }
        }
        return result;
    }

    public long getWorkingTime(String start, String end, String[] tags) {
        List<TimesheetTask> tasks = this.getPeriod(start, end);
        tasks = CSVInfoHolder.getTags(tasks, tags);
        return CSVInfoHolder.getWorkingTime(tasks);
    }

    public long getWorkingTime(String start, String end) {
        return CSVInfoHolder.getWorkingTime(this.getPeriod(start, end));
    }

    public static long getWorkingTime(List<TimesheetTask> tasks) {
        long workedMS = 0;
        for (TimesheetTask t: tasks) {
            workedMS += t.getDuration();
        }
        return workedMS;
    }
}
