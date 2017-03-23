package com.bocha.organized.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oCocha on 31.01.2017.
 */

public class MiscUtility {
    /**Get the combined duration of a given hour and minute count in milliseconds
     *
     * @param durationHour
     * @param durationMinute
     * @return duration in milliseconds
     */
    public static Long getDurationMillis(int durationHour, int durationMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, durationHour);
        calendar.set(Calendar.MINUTE, durationMinute);
        Long startingDate = calendar.getTime().getTime();

        return startingDate;
    }

    /** Combine a given date, hour and minute to get a combined date from these values
     *
     * @param date
     * @param hour
     * @param minute
     * @return
     */
    public static Date getStartingDate(Date date, int hour, int minute) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        String yearString = dateString.substring(0, 4);
        String monthString = dateString.substring(5, 7);
        String dayString = dateString.substring(8);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, Integer.parseInt(monthString)-1);
        calendar.set(Calendar.YEAR, Integer.parseInt(yearString));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayString));
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        Date startingDate = calendar.getTime();

        return startingDate;
    }

    /**Convert a string date to a date object
     * format: yyyy-MM-dd:HH:mm
     *
     * @param dateString date string
     * @return date object
     */
    public static Date stringToDate(String dateString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                dateFormat, Locale.GERMAN);
        Date date = null;

        //dateString = dateString.substring(0, 10)+dateString.substring(13, 18);

        try{
            date = formatter.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }
}
