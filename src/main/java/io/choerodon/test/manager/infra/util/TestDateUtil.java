package io.choerodon.test.manager.infra.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by WangZhe@choerodon.io on 2019-02-28.
 * Email: ettwz@hotmail.com
 */
public class TestDateUtil {
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    public static Date increaseDaysOnDate(Date date, int num) {
        long result = date.getTime() + num * 1000 * 3600 * 24;
        return new Date(result);
    }

    public static Date formatDate(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        return cal1.getTime();
    }
}
