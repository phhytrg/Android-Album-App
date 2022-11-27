package com.example.album.gallery;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtils {

    public static String formatDate(@NonNull LocalDateTime date) {
        long millis = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if(isToday(millis)){
            return "Today";
        }
        if(isYesterday(millis)){
            return "Yesterday";
        }
        if(isSameWeek(date)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
            return date.format(formatter);
        }
        if(isSameYear(date)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL dd");
            return date.format(formatter);
        }
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    private static boolean isToday(long millis) {
        LocalDateTime today = LocalDateTime.now();
        long todayMillis = today.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return isSameDate(todayMillis, millis);
    }

    private static boolean isYesterday(long millis){
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

        long yesterdayMillis = yesterday.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return isSameDate(millis,yesterdayMillis);
    }

    private static boolean isSameWeek(@NonNull LocalDateTime dateTime){
        LocalDateTime now = LocalDateTime.now();
        int d1 = now.getDayOfWeek().getValue();
        int d2 = now.getDayOfMonth();
        int start = d2 - d1 + 1;
        return dateTime.getDayOfMonth() >= start && dateTime.getDayOfMonth() <= start + 7 - 1
                && dateTime.getMonthValue() == now.getMonthValue()
                && dateTime.getYear() == now.getYear();
    }

    private static boolean isSameYear(@NonNull LocalDateTime dateTime){
        return dateTime.getYear() == Year.now().getValue();
    }

    private static boolean isSameDate(long oneMillis, long twoMillis) {
        ZoneId zoneId = ZoneId.systemDefault();

        Instant oneInstant = Instant.ofEpochMilli(oneMillis);
        LocalDateTime oneLocalDateTime = LocalDateTime.ofInstant(oneInstant, zoneId);

        Instant twoInstant = Instant.ofEpochMilli(twoMillis);
        LocalDateTime twoLocalDateTime = LocalDateTime.ofInstant(twoInstant, zoneId);

        return (oneLocalDateTime.getYear() == twoLocalDateTime.getYear())
                && (oneLocalDateTime.getMonthValue() == twoLocalDateTime.getMonthValue())
                && (oneLocalDateTime.getDayOfMonth() == twoLocalDateTime.getDayOfMonth());
    }
}