package com.xinling.app.service;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.AudioMix;

import java.util.List;
import java.util.Map;

public interface IAudioService {

    List<AudioItem> getAudioList(String fileType, int page, int size);

    int countAudio(String fileType);

    AudioItem getAudioDetail(Long id);

    List<AudioItem> searchAudio(String keyword, int page, int size);

    int countSearchAudio(String keyword);

    List<AudioMix> getMixList();

    AudioMix saveMix(String name, String description, List<Long> audioIds);

    void recordPlay(Long userId, Long audioId, int playedDuration);

    List<AudioItem> getPlayHistory(Long userId, int page, int size);

    int countPlayHistory(Long userId);
}
