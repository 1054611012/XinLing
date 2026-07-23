package com.xinling.app.enums;

/**
 * APP用户状态
 */
public enum AppUserStatus {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用"),
    FROZEN(2, "冻结");

    private final int code;
    private final String desc;

    AppUserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static AppUserStatus fromCode(Integer code) {
        if (code == null) return NORMAL;
        for (AppUserStatus s : values()) {
            if (s.code == code) return s;
        }
        return NORMAL;
    }
}