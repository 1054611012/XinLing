package com.xinling.app.domain.entity;

/**
 * 冥想-素材关联（含关联老师）
 *
 * @author xinling
 */
public class MeditationAudio {
    private Long id;
    private Long meditationId;
    private Long audioItemId;
    /** 关联老师ID（null=纯背景音乐，无指定老师） */
    private Long authorId;
    private Integer sortOrder;

    /** 关联的素材详情（非 DB 字段，关联查询填充） */
    private AudioItem audioItem;
    /** 关联的老师信息（非 DB 字段，关联查询填充） */
    private Teacher teacher;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeditationId() { return meditationId; }
    public void setMeditationId(Long meditationId) { this.meditationId = meditationId; }
    public Long getAudioItemId() { return audioItemId; }
    public void setAudioItemId(Long audioItemId) { this.audioItemId = audioItemId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public AudioItem getAudioItem() { return audioItem; }
    public void setAudioItem(AudioItem audioItem) { this.audioItem = audioItem; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
