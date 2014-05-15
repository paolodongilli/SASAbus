
package it.sasabz.sasabus.logic;

import java.util.Calendar;

public class SASAbusTimeUtils
{
   public static long getDaySeconds()
   {
      Calendar c = Calendar.getInstance();
      long now = c.getTimeInMillis();
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND, 0);
      long midnight = c.getTimeInMillis();
      long secondsFromMidnight = (now - midnight) / 1000L;
      return secondsFromMidnight;
   }
}
