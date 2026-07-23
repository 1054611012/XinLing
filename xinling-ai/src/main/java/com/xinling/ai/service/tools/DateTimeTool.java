package com.xinling.ai.service.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具 — LLM 可调用以获取当前时间
 *
 * @author SuXia
 * @date 2026/05/21
 */
@Slf4j
@Component
public class DateTimeTool {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool("获取当前日期和时间，返回 yyyy-MM-dd HH:mm:ss 格式。用于处理'今天'、'本月'、'最近一周'等时间相关问题")
    public String getCurrentDateTime() {
        String now = LocalDateTime.now().format(FORMATTER);
        log.debug("DateTimeTool 调用: {}", now);
        return "当前时间: " + now;
    }
}
