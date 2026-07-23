package com.xinling.app.mapper;

import com.xinling.app.domain.entity.MeditationAudio;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 冥想-素材关联 Mapper
 *
 * @author xinling
 */
public interface MeditationAudioMapper {
    List<MeditationAudio> selectByMeditationId(@Param("meditationId") Long meditationId);
    List<MeditationAudio> batchSelectByMeditationIds(@Param("ids") List<Long> ids);
    int insert(MeditationAudio audio);
    int batchInsert(@Param("list") List<MeditationAudio> list);
    int deleteByMeditationId(@Param("meditationId") Long meditationId);
}
