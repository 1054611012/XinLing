package com.xinling.app.service;

import com.xinling.app.domain.entity.MallGoods;
import com.xinling.app.domain.entity.UserExchange;
import com.xinling.app.domain.entity.UserGrowth;

import java.util.List;
import java.util.Map;

/**
 * 成长体系服务
 */
public interface IGrowthService {

    /**
     * 获取用户成长信息（等级、经验、积分等）
     */
    UserGrowth getGrowthInfo(Long userId);

    /**
     * 获取成就列表（含用户已达成状态）
     */
    List<Map<String, Object>> getAchievementList(Long userId);

    /**
     * 获取每日任务列表（含用户今日进度）
     */
    List<Map<String, Object>> getDailyTasks(Long userId);

    /**
     * 领取任务奖励
     */
    void claimTaskReward(Long userId, Long taskId);

    /**
     * 获取积分商城商品列表
     */
    List<MallGoods> getMallGoods();

    /**
     * 兑换商品
     */
    UserExchange exchangeGoods(Long userId, Long goodsId);
}
