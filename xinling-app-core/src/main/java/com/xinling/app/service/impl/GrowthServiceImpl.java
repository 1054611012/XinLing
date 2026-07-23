package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.*;
import com.xinling.app.mapper.*;
import com.xinling.app.service.IGrowthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 成长体系服务实现
 */
@Service
public class GrowthServiceImpl implements IGrowthService {

    private static final Logger log = LoggerFactory.getLogger(GrowthServiceImpl.class);

    private final UserGrowthMapper userGrowthMapper;
    private final AchievementMapper achievementMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final DailyTaskMapper dailyTaskMapper;
    private final UserTaskMapper userTaskMapper;
    private final MallGoodsMapper mallGoodsMapper;
    private final UserExchangeMapper userExchangeMapper;

    public GrowthServiceImpl(UserGrowthMapper userGrowthMapper,
                             AchievementMapper achievementMapper,
                             UserAchievementMapper userAchievementMapper,
                             DailyTaskMapper dailyTaskMapper,
                             UserTaskMapper userTaskMapper,
                             MallGoodsMapper mallGoodsMapper,
                             UserExchangeMapper userExchangeMapper) {
        this.userGrowthMapper = userGrowthMapper;
        this.achievementMapper = achievementMapper;
        this.userAchievementMapper = userAchievementMapper;
        this.dailyTaskMapper = dailyTaskMapper;
        this.userTaskMapper = userTaskMapper;
        this.mallGoodsMapper = mallGoodsMapper;
        this.userExchangeMapper = userExchangeMapper;
    }

    @Override
    public UserGrowth getGrowthInfo(Long userId) {
        UserGrowth growth = userGrowthMapper.selectByUserId(userId);
        if (growth == null) {
            growth = new UserGrowth();
            growth.setUserId(userId);
            growth.setLevel(1);
            growth.setExp(0);
            growth.setPoints(0);
            growth.setContinuousFocusDays(0);
            growth.setContinuousSleepDays(0);
            userGrowthMapper.insert(growth);
        }
        return growth;
    }

    @Override
    public List<Map<String, Object>> getAchievementList(Long userId) {
        List<Achievement> all = achievementMapper.selectAll();
        List<UserAchievement> userAchievements = userAchievementMapper.selectByUserId(userId);
        Set<Long> obtainedIds = new HashSet<>();
        for (UserAchievement ua : userAchievements) {
            obtainedIds.add(ua.getAchievementId());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Achievement a : all) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("name", a.getName());
            item.put("description", a.getDescription());
            item.put("icon", a.getIcon());
            item.put("conditionType", a.getConditionType());
            item.put("conditionValue", a.getConditionValue());
            item.put("pointsReward", a.getPointsReward());
            item.put("obtained", obtainedIds.contains(a.getId()));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDailyTasks(Long userId) {
        List<DailyTask> all = dailyTaskMapper.selectAll();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        List<Map<String, Object>> result = new ArrayList<>();
        for (DailyTask task : all) {
            UserTask userTask = null;
            try {
                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(today);
                userTask = userTaskMapper.selectByUserTaskAndDate(userId, task.getId(), date);
            } catch (Exception e) {
                // ignore parse exception
            }

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", task.getId());
            item.put("name", task.getName());
            item.put("description", task.getDescription());
            item.put("conditionType", task.getConditionType());
            item.put("conditionValue", task.getConditionValue());
            item.put("pointsReward", task.getPointsReward());
            item.put("icon", task.getIcon());
            item.put("progress", userTask != null ? userTask.getProgress() : 0);
            item.put("status", userTask != null ? userTask.getStatus() : 0);
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional
    public void claimTaskReward(Long userId, Long taskId) {
        DailyTask task = dailyTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        UserTask userTask;
        try {
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(today);
            userTask = userTaskMapper.selectByUserTaskAndDate(userId, taskId, date);
        } catch (Exception e) {
            throw new RuntimeException("日期解析错误");
        }

        if (userTask == null) {
            throw new RuntimeException("今日任务尚未完成");
        }
        if (userTask.getStatus() == 2) {
            throw new RuntimeException("奖励已领取");
        }
        if (userTask.getStatus() != 1) {
            throw new RuntimeException("任务尚未完成");
        }

        // 更新任务状态为已领取
        userTask.setStatus(2);
        userTaskMapper.updateById(userTask);

        // 增加用户积分
        UserGrowth growth = userGrowthMapper.selectByUserId(userId);
        if (growth == null) {
            growth = new UserGrowth();
            growth.setUserId(userId);
            growth.setLevel(1);
            growth.setExp(0);
            growth.setPoints(task.getPointsReward());
            userGrowthMapper.insert(growth);
        } else {
            growth.setPoints(growth.getPoints() + task.getPointsReward());
            growth.setExp(growth.getExp() + task.getPointsReward());
            userGrowthMapper.updateById(growth);
        }

        log.info("用户领取任务奖励: userId={}, taskId={}, points={}", userId, taskId, task.getPointsReward());
    }

    @Override
    public List<MallGoods> getMallGoods() {
        return mallGoodsMapper.selectAvailable();
    }

    @Override
    @Transactional
    public UserExchange exchangeGoods(Long userId, Long goodsId) {
        MallGoods goods = mallGoodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (goods.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (goods.getStock() <= 0) {
            throw new RuntimeException("商品库存不足");
        }

        UserGrowth growth = userGrowthMapper.selectByUserId(userId);
        if (growth == null || growth.getPoints() < goods.getPrice()) {
            throw new RuntimeException("积分不足");
        }

        // 扣减积分
        growth.setPoints(growth.getPoints() - goods.getPrice());
        userGrowthMapper.updateById(growth);

        // 扣减库存
        goods.setStock(goods.getStock() - 1);
        mallGoodsMapper.updateById(goods);

        // 记录兑换
        UserExchange exchange = new UserExchange();
        exchange.setUserId(userId);
        exchange.setGoodsId(goodsId);
        exchange.setPoints(goods.getPrice());
        userExchangeMapper.insert(exchange);

        log.info("用户兑换商品: userId={}, goodsId={}, points={}", userId, goodsId, goods.getPrice());
        return exchange;
    }
}
