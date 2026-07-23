package com.xinling.app.mapper;

import com.xinling.app.domain.entity.ContentBg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容背景图 Mapper（冥想/睡眠/白噪音共用）
 *
 * @author xinling
 */
public interface ContentBgMapper {
    List<ContentBg> selectByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);

    /** 按内容类型批量查询（支持多个 contentId，用于 N+1 优化） */
    List<ContentBg> batchSelectByContentType(@Param("contentType") String contentType,
                                              @Param("contentIds") List<Long> contentIds);

    /** 供 MeditationMapper 关联查询 */
    List<ContentBg> selectMeditationBg(@Param("contentId") Long contentId);

    /** 供 SleepItemMapper 关联查询 */
    List<ContentBg> selectSleepBg(@Param("contentId") Long contentId);

    /** 供 WhiteNoiseMapper 关联查询 */
    List<ContentBg> selectWhiteNoiseBg(@Param("contentId") Long contentId);

    int insert(ContentBg bg);
    int batchInsert(@Param("list") List<ContentBg> list);
    int deleteByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);
}
