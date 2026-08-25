package com.xinling.stock.controller;

import com.xinling.stock.service.data.DataSyncService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据同步 REST API
 *
 * 触发从东方财富拉取真实行情数据并写入数据库
 */
@RestController
@RequestMapping("/api/stock/sync")
public class DataSyncController {

    private final DataSyncService dataSyncService;

    public DataSyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    /**
     * 同步所有板块K线
     * POST /api/stock/sync/sectors
     */
    @PostMapping("/sectors")
    public Map<String, Object> syncSectors() {
        DataSyncService.SyncResult result = dataSyncService.syncAllSectors();
        return syncResponse(result);
    }

    /**
     * 同步单个板块K线
     * POST /api/stock/sync/sector/BK0477
     */
    @PostMapping("/sector/{sectorCode}")
    public Map<String, Object> syncSector(@PathVariable String sectorCode) {
        int count = dataSyncService.syncSectorKLine(sectorCode);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "板块[" + sectorCode + "]同步成功");
        map.put("count", count);
        return map;
    }

    /**
     * 同步板块排名和表现
     * POST /api/stock/sync/sector-performance
     */
    @PostMapping("/sector-performance")
    public Map<String, Object> syncSectorPerformance() {
        int count = dataSyncService.syncSectorPerformance();
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "板块排名同步完成");
        map.put("count", count);
        return map;
    }

    /**
     * 同步市场情绪
     * POST /api/stock/sync/market-sentiment
     */
    @PostMapping("/market-sentiment")
    public Map<String, Object> syncMarketSentiment() {
        var sentiment = dataSyncService.syncMarketSentiment();
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "市场情绪同步完成");
        map.put("limitUp", sentiment.getLimitUpCount());
        map.put("limitDown", sentiment.getLimitDownCount());
        map.put("sentimentScore", sentiment.getSentimentScore());
        return map;
    }

    /**
     * 全量同步（板块 + 排名 + 情绪）
     * POST /api/stock/sync/all
     */
    @PostMapping("/all")
    public Map<String, Object> syncAll() {
        long start = System.currentTimeMillis();

        // 1. 板块K线
        var sectorResult = dataSyncService.syncAllSectors();
        // 2. 板块排名
        int perfCount = dataSyncService.syncSectorPerformance();
        // 3. 市场情绪
        dataSyncService.syncMarketSentiment();

        long elapsed = System.currentTimeMillis() - start;

        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", "全量同步完成");
        map.put("sectors", sectorResult.getSummary());
        map.put("sectorPerformanceCount", perfCount);
        map.put("elapsedMs", elapsed);
        return map;
    }

    private Map<String, Object> syncResponse(DataSyncService.SyncResult result) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("message", result.getSummary());
        map.put("total", result.getTotal());
        map.put("success", result.getSuccess());
        map.put("fail", result.getFail());
        return map;
    }
}
