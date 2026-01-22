package com.xinling.common.utils;


import cn.hutool.core.date.ChineseDate;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 节日工具类
 *
 * @author xinling
 * @date 2025/12/17 10:11
 */
public class FestivalUtil {

    /**
     * 节假日倒计时
     */
    public static void printHolidayCountdowns(LocalDate today) {
        int currentYear = today.getYear();
        Map<String, LocalDate> holidays = new HashMap<>();
        holidays.put("元旦节", LocalDate.of(currentYear, 1, 1));
        holidays.put("春节", getNextChineseNewYear(today));
        holidays.put("清明节", getNextQingMing(today));
        holidays.put("劳动节", LocalDate.of(currentYear, 5, 1));
        holidays.put("中秋节", getNextMidAutumn(today));
        holidays.put("国庆节", LocalDate.of(currentYear, 10, 1));

        Map<String, Long> countdowns = new HashMap<>();
        for (Map.Entry<String, LocalDate> entry : holidays.entrySet()) {
            LocalDate holidayDate = entry.getValue();
            if (holidayDate.isBefore(today)) {
                holidayDate = holidayDate.plusYears(1);
            }
            long days = Duration.between(
                    LocalDateTime.now(),
                    holidayDate.atTime(LocalTime.MIDNIGHT).plusDays(1)
            ).toDays();
            countdowns.put(entry.getKey(), days);
        }

        countdowns.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e ->
                        System.out.println(e.getKey() + "倒计时: " + e.getValue() + "天"));
    }


    public static LocalDate getNextChineseNewYear(LocalDate today) {
        int currentYear = today.getYear();
        ChineseDate newYear = new ChineseDate(currentYear, 1, 1, false);
        LocalDate date = newYear.getGregorianDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        if (date.isBefore(today)) {
            newYear = new ChineseDate(currentYear + 1, 1, 1, false);
            date = newYear.getGregorianDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date;
    }

    public static LocalDate getNextQingMing(LocalDate today) {
        LocalDate date = LocalDate.of(today.getYear(), 4, 4);
        return date.isBefore(today) ? date.plusYears(1) : date;
    }

    public static LocalDate getNextMidAutumn(LocalDate today) {
        int currentYear = today.getYear();
        ChineseDate midAutumn = new ChineseDate(currentYear, 8, 15, false);
        LocalDate date = midAutumn.getGregorianDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        if (date.isBefore(today)) {
            midAutumn = new ChineseDate(currentYear + 1, 8, 15, false);
            date = midAutumn.getGregorianDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date;
    }


    /**
     * 获取节假日倒计时（用于Web接口）
     */
    public static Map<String, Long> getHolidayCountdowns(LocalDate today) {
        int currentYear = today.getYear();
        Map<String, LocalDate> holidays = new HashMap<>();
        holidays.put("newYearsDay", LocalDate.of(currentYear, 1, 1));// 元旦
        holidays.put("newYear", FestivalUtil.getNextChineseNewYear(today)); // 春节
        holidays.put("qingming", FestivalUtil.getNextQingMing(   today)); // 清明节
        holidays.put("laborDay", LocalDate.of(currentYear, 5, 1)); // 劳动节
        holidays.put("nextMidAutumn", FestivalUtil.getNextMidAutumn(today)); // 中秋节
        holidays.put("nationalDay", LocalDate.of(currentYear, 10, 1));  // 国庆节

        Map<String, Long> countdowns = new HashMap<>();
        for (Map.Entry<String, LocalDate> entry : holidays.entrySet()) {
            LocalDate holidayDate = entry.getValue();
            if (holidayDate.isBefore(today)) {
                holidayDate = holidayDate.plusYears(1);
            }
            long days = Duration.between(
                    LocalDateTime.now(),
                    holidayDate.atTime(LocalTime.MIDNIGHT).plusDays(1)
            ).toDays();
            countdowns.put(entry.getKey(), days);
        }

        return countdowns;
    }
}
