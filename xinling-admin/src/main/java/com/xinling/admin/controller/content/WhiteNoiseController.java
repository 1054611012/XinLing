package com.xinling.admin.controller.content;

import com.xinling.app.domain.entity.ContentBg;
import com.xinling.app.domain.entity.WhiteNoise;
import com.xinling.app.mapper.ContentBgMapper;
import com.xinling.app.mapper.WhiteNoiseMapper;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 白噪音内容管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/content/white-noise")
@Tag(name = "白噪音内容管理", description = "白噪音内容管理")
public class WhiteNoiseController extends BaseController {

    private final WhiteNoiseMapper whiteNoiseMapper;
    private final ContentBgMapper contentBgMapper;

    public WhiteNoiseController(WhiteNoiseMapper whiteNoiseMapper,
                                 ContentBgMapper contentBgMapper) {
        this.whiteNoiseMapper = whiteNoiseMapper;
        this.contentBgMapper = contentBgMapper;
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status) {
        startPage();
        List<WhiteNoise> list = whiteNoiseMapper.selectList(keyword, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        WhiteNoise item = whiteNoiseMapper.selectById(id);
        if (item == null) return error("白噪音内容不存在");
        return success(item);
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:create')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody WhiteNoise item) {
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        return toAjax(whiteNoiseMapper.insert(item));
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:update')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody WhiteNoise item) {
        WhiteNoise exist = whiteNoiseMapper.selectById(id);
        if (exist == null) return error("白噪音内容不存在");
        item.setId(id);
        item.setUpdateTime(new Date());
        return toAjax(whiteNoiseMapper.updateById(item));
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:delete')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        WhiteNoise exist = whiteNoiseMapper.selectById(id);
        if (exist == null) return error("白噪音内容不存在");
        contentBgMapper.deleteByContent("white_noise", id);
        return toAjax(whiteNoiseMapper.deleteById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:update')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/online/{id}")
    public AjaxResult online(@PathVariable Long id) {
        WhiteNoise item = whiteNoiseMapper.selectById(id);
        if (item == null) return error("白噪音内容不存在");
        item.setStatus(1);
        item.setUpdateTime(new Date());
        return toAjax(whiteNoiseMapper.updateById(item));
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:update')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/offline/{id}")
    public AjaxResult offline(@PathVariable Long id) {
        WhiteNoise item = whiteNoiseMapper.selectById(id);
        if (item == null) return error("白噪音内容不存在");
        item.setStatus(0);
        item.setUpdateTime(new Date());
        return toAjax(whiteNoiseMapper.updateById(item));
    }

    @PreAuthorize("@ss.hasPermi('content:white-noise:update')")
    @Log(title = "白噪音内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/bg/batch")
    public AjaxResult batchBg(@PathVariable Long id, @RequestBody List<String> bgUrls) {
        WhiteNoise exist = whiteNoiseMapper.selectById(id);
        if (exist == null) return error("白噪音内容不存在");
        contentBgMapper.deleteByContent("white_noise", id);
        if (bgUrls != null) {
            int order = 0;
            for (String url : bgUrls) {
                ContentBg bg = new ContentBg();
                bg.setContentType("white_noise");
                bg.setContentId(id);
                bg.setUrl(url);
                bg.setSortOrder(order++);
                contentBgMapper.insert(bg);
            }
        }
        return success(contentBgMapper.selectByContent("white_noise", id));
    }
}
