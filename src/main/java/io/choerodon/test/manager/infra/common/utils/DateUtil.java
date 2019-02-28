package io.choerodon.test.manager.infra.common.utils;

import java.util.Date;

/**
 * Created by WangZhe@choerodon.io on 2019-02-28.
 * Email: ettwz@hotmail.com
 */
public class DateUtil {
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    public static Date increaseDaysOnDate(Date date, int num) {
        long result = date.getTime() + num * 1000 * 3600 * 24;
        return new Date(result);
    }
}
