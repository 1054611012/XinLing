package com.xinling.admin.controller.content;

import com.xinling.app.domain.entity.ContentBg;
import com.xinling.app.domain.entity.Meditation;
import com.xinling.app.domain.entity.MeditationAudio;
import com.xinling.app.service.IMeditationService;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 冥想内容管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/content/meditation")
@Tag(name = "冥想内容管理", description = "冥想内容管理")
public class MeditationController extends BaseController {

    private final IMeditationService meditationService;

    public MeditationController(IMeditationService meditationService) {
        this.meditationService = meditationService;
    }

    // ==================== 冥想内容 CRUD ====================

    @PreAuthorize("@ss.hasPermi('content:meditation:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status) {
        startPage();
        List<Meditation> list = meditationService.getList(keyword, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Meditation item = meditationService.getById(id);
        if (item == null) {
            return error("冥想内容不存在");
        }
        return success(item);
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:create')")
    @Log(title = "冥想内容管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Meditation item) {
        return success(meditationService.create(item));
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody Meditation item) {
        return success(meditationService.update(id, item));
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:delete')")
    @Log(title = "冥想内容管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        meditationService.delete(id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/online/{id}")
    public AjaxResult online(@PathVariable Long id) {
        meditationService.online(id);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/offline/{id}")
    public AjaxResult offline(@PathVariable Long id) {
        meditationService.offline(id);
        return success();
    }

    // ==================== 音频素材 + 老师统一管理 ====================

    /**
     * 批量设置音频素材（含老师关联）。全量替换。
     * <p>
     * 替代旧的 /audio/batch + /author/batch 两个接口。
     * authorId 可选，null=纯背景音乐，无指定老师。
     * <pre>
     * [
     *   { "audioItemId": 1, "authorId": 1, "sortOrder": 0 },
     *   { "audioItemId": 5, "authorId": null, "sortOrder": 1 }
     * ]
     * </pre>
     */
    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/audio-items")
    public AjaxResult batchAudioItems(@PathVariable Long id,
                                       @RequestBody List<MeditationAudio> audioList) {
        return success(meditationService.saveAudioItems(id, audioList));
    }

    // ==================== 背景图管理 ====================

    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/bg/batch")
    public AjaxResult batchBg(@PathVariable Long id, @RequestBody List<String> bgUrls) {
        return success(meditationService.saveBackgroundImages(id, bgUrls));
    }

    // ==================== 废弃接口（兼容旧版） ====================

    /**
     * 废弃，请使用 /{id}/audio-items 替代。
     */
    @Deprecated
    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/audio/batch")
    public AjaxResult batchAudio(@PathVariable Long id, @RequestBody List<MeditationAudio> audioList) {
        return success(meditationService.saveAudioItems(id, audioList));
    }

    /**
     * 废弃，请使用 /{id}/audio-items 替代（将 authorId 携带在 audioItem 中）。
     * 保留此接口兼容旧前端，内部直接将参数转为音频关联。
     * 注意：此兼容接口不再处理独立作者写入，作者信息需通过老师库管理 + audio-items 关联。
     */
    @Deprecated
    @PreAuthorize("@ss.hasPermi('content:meditation:update')")
    @Log(title = "冥想内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/author/batch")
    public AjaxResult batchAuthor(@PathVariable Long id, @RequestBody List<MeditationAudio> audioList) {
        // 兼容处理：旧版 author/batch 请求体中包含 audioItemId+name+avatar，
        // 新版不再支持从 author/batch 写入，引导前端切换到 audio-items
        // 此处仅保留接口响应，不执行实际写入
        return success("接口已废弃，请使用 POST /content/meditation/{id}/audio-items");
    }
}
