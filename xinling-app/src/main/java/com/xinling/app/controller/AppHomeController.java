package com.xinling.app.controller;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.UserGrowth;
import com.xinling.app.domain.model.HomeInfoVO;
import com.xinling.app.domain.model.MomentVO;
import com.xinling.app.service.IAudioService;
import com.xinling.app.service.ICommunityService;
import com.xinling.app.service.IGrowthService;
import com.xinling.app.service.IStatisticsService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/app/home")
public class AppHomeController {

    private final IAudioService audioService;
    private final ICommunityService communityService;
    private final IGrowthService growthService;
    private final IStatisticsService statisticsService;

    public AppHomeController(IAudioService audioService,
                             ICommunityService communityService,
                             IGrowthService growthService,
                             IStatisticsService statisticsService) {
        this.audioService = audioService;
        this.communityService = communityService;
        this.growthService = growthService;
        this.statisticsService = statisticsService;
    }

    /**
     * 首页数据
     */
    @GetMapping("/info")
    public R<HomeInfoVO> homeData() {
        Long userId = AppContextUtil.getUserId();
        HomeInfoVO vo = new HomeInfoVO();

        // 问候语（根据时间段）
        int hour = LocalTime.now().getHour();
        String greeting;
        if (hour < 6) greeting = "夜深了，早点休息";
        else if (hour < 9) greeting = "早上好，新的一天开始了";
        else if (hour < 12) greeting = "上午好，保持好心情";
        else if (hour < 14) greeting = "中午好，适当休息一下";
        else if (hour < 18) greeting = "下午好，继续加油";
        else greeting = "晚上好，放松一下吧";
        vo.setGreeting(greeting);

        // 今日专注时长
        List<Map<String, Object>> focusStatsList = statisticsService.getFocusStats(userId, "today");
        if (focusStatsList != null && !focusStatsList.isEmpty()) {
            Map<String, Object> focusStats = focusStatsList.get(0);
            if (focusStats.get("totalMinutes") != null) {
                vo.setTodayFocusMinutes(((Number) focusStats.get("totalMinutes")).intValue());
            } else {
                vo.setTodayFocusMinutes(0);
            }
        } else {
            vo.setTodayFocusMinutes(0);
        }

        // 今日睡眠时长
        List<Map<String, Object>> sleepStatsList = statisticsService.getSleepStats(userId, "today");
        if (sleepStatsList != null && !sleepStatsList.isEmpty()) {
            Map<String, Object> sleepStats = sleepStatsList.get(0);
            if (sleepStats.get("totalMinutes") != null) {
                vo.setTodaySleepMinutes(((Number) sleepStats.get("totalMinutes")).intValue());
            } else {
                vo.setTodaySleepMinutes(0);
            }
        } else {
            vo.setTodaySleepMinutes(0);
        }

        // 连续天数
        UserGrowth growth = growthService.getGrowthInfo(userId);
        if (growth != null) {
            int continuousDays = Math.max(growth.getContinuousFocusDays(), growth.getContinuousSleepDays());
            vo.setContinuousDays(continuousDays);
        } else {
            vo.setContinuousDays(0);
        }

        // 推荐音频（取前6条）
        List<AudioItem> audioList = audioService.getAudioList(null, 1, 6);
        if (audioList != null) {
            List<HomeInfoVO.RecommendedAudio> recommended = audioList.stream().map(a -> {
                HomeInfoVO.RecommendedAudio ra = new HomeInfoVO.RecommendedAudio();
                ra.setId(a.getId());
                ra.setTitle(a.getTitle());
                ra.setCover(null);
                ra.setAuthor(a.getNarrator());
                ra.setDuration(a.getDuration());
                ra.setPlayCount(a.getPlayCount());
                return ra;
            }).collect(Collectors.toList());
            vo.setRecommendedAudio(recommended);
        }

        // AI话题
        HomeInfoVO.AiTopic aiTopic = new HomeInfoVO.AiTopic();
        aiTopic.setTitle("今日心灵小语");
        aiTopic.setContent("倾听内心的声音，与自己对话。今天你有什么想分享的吗？");
        vo.setAiTopic(aiTopic);

        // 热门动态（取前5条）
        List<MomentVO> moments = communityService.listMoments(userId, null, 1, 5);
        if (moments != null) {
            List<HomeInfoVO.HotMoment> hotMoments = moments.stream().map(m -> {
                HomeInfoVO.HotMoment hm = new HomeInfoVO.HotMoment();
                hm.setId(m.getId());
                hm.setUserId(m.getUserId());
                hm.setNickname(m.getNickname());
                hm.setAvatar(m.getAvatar());
                hm.setContent(m.getContent());
                hm.setLikeCount(m.getLikeCount());
                hm.setCommentCount(m.getCommentCount());
                hm.setCreateTime(m.getCreateTime() != null ? m.getCreateTime().toString() : null);
                return hm;
            }).collect(Collectors.toList());
            vo.setHotMoments(hotMoments);
        }

        return R.ok(vo);
    }
}
