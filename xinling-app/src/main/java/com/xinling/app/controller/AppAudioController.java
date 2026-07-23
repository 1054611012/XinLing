package com.xinling.app.controller;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.AudioMix;
import com.xinling.app.service.IAudioService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/audio")
public class AppAudioController {

    private final IAudioService audioService;

    public AppAudioController(IAudioService audioService) {
        this.audioService = audioService;
    }

    /**
     * 音频列表（分页）
     */
    @GetMapping("/list")
    public R<Map<String, Object>> list(@RequestParam(required = false) String fileType,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        List<AudioItem> list = audioService.getAudioList(fileType, page, size);
        int total = audioService.countAudio(fileType);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 音频详情
     */
    @GetMapping({"/{id}", "/detail/{id}"})
    public R<AudioItem> detail(@PathVariable Long id) {
        AudioItem item = audioService.getAudioDetail(id);
        return R.ok(item);
    }

    /**
     * 搜索音频（分页）
     */
    @GetMapping("/search")
    public R<Map<String, Object>> search(@RequestParam String keyword,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        List<AudioItem> list = audioService.searchAudio(keyword, page, size);
        int total = audioService.countSearchAudio(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 音频混音列表
     */
    @GetMapping({"/mix/list", "/mixList"})
    public R<List<AudioMix>> mixList() {
        List<AudioMix> list = audioService.getMixList();
        return R.ok(list);
    }

    /**
     * 保存自定义混音
     */
    @PostMapping({"/mix", "/saveMix"})
    public R<AudioMix> saveMix(@RequestParam String name,
                               @RequestParam(required = false) String description,
                               @RequestBody List<Long> audioIds) {
        AudioMix mix = audioService.saveMix(name, description, audioIds);
        return R.ok(mix);
    }

    /**
     * 播放记录
     */
    @GetMapping({"/history", "/playHistory"})
    public R<Map<String, Object>> playHistory(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Long userId = AppContextUtil.getUserId();
        List<AudioItem> list = audioService.getPlayHistory(userId, page, size);
        int total = audioService.countPlayHistory(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 记录播放
     */
    @PostMapping("/recordPlay")
    public R<?> recordPlay(@RequestParam Long audioId,
                           @RequestParam(defaultValue = "0") int playedDuration) {
        Long userId = AppContextUtil.getUserId();
        audioService.recordPlay(userId, audioId, playedDuration);
        return R.ok();
    }
}
