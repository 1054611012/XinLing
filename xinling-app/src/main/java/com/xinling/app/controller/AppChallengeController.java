package com.xinling.app.controller;

import com.xinling.app.domain.entity.Challenge;
import com.xinling.app.domain.entity.UserChallenge;
import com.xinling.app.service.IChallengeService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/challenge")
public class AppChallengeController {

    private final IChallengeService challengeService;

    public AppChallengeController(IChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * 挑战列表
     */
    @GetMapping("/list")
    public R<List<Challenge>> list() {
        List<Challenge> list = challengeService.listChallenges();
        return R.ok(list);
    }

    /**
     * 参加挑战
     */
    @PostMapping("/join/{challengeId}")
    public R<?> join(@PathVariable Long challengeId) {
        Long userId = AppContextUtil.getUserId();
        challengeService.joinChallenge(userId, challengeId);
        return R.ok("参加成功");
    }

    /**
     * 我的挑战列表
     */
    @GetMapping({"/my", "/myChallenges"})
    public R<List<Map<String, Object>>> myChallenges() {
        Long userId = AppContextUtil.getUserId();
        List<Map<String, Object>> list = challengeService.getMyChallenges(userId);
        return R.ok(list);
    }

    /**
     * 挑战进度
     */
    @GetMapping("/progress/{challengeId}")
    public R<?> progress(@PathVariable Long challengeId) {
        Long userId = AppContextUtil.getUserId();
        Map<String, Object> progress = challengeService.getChallengeProgress(userId, challengeId);
        return R.ok(progress);
    }

    /**
     * 每日打卡
     */
    @PostMapping("/dailyCheckin/{challengeId}")
    public R<?> dailyCheckin(@PathVariable Long challengeId) {
        Long userId = AppContextUtil.getUserId();
        challengeService.dailyCheckin(userId, challengeId);
        return R.ok("打卡成功");
    }
}
