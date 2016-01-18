package com.github.aleneum.timesheetdlc;

import android.util.ArraySet;
import android.util.Log;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alneuman on 03/01/16.
 */
public class CSVInfoHolder {
    public static final String TAG = "CSVInfoHolder";
    public static final String ALL = "_all";

    private Map<String, List<TimesheetTask>> tasks;
    private String currentProject = ALL;

    public CSVInfoHolder() {
        this.tasks = new HashMap<String, List<TimesheetTask>>();
        this.tasks.put(ALL, new ArrayList<TimesheetTask>());
    }

    public CSVInfoHolder(List<TimesheetTask> tasks)  {
        this();
        for (TimesheetTask t: tasks) {
            Log.d(TAG, t.getProject() + " : " + t.getDuration());
            if (!this.tasks.containsKey(t.getProject())) {
                this.tasks.put(t.getProject(), new ArrayList<TimesheetTask>());
            }
            this.tasks.get(t.getProject()).add(t);
            this.tasks.get(ALL).add(t);
        }
    }

    public void parse(InputStream in) {
        if (! tasks.get(ALL).isEmpty()) return;
        try {
            InputStreamReader inReader = new InputStreamReader(in);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(inReader);
            for (CSVRecord record : records) {
                Date date = Config.getInstance().getDateFormat().parse(record.get("Date"));
                String stringDuration = record.get("rel. Duration");
                long duration = Config.parseDuration(stringDuration);
                if (duration <= 0) return;
                String tags = record.get("Tags");
                String project = record.get("Project");
                TimesheetTask t = new TimesheetTask(date, duration, project, tags);
                if (!this.tasks.containsKey(project)) {
                    this.tasks.put(project, new ArrayList<TimesheetTask>());
                }
                this.tasks.get(project).add(t);
                this.tasks.get(ALL).add(t);
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
                    this.tasks.get(this.currentProject).get(this.tasks.size() - 1).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<TimesheetTask> getUntil(String end) {
        List<TimesheetTask> result = new ArrayList<TimesheetTask>();
        try {
            Date endDate = Config.getInstance().getDateFormat().parse(end);
            result = this.getPeriod(this.tasks.get(this.currentProject).get(0).getDate(), endDate);
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
        for (TimesheetTask t: this.tasks.get(this.currentProject)) {
            Date d = t.getDate();
            if (start.getTime() <= d.getTime() &&
                    d.getTime() <= end.getTime()) {
                result.add(t);
            }
        }
        return result;
    }

    public List<TimesheetTask> getTags(String[] tags) {
        return this.getTags(this.tasks.get(this.currentProject), tags);
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

    public ArrayList<String> getProjects() {
        ArrayList<String> list = this.asSortedList(this.tasks.keySet());
        list.remove(ALL);
        return list;
    }

    public String getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(String currentProject) {
        if (currentProject.length() > 0) {
            this.currentProject = currentProject;
        } else {
            this.currentProject = ALL;
        }
    }

    public static
    <T extends Comparable<? super T>> ArrayList<T> asSortedList(Collection<T> c) {
        ArrayList<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

}
