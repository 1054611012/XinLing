package com.xinling.common.utils;

/**
 * 星座工具类
 */
public class ZodiacSignUtil {

    private static final String[] ZODIAC_SIGNS = {
            "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
            "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
    };

    private static final int[] ZODIAC_DAYS = {20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22, 20};

    public static String getZodiacSign(int month, int day) {
        // 将月份转换为索引（0-11），并处理跨年情况
        int index = month - 1;

        // 如果日期小于该星座开始日期，则属于上一个星座
        if (day < ZODIAC_DAYS[index]) {
            index = (index + 11) % 12; // 上一个月
        }

        return ZODIAC_SIGNS[index];
    }
}
