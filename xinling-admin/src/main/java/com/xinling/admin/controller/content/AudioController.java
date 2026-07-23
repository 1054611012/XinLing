package com.xinling.admin.controller.content;

import com.xinling.app.domain.entity.AudioItem;
import com.xinling.app.domain.entity.AudioMix;
import com.xinling.app.mapper.AudioItemMapper;
import com.xinling.app.mapper.AudioMixMapper;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.system.domain.entity.file.FileRecord;
import com.xinling.system.service.IFileRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 公共素材库管理控制器
 * <p>
 * 管理音频/视频素材（AudioItem），纯文件元数据，不含业务包装。
 * 业务内容（冥想/睡眠/白噪音）通过各自模块管理。
 *
 * @author xinling
 */
@RestController
@RequestMapping("/content/audio")
@Tag(name = "音频素材库", description = "音频素材库")
public class AudioController extends BaseController {

    private final AudioItemMapper audioItemMapper;
    private final AudioMixMapper audioMixMapper;
    private final IFileRecordService fileRecordService;

    public AudioController(AudioItemMapper audioItemMapper,
                           AudioMixMapper audioMixMapper,
                           IFileRecordService fileRecordService) {
        this.audioItemMapper = audioItemMapper;
        this.audioMixMapper = audioMixMapper;
        this.fileRecordService = fileRecordService;
    }

    // ==================== 素材库管理 ====================

    /**
     * 查询素材列表
     */
    @PreAuthorize("@ss.hasPermi('content:audio:list')")
    @GetMapping("/item/list")
    public TableDataInfo itemList(@RequestParam(required = false) String fileType,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer status) {
        startPage();
        List<AudioItem> list = audioItemMapper.selectAdminList(fileType, keyword, status);
        return getDataTable(list);
    }

    /**
     * 获取素材详情
     */
    @PreAuthorize("@ss.hasPermi('content:audio:query')")
    @GetMapping("/item/{id}")
    public AjaxResult itemInfo(@PathVariable Long id) {
        AudioItem item = audioItemMapper.selectById(id);
        if (item == null) {
            return error("素材不存在");
        }
        return success(item);
    }

    /**
     * 新增素材
     */
    @PreAuthorize("@ss.hasPermi('content:audio:create')")
    @Log(title = "素材库管理", businessType = BusinessType.INSERT)
    @PostMapping("/item/create")
    public AjaxResult itemCreate(@RequestBody AudioItem item) {
        fillAudioUrl(item);
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        return toAjax(audioItemMapper.insert(item));
    }

    /**
     * 修改素材
     */
    @PreAuthorize("@ss.hasPermi('content:audio:update')")
    @Log(title = "素材库管理", businessType = BusinessType.UPDATE)
    @PostMapping("/item/update/{id}")
    public AjaxResult itemUpdate(@PathVariable Long id, @RequestBody AudioItem item) {
        AudioItem exist = audioItemMapper.selectById(id);
        if (exist == null) {
            return error("素材不存在");
        }
        fillAudioUrl(item);
        item.setId(id);
        item.setUpdateTime(new Date());
        return toAjax(audioItemMapper.updateById(item));
    }

    /**
     * 删除素材
     */
    @PreAuthorize("@ss.hasPermi('content:audio:delete')")
    @Log(title = "素材库管理", businessType = BusinessType.DELETE)
    @PostMapping("/item/delete/{id}")
    public AjaxResult itemDelete(@PathVariable Long id) {
        AudioItem exist = audioItemMapper.selectById(id);
        if (exist == null) {
            return error("素材不存在");
        }
        return toAjax(audioItemMapper.deleteById(id));
    }

    /**
     * 上架素材
     */
    @PreAuthorize("@ss.hasPermi('content:audio:update')")
    @Log(title = "素材库管理", businessType = BusinessType.UPDATE)
    @PostMapping("/item/online/{id}")
    public AjaxResult itemOnline(@PathVariable Long id) {
        AudioItem item = audioItemMapper.selectById(id);
        if (item == null) {
            return error("素材不存在");
        }
        item.setStatus(1);
        item.setUpdateTime(new Date());
        return toAjax(audioItemMapper.updateById(item));
    }

    /**
     * 下架素材
     */
    @PreAuthorize("@ss.hasPermi('content:audio:update')")
    @Log(title = "素材库管理", businessType = BusinessType.UPDATE)
    @PostMapping("/item/offline/{id}")
    public AjaxResult itemOffline(@PathVariable Long id) {
        AudioItem item = audioItemMapper.selectById(id);
        if (item == null) {
            return error("素材不存在");
        }
        item.setStatus(0);
        item.setUpdateTime(new Date());
        return toAjax(audioItemMapper.updateById(item));
    }

    // ==================== 混音预设管理 ====================

    @PreAuthorize("@ss.hasPermi('content:audio:mix:list')")
    @GetMapping("/mix/list")
    public TableDataInfo mixList(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer status) {
        startPage();
        List<AudioMix> list = audioMixMapper.selectAdminList(keyword, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:audio:mix:query')")
    @GetMapping("/mix/{id}")
    public AjaxResult mixInfo(@PathVariable Long id) {
        AudioMix mix = audioMixMapper.selectById(id);
        if (mix == null) {
            return error("混音预设不存在");
        }
        return success(mix);
    }

    @PreAuthorize("@ss.hasPermi('content:audio:mix:create')")
    @Log(title = "素材库管理", businessType = BusinessType.INSERT)
    @PostMapping("/mix/create")
    public AjaxResult mixCreate(@RequestBody AudioMix mix) {
        mix.setCreateTime(new Date());
        mix.setUpdateTime(new Date());
        return toAjax(audioMixMapper.insert(mix));
    }

    @PreAuthorize("@ss.hasPermi('content:audio:mix:update')")
    @Log(title = "素材库管理", businessType = BusinessType.UPDATE)
    @PostMapping("/mix/update/{id}")
    public AjaxResult mixUpdate(@PathVariable Long id, @RequestBody AudioMix mix) {
        AudioMix exist = audioMixMapper.selectById(id);
        if (exist == null) {
            return error("混音预设不存在");
        }
        mix.setId(id);
        mix.setUpdateTime(new Date());
        return toAjax(audioMixMapper.updateById(mix));
    }

    @PreAuthorize("@ss.hasPermi('content:audio:mix:delete')")
    @Log(title = "素材库管理", businessType = BusinessType.DELETE)
    @PostMapping("/mix/delete/{id}")
    public AjaxResult mixDelete(@PathVariable Long id) {
        AudioMix exist = audioMixMapper.selectById(id);
        if (exist == null) {
            return error("混音预设不存在");
        }
        return toAjax(audioMixMapper.deleteById(id));
    }

    // ==================== 私有方法 ====================

    private void fillAudioUrl(AudioItem item) {
        if (item.getFileId() != null) {
            FileRecord file = fileRecordService.selectFileRecordById(item.getFileId());
            if (file != null && file.getFileUrl() != null) {
                item.setAudioUrl(file.getFileUrl());
            }
        }
    }
}
