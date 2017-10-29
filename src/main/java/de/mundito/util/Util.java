package de.mundito.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *  webbasan, 2015-07-16
 */
public final class Util {

    public static Calendar createLocalCalendar() {
        return Calendar.getInstance();
    }

    public static Calendar createUtcCalendar() {
        Calendar localTimeStamp = Calendar.getInstance();
        TimeZone tz = localTimeStamp.getTimeZone();
        int offsetMillis = tz.getRawOffset() + (tz.inDaylightTime(localTimeStamp.getTime()) ? tz.getDSTSavings() : 0);
        int offsetHrs = offsetMillis / 1000 / 60 / 60;
        int offsetMins = offsetMillis / 1000 / 60 % 60;

        localTimeStamp.add(Calendar.HOUR_OF_DAY, -offsetHrs);
        localTimeStamp.add(Calendar.MINUTE, -offsetMins);
        localTimeStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
        return localTimeStamp;
    }

    public static void log(final String msg) {
        System.err.printf("%s: %s\n", DateFormat.getDateTimeInstance().format(new Date()), msg);
    }

    private Util() {
        // do not instantiate.
    }
}
