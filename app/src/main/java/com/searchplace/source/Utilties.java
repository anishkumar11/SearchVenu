package com.searchplace.source;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Login on 12/24/2016.
 */

public final class Utilties {

    public static String getVersionCode()
    {
        Date date = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        try
        {
            int dateValue  = cal.get(Calendar.DATE);
            String dateStr = ""+dateValue;
            if(dateValue < 10)
            {
                dateStr = "0"+dateValue;
            }

            int monthValue  = cal.get(Calendar.MONTH)+1;
            String monthStr = ""+monthValue;
            if(monthValue < 10)
            {
                monthStr = "0"+monthValue;
            }
            return cal.get(Calendar.YEAR)+monthStr+dateStr;
        }
        catch(Exception e)
        {
        }
        return date.toString();
    }

}
