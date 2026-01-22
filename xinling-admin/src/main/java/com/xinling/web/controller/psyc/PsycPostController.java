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
import com.xinling.psyc.domain.PsycPost;
import com.xinling.psyc.service.IPsycPostService;
import com.xinling.common.utils.poi.ExcelUtil;
import com.xinling.common.core.page.TableDataInfo;

/**
 * 动态管理Controller
 *
 * @author xinling
 * @date 2025-10-30
 */
@RestController
@RequestMapping("/psyc/post")
public class PsycPostController extends BaseController
{
    @Autowired
    private IPsycPostService psycPostService;

    /**
     * 查询动态管理列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(PsycPost psycPost)
    {
        startPage();
        List<PsycPost> list = psycPostService.selectPsycPostList(psycPost);
        return getDataTable(list);
    }

    /**
     * 导出动态管理列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:export')")
    @Log(title = "动态管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PsycPost psycPost)
    {
        List<PsycPost> list = psycPostService.selectPsycPostList(psycPost);
        ExcelUtil<PsycPost> util = new ExcelUtil<PsycPost>(PsycPost.class);
        util.exportExcel(response, list, "动态管理数据");
    }

    /**
     * 获取动态管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(psycPostService.selectPsycPostById(id));
    }

    /**
     * 新增动态管理
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:add')")
    @Log(title = "动态管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PsycPost psycPost)
    {
        return toAjax(psycPostService.insertPsycPost(psycPost));
    }

    /**
     * 修改动态管理
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:edit')")
    @Log(title = "动态管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PsycPost psycPost)
    {
        return toAjax(psycPostService.updatePsycPost(psycPost));
    }

    /**
     * 删除动态管理
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:remove')")
    @Log(title = "动态管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(psycPostService.deletePsycPostByIds(ids));
    }
}
