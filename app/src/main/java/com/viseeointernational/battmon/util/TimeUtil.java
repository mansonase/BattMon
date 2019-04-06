package com.viseeointernational.battmon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static int getDaysOfMonth(int year, int month) {
        int realMonth = month + 1;
        switch (realMonth) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (isLeap(year)) {
                    return 29;
                } else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return -1;
    }

    public static boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static String getFormatTime(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));
    }

    public static String getEnglishMonth(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "Augest";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
        }
        return "";
    }

    public static String getEnglishMonthAbbr(int month) {
        switch (month) {
            case 0:
                return "Jan.";
            case 1:
                return "Feb.";
            case 2:
                return "Mar.";
            case 3:
                return "Apr.";
            case 4:
                return "May.";
            case 5:
                return "Jun.";
            case 6:
                return "Jul.";
            case 7:
                return "Aug.";
            case 8:
                return "Sep.";
            case 9:
                return "Oct.";
            case 10:
                return "Nov.";
            case 11:
                return "Dec.";
        }
        return "";
    }
}
