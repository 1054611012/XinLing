package com.xinling.app.service;

import com.xinling.app.domain.entity.Challenge;
import com.xinling.app.domain.entity.UserChallenge;

import java.util.List;
import java.util.Map;

/**
 * 挑战活动服务
 */
public interface IChallengeService {

    /**
     * 获取挑战活动列表
     */
    List<Challenge> listChallenges();

    /**
     * 参与挑战
     */
    UserChallenge joinChallenge(Long userId, Long challengeId);

    /**
     * 获取我的挑战列表
     */
    List<Map<String, Object>> getMyChallenges(Long userId);

    /**
     * 获取挑战进度
     */
    Map<String, Object> getChallengeProgress(Long userId, Long challengeId);

    /**
     * 获取带参与人数的挑战列表
     */
    List<Map<String, Object>> listChallengeVOs();

    /**
     * 每日打卡
     */
    Map<String, Object> dailyCheckin(Long userId, Long challengeId);
}
