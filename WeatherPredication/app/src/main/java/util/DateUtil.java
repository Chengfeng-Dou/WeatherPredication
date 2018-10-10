package util;

import java.util.Calendar;

/**
 * Created by douchengfeng on 2018/10/10.
 *
 */

public class DateUtil {

    public static boolean isDayTime(){
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 0 && hour < 6) || (hour >= 18 && hour< 24);
    }


}
