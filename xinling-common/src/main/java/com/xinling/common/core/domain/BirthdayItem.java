package com.xinling.common.core.domain;

import java.io.Serializable;

/**
 * 用户生日条目（存储于 Redis，按登录用户隔离）
 *
 * <p>以 List 形式存放在 key {@code user_birthday:{userId}} 下，支持同一用户设置多个人的生日。
 * 农历生日也以“出生公历日期”锚定（date 字段），后端用 {@code lunarFromSolar} 反推农历计算倒计时，
 * 与《日期提醒》模块的统一约定保持一致。
 */
public class BirthdayItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 称呼，如「妈妈」「宝宝」 */
    private String name;

    /** 出生公历日期 yyyy-MM-dd（农历生日同样用公历锚定） */
    private String date;

    /** 历法：solar=新历 / lunar=农历（默认 lunar） */
    private String calendar;

    public BirthdayItem() {
    }

    public BirthdayItem(String name, String date, String calendar) {
        this.name = name;
        this.date = date;
        this.calendar = calendar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }
}
