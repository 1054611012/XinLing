package com.xinling.app.domain.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

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
@Data
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
