package com.xinling.app.service;

import com.xinling.app.domain.entity.ContentBg;
import com.xinling.app.domain.entity.Meditation;
import com.xinling.app.domain.entity.MeditationAudio;

import java.util.List;

/**
 * 冥想内容 Service 接口
 *
 * @author xinling
 */
public interface IMeditationService {

    /**
     * 获取冥想详情（含完整嵌套：音频素材+老师信息+背景图）
     */
    Meditation getById(Long id);

    /**
     * 分页查询冥想列表（基础信息，不含嵌套数据）
     */
    List<Meditation> getList(String keyword, Integer status);

    /**
     * 新增冥想
     */
    Meditation create(Meditation meditation);

    /**
     * 更新冥想基本信息
     */
    Meditation update(Long id, Meditation meditation);

    /**
     * 删除冥想（级联删除关联音频、背景图）
     */
    void delete(Long id);

    /**
     * 上架
     */
    void online(Long id);

    /**
     * 下架
     */
    void offline(Long id);

    /**
     * 批量设置音频素材（含老师关联）。全量替换。
     */
    List<MeditationAudio> saveAudioItems(Long meditationId, List<MeditationAudio> audioList);

    /**
     * 批量设置背景图。全量替换。
     */
    List<ContentBg> saveBackgroundImages(Long meditationId, List<String> bgUrls);
}
