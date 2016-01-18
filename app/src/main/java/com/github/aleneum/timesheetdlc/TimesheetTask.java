package com.github.aleneum.timesheetdlc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alneuman on 03/01/16.
 */
public class TimesheetTask {
    private Date date;
    private long duration;
    private List<String> tags;
    private String project;

    public TimesheetTask(Date date, long duration, String project, String tags) {
        this.date = date;
        this.duration = duration;
        this.project = project;
        List<String> newList = new ArrayList<String>();
        for (String tag: tags.split(",")) {
            newList.add(tag.trim());
        }
    }

    // auto-generated getter/setter
    public Date getDate() {
        return date;
    }

    public String getDateAsString() {
        return Config.getInstance().getDateFormat().format(this.date).toString();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getProject() { return this.project; }

    public void setProject(String project) { this.project = project; }

}
