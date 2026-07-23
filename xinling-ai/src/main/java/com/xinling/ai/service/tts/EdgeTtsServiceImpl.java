package com.xinling.ai.service.tts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Edge-TTS 语音合成实现
 * <p>
 * 使用微软 Edge 浏览器的内置 TTS 引擎，完全免费且无需 API Key。
 * 依赖：Python + edge-tts 库 (pip3 install edge-tts)
 * <p>
 * 支持中文语音：晓晓（温柔）、晓萱（活泼）、云希（阳光）等
 *
 * @author SuXia
 * @date 2026/06/01
 */
@Slf4j
@Service
public class EdgeTtsServiceImpl implements TtsService {

    /** 临时音频文件目录 */
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir") + "/xinling-tts/";

    /** edge-tts 命令超时时间（毫秒） */
    private static final long TIMEOUT_MS = 30_000;

    @Override
    public File synthesize(String text, TtsVoice voice) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("合成文本不能为空");
        }

        // 截断过长文本（Edge-TTS 有限制，约 3000 字符）
        String ttsText = text.length() > 2000 ? text.substring(0, 2000) : text;

        // 确保临时目录存在
        Files.createDirectories(Path.of(TMP_DIR));

        // 生成唯一文件名
        String filename = UUID.randomUUID().toString() + ".mp3";
        File outputFile = new File(TMP_DIR + filename);

        // 构建 edge-tts 命令
        // 使用 --rate 调整语速（默认 0%，可调 +20% 更快）
        // 使用 --volume 调整音量（默认 +0%）
        ProcessBuilder pb = new ProcessBuilder(
                "edge-tts",
                "--voice", voice.getVoiceName(),
                "--text", ttsText,
                "--write-media", outputFile.getAbsolutePath(),
                "--rate", "+10%"
        );

        // 合并错误流（便于调试）
        pb.redirectErrorStream(true);

        log.info("Edge-TTS 开始合成: voice={}, textLen={}", voice.getVoiceName(), ttsText.length());

        Process process = pb.start();

        // 读取输出（避免缓冲区阻塞）
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }

        // 等待进程完成（带超时）
        boolean finished = process.waitFor(TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Edge-TTS 合成超时（" + TIMEOUT_MS + "ms）");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            log.error("Edge-TTS 合成失败: exitCode={}, output={}", exitCode, output);
            throw new RuntimeException("Edge-TTS 合成失败，退出码: " + exitCode);
        }

        if (!outputFile.exists() || outputFile.length() == 0) {
            throw new RuntimeException("Edge-TTS 合成失败: 输出文件为空");
        }

        log.info("Edge-TTS 合成成功: file={}, size={}KB",
                outputFile.getName(), outputFile.length() / 1024);

        return outputFile;
    }

    @Override
    public byte[] synthesizeToBytes(String text, TtsVoice voice) throws Exception {
        File audioFile = synthesize(text, voice);
        try {
            return Files.readAllBytes(audioFile.toPath());
        } finally {
            // 清理临时文件
            boolean deleted = audioFile.delete();
            if (!deleted) {
                log.warn("临时文件删除失败: {}", audioFile.getAbsolutePath());
                audioFile.deleteOnExit();
            }
        }
    }

    @Override
    public String getProvider() {
        return "edge-tts";
    }
}
