package com.xinling.app.domain.model;

/**
 * 睡眠设置VO
 */
public class SleepSettingsVO {

    private String targetBedtime;
    private String targetWakeUpTime;
    private Integer smartAlarm;
    private Integer smartAlarmAdvance;
    private Integer windDownReminder;
    private String windDownTime;
    private Integer sleepReminder;
    private String sleepReminderTime;
    private Integer sleepSoundEnabled;
    private Long defaultAudioId;

    public String getTargetBedtime() { return targetBedtime; }
    public void setTargetBedtime(String targetBedtime) { this.targetBedtime = targetBedtime; }

    public String getTargetWakeUpTime() { return targetWakeUpTime; }
    public void setTargetWakeUpTime(String targetWakeUpTime) { this.targetWakeUpTime = targetWakeUpTime; }

    public Integer getSmartAlarm() { return smartAlarm; }
    public void setSmartAlarm(Integer smartAlarm) { this.smartAlarm = smartAlarm; }

    public Integer getSmartAlarmAdvance() { return smartAlarmAdvance; }
    public void setSmartAlarmAdvance(Integer smartAlarmAdvance) { this.smartAlarmAdvance = smartAlarmAdvance; }

    public Integer getWindDownReminder() { return windDownReminder; }
    public void setWindDownReminder(Integer windDownReminder) { this.windDownReminder = windDownReminder; }

    public String getWindDownTime() { return windDownTime; }
    public void setWindDownTime(String windDownTime) { this.windDownTime = windDownTime; }

    public Integer getSleepReminder() { return sleepReminder; }
    public void setSleepReminder(Integer sleepReminder) { this.sleepReminder = sleepReminder; }

    public String getSleepReminderTime() { return sleepReminderTime; }
    public void setSleepReminderTime(String sleepReminderTime) { this.sleepReminderTime = sleepReminderTime; }

    public Integer getSleepSoundEnabled() { return sleepSoundEnabled; }
    public void setSleepSoundEnabled(Integer sleepSoundEnabled) { this.sleepSoundEnabled = sleepSoundEnabled; }

    public Long getDefaultAudioId() { return defaultAudioId; }
    public void setDefaultAudioId(Long defaultAudioId) { this.defaultAudioId = defaultAudioId; }
}
