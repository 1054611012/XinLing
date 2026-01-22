package com.xinling.web.controller.api;

import com.xinling.common.annotation.Anonymous;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.utils.BirthdayCalculator;
import com.xinling.system.domain.SysNotice;
import com.xinling.system.service.ISysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * 日期提醒Controller
 *
 * @author xinling
 * @date 2025-10-30
 */
@Tag(name = "日期提醒", description = "日期提醒")
@RestController
@RequestMapping("/api")
@Anonymous
public class RemindController extends BaseController
{
    @Autowired
    private ISysNoticeService noticeService;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * 获取通知公告列表（WebFlux SSE 实时更新，友好异常处理）
     */
    @Operation(summary = "获取通知公告列表（WebFlux SSE 实时更新）", description = "获取通知公告列表（WebFlux SSE 实时更新）")
    @GetMapping(value = "/noticeList", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<List<SysNotice>>> streamNotices() {

        Flux<ServerSentEvent<List<SysNotice>>> noticeFlux =
                Flux.interval(Duration.ofSeconds(15))
                        .flatMap(seq ->
                                noticeService.getTop5Notices()
                                        .flatMapMany(list -> Flux.fromIterable(list))
                                        .collectList()
                                        .map(list ->
                                                ServerSentEvent.<List<SysNotice>>builder()
                                                        .id(String.valueOf(seq))
                                                        .event("message")
                                                        .data(list)
                                                        .build()
                                        )
                        );

        Flux<ServerSentEvent<List<SysNotice>>> heartbeatFlux =
                Flux.interval(Duration.ofSeconds(15))
                        .map(seq -> ServerSentEvent.<List<SysNotice>>builder()
                                .event("ping")
                                .comment("heartbeat")
                                .build()
                        );

        return noticeFlux.mergeWith(heartbeatFlux)
                .timeout(Duration.ofMinutes(30));
    }


    /**
     * 日期提醒倒计时
     */
    @Operation(summary = "日期提醒倒计时", description = "日期提醒倒计时")
    @GetMapping("/remind")
    public AjaxResult remind(String birthDate)
    {
        birthDate = birthDate == null ? "1994-11-10" : birthDate;
        return AjaxResult.success(BirthdayCalculator.calculateRemind(birthDate));
    }


    /**
     * 金价实时推送
     */
    @Operation(summary = "金价实时推送", description = "金价实时推送")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(0L); // 永不超时
        emitters.add(emitter);

        // 发送连接确认消息
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data("SSE connection established at " + System.currentTimeMillis()));
        } catch (IOException e) {
            emitters.remove(emitter);
            logger.error("Failed to send initial connection message", e);
        }

        // 注册事件监听器
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            logger.info("SSE connection completed");
        });

        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            logger.info("SSE connection timeout");
        });

        emitter.onError((throwable) -> {
            emitters.remove(emitter);
            logger.error("SSE connection error", throwable);
        });

        return emitter;
    }

}
