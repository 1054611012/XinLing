package com.xinling.admin.controller.api;

import com.xinling.common.annotation.Anonymous;
import com.xinling.common.constant.CacheConstants;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.domain.BirthdayItem;
import com.xinling.common.core.domain.model.LoginUser;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.common.utils.FestivalUtil;
import com.xinling.common.utils.FestivalUtil.BirthdayRequest;
import com.xinling.common.utils.FestivalUtil.RemindResponse;
import com.xinling.common.utils.StringUtils;
import com.xinling.framework.web.service.TokenService;
import com.xinling.system.domain.SysNotice;
import com.xinling.system.service.ISysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TokenService tokenService;

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

    /* ==================== 日期提醒（节假日 + 生日倒计时） ==================== */

    /**
     * 计算节日 + 生日倒计时（完整版，生日由请求体传入）
     */
    @Operation(summary = "计算节日+生日倒计时", description = "POST /api/remind/calculate 计算节日与生日倒计时，参数见下方请求体字段说明及类注释")
    @PostMapping("/remind/calculate")
    public AjaxResult calculate(@RequestBody RemindRequest request) {
        try {
            LocalDate today = request.getToday() != null ? request.getToday() : LocalDate.now();
            RemindResponse result = FestivalUtil.calculateRemind(
                    today, request.getBirthdays(), request.isIncludeHolidays());
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 仅查询法定假日倒计时（快捷接口）
     */
    @Operation(summary = "查询法定假日倒计时", description = "GET /api/remind/holidays 仅查询法定假日倒计时")
    @GetMapping("/remind/holidays")
    public AjaxResult holidays(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today) {
        return AjaxResult.success(FestivalUtil.calculateHolidayCountdowns(today));
    }

    /**
     * 获取最近的下一个事件（节日或生日）
     */
    @Operation(summary = "获取最近的下一个事件", description = "GET /api/remind/next 获取最近的下一个节日或生日")
    @GetMapping("/remind/next")
    public AjaxResult nextEvent(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today,
            @RequestParam(required = false) List<BirthdayRequest> birthdays) {
        RemindResponse response = FestivalUtil.calculateRemind(today, birthdays, true);
        return AjaxResult.success(response.getNext());
    }

    /**
     * 获取当前登录用户的日期提醒：从 Redis 读取其生日列表，合并法定假日计算倒计时。
     */
    @Operation(summary = "获取我的日期提醒", description = "GET /api/remind/my 读取当前用户 Redis 生日列表，合并法定假日计算（返回 all/next 等）")
    @GetMapping("/remind/my")
    public AjaxResult myRemind(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("请先登录");
        }
        List<BirthdayItem> items = redisCache.getCacheList(userBirthdayKey(loginUser.getUserId()));
        List<BirthdayRequest> birthdays = toBirthdayRequests(items);
        RemindResponse response = FestivalUtil.calculateRemind(null, birthdays, true);
        return AjaxResult.success(response);
    }

    /**
     * 读取当前登录用户已保存的生日列表（按用户隔离）
     */
    @Operation(summary = "读取我的生日列表", description = "GET /api/remind/birthdays 返回当前用户的生日列表（多人）")
    @GetMapping("/remind/birthdays")
    public AjaxResult getBirthdays(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("请先登录");
        }
        List<BirthdayItem> items = redisCache.getCacheList(userBirthdayKey(loginUser.getUserId()));
        return AjaxResult.success(items == null ? new ArrayList<>() : items);
    }

    /**
     * 保存当前登录用户的生日列表（覆盖写入 Redis，支持多人）。
     * 每条含 name（称呼）、date（出生公历 yyyy-MM-dd）、calendar（solar 新历 / lunar 农历）。
     */
    @Operation(summary = "保存我的生日列表", description = "POST /api/remind/birthdays 以列表覆盖保存当前用户的多人生日，按用户隔离存储到 Redis")
    @PostMapping("/remind/birthdays")
    public AjaxResult saveBirthdays(HttpServletRequest request, @RequestBody List<BirthdayItem> birthdays) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return AjaxResult.error("请先登录");
        }
        List<BirthdayItem> list = birthdays == null ? new ArrayList<>() : birthdays;
        // 基础校验：剔除空项、规范历法字段
        List<BirthdayItem> cleaned = new ArrayList<>();
        for (BirthdayItem it : list) {
            if (it == null || StringUtils.isEmpty(it.getName()) || StringUtils.isEmpty(it.getDate())) {
                continue;
            }
            if (!it.getDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
                return AjaxResult.error("生日格式应为 yyyy-MM-dd：" + it.getName());
            }
            it.setCalendar("lunar".equals(it.getCalendar()) ? "lunar" : "solar");
            cleaned.add(it);
        }
        String key = userBirthdayKey(loginUser.getUserId());
        redisCache.deleteObject(key);
        if (!cleaned.isEmpty()) {
            redisCache.setCacheList(key, cleaned);
        }
        return AjaxResult.success("保存成功");
    }

    /** 构造按用户隔离的生日 Redis key */
    private String userBirthdayKey(Long userId) {
        return CacheConstants.USER_BIRTHDAY_KEY + userId;
    }

    /** 将存储的生日条目转换为 FestivalUtil 计算用的请求体（农历走 lunarFromSolar 反推） */
    private List<BirthdayRequest> toBirthdayRequests(List<BirthdayItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        List<BirthdayRequest> list = new ArrayList<>();
        for (BirthdayItem it : items) {
            if (it == null || StringUtils.isEmpty(it.getName()) || StringUtils.isEmpty(it.getDate())) {
                continue;
            }
            BirthdayRequest br = new BirthdayRequest();
            br.setName(it.getName());
            if ("lunar".equals(it.getCalendar())) {
                // 农历生日：用公历出生日期反推农历，计算下一个农历生日倒计时
                br.setType("lunarFromSolar");
                br.setSolarDate(it.getDate());
            } else {
                br.setType("solar");
                try {
                    LocalDate d = LocalDate.parse(it.getDate());
                    br.setMonth(d.getMonthValue());
                    br.setDay(d.getDayOfMonth());
                } catch (Exception e) {
                    continue;
                }
            }
            list.add(br);
        }
        return list.isEmpty() ? null : list;
    }

    /**
     * 请求体封装：计算节日+生日
     */
    @Schema(description = "日期提醒计算请求体")
    public static class RemindRequest {
        @Schema(description = "基准日期，yyyy-MM-dd，不传则使用服务端当天", example = "2026-08-28")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate today;
        @Schema(description = "生日列表，每项见 BirthdayRequest；不传则只返回节日", example = "[{\"name\":\"老婆生日\",\"type\":\"solar\",\"month\":3,\"day\":8}]")
        private List<BirthdayRequest> birthdays;
        @Schema(description = "是否包含 7 大法定节假日倒计时，默认 true", example = "true")
        private boolean includeHolidays = true;

        public LocalDate getToday() { return today; }
        public void setToday(LocalDate today) { this.today = today; }
        public List<BirthdayRequest> getBirthdays() { return birthdays; }
        public void setBirthdays(List<BirthdayRequest> birthdays) { this.birthdays = birthdays; }
        public boolean isIncludeHolidays() { return includeHolidays; }
        public void setIncludeHolidays(boolean includeHolidays) { this.includeHolidays = includeHolidays; }
    }

}
