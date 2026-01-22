package com.xinling.web.controller.psyc;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.enums.BusinessType;
import com.xinling.psyc.domain.PsycFeedback;
import com.xinling.psyc.service.IPsycFeedbackService;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.common.core.page.TableDataInfo;

/**
 * 意见反馈Controller
 *
 * @author xinling
 * @date 2025-11-27
 */
@RestController
@RequestMapping("/psyc/feedback")
public class PsycFeedbackController extends BaseController
{
    @Autowired
    private IPsycFeedbackService psycFeedbackService;

    /**
     * 查询意见反馈列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(PsycFeedback psycFeedback)
    {
        startPage();
        List<PsycFeedback> list = psycFeedbackService.selectPsycFeedbackList(psycFeedback);
        return getDataTable(list);
    }

    /**
     * 导出意见反馈列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:export')")
    @Log(title = "意见反馈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PsycFeedback psycFeedback)
    {
        List<PsycFeedback> list = psycFeedbackService.selectPsycFeedbackList(psycFeedback);
        ExcelUtil<PsycFeedback> util = new ExcelUtil<PsycFeedback>(PsycFeedback.class);
        util.exportExcel(response, list, "意见反馈数据");
    }

    /**
     * 获取意见反馈详细信息
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(psycFeedbackService.selectPsycFeedbackById(id));
    }

    /**
     * 新增意见反馈
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:add')")
    @Log(title = "意见反馈", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PsycFeedback psycFeedback)
    {
        return toAjax(psycFeedbackService.insertPsycFeedback(psycFeedback));
    }

    /**
     * 修改意见反馈
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:edit')")
    @Log(title = "意见反馈", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PsycFeedback psycFeedback)
    {
        return toAjax(psycFeedbackService.updatePsycFeedback(psycFeedback));
    }

    /**
     * 删除意见反馈
     */
    @PreAuthorize("@ss.hasPermi('psyc:feedback:remove')")
    @Log(title = "意见反馈", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(psycFeedbackService.deletePsycFeedbackByIds(ids));
    }
}
