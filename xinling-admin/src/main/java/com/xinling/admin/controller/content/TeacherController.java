package com.xinling.admin.controller.content;

import com.xinling.app.domain.entity.Teacher;
import com.xinling.app.service.ITeacherService;
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
 * 老师管理 Controller
 *
 * @author xinling
 */
@RestController
@RequestMapping("/content/teacher")
@Tag(name = "老师管理", description = "老师管理")
public class TeacherController extends BaseController {

    private final ITeacherService teacherService;

    public TeacherController(ITeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PreAuthorize("@ss.hasPermi('content:teacher:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String keyword) {
        startPage();
        List<Teacher> list = teacherService.getList(keyword);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('content:teacher:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        Teacher item = teacherService.getById(id);
        if (item == null) {
            return error("老师不存在");
        }
        return success(item);
    }

    @PreAuthorize("@ss.hasPermi('content:teacher:create')")
    @Log(title = "老师管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody Teacher teacher) {
        return success(teacherService.create(teacher));
    }

    @PreAuthorize("@ss.hasPermi('content:teacher:update')")
    @Log(title = "老师管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody Teacher teacher) {
        return success(teacherService.update(id, teacher));
    }

    @PreAuthorize("@ss.hasPermi('content:teacher:delete')")
    @Log(title = "老师管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        teacherService.delete(id);
        return success();
    }
}
