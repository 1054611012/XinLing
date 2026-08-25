package com.xinling.stock.service.data;

import com.xinling.stock.domain.entity.*;
import com.xinling.stock.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据同步服务
 *
 * 从东方财富 API 拉取真实行情数据并写入数据库。
 * 支持同步：板块K线 / 个股K线 / 期货K线 / 板块排名 / 市场情绪
 */
@Service
public class DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);

    private final EastMoneyApiService api;
    private final KlineDailyMapper klineDailyMapper;
    private final SectorMapper sectorMapper;
    private final StockMapper stockMapper;
    private final SectorPerformanceMapper sectorPerformanceMapper;
    private final FuturesQuoteMapper futuresQuoteMapper;
    private final MarketSentimentMapper marketSentimentMapper;

    public DataSyncService(EastMoneyApiService api,
                           KlineDailyMapper klineDailyMapper,
                           SectorMapper sectorMapper,
                           StockMapper stockMapper,
                           SectorPerformanceMapper sectorPerformanceMapper,
                           FuturesQuoteMapper futuresQuoteMapper,
                           MarketSentimentMapper marketSentimentMapper) {
        this.api = api;
        this.klineDailyMapper = klineDailyMapper;
        this.sectorMapper = sectorMapper;
        this.stockMapper = stockMapper;
        this.sectorPerformanceMapper = sectorPerformanceMapper;
        this.futuresQuoteMapper = futuresQuoteMapper;
        this.marketSentimentMapper = marketSentimentMapper;
    }

    /**
     * 同步所有板块K线数据
     */
    public SyncResult syncAllSectors() {
        List<Sector> sectors = sectorMapper.selectAll();
        log.info("开始同步板块数据: 共{}个板块", sectors.size());

        int success = 0, fail = 0;
        for (Sector sector : sectors) {
            try {
                int count = syncSectorKLine(sector.getSectorCode());
                if (count > 0) success++;
                else fail++;
                Thread.sleep(200); // 避免触发反爬
            } catch (Exception e) {
                log.warn("同步板块[{}]失败: {}", sector.getSectorName(), e.getMessage());
                fail++;
            }
        }
        return new SyncResult("sector", sectors.size(), success, fail);
    }

    /**
     * 同步单个板块K线
     */
    public int syncSectorKLine(String sectorCode) {
        // 获取本地已有最新日期
        Date maxDate = klineDailyMapper.selectMaxTradeDate("sector", sectorCode);
        int needCount = maxDate == null ? 365 : 20; // 无数据拉一年，有数据补最近20天

        List<KlineDaily> klineList = api.fetchSectorKLine(sectorCode, needCount);
        if (klineList.isEmpty()) {
            log.warn("板块[{}]未获取到K线数据", sectorCode);
            return 0;
        }

        return saveKLineBatch(klineList);
    }

    /**
     * 同步指定股票K线
     */
    public int syncStockKLine(String stockCode, String market) {
        int mk = "SH".equalsIgnoreCase(market) ? 1 : 0;
        Date maxDate = klineDailyMapper.selectMaxTradeDate("stock", stockCode);
        int needCount = maxDate == null ? 365 : 20;

        List<KlineDaily> klineList = api.fetchStockKLine(stockCode, mk, needCount);
        if (klineList.isEmpty()) {
            log.warn("股票[{}]未获取到K线数据", stockCode);
            return 0;
        }
        return saveKLineBatch(klineList);
    }

    /**
     * 批量同步所有股票K线（慎用，数量多）
     */
    public SyncResult syncAllStocks() {
        List<Stock> stocks = stockMapper.selectList(null, null, null, null);
        log.info("开始同步个股K线: 共{}只", stocks.size());

        int success = 0, fail = 0;
        for (Stock stock : stocks) {
            try {
                int count = syncStockKLine(stock.getStockCode(), stock.getMarket());
                if (count > 0) success++;
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步股票[{}]失败: {}", stock.getStockCode(), e.getMessage());
                fail++;
            }
        }
        return new SyncResult("stock", stocks.size(), success, fail);
    }

    /**
     * 同步板块每日表现和排名
     */
    @Transactional
    public int syncSectorPerformance() {
        // 获取行业板块排名（type=2）
        var sectorList = api.fetchSectorRanking(2, 100);
        if (sectorList.isEmpty()) {
            log.warn("未获取到板块排名数据");
            return 0;
        }

        Date today = new java.sql.Date(System.currentTimeMillis());
        int count = 0;

        for (var item : sectorList) {
            try {
                String code = item.getString("f12");
                if (code == null) continue;

                Sector sector = sectorMapper.selectByCode(code);
                if (sector == null) continue;

                SectorPerformance sp = new SectorPerformance();
                sp.setSectorId(sector.getId());
                sp.setTradeDate(today);
                sp.setChangePct(item.getBigDecimal("f3"));
                sp.setVolume(item.getLong("f8"));
                sp.setAmount(item.getBigDecimal("f20"));
                sp.setAdvanceCount(item.getInteger("f15"));
                sp.setDeclineCount(item.getInteger("f16"));
                sp.setLimitUpCount(item.getInteger("f17"));
                sp.setLimitDownCount(item.getInteger("f18"));
                sp.setLeaderStock(item.getString("f14"));
                sp.setLeaderChangePct(item.getBigDecimal("f4"));

                // upsert
                var exists = sectorPerformanceMapper.selectBySectorAndDate(sector.getId(), today);
                if (exists != null) {
                    sectorPerformanceMapper.updateBySectorAndDate(sp);
                } else {
                    sectorPerformanceMapper.insert(sp);
                }
                count++;
            } catch (Exception e) {
                log.warn("保存板块表现失败: {}", e.getMessage());
            }
        }

        // 更新排名
        updateSectorRanks(today);
        return count;
    }

    /**
     * 更新板块排名
     */
    private void updateSectorRanks(Date tradeDate) {
        var perfList = sectorPerformanceMapper.selectByDateOrderByChange(tradeDate, 300);
        int rank = 1;
        for (var perf : perfList) {
            perf.setRank1d(rank++);
            sectorPerformanceMapper.updateBySectorAndDate(perf);
        }
    }

    /**
     * 同步市场情绪数据（涨停/跌停统计）
     */
    @Transactional
    public MarketSentiment syncMarketSentiment() {
        Date today = new java.sql.Date(System.currentTimeMillis());

        // 涨停数据
        var limitUpData = api.fetchLimitUpData();
        var limitDownData = api.fetchLimitDownData();

        int limitUp = countFromApiResult(limitUpData);
        int limitDown = countFromApiResult(limitDownData);

        MarketSentiment ms = new MarketSentiment();
        ms.setTradeDate(today);
        ms.setLimitUpCount(limitUp);
        ms.setLimitDownCount(limitDown);
        ms.setSentimentScore(calculateSentimentScore(limitUp, limitDown));

        var existing = marketSentimentMapper.selectByDate(today);
        if (existing != null) {
            marketSentimentMapper.updateByDate(ms);
        } else {
            marketSentimentMapper.insert(ms);
        }
        return ms;
    }

    // ========================================================================
    //  内部方法
    // ========================================================================

    private int saveKLineBatch(List<KlineDaily> klineList) {
        if (klineList.isEmpty()) return 0;

        // 去重：数据库已有则跳过
        var first = klineList.get(0);
        var last = klineList.get(klineList.size() - 1);
        var existing = klineDailyMapper.selectBySymbolAndDateRange(
                first.getSymbolType(), first.getSymbolCode(),
                first.getTradeDate(), last.getTradeDate());

        Set<String> existDates = existing.stream()
                .map(k -> k.getTradeDate().toString())
                .collect(Collectors.toSet());

        List<KlineDaily> toInsert = klineList.stream()
                .filter(k -> !existDates.contains(k.getTradeDate().toString()))
                .collect(Collectors.toList());

        if (toInsert.isEmpty()) {
            log.info("[{}] 数据已是最新，无需更新", first.getSymbolCode());
            return 0;
        }

        klineDailyMapper.insertBatch(toInsert);
        log.info("[{}] 同步{}条K线数据", first.getSymbolCode(), toInsert.size());
        return toInsert.size();
    }

    private int countFromApiResult(com.alibaba.fastjson2.JSONObject result) {
        try {
            var data = result.getJSONObject("data");
            if (data != null) {
                var diff = data.getJSONArray("diff");
                return diff != null ? diff.size() : 0;
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private java.math.BigDecimal calculateSentimentScore(int limitUp, int limitDown) {
        int total = limitUp + limitDown;
        if (total == 0) return java.math.BigDecimal.valueOf(50);
        double ratio = (double) limitUp / total;
        // ratio=1 → 100分, ratio=0.5 → 50分, ratio=0 → 0分
        double score = ratio * 100;
        return java.math.BigDecimal.valueOf(Math.round(score));
    }

    // ========================================================================
    //  同步结果
    // ========================================================================

    public static class SyncResult {
        private final String type;
        private final int total;
        private final int success;
        private final int fail;
        private final long timestamp;

        public SyncResult(String type, int total, int success, int fail) {
            this.type = type;
            this.total = total;
            this.success = success;
            this.fail = fail;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() { return type; }
        public int getTotal() { return total; }
        public int getSuccess() { return success; }
        public int getFail() { return fail; }
        public long getTimestamp() { return timestamp; }
        public String getSummary() {
            return String.format("[%s] 总计%d 成功%d 失败%d", type, total, success, fail);
        }
    }
}
