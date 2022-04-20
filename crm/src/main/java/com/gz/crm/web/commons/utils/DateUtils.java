package com.gz.crm.web.commons.utils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String formatDateTime(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        String s = sf.format(date);
        return s;
    }



}
