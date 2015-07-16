/**
 *
 */
package de.mundito.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author hweber1
 *         Project: OTC - HotasCtrl
 *         Implementor: ISB AG
 *         Create Date: 16.07.2015
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
        return localTimeStamp;
    }

    private Util() {
        // do not instantiate.
    }
}
