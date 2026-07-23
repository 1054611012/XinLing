package com.xinling.app.domain.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 公共素材库（音频/视频纯文件元数据）
 * <p>
 * 不包含封面、背景图、作者等业务字段。
 * 业务内容（冥想/睡眠/白噪音）通过关联表引用此素材。
 */
public class AudioItem {
    private Long id;
    private Long fileId;
    private String title;
    private String audioUrl;
    private Integer duration;
    private String fileType;          // audio / video / image
    private String fileExt;           // mp3 / wav / mp4 ...
    private String sourceType;        // upload / system
    private String narrator;
    private Integer playCount;

    @JsonDeserialize(using = TagsDeserializer.class)
    private List<String> tags;

    private Integer status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getFileExt() { return fileExt; }
    public void setFileExt(String fileExt) { this.fileExt = fileExt; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getNarrator() { return narrator; }
    public void setNarrator(String narrator) { this.narrator = narrator; }
    public Integer getPlayCount() { return playCount; }
    public void setPlayCount(Integer playCount) { this.playCount = playCount; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    static class TagsDeserializer extends JsonDeserializer<List<String>> {
        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.currentToken() == JsonToken.START_ARRAY) {
                List<String> result = new ArrayList<>();
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    result.add(p.getValueAsString());
                }
                return result;
            } else if (p.currentToken() == JsonToken.VALUE_STRING) {
                String value = p.getValueAsString();
                if (value == null || value.trim().isEmpty()) {
                    return Collections.emptyList();
                }
                value = value.trim();
                if (value.startsWith("[") && value.endsWith("]")) {
                    try { return MAPPER.readValue(value, new TypeReference<List<String>>() {}); }
                    catch (Exception ignored) { }
                }
                List<String> result = new ArrayList<>();
                for (String s : value.split(",")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) result.add(trimmed);
                }
                return result;
            }
            return Collections.emptyList();
        }
    }
}
