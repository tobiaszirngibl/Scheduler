package com.ase_1617.organizedlib.utility;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.ase_1617.organizedlib.data.CalEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class providing various utility methods:
 * - Get combined duration of hours and minutes in milliseconds
 * - Get combined date from date, hours and minutes
 * - Get string date from Date object
 * - Get combined date and time strings from a starting and an ending date
 * - Get string date from a milliseconds date
 * - Get info string from a calEvent object
 * - Format title and info texts for use in an alertdialog
 */

public class MiscUtility {

    /**
     * Get the combined duration of a given hour and minute count in milliseconds
     * @param durationHour The amount of hours to be added to the time count
     * @param durationMinute The amount of minutes to be added to the time count
     * @return duration in milliseconds
     */
    public static Long getDurationMillis(int durationHour, int durationMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, durationHour);
        calendar.set(Calendar.MINUTE, durationMinute);

        return calendar.getTime().getTime();
    }

    /**
     * Combine a given date, hour and minute to get a combined date from these values
     * @param date The given date object which is the base for the new combined date object
     * @param hour The amount of hours to be added to the new combined date object
     * @param minute The amount of mintues to be added to the new combined date object
     * @return The new combined date object
     */
    public static Date getStartingDate(Date date, int hour, int minute) {
        //Extract the year, month and day data from the given date obect
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
        String dateString = formatter.format(date);
        String yearString = dateString.substring(0, 4);
        String monthString = dateString.substring(5, 7);
        String dayString = dateString.substring(8);

        //Create a new calendar object from the given date data
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, Integer.parseInt(monthString)-1);
        calendar.set(Calendar.YEAR, Integer.parseInt(yearString));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayString));
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTime();
    }

    /**
     * Convert a string date to a date object.
     * @param dateString The date string
     * @param dateFormat The string describing the format of the date string
     * @return The new created date object
     */
    static Date stringToDate(String dateString, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                dateFormat, Locale.GERMAN);
        Date date = null;

        try{
            date = formatter.parse(dateString);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Convert a start and an end date in milliseconds to
     * a string array containing a date and a time string
     * format: dd/MM/yyyy
     * format: hh:mm
     *
     * @param startMillis starting date in milliseconds
     * @param endMillis ending date in milliseconds
     * @return string array
     */
    public static String[] calculateDate(Long startMillis, Long endMillis) {
        String[] dateData = new String[2];
        //Convert the given starting/ending dates in milliseconds to string dates
        String startString = millisToDate(startMillis);
        String endString = millisToDate(endMillis);
        //Extract the starting/ending dates from the string dates
        String date1 = startString.split(" ")[0];
        String date2 = endString.split(" ")[0];
        String time1 = startString.split(" ")[1];
        String time2 = endString.split(" ")[1];
        String dateString;
        String timeString;

        //Create the date and time string
        if(date1.equals(date2)){
            dateString = date1;
        }else{
            dateString = date1 + " - " + date2;
        }

        timeString = time1 + " - " +time2;

        //Save the string in an array
        dateData[0] = dateString;
        dateData[1] = timeString;

        return dateData;
    }

    /**
     * Convert a milliseconds date to a String date
     * format: dd/MM/yyyy hh:mm a
     * @param milliSeconds date in milliseconds
     * @return date as String
     */
    private static String millisToDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm a", Locale.GERMAN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * Create an info text from a given calEvent object
     * @param event The given calEvent object
     * @return The created info text string
     */
    public static String calEventToInfo(CalEvent event) {
        String eventInfo = "\n" + event.getEventLocation() + "\n";

        //Convert the starting/ending date to string dates
        String[] dateData = MiscUtility.calculateDate(event.getEventStartDate().getTime(), event.getEventEndDate().getTime());
        eventInfo += dateData[0] + "\n";
        eventInfo += dateData[1] + "\n";
        eventInfo += "" + event.getEventDescription() + "\n";

        return eventInfo;
    }

    /**
     * Format given event titles and info texts for use in alertDialogs.
     * @param titles Array of event titles to be formatted
     * @param infoTexts Array of event info texts to be formatted
     * @return The created spannableStringBuilder containing the event titles and infos texts
     */
    public static SpannableStringBuilder formatEventInfo(String[] titles, String[] infoTexts) {
        SpannableStringBuilder infoContent = new SpannableStringBuilder();
        int start = 0;

        //Format the info content
        //->Event titles bold and red
        for(int i = 0, j = titles.length; i < j; i++){
            infoContent.append(titles[i]);
            infoContent.setSpan(new ForegroundColorSpan(Color.RED), start, infoContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            infoContent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, infoContent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            infoContent.append(infoTexts[i]);
            start = infoContent.length();
        }

        return infoContent;
    }
}
