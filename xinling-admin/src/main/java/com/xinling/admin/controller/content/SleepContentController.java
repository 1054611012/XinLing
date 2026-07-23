package com.xinling.admin.controller.content;

import com.xinling.app.domain.entity.ContentBg;
import com.xinling.app.domain.entity.SleepItem;
import com.xinling.app.mapper.ContentBgMapper;
import com.xinling.app.mapper.SleepItemMapper;
import com.xinling.app.mapper.SleepDiaryMapper;
import com.xinling.app.mapper.SleepRecordMapper;
import com.xinling.app.domain.entity.SleepDiary;
import com.xinling.app.domain.entity.SleepRecord;
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
 * 睡眠内容管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/content/sleep")
@Tag(name = "睡眠内容管理", description = "睡眠内容管理")
public class SleepContentController extends BaseController {

    private final SleepItemMapper sleepItemMapper;
    private final ContentBgMapper contentBgMapper;
    private final SleepRecordMapper sleepRecordMapper;
    private final SleepDiaryMapper sleepDiaryMapper;

    public SleepContentController(SleepItemMapper sleepItemMapper,
                                   ContentBgMapper contentBgMapper,
                                   SleepRecordMapper sleepRecordMapper,
                                   SleepDiaryMapper sleepDiaryMapper) {
        this.sleepItemMapper = sleepItemMapper;
        this.contentBgMapper = contentBgMapper;
        this.sleepRecordMapper = sleepRecordMapper;
        this.sleepDiaryMapper = sleepDiaryMapper;
    }

    // ==================== 睡眠内容管理 ====================

    @PreAuthorize("@ss.hasPermi('content:sleep:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status) {
        startPage();
        List<SleepItem> list = sleepItemMapper.selectList(keyword, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        SleepItem item = sleepItemMapper.selectById(id);
        if (item == null) return error("睡眠内容不存在");
        return success(item);
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:create')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody SleepItem item) {
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        return toAjax(sleepItemMapper.insert(item));
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:update')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody SleepItem item) {
        SleepItem exist = sleepItemMapper.selectById(id);
        if (exist == null) return error("睡眠内容不存在");
        item.setId(id);
        item.setUpdateTime(new Date());
        return toAjax(sleepItemMapper.updateById(item));
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:delete')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        SleepItem exist = sleepItemMapper.selectById(id);
        if (exist == null) return error("睡眠内容不存在");
        contentBgMapper.deleteByContent("sleep", id);
        return toAjax(sleepItemMapper.deleteById(id));
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:update')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/online/{id}")
    public AjaxResult online(@PathVariable Long id) {
        SleepItem item = sleepItemMapper.selectById(id);
        if (item == null) return error("睡眠内容不存在");
        item.setStatus(1);
        item.setUpdateTime(new Date());
        return toAjax(sleepItemMapper.updateById(item));
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:update')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/offline/{id}")
    public AjaxResult offline(@PathVariable Long id) {
        SleepItem item = sleepItemMapper.selectById(id);
        if (item == null) return error("睡眠内容不存在");
        item.setStatus(0);
        item.setUpdateTime(new Date());
        return toAjax(sleepItemMapper.updateById(item));
    }

    // ==================== 背景图管理 ====================

    @PreAuthorize("@ss.hasPermi('content:sleep:update')")
    @Log(title = "睡眠内容管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/bg/batch")
    public AjaxResult batchBg(@PathVariable Long id, @RequestBody List<String> bgUrls) {
        SleepItem exist = sleepItemMapper.selectById(id);
        if (exist == null) return error("睡眠内容不存在");
        contentBgMapper.deleteByContent("sleep", id);
        if (bgUrls != null) {
            int order = 0;
            for (String url : bgUrls) {
                ContentBg bg = new ContentBg();
                bg.setContentType("sleep");
                bg.setContentId(id);
                bg.setUrl(url);
                bg.setSortOrder(order++);
                contentBgMapper.insert(bg);
            }
        }
        return success(contentBgMapper.selectByContent("sleep", id));
    }

    // ==================== 睡眠记录查看 ====================

    @PreAuthorize("@ss.hasPermi('content:sleep:record:list')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(@RequestParam(required = false) Long userId,
                                    @RequestParam(required = false) String beginTime,
                                    @RequestParam(required = false) String endTime) {
        startPage();
        List<SleepRecord> list = sleepRecordMapper.selectList(userId, beginTime, endTime);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:record:query')")
    @GetMapping("/record/{id}")
    public AjaxResult recordInfo(@PathVariable Long id) {
        SleepRecord record = sleepRecordMapper.selectById(id);
        if (record == null) return error("睡眠记录不存在");
        return success(record);
    }

    // ==================== 睡眠日记查看 ====================

    @PreAuthorize("@ss.hasPermi('content:sleep:diary:list')")
    @GetMapping("/diary/list")
    public TableDataInfo diaryList(@RequestParam(required = false) Long userId) {
        startPage();
        List<SleepDiary> list = sleepDiaryMapper.selectAll(userId);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:sleep:diary:query')")
    @GetMapping("/diary/{id}")
    public AjaxResult diaryInfo(@PathVariable Long id) {
        SleepDiary diary = sleepDiaryMapper.selectById(id);
        if (diary == null) return error("睡眠日记不存在");
        return success(diary);
    }
}
