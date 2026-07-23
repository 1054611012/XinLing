package com.xinling.ai.service.tts;

import java.io.File;

/**
 * 语音合成服务接口
 * 支持多种TTS提供商，可通过配置切换
 *
 * @author SuXia
 * @date 2026/06/01
 */
public interface TtsService {

    /**
     * 合成语音并保存到文件
     *
     * @param text  要合成的文本
     * @param voice 语音角色
     * @return 生成的音频文件
     * @throws Exception 合成失败时抛出
     */
    File synthesize(String text, TtsVoice voice) throws Exception;

    /**
     * 合成语音并保存到文件（使用默认语音）
     */
    default File synthesize(String text) throws Exception {
        return synthesize(text, TtsVoice.getDefault());
    }

    /**
     * 合成语音并返回字节数组
     *
     * @param text  要合成的文本
     * @param voice 语音角色
     * @return 音频字节数据
     * @throws Exception 合成失败时抛出
     */
    byte[] synthesizeToBytes(String text, TtsVoice voice) throws Exception;

    /**
     * 获取当前使用的TTS提供商名称
     */
    String getProvider();
}