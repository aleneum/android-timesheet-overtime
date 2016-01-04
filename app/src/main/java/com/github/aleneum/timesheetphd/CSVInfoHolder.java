package com.github.aleneum.timesheetphd;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by alneuman on 03/01/16.
 */
public class CSVInfoHolder {
    private List<TimesheetTask> tasks;

    public CSVInfoHolder() {
        this.tasks = new ArrayList<TimesheetTask>();
    }

    public CSVInfoHolder(List<TimesheetTask> tasks)  {
        this.tasks = tasks;
    }

    public void parse(InputStream in) {
        if (! tasks.isEmpty()) return;
        try {
            InputStreamReader inReader = new InputStreamReader(in);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(inReader);
            for (CSVRecord record : records) {
                Date date = Config.getInstance().getDateFormat().parse(record.get("Date"));
                String stringDuration = record.get("rel. Duration");
                long duration = Config.getInstance().getDurationFormat().parse(stringDuration).getTime();
                String tags = record.get("Tags");
                this.tasks.add(new TimesheetTask(date, duration, tags));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TimesheetTask> getFrom(String begin) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        try {
            Date startDate = Config.getInstance().getDateFormat().parse(begin);
            result = this.getPeriod(startDate,
                    this.tasks.get(this.tasks.size() - 1).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TimesheetTask> getUntil(String end) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        try {
            Date endDate = Config.getInstance().getDateFormat().parse(end);
            result = this.getPeriod(this.tasks.get(0).getDate(), endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TimesheetTask> getPeriod(String begin, String end) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        try {
            Date startDate = Config.getInstance().getDateFormat().parse(begin);
            Date endDate = Config.getInstance().getDateFormat().parse(end);
            result = this.getPeriod(startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TimesheetTask> getPeriod(Date start, Date end) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        for (TimesheetTask t: this.tasks) {
            Date d = t.getDate();
            if (start.getTime() <= d.getTime() &&
                    d.getTime() <= end.getTime()) {
                result.add(t);
            }
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
}
