package com.xinling.common.utils;

import cn.hutool.core.date.ChineseDate;
import com.xinling.common.core.domain.CountdownResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 中国节假日与生日倒计时工具类（完整版 + Web API 支持）
 *
 * <p>支持：
 * <ul>
 *   <li>7 大法定节假日倒计时</li>
 *   <li>新历（公历）生日倒计时</li>
 *   <li>农历生日倒计时（闰月支持、公历反推、自动降级）</li>
 *   <li>Web 接口 DTO 与响应封装</li>
 * </ul>
 */
public class FestivalUtil {

    /* ==================== 枚举 ==================== */

    public enum FestivalType {
        OFFICIAL_HOLIDAY("法定假日"),
        SOLAR_BIRTHDAY("新历生日"),
        LUNAR_BIRTHDAY("农历生日");

        private final String label;
        FestivalType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Festival {
        NEW_YEAR("元旦", "newYear", false),
        SPRING_FESTIVAL("春节", "springFestival", true),
        QINGMING("清明节", "qingming", false),
        LABOR_DAY("劳动节", "laborDay", false),
        DRAGON_BOAT("端午节", "dragonBoat", true),
        MID_AUTUMN("中秋节", "midAutumn", true),
        NATIONAL_DAY("国庆节", "nationalDay", false);

        private final String chineseName, code;
        private final boolean lunar;
        Festival(String chineseName, String code, boolean lunar) {
            this.chineseName = chineseName; this.code = code; this.lunar = lunar;
        }
        public String getChineseName() { return chineseName; }
        public String getCode() { return code; }
        public boolean isLunar() { return lunar; }
    }

    /** 请求参数：生日信息 */
    @Schema(description = "生日信息（单条）")
    public static class BirthdayRequest {
        @Schema(description = "显示名称，如 \"老婆生日\"", example = "老婆生日")
        private String name;
        @Schema(description = "新历月份 1-12，type=solar 时必填", example = "3")
        private Integer month;
        @Schema(description = "新历日期 1-31，type=solar 时必填", example = "8")
        private Integer day;
        @Schema(description = "农历月份 1-12，闰月用负数如 -4；type=lunar 时必填", example = "6")
        private Integer lunarMonth;
        @Schema(description = "农历日期 1-30，type=lunar 时必填", example = "15")
        private Integer lunarDay;
        @Schema(description = "公历出生日期 yyyy-MM-dd，type=lunarFromSolar 时必填（自动反推农历）", example = "2020-01-01")
        private String solarDate;
        @Schema(description = "生日类型（必填）：solar=新历生日 / lunar=农历生日 / lunarFromSolar=公历反推农历", example = "solar", allowableValues = {"solar", "lunar", "lunarFromSolar"})
        private String type;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }
        public Integer getLunarMonth() { return lunarMonth; }
        public void setLunarMonth(Integer lunarMonth) { this.lunarMonth = lunarMonth; }
        public Integer getLunarDay() { return lunarDay; }
        public void setLunarDay(Integer lunarDay) { this.lunarDay = lunarDay; }
        public String getSolarDate() { return solarDate; }
        public void setSolarDate(String solarDate) { this.solarDate = solarDate; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }



    /** 完整响应数据 */
    @Schema(description = "日期提醒计算响应（data 部分）")
    public static class RemindResponse {
        @Schema(description = "本次计算使用的基准日期", example = "2026-08-28")
        private LocalDate today;
        @Schema(description = "法定假日倒计时列表（includeHolidays=true 时返回），按剩余天数升序")
        private List<CountdownResult> holidays;
        @Schema(description = "生日倒计时列表，按剩余天数升序")
        private List<CountdownResult> birthdays;
        @Schema(description = "holidays + birthdays 合并后按剩余天数升序，前端可直接渲染")
        private List<CountdownResult> all;
        @Schema(description = "最近的一个事件（剩余天数 >= 0 中最小），无数据时为 null")
        private CountdownResult next;

        public LocalDate getToday() { return today; }
        public void setToday(LocalDate today) { this.today = today; }
        public List<CountdownResult> getHolidays() { return holidays; }
        public void setHolidays(List<CountdownResult> holidays) { this.holidays = holidays; }
        public List<CountdownResult> getBirthdays() { return birthdays; }
        public void setBirthdays(List<CountdownResult> birthdays) { this.birthdays = birthdays; }
        public List<CountdownResult> getAll() { return all; }
        public void setAll(List<CountdownResult> all) { this.all = all; }
        public CountdownResult getNext() { return next; }
        public void setNext(CountdownResult next) { this.next = next; }
    }

    /* ==================== 核心计算 ==================== */

    public static List<CountdownResult> calculateHolidayCountdowns(LocalDate today) {
        if (today == null) today = LocalDate.now();
        final LocalDate base = today;
        List<CountdownResult> results = new ArrayList<>();
        for (Festival f : Festival.values()) {
            try {
                LocalDate date = resolveHolidayDate(f, base.getYear());
                if (date.isBefore(base)) date = resolveHolidayDate(f, base.getYear() + 1);
                long days = ChronoUnit.DAYS.between(base, date);
                results.add(new CountdownResult(f.getCode(), f.getChineseName(), date, days, FestivalType.OFFICIAL_HOLIDAY));
            } catch (Exception e) {
                System.err.printf("[%s] 计算失败: %s%n", f.getChineseName(), e.getMessage());
            }
        }
        results.sort(Comparator.comparingLong(CountdownResult::getCountdownDays));
        return results;
    }

    public static CountdownResult calculateSolarBirthday(int month, int day, LocalDate today, String name) {
        if (today == null) today = LocalDate.now();
        LocalDate birthday = LocalDate.of(today.getYear(), month, day);
        if (!birthday.isAfter(today)) birthday = birthday.plusYears(1);
        long days = ChronoUnit.DAYS.between(today, birthday);
        return new CountdownResult("solarBirthday", name, birthday, days, FestivalType.SOLAR_BIRTHDAY);
    }

    public static CountdownResult calculateLunarBirthday(int lunarMonth, int lunarDay, LocalDate today, String name) {
        if (today == null) today = LocalDate.now();
        LocalDate targetDate = resolveLunarDate(today.getYear(), lunarMonth, lunarDay);
        if (targetDate != null && !targetDate.isAfter(today)) {
            targetDate = resolveLunarDate(today.getYear() + 1, lunarMonth, lunarDay);
        }
        if (targetDate == null && lunarMonth < 0) {
            targetDate = resolveLunarDate(today.getYear(), Math.abs(lunarMonth), lunarDay);
            if (targetDate != null && !targetDate.isAfter(today)) {
                targetDate = resolveLunarDate(today.getYear() + 1, Math.abs(lunarMonth), lunarDay);
            }
        }
        if (targetDate == null) throw new IllegalStateException(String.format("无法计算农历生日: %d月%d日", lunarMonth, lunarDay));
        long days = ChronoUnit.DAYS.between(today, targetDate);
        return new CountdownResult("lunarBirthday", name, targetDate, days, FestivalType.LUNAR_BIRTHDAY);
    }

    public static CountdownResult calculateLunarBirthdayFromSolar(LocalDate solarBirthday, LocalDate today, String name) {
        if (today == null) today = LocalDate.now();
        ChineseDate cd = new ChineseDate(solarBirthday);
        int lunarMonth = cd.getMonth();
        int lunarDay = cd.getDay();
        boolean isLeap = cd.isLeapMonth();
        int month = isLeap ? -lunarMonth : lunarMonth;
        return calculateLunarBirthday(month, lunarDay, today, name);
    }

    /* ==================== Web 接口核心方法 ==================== */

    /**
     * 统一提醒计算接口（支持节日 + 多个生日）
     *
     * @param today      基准日期，null 则使用今天
     * @param birthdays  生日列表，null 则只返回节日
     * @param includeHolidays 是否包含法定假日
     * @return 完整提醒数据
     */
    public static RemindResponse calculateRemind(LocalDate today, List<BirthdayRequest> birthdays, boolean includeHolidays) {
        if (today == null) today = LocalDate.now();

        RemindResponse response = new RemindResponse();
        response.setToday(today);

        List<CountdownResult> all = new ArrayList<>();

        // 1. 节日
        if (includeHolidays) {
            List<CountdownResult> holidays = calculateHolidayCountdowns(today);
            response.setHolidays(holidays);
            all.addAll(holidays);
        }

        // 2. 生日
        List<CountdownResult> birthdayResults = new ArrayList<>();
        if (birthdays != null) {
            for (BirthdayRequest req : birthdays) {
                try {
                    CountdownResult br = parseAndCalculateBirthday(req, today);
                    if (br != null) {
                        birthdayResults.add(br);
                        all.add(br);
                    }
                } catch (Exception e) {
                    System.err.printf("[生日计算失败] %s: %s%n", req.getName(), e.getMessage());
                }
            }
        }
        response.setBirthdays(birthdayResults);

        // 3. 排序
        all.sort(Comparator.comparingLong(CountdownResult::getCountdownDays));
        response.setAll(all);

        // 4. 最近的一个
        response.setNext(
                all.stream().filter(r -> r.getCountdownDays() >= 0)
                        .min(Comparator.comparingLong(CountdownResult::getCountdownDays)).orElse(null)
        );

        return response;
    }

    /** 解析单个生日请求并计算 */
    private static CountdownResult parseAndCalculateBirthday(BirthdayRequest req, LocalDate today) {
        String type = req.getType() == null ? "solar" : req.getType();
        String name = req.getName() == null ? "生日" : req.getName();

        switch (type) {
            case "solar":
                if (req.getMonth() == null || req.getDay() == null) {
                    throw new IllegalArgumentException("新历生日需要传入 month 和 day");
                }
                return calculateSolarBirthday(req.getMonth(), req.getDay(), today, name);

            case "lunar":
                if (req.getLunarMonth() == null || req.getLunarDay() == null) {
                    throw new IllegalArgumentException("农历生日需要传入 lunarMonth 和 lunarDay");
                }
                return calculateLunarBirthday(req.getLunarMonth(), req.getLunarDay(), today, name);

            case "lunarFromSolar":
                if (req.getSolarDate() == null) {
                    throw new IllegalArgumentException("反推农历生日需要传入 solarDate (yyyy-MM-dd)");
                }
                LocalDate solar = LocalDate.parse(req.getSolarDate());
                return calculateLunarBirthdayFromSolar(solar, today, name);

            default:
                throw new IllegalArgumentException("不支持的生日类型: " + type);
        }
    }

    /* ==================== 内部工具 ==================== */

    private static LocalDate resolveHolidayDate(Festival festival, int year) {
        switch (festival) {
            case SPRING_FESTIVAL: return lunarToSolar(year, 1, 1);
            case DRAGON_BOAT:     return lunarToSolar(year, 5, 5);
            case MID_AUTUMN:      return lunarToSolar(year, 8, 15);
            case QINGMING:        return LocalDate.of(year, 4, 4);
            case NEW_YEAR:        return LocalDate.of(year, 1, 1);
            case LABOR_DAY:       return LocalDate.of(year, 5, 1);
            case NATIONAL_DAY:    return LocalDate.of(year, 10, 1);
            default: throw new IllegalArgumentException("未知节日: " + festival);
        }
    }

    private static LocalDate resolveLunarDate(int year, int lunarMonth, int lunarDay) {
        try {
            boolean leap = lunarMonth < 0;
            int month = Math.abs(lunarMonth);
            ChineseDate cd = new ChineseDate(year, month, lunarDay, leap);
            return cd.getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDate lunarToSolar(int year, int month, int day) {
        ChineseDate cd = new ChineseDate(year, month, day, false);
        return cd.getGregorianDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}