package com.xinling.stock.domain.dto;

/**
 * AI分析查询参数 DTO
 */
public class AnalysisQueryDTO {

    /** 标的类型: stock / sector */
    private String symbolType;

    /** 标的代码: 600519 / BK0477 */
    private String symbolCode;

    /** 分析类型: daily / weekly / monthly */
    private String analysisType;

    /** 是否使用深度分析（调用LLM） */
    private Boolean deepAnalysis;

    public String getSymbolType() { return symbolType; }
    public void setSymbolType(String symbolType) { this.symbolType = symbolType; }

    public String getSymbolCode() { return symbolCode; }
    public void setSymbolCode(String symbolCode) { this.symbolCode = symbolCode; }

    public String getAnalysisType() { return analysisType; }
    public void setAnalysisType(String analysisType) { this.analysisType = analysisType; }

    public Boolean getDeepAnalysis() { return deepAnalysis; }
    public void setDeepAnalysis(Boolean deepAnalysis) { this.deepAnalysis = deepAnalysis; }
}
