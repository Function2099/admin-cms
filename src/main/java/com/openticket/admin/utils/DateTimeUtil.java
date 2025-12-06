package com.openticket.admin.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未設定";
        }
        return dateTime.format(FORMATTER);
    }
}
