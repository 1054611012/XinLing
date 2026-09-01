package com.xinling.common.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xinling.common.utils.FestivalUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "节日/生日倒计时条目")
public class CountdownResult {

    @Schema(description = "事件编码：newYear/springFestival/qingming/laborDay/dragonBoat/midAutumn/nationalDay 或 solarBirthday/lunarBirthday", example = "newYear")
    private String code;
    @Schema(description = "显示名称，如 \"春节\"、\"老婆生日\"", example = "春节")
    private String name;
    @Schema(description = "发生日期 yyyy-MM-dd", example = "2027-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @Schema(description = "距今天数（0=今天，>=0 为未发生；负数表示已过期）", example = "126")
    private long countdownDays;
    @Schema(description = "是否为今天", example = "false")
    private boolean today;
    @Schema(description = "是否为明天", example = "false")
    private boolean tomorrow;
    @Schema(description = "类型枚举：OFFICIAL_HOLIDAY=法定假日 / SOLAR_BIRTHDAY=新历生日 / LUNAR_BIRTHDAY=农历生日", example = "OFFICIAL_HOLIDAY")
    private FestivalUtil.FestivalType type;
    @Schema(description = "类型中文标签", example = "法定假日")
    private String typeLabel;

    public CountdownResult() {}
    public CountdownResult(String code, String name, LocalDate date, long countdownDays, FestivalUtil.FestivalType type) {
        this.code = code; this.name = name; this.date = date;
        this.countdownDays = countdownDays;
        this.today = countdownDays == 0;
        this.tomorrow = countdownDays == 1;
        this.type = type;
        this.typeLabel = type.getLabel();
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public long getCountdownDays() { return countdownDays; }
    public void setCountdownDays(long countdownDays) { this.countdownDays = countdownDays; }
    public boolean isToday() { return today; }
    public void setToday(boolean today) { this.today = today; }
    public boolean isTomorrow() { return tomorrow; }
    public void setTomorrow(boolean tomorrow) { this.tomorrow = tomorrow; }
    public FestivalUtil.FestivalType getType() { return type; }
    public void setType(FestivalUtil.FestivalType type) { this.type = type; }
    public String getTypeLabel() { return typeLabel; }
    public void setTypeLabel(String typeLabel) { this.typeLabel = typeLabel; }

}
