package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.ContentBg;
import com.xinling.app.domain.entity.Meditation;
import com.xinling.app.domain.entity.MeditationAudio;
import com.xinling.app.mapper.ContentBgMapper;
import com.xinling.app.mapper.MeditationAudioMapper;
import com.xinling.app.mapper.MeditationMapper;
import com.xinling.app.service.IMeditationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 冥想内容 Service 实现
 *
 * @author xinling
 */
@Service
public class MeditationServiceImpl implements IMeditationService {

    private static final Logger log = LoggerFactory.getLogger(MeditationServiceImpl.class);

    private final MeditationMapper meditationMapper;
    private final MeditationAudioMapper meditationAudioMapper;
    private final ContentBgMapper contentBgMapper;

    public MeditationServiceImpl(MeditationMapper meditationMapper,
                                  MeditationAudioMapper meditationAudioMapper,
                                  ContentBgMapper contentBgMapper) {
        this.meditationMapper = meditationMapper;
        this.meditationAudioMapper = meditationAudioMapper;
        this.contentBgMapper = contentBgMapper;
    }

    @Override
    public Meditation getById(Long id) {
        Meditation meditation = meditationMapper.selectById(id);
        if (meditation == null) {
            return null;
        }
        // 分步查询：替代 N+1
        List<MeditationAudio> audios = meditationAudioMapper.selectByMeditationId(id);
        meditation.setAudioItems(audios);

        List<ContentBg> bgs = contentBgMapper.selectByContent("meditation", id);
        meditation.setBackgroundImages(bgs);

        return meditation;
    }

    @Override
    public List<Meditation> getList(String keyword, Integer status) {
        List<Meditation> list = meditationMapper.selectList(keyword, status);
        if (list.isEmpty()) {
            return list;
        }

        // 批量查询音频和背景图，按 meditation_id 分组，在应用层组装
        List<Long> ids = list.stream().map(Meditation::getId).collect(Collectors.toList());

        List<MeditationAudio> allAudios = meditationAudioMapper.batchSelectByMeditationIds(ids);
        Map<Long, List<MeditationAudio>> audioMap = allAudios.stream()
                .collect(Collectors.groupingBy(MeditationAudio::getMeditationId));

        List<ContentBg> allBgs = contentBgMapper.batchSelectByContentType("meditation", ids);
        Map<Long, List<ContentBg>> bgMap = allBgs.stream()
                .collect(Collectors.groupingBy(ContentBg::getContentId));

        for (Meditation meditation : list) {
            meditation.setAudioItems(audioMap.getOrDefault(meditation.getId(), Collections.emptyList()));
            meditation.setBackgroundImages(bgMap.getOrDefault(meditation.getId(), Collections.emptyList()));
        }

        return list;
    }

    @Override
    @Transactional
    public Meditation create(Meditation meditation) {
        Date now = new Date();
        meditation.setCreateTime(now);
        meditation.setUpdateTime(now);
        meditationMapper.insert(meditation);
        return meditation;
    }

    @Override
    @Transactional
    public Meditation update(Long id, Meditation meditation) {
        Meditation exist = meditationMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + id);
        }
        meditation.setId(id);
        meditation.setUpdateTime(new Date());
        meditationMapper.updateById(meditation);
        return meditationMapper.selectById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Meditation exist = meditationMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + id);
        }
        meditationAudioMapper.deleteByMeditationId(id);
        contentBgMapper.deleteByContent("meditation", id);
        meditationMapper.deleteById(id);
        log.info("删除冥想内容: id={}", id);
    }

    @Override
    @Transactional
    public void online(Long id) {
        Meditation exist = meditationMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + id);
        }
        Meditation update = new Meditation();
        update.setId(id);
        update.setStatus(1);
        update.setUpdateTime(new Date());
        meditationMapper.updateById(update);
    }

    @Override
    @Transactional
    public void offline(Long id) {
        Meditation exist = meditationMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + id);
        }
        Meditation update = new Meditation();
        update.setId(id);
        update.setStatus(0);
        update.setUpdateTime(new Date());
        meditationMapper.updateById(update);
    }

    @Override
    @Transactional
    public List<MeditationAudio> saveAudioItems(Long meditationId, List<MeditationAudio> audioList) {
        Meditation exist = meditationMapper.selectById(meditationId);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + meditationId);
        }

        meditationAudioMapper.deleteByMeditationId(meditationId);

        if (audioList != null && !audioList.isEmpty()) {
            for (MeditationAudio audio : audioList) {
                audio.setMeditationId(meditationId);
                audio.setId(null);
                meditationAudioMapper.insert(audio);
            }
        }

        return meditationAudioMapper.selectByMeditationId(meditationId);
    }

    @Override
    @Transactional
    public List<ContentBg> saveBackgroundImages(Long meditationId, List<String> bgUrls) {
        Meditation exist = meditationMapper.selectById(meditationId);
        if (exist == null) {
            throw new RuntimeException("冥想内容不存在: " + meditationId);
        }

        contentBgMapper.deleteByContent("meditation", meditationId);

        if (bgUrls != null && !bgUrls.isEmpty()) {
            int order = 0;
            for (String url : bgUrls) {
                ContentBg bg = new ContentBg();
                bg.setContentType("meditation");
                bg.setContentId(meditationId);
                bg.setUrl(url);
                bg.setSortOrder(order++);
                contentBgMapper.insert(bg);
            }
        }

        return contentBgMapper.selectByContent("meditation", meditationId);
    }
}
