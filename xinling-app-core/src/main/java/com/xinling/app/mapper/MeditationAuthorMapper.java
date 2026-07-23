package com.xinling.app.mapper;

import com.xinling.app.domain.entity.MeditationAuthor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 冥想作者 Mapper
 *
 * @author xinling
 */
public interface MeditationAuthorMapper {
    List<MeditationAuthor> selectByMeditationId(@Param("meditationId") Long meditationId);
    int insert(MeditationAuthor author);
    int batchInsert(@Param("list") List<MeditationAuthor> list);
    int deleteByMeditationId(@Param("meditationId") Long meditationId);
}
