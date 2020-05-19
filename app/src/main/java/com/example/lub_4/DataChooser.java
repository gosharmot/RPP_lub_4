package com.example.lub_4;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DataChooser {
    private Calendar calendar = new GregorianCalendar();
    private Calendar calendarCurrent = new GregorianCalendar();

    public int dataLeft(int year, int month, int dayOfMonth) {
        calendar.set(dayOfMonth, (month + 1), year);
        long diffInMs = calendarCurrent.getTimeInMillis() - calendar.getTimeInMillis();
        int days = (int) (diffInMs / (24 * 60 * 60 * 1000));
        int daysLong=days+61;
        return daysLong;

    }
}

