package com.xinling.app.domain.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 睡眠日记实体
 */
public class SleepDiary implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Date date;
    private String bedtimeActivity;
    private Integer caffeineIntake;
    private Integer exercise;
    private String emotion;
    private String note;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getBedtimeActivity() { return bedtimeActivity; }
    public void setBedtimeActivity(String bedtimeActivity) { this.bedtimeActivity = bedtimeActivity; }

    public Integer getCaffeineIntake() { return caffeineIntake; }
    public void setCaffeineIntake(Integer caffeineIntake) { this.caffeineIntake = caffeineIntake; }

    public Integer getExercise() { return exercise; }
    public void setExercise(Integer exercise) { this.exercise = exercise; }

    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
