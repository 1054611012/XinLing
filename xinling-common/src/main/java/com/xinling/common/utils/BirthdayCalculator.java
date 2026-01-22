package com.xinling.common.utils;

import cn.hutool.core.date.ChineseDate;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 生日计算工具类
 * 支持阳历/农历生日、星座与节日倒计时
 */
public class BirthdayCalculator {

    /**
     * 计算农历对应阳历生日
     */
    public static LocalDate calculateLunarToSolar(LocalDate today, ChineseDate birthLunar) {
        int lunarYear = new ChineseDate(today).getChineseYear();
        try {
            return new ChineseDate(lunarYear, birthLunar.getMonth(), birthLunar.getDay(), birthLunar.isLeapMonth())
                    .getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return new ChineseDate(lunarYear, birthLunar.getMonth(), birthLunar.getDay(), false)
                    .getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }


    /**
     * 计算下一次农历生日阳历日期
     */
    public static LocalDate calculateNextLunarBirthday(LocalDate today, ChineseDate birthLunar) {
        int nextLunarYear = new ChineseDate(today).getChineseYear() + 1;
        try {
            return new ChineseDate(nextLunarYear, birthLunar.getMonth(), birthLunar.getDay(), birthLunar.isLeapMonth())
                    .getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return new ChineseDate(nextLunarYear, birthLunar.getMonth(), birthLunar.getDay(), false)
                    .getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    /**
     * 调整闰年2月29日生日情况
     */
    public static LocalDate adjustSolarBirthday(LocalDate birthDate, int year) {
        try {
            return birthDate.withYear(year);
        } catch (DateTimeException e) {
            return LocalDate.of(year, 2, 28);
        }
    }


    /**
     * Web接口调用：返回Map结构
     */
    public static Map<String, Object> calculateRemind(String input) {
        Map<String, Object> result = new HashMap<>();
        try {
            LocalDate birthDate = LocalDate.parse(input);
            LocalDate today = LocalDate.now();
            int currentYear = today.getYear();

            // 计算阳历生日（若已过，则加一年）
            LocalDate solarBirthday = adjustSolarBirthday(birthDate, currentYear);
            if (solarBirthday.isBefore(today)) {
                solarBirthday = adjustSolarBirthday(birthDate, currentYear + 1);
            }

            ChineseDate solarLunar = new ChineseDate(solarBirthday);
            ChineseDate birthLunar = new ChineseDate(birthDate);
            LocalDate lunarToSolar = calculateLunarToSolar(today, birthLunar);

            // 判断农历生日是否已过
            LocalDate nextLunar = lunarToSolar.isBefore(today)
                    ? calculateNextLunarBirthday(today, birthLunar)
                    : lunarToSolar;

            // 计算最近的生日（阳历或农历）
            LocalDate nearest = solarBirthday.isBefore(nextLunar) ? solarBirthday : nextLunar;
            long daysLeft = Duration.between(LocalDateTime.now(),
                    nearest.atTime(LocalTime.MIDNIGHT).plusDays(1)).toDays();

            result.put("solarBirthday", solarBirthday); // 阳历生日（已修正）
            result.put("nextLunar", nextLunar); // 农历生日（已修正）
            result.put("birthDate", ZodiacSignUtil.getZodiacSign(birthDate.getMonthValue(), birthDate.getDayOfMonth()));
            result.put("daysLeft", daysLeft); // 距离生日的剩余天数
            // 添加节假日倒计时
            Map<String, Long> holidayCountdowns = FestivalUtil.getHolidayCountdowns(today);
            result.put("holidayCountdowns", holidayCountdowns);
        } catch (Exception e) {
            result.put("error", "日期格式错误或计算异常");
        }
        return result;
    }


}
