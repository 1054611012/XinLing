package com.xinling.stock.controller;

import com.xinling.stock.domain.dto.AnalysisQueryDTO;
import com.xinling.stock.domain.vo.AnalysisResultVO;
import com.xinling.stock.service.analysis.FundAnalysisService;
import com.xinling.stock.service.analysis.StockAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI股票分析 REST API
 *
 * 覆盖：个股分析 / 板块分析 / 基金分析 / 数据刷新
 */
@RestController
@RequestMapping("/api/stock/analysis")
public class StockAnalysisController {

    private final StockAnalysisService stockAnalysisService;
    private final FundAnalysisService fundAnalysisService;

    public StockAnalysisController(StockAnalysisService stockAnalysisService,
                                   FundAnalysisService fundAnalysisService) {
        this.stockAnalysisService = stockAnalysisService;
        this.fundAnalysisService = fundAnalysisService;
    }

    /**
     * 分析个股
     * GET /api/stock/analysis/stock/600519?type=daily&deep=false
     */
    @GetMapping("/stock/{stockCode}")
    public AnalysisResultVO analyzeStock(
            @PathVariable String stockCode,
            @RequestParam(defaultValue = "daily") String type,
            @RequestParam(defaultValue = "false") Boolean deep) {
        return stockAnalysisService.analyzeStock(stockCode, type, deep);
    }

    /**
     * 分析板块
     * GET /api/stock/analysis/sector/BK0477?type=daily&deep=false
     */
    @GetMapping("/sector/{sectorCode}")
    public AnalysisResultVO analyzeSector(
            @PathVariable String sectorCode,
            @RequestParam(defaultValue = "daily") String type,
            @RequestParam(defaultValue = "false") Boolean deep) {
        return stockAnalysisService.analyzeSector(sectorCode, type, deep);
    }

    /**
     * 分析基金
     * GET /api/stock/analysis/fund/110011?deep=false
     */
    @GetMapping("/fund/{fundCode}")
    public AnalysisResultVO analyzeFund(
            @PathVariable String fundCode,
            @RequestParam(defaultValue = "false") Boolean deep) {
        return fundAnalysisService.analyze(fundCode, deep);
    }

    /**
     * 智能分析（自动识别类型）
     * POST /api/stock/analysis/analyze
     *
     * 支持类型: stock / sector / fund
     */
    @PostMapping("/analyze")
    public AnalysisResultVO analyze(@RequestBody AnalysisQueryDTO query) {
        String type = query.getSymbolType();
        String code = query.getSymbolCode();

        if ("fund".equals(type)) {
            return fundAnalysisService.analyze(code,
                    query.getDeepAnalysis() != null ? query.getDeepAnalysis() : false);
        }
        if ("sector".equals(type)) {
            return stockAnalysisService.analyzeSector(code,
                    query.getAnalysisType() != null ? query.getAnalysisType() : "daily",
                    query.getDeepAnalysis() != null ? query.getDeepAnalysis() : false);
        }
        return stockAnalysisService.analyzeStock(code,
                query.getAnalysisType() != null ? query.getAnalysisType() : "daily",
                query.getDeepAnalysis() != null ? query.getDeepAnalysis() : false);
    }

    /**
     * 健康检查
     * GET /api/stock/analysis/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("module", "xinling-stock");
        result.put("version", "1.0.0");
        result.put("features", new String[]{"stock", "sector", "futures", "fund", "technical_indicators"});
        return result;
    }
}
