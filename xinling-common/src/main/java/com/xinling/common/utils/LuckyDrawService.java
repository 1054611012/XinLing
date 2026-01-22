package com.xinling.common.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通用活动抽奖功能
 * 支持概率控制 + 库存控制 + 并发安全
 */
public class LuckyDrawService {

    // 奖品类
    public static class Prize {
        public String name;     // 奖品名
        public int weight;      // 抽中权重（概率）
        public int stock;       // 当前库存
        public Prize(String name, int weight, int stock) {
            this.name = name;
            this.weight = weight;
            this.stock = stock;
        }
        @Override
        public String toString() {
            return String.format("%s [权重:%d 库存:%d]", name, weight, stock);
        }
    }

    // 奖品池
    private final Map<String, Prize> prizeMap = new ConcurrentHashMap<>();

    // 抽奖记录（userId -> List of prizes）
    private final Map<Long, List<String>> userRecords = new ConcurrentHashMap<>();

    // 添加奖品
    public void addPrize(String name, int weight, int stock) {
        prizeMap.put(name, new Prize(name, weight, stock));
    }

    // 抽奖（单次）
    public synchronized Prize draw(long userId) {
        // 过滤库存为0的奖品
        List<Prize> available = new ArrayList<>();
        for (Prize p : prizeMap.values()) {
            if (p.stock > 0 && p.weight > 0) available.add(p);
        }
        if (available.isEmpty()) return null;

        // 计算总权重
        int totalWeight = available.stream().mapToInt(p -> p.weight).sum();

        int rand = ThreadLocalRandom.current().nextInt(totalWeight);
        int current = 0;
        for (Prize p : available) {
            current += p.weight;
            if (rand < current) {
                p.stock--; // 扣库存
                // 记录结果
                userRecords.computeIfAbsent(userId, k -> new ArrayList<>()).add(p.name);
                return p;
            }
        }
        return null;
    }

    // 多次抽奖（十连）
    public synchronized List<Prize> drawMultiple(long userId, int times) {
        List<Prize> result = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            Prize p = draw(userId);
            if (p != null) result.add(p);
        }
        return result;
    }

    // 查看所有奖品情况
    public void showPrizes() {
        System.out.println("=== 奖品池状态 ===");
        prizeMap.values().forEach(System.out::println);
    }

    // 查看某个用户的抽奖记录
    public void showUserRecord(long userId) {
        List<String> list = userRecords.getOrDefault(userId, Collections.emptyList());
        System.out.println("用户 " + userId + " 抽中：" + list);
    }

    // ======== 测试示例 ========
    public static void main(String[] args) {
        LuckyDrawService draw = new LuckyDrawService();
        draw.addPrize("一等奖：iPhone 16 Pro", 1, 1);
        draw.addPrize("二等奖：耳机", 5, 3);
        draw.addPrize("三等奖：咖啡券", 20, 10);
        draw.addPrize("谢谢参与", 74, 9999);

        long userId = 1001;

        draw.showPrizes();

        System.out.println("\n=== 用户抽奖示例 ===");
        for (int i = 0; i < 100; i++) {
            Prize p = draw.draw(userId);
            System.out.println("第 " + (i + 1) + " 次抽奖结果：" + (p == null ? "无" : p.name));
        }

        draw.showUserRecord(userId);
        draw.showPrizes();
    }
}
