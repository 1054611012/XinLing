package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.AudioMix;
import com.xinling.app.domain.entity.AudioPlayHistory;
import com.xinling.app.mapper.AudioItemMapper;
import com.xinling.app.mapper.AudioMixMapper;
import com.xinling.app.service.IAudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AudioServiceImpl implements IAudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioServiceImpl.class);

    private final AudioItemMapper audioItemMapper;
    private final AudioMixMapper audioMixMapper;

    public AudioServiceImpl(AudioItemMapper audioItemMapper, AudioMixMapper audioMixMapper) {
        this.audioItemMapper = audioItemMapper;
        this.audioMixMapper = audioMixMapper;
    }

    @Override
    public List<AudioItem> getAudioList(String fileType, int page, int size) {
        int offset = (page - 1) * size;
        return audioItemMapper.selectList(fileType, null, offset, size);
    }

    @Override
    public int countAudio(String fileType) {
        return audioItemMapper.countList(fileType, null);
    }

    @Override
    public AudioItem getAudioDetail(Long id) {
        AudioItem item = audioItemMapper.selectById(id);
        if (item != null) {
            audioItemMapper.updatePlayCount(id);
        }
        return item;
    }

    @Override
    public List<AudioItem> searchAudio(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        return audioItemMapper.selectList(null, keyword, offset, size);
    }

    @Override
    public int countSearchAudio(String keyword) {
        return audioItemMapper.countList(null, keyword);
    }

    @Override
    public List<AudioMix> getMixList() {
        return audioMixMapper.selectList();
    }

    @Override
    @Transactional
    public AudioMix saveMix(String name, String description, List<Long> audioIds) {
        AudioMix mix = new AudioMix();
        mix.setName(name);
        mix.setDescription(description);
        mix.setAudioIds(audioIds != null ? audioIds.toString() : "[]");
        mix.setStatus(1);
        audioMixMapper.insert(mix);
        log.info("保存混音: id={}, name={}", mix.getId(), name);
        return mix;
    }

    @Override
    public void recordPlay(Long userId, Long audioId, int playedDuration) {
        AudioPlayHistory history = new AudioPlayHistory();
        history.setUserId(userId);
        history.setAudioId(audioId);
        history.setPlayedDuration(playedDuration);
        audioItemMapper.insertHistory(history);
    }

    @Override
    public List<AudioItem> getPlayHistory(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return audioItemMapper.selectHistoryByUserId(userId, offset, size);
    }

    @Override
    public int countPlayHistory(Long userId) {
        return audioItemMapper.countHistoryByUserId(userId);
    }
}
