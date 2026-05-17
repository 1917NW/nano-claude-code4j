package com.lxy.utils;

import cn.hutool.cron.pattern.CronPattern;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class CronUtils {

    public static LocalDateTime nextTime(String cron, LocalDateTime now) {
        CronPattern pattern = CronPattern.of(cron);

        Calendar calendar = Calendar.getInstance();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        calendar.setTime(date);

        Calendar next = pattern.nextMatchAfter(calendar);
        return LocalDateTime.ofInstant(next.toInstant(), ZoneId.systemDefault());
    }

    public static void main(String[] args) {
        LocalDateTime next = CronUtils.nextTime("30 * * * *", LocalDateTime.now());
        System.out.println(next);
    }
}
