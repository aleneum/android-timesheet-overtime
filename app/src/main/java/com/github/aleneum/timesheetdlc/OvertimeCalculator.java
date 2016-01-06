package com.github.aleneum.timesheetdlc;

import java.util.List;

/**
 * Created by alneuman on 04/01/16.
 */
public class OvertimeCalculator {
    CSVInfoHolder holder;
    long workingTime = 0;
    long expectedTime = 0;
    long overtime = 0;
    int expectedHours = 0;
    double expectedOvertimePercentage = 0.0;

    public OvertimeCalculator(CSVInfoHolder holder) {
        this(holder, 8);
    }

    public OvertimeCalculator(CSVInfoHolder holder, int hours) {
        this(holder, hours, 0);
    }

    public OvertimeCalculator(CSVInfoHolder holder, int hours, double overtime) {
        this.holder = holder;
        this.expectedHours = hours;
        this.expectedOvertimePercentage = overtime;
    }

    public void process(String start, String end, String[] tags) {
        List<TimesheetTask> tasks = this.holder.getPeriod(start, end);
        tasks = CSVInfoHolder.getTags(tasks, tags);
        this.process(tasks, start, end);
    }

    public void process(String start, String end) {
        if (start.length() > 0) {
            this.process(this.holder.getPeriod(start, end), start, end);
        } else {
            List<TimesheetTask> tasks = this.holder.getUntil(end);
            this.process(tasks, tasks.get(0).getDateAsString(), end);
        }
    }

    private void process(List<TimesheetTask> tasks, String start, String end) {
        this.workingTime = 0;
        for (TimesheetTask t: tasks) {
            this.workingTime += t.getDuration();
        }
        this.expectedTime = Holidays.GERMANY_NRW.getBusinessDayCount(start, end)
                * expectedHours * Config.getInstance().MS_EACH_HOUR;
        this.overtime = this.workingTime - this.expectedTime;
        if (this.overtime > 0) {
            this.overtime -= this.expectedTime * this.expectedOvertimePercentage;
        }
    }

    public long getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(long workingTime) {
        this.workingTime = workingTime;
    }

    public long getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(long expectedTime) {
        this.expectedTime = expectedTime;
    }

    public long getOvertime() {
        return overtime;
    }

    public void setOvertime(long overtime) {
        this.overtime = overtime;
    }

    public int getExpectedHours() {
        return expectedHours;
    }

    public void setExpectedHours(int expectedHours) {
        this.expectedHours = expectedHours;
    }

    public double getExpectedOvertimePercentage() {
        return expectedOvertimePercentage;
    }

    public void setExpectedOvertimePercentage(double expectedOvertimePercentage) {
        this.expectedOvertimePercentage = expectedOvertimePercentage;
    }
}
