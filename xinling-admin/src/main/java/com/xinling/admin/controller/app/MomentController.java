package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.Moment;
import com.xinling.app.domain.model.MomentVO;
import com.xinling.app.mapper.MomentMapper;
import com.xinling.app.service.ICommunityService;
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
 * 动态管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/moment")
@Tag(name = "动态管理", description = "动态管理")
public class MomentController extends BaseController {

    private final ICommunityService communityService;
    private final MomentMapper momentMapper;

    public MomentController(ICommunityService communityService, MomentMapper momentMapper) {
        this.communityService = communityService;
        this.momentMapper = momentMapper;
    }

    /**
     * 查询动态列表（含用户昵称/头像）
     */
    @PreAuthorize("@ss.hasPermi('app:moment:list')")
    @GetMapping("/list")
    public TableDataInfo list(Moment moment) {
        startPage();
        List<MomentVO> list = momentMapper.selectAdminList(
                moment.getUserId(), moment.getContent(), moment.getType(),
                moment.getVisibility(), moment.getIsDeleted(),
                null, null);
        return getDataTable(list);
    }

    /**
     * 获取动态详情（含用户昵称/头像）
     */
    @PreAuthorize("@ss.hasPermi('app:moment:detail')")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable Long id) {
        MomentVO moment = momentMapper.selectByIdRaw(id);
        if (moment == null) {
            return error("动态不存在");
        }
        return success(moment);
    }

    /**
     * 隐藏动态（软删除）
     */
    @PreAuthorize("@ss.hasPermi('app:moment:delete')")
    @Log(title = "动态管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        Moment moment = momentMapper.selectById(id);
        if (moment == null) {
            return error("动态不存在");
        }
        moment.setIsDeleted(1);
        return toAjax(momentMapper.updateById(moment));
    }
}
