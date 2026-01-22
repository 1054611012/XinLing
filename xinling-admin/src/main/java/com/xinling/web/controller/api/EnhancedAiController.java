package com.xinling.web.controller.api;

import com.xinling.ai.domain.vo.ChatRequest;
import com.xinling.ai.domain.vo.ChatResponse;
import com.xinling.ai.service.DatabaseRagService;
import com.xinling.ai.service.DocumentLoaderService;
import com.xinling.ai.service.OllamaService;
import com.xinling.ai.service.RagExecutor;
import com.xinling.ai.enums.RagScene;
import com.xinling.ai.service.XinLingAiService;
import com.xinling.ai.utils.QueryTypeUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 增强版AI控制器
 *
 * @author SuXia
 * @date 2025/12/29
 */
@Slf4j
@RestController
@RequestMapping("/enhanced-ai")
@Tag(name = "增强版AI聊天接口", description = "增强版AI控制器")
public class EnhancedAiController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private XinLingAiService xinLingAiService;

    @Autowired
    private RagExecutor ragExecutor;

    /**
     * 多场景智能聊天
     */
    @Operation(summary = "多场景智能聊天", description = "根据输入内容自动判断场景并处理")
    @PostMapping(value = "/multiSceneChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> multiSceneChat(@RequestBody ChatRequest request) {
        try {
            RagScene scene;
            String prompt = request.getPrompt().toLowerCase();

            if (QueryTypeUtils.isStatisticsQuery(request.getPrompt()) ||
                QueryTypeUtils.isSqlQuery(request.getPrompt()) ||
                prompt.contains("数据库") || prompt.contains("表") || prompt.contains("字段")) {
                scene = RagScene.NL2SQL;
            } else if (QueryTypeUtils.isKnowledgeQuery(request.getPrompt())) {
                scene = RagScene.KNOWLEDGE_QA;
            } else {
                scene = RagScene.QA;
            }

            return ragExecutor.stream(request.getPrompt(), scene)
                    .map(content -> ChatResponse.of(content, request.getSessionId()))
                    .concatWith(Flux.just(ChatResponse.finish(request.getSessionId())));
        } catch (Exception e) {
            log.error("多场景聊天错误", e);
            return Flux.just(ChatResponse.error("多场景聊天服务错误: " + e.getMessage(), request.getSessionId()));
        }
    }

    /**
     * 获取系统支持的模型列表
     */
    @Operation(summary = "获取模型列表", description = "获取系统支持的所有模型列表")
    @GetMapping("/models")
    public ResponseEntity<String[]> getModels() {
        // 返回配置的模型名称
        String model = ollamaService.listModels().blockFirst();
        return ResponseEntity.ok(new String[]{model});
    }

    /**
     * 获取系统状态
     */
    @Operation(summary = "获取系统状态", description = "获取AI系统运行状态")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("model", ollamaService.listModels().blockFirst());
        status.put("rag_enabled", true);
        status.put("streaming_enabled", true);
        status.put("initialized", true);

        return ResponseEntity.ok(status);
    }

    /**
     * 清除会话
     */
    @Operation(summary = "清除会话", description = "清除指定会话的上下文")
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<String> clearSession(@PathVariable String sessionId) {
        xinLingAiService.clearSession(sessionId);
        return ResponseEntity.ok("会话已清除: " + sessionId);
    }
}
