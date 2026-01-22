package com.xinling.common.utils.api;
/**
 * 第三方API URL枚举类
 */
public enum APIUrlEnum {

    GOLD_PRICE_URL("https://api.pearktrue.cn/api/goldprice/", "金价接口"),
    COUNTDOWN_DAY_URL("https://api.pearktrue.cn/api/countdownday/", "倒数日获取"),
    CREATE_SHORT_URL("https://api.pearktrue.cn/api/short/dwz.php", "生成短链接"),
    AI_CHAT_URL("https://api.pearktrue.cn/api/aichat/","AI 对话接口");

    private final String path;
    private final String desc;

    APIUrlEnum(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public String getDesc() {
        return desc;
    }

}
