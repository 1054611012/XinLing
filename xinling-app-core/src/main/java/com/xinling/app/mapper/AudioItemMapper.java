package com.xinling.app.mapper;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.AudioPlayHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AudioItemMapper {

    AudioItem selectById(@Param("id") Long id);

    List<AudioItem> selectList(@Param("fileType") String fileType,
                               @Param("keyword") String keyword,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    int countList(@Param("fileType") String fileType,
                  @Param("keyword") String keyword);

    List<AudioItem> selectHistoryByUserId(@Param("userId") Long userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    int countHistoryByUserId(@Param("userId") Long userId);

    int insert(AudioItem item);

    int updateById(AudioItem item);

    int updatePlayCount(@Param("id") Long id);

    int deleteById(@Param("id") Long id);

    int insertHistory(AudioPlayHistory history);

    // ==================== 管理后台接口 ====================

    /**
     * 管理后台查询音频列表（含所有状态）
     */
    List<AudioItem> selectAdminList(@Param("fileType") String fileType,
                                    @Param("keyword") String keyword,
                                    @Param("status") Integer status);

    /**
     * 管理后台计数
     */
    int countAdminList(@Param("fileType") String fileType,
                       @Param("keyword") String keyword,
                       @Param("status") Integer status);
}
