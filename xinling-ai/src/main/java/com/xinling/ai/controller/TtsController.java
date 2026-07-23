package com.xinling.ai.controller;

import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.service.tts.TtsService;
import com.xinling.ai.service.tts.TtsVoice;
import com.xinling.common.config.XinLingConfig;
import com.xinling.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 语音合成控制器
 * 提供文字转语音（TTS）能力
 *
 * @author SuXia
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class TtsController {

    @Autowired(required = false)
    private TtsService ttsService;

    /**
     * 文字转语音
     */
    @PostMapping("/tts")
    public AjaxResult textToSpeech(@RequestBody ChatRequest request) {
        if (ttsService == null) {
            return AjaxResult.error("TTS服务未配置，请检查 edge-tts 是否已安装");
        }

        String text = request.getPrompt();
        if (text == null || text.trim().isEmpty()) {
            return AjaxResult.error("请输入要合成的文本");
        }

        try {
            TtsVoice voice = TtsVoice.getDefault();
            if (request.getVoice() != null && !request.getVoice().isEmpty()) {
                try {
                    voice = TtsVoice.valueOf(request.getVoice().toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    // 使用默认语音
                }
            }

            byte[] audioData = ttsService.synthesizeToBytes(text, voice);
            String fileName = "tts_" + System.currentTimeMillis() + ".mp3";
            String relativePath = "ai/voice/" + fileName;
            Path outputPath = Path.of(XinLingConfig.getUploadPath(), relativePath);
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, audioData);

            String audioUrl = "/uploads/ai/voice/" + fileName;
            return AjaxResult.success("语音合成成功", audioUrl);
        } catch (Exception e) {
            log.error("TTS合成失败", e);
            return AjaxResult.error("语音合成失败: " + e.getMessage());
        }
    }

    /**
     * 获取可用语音列表
     */
    @GetMapping("/voices")
    public AjaxResult getVoices() {
        TtsVoice[] voices = TtsVoice.values();
        List<Map<String, String>> voiceList = new ArrayList<>();
        for (TtsVoice voice : voices) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("name", voice.name());
            item.put("displayName", voice.getDisplayName());
            item.put("style", voice.getStyle());
            voiceList.add(item);
        }
        return AjaxResult.success(voiceList);
    }
}
