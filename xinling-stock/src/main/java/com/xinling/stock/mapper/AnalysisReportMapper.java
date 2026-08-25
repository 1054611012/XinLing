package com.xinling.stock.mapper;

import com.xinling.stock.domain.entity.AnalysisReport;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * AI分析报告 Mapper
 */
public interface AnalysisReportMapper {

    AnalysisReport selectById(@Param("id") Long id);

    AnalysisReport selectBySymbolAndDate(@Param("symbolType") String symbolType,
                                          @Param("symbolCode") String symbolCode,
                                          @Param("analysisDate") Date analysisDate,
                                          @Param("analysisType") String analysisType);

    List<AnalysisReport> selectBySymbol(@Param("symbolType") String symbolType,
                                         @Param("symbolCode") String symbolCode,
                                         @Param("limit") int limit);

    List<AnalysisReport> selectByDate(@Param("analysisDate") Date analysisDate,
                                       @Param("analysisType") String analysisType);

    List<AnalysisReport> selectByRecommendation(@Param("recommendation") String recommendation,
                                                  @Param("analysisDate") Date analysisDate);

    int insert(AnalysisReport report);

    int updateById(AnalysisReport report);
}
