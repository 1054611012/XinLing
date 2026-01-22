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
import com.xinling.psyc.domain.PsycQuestions;
import com.xinling.psyc.service.IPsycQuestionsService;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.common.core.page.TableDataInfo;

/**
 * 题目Controller
 *
 * @author xinling
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/psyc/questions")
public class PsycQuestionsController extends BaseController
{
    @Autowired
    private IPsycQuestionsService psycQuestionsService;

    /**
     * 查询题目列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:list')")
    @GetMapping("/list")
    public TableDataInfo list(PsycQuestions psycQuestions)
    {
        startPage();
        List<PsycQuestions> list = psycQuestionsService.selectPsycQuestionsList(psycQuestions);
        return getDataTable(list);
    }

    /**
     * 导出题目列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:export')")
    @Log(title = "题目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PsycQuestions psycQuestions)
    {
        List<PsycQuestions> list = psycQuestionsService.selectPsycQuestionsList(psycQuestions);
        ExcelUtil<PsycQuestions> util = new ExcelUtil<PsycQuestions>(PsycQuestions.class);
        util.exportExcel(response, list, "题目数据");
    }

    /**
     * 获取题目详细信息
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(psycQuestionsService.selectPsycQuestionsById(id));
    }

    /**
     * 新增题目
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:add')")
    @Log(title = "题目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PsycQuestions psycQuestions)
    {
        return toAjax(psycQuestionsService.insertPsycQuestions(psycQuestions));
    }

    /**
     * 修改题目
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:edit')")
    @Log(title = "题目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PsycQuestions psycQuestions)
    {
        return toAjax(psycQuestionsService.updatePsycQuestions(psycQuestions));
    }

    /**
     * 删除题目
     */
    @PreAuthorize("@ss.hasPermi('psyc:questions:remove')")
    @Log(title = "题目", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(psycQuestionsService.deletePsycQuestionsByIds(ids));
    }
}
