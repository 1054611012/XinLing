package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.Challenge;
import com.xinling.app.domain.entity.UserChallenge;
import com.xinling.app.mapper.ChallengeMapper;
import com.xinling.app.mapper.UserChallengeMapper;
import com.xinling.app.service.IChallengeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * 挑战活动服务实现
 */
@Service
public class ChallengeServiceImpl implements IChallengeService {

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImpl.class);

    private final ChallengeMapper challengeMapper;
    private final UserChallengeMapper userChallengeMapper;

    public ChallengeServiceImpl(ChallengeMapper challengeMapper,
                                UserChallengeMapper userChallengeMapper) {
        this.challengeMapper = challengeMapper;
        this.userChallengeMapper = userChallengeMapper;
    }

    @Override
    public List<Challenge> listChallenges() {
        return challengeMapper.selectAvailable();
    }

    @Override
    public List<Map<String, Object>> listChallengeVOs() {
        List<Challenge> challenges = challengeMapper.selectAvailable();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Challenge c : challenges) {
            Map<String, Object> item = challengeToMap(c);
            // 查询参与人数
            int joinedCount = userChallengeMapper.countByChallengeId(c.getId());
            item.put("joinedCount", joinedCount);
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional
    public UserChallenge joinChallenge(Long userId, Long challengeId) {
        Challenge challenge = challengeMapper.selectById(challengeId);
        if (challenge == null) {
            throw new RuntimeException("挑战活动不存在");
        }
        UserChallenge existing = userChallengeMapper.selectByUserAndChallenge(userId, challengeId);
        if (existing != null) {
            throw new RuntimeException("已参与该挑战");
        }
        UserChallenge userChallenge = new UserChallenge();
        userChallenge.setUserId(userId);
        userChallenge.setChallengeId(challengeId);
        userChallenge.setCurrentDay(0);
        userChallenge.setCompletedDays(0);
        userChallenge.setStatus(0);
        userChallengeMapper.insert(userChallenge);
        log.info("用户参与挑战: userId={}, challengeId={}", userId, challengeId);
        return userChallenge;
    }

    @Override
    @Transactional
    public Map<String, Object> dailyCheckin(Long userId, Long challengeId) {
        Challenge challenge = challengeMapper.selectById(challengeId);
        if (challenge == null) {
            throw new RuntimeException("挑战活动不存在");
        }
        UserChallenge uc = userChallengeMapper.selectByUserAndChallenge(userId, challengeId);
        if (uc == null) {
            throw new RuntimeException("您尚未参与该挑战");
        }
        if (uc.getStatus() == 1) {
            throw new RuntimeException("该挑战已完成");
        }

        // 检查今天是否已打卡
        Date lastCheckin = uc.getUpdateTime();
        if (lastCheckin != null) {
            LocalDate lastDate = lastCheckin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();
            if (lastDate.equals(today)) {
                throw new RuntimeException("今天已打卡");
            }
        }

        // 更新进度
        int newCurrentDay = (uc.getCurrentDay() != null ? uc.getCurrentDay() : 0) + 1;
        uc.setCurrentDay(newCurrentDay);

        if (newCurrentDay >= challenge.getDuration()) {
            // 挑战完成
            uc.setCompletedDays(challenge.getDuration());
            uc.setStatus(1);
            log.info("用户完成挑战: userId={}, challengeId={}", userId, challengeId);
        }

        userChallengeMapper.updateById(uc);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("currentDay", uc.getCurrentDay());
        result.put("completedDays", uc.getCompletedDays());
        result.put("status", uc.getStatus());
        result.put("totalDays", challenge.getDuration());
        result.put("progressPercent", challenge.getDuration() > 0
                ? (uc.getCompletedDays() * 100 / challenge.getDuration()) : 0);
        result.put("rewardEarned", uc.getStatus() == 1);
        return result;
    }

    @Override
    public List<Map<String, Object>> getMyChallenges(Long userId) {
        List<UserChallenge> list = userChallengeMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserChallenge uc : list) {
            Challenge challenge = challengeMapper.selectById(uc.getChallengeId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", uc.getId());
            item.put("challengeId", uc.getChallengeId());
            item.put("title", challenge != null ? challenge.getTitle() : null);
            item.put("cover", challenge != null ? challenge.getCover() : null);
            item.put("type", challenge != null ? challenge.getType() : null);
            item.put("duration", challenge != null ? challenge.getDuration() : 0);
            item.put("pointsReward", challenge != null ? challenge.getPointsReward() : 0);
            item.put("vipDaysReward", challenge != null ? challenge.getVipDaysReward() : 0);
            item.put("joinTime", uc.getJoinTime());
            item.put("currentDay", uc.getCurrentDay());
            item.put("completedDays", uc.getCompletedDays());
            item.put("status", uc.getStatus());
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> getChallengeProgress(Long userId, Long challengeId) {
        Challenge challenge = challengeMapper.selectById(challengeId);
        if (challenge == null) {
            throw new RuntimeException("挑战活动不存在");
        }
        UserChallenge userChallenge = userChallengeMapper.selectByUserAndChallenge(userId, challengeId);
        if (userChallenge == null) {
            throw new RuntimeException("您尚未参与该挑战");
        }
        Map<String, Object> result = challengeToMap(challenge);
        result.put("currentDay", userChallenge.getCurrentDay());
        result.put("completedDays", userChallenge.getCompletedDays());
        result.put("totalDays", challenge.getDuration());
        result.put("userStatus", userChallenge.getStatus());
        result.put("progressPercent", challenge.getDuration() > 0
                ? (userChallenge.getCompletedDays() * 100 / challenge.getDuration()) : 0);
        return result;
    }

    /** Challenge 转 Map */
    private Map<String, Object> challengeToMap(Challenge c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("title", c.getTitle());
        map.put("description", c.getDescription());
        map.put("cover", c.getCover());
        map.put("type", c.getType());
        map.put("duration", c.getDuration());
        map.put("conditionValue", c.getConditionValue());
        map.put("pointsReward", c.getPointsReward());
        map.put("vipDaysReward", c.getVipDaysReward());
        map.put("startTime", c.getStartTime());
        map.put("endTime", c.getEndTime());
        map.put("status", c.getStatus());
        return map;
    }
}
