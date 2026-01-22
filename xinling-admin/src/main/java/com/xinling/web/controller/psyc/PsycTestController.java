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
import com.xinling.psyc.domain.PsycTest;
import com.xinling.psyc.service.IPsycTestService;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.common.core.page.TableDataInfo;

/**
 * 心理测评Controller
 *
 * @author xinling
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/psyc/test")
public class PsycTestController extends BaseController
{
    @Autowired
    private IPsycTestService psycTestService;

    /**
     * 查询心理测评列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:list')")
    @GetMapping("/list")
    public TableDataInfo list(PsycTest psycTest)
    {
        startPage();
        List<PsycTest> list = psycTestService.selectPsycTestList(psycTest);
        return getDataTable(list);
    }

    /**
     * 导出心理测评列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:export')")
    @Log(title = "心理测评", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PsycTest psycTest)
    {
        List<PsycTest> list = psycTestService.selectPsycTestList(psycTest);
        ExcelUtil<PsycTest> util = new ExcelUtil<PsycTest>(PsycTest.class);
        util.exportExcel(response, list, "心理测评数据");
    }

    /**
     * 获取心理测评详细信息
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(psycTestService.selectPsycTestById(id));
    }

    /**
     * 新增心理测评
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:add')")
    @Log(title = "心理测评", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PsycTest psycTest)
    {
        return toAjax(psycTestService.insertPsycTest(psycTest));
    }

    /**
     * 修改心理测评
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:edit')")
    @Log(title = "心理测评", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PsycTest psycTest)
    {
        return toAjax(psycTestService.updatePsycTest(psycTest));
    }

    /**
     * 删除心理测评
     */
    @PreAuthorize("@ss.hasPermi('psyc:test:remove')")
    @Log(title = "心理测评", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(psycTestService.deletePsycTestByIds(ids));
    }
}
