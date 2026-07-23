package com.xinling.admin.controller.app;

import com.xinling.app.domain.entity.Achievement;
import com.xinling.app.domain.entity.DailyTask;
import com.xinling.app.domain.entity.MallGoods;
import com.xinling.app.mapper.AchievementMapper;
import com.xinling.app.mapper.DailyTaskMapper;
import com.xinling.app.mapper.MallGoodsMapper;
import com.xinling.app.service.IGrowthService;
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
 * 成长体系管理控制器
 *
 * @author xinling
 */
@RestController
@RequestMapping("/app/growth")
@Tag(name = "成长体系管理", description = "成长体系管理")
public class GrowthController extends BaseController {

    private final IGrowthService growthService;
    private final AchievementMapper achievementMapper;
    private final DailyTaskMapper dailyTaskMapper;
    private final MallGoodsMapper mallGoodsMapper;

    public GrowthController(IGrowthService growthService,
                                  AchievementMapper achievementMapper,
                                  DailyTaskMapper dailyTaskMapper,
                                  MallGoodsMapper mallGoodsMapper) {
        this.growthService = growthService;
        this.achievementMapper = achievementMapper;
        this.dailyTaskMapper = dailyTaskMapper;
        this.mallGoodsMapper = mallGoodsMapper;
    }

    /**
     * 查询成就列表
     */
    @PreAuthorize("@ss.hasPermi('app:growth:achievement:list')")
    @GetMapping("/achievement/list")
    public TableDataInfo achievementList() {
        startPage();
        List<Achievement> list = achievementMapper.selectAll();
        return getDataTable(list);
    }

    /**
     * 新增成就
     */
    @PreAuthorize("@ss.hasPermi('app:growth:achievement:create')")
    @Log(title = "成就管理", businessType = BusinessType.INSERT)
    @PostMapping("/achievement/create")
    public AjaxResult createAchievement(@RequestBody Achievement achievement) {
        achievement.setCreateTime(new Date());
        return toAjax(achievementMapper.insert(achievement));
    }

    /**
     * 查询每日任务列表
     */
    @PreAuthorize("@ss.hasPermi('app:growth:task:list')")
    @GetMapping("/task/list")
    public TableDataInfo taskList() {
        startPage();
        List<DailyTask> list = dailyTaskMapper.selectAll();
        return getDataTable(list);
    }

    /**
     * 新增每日任务
     */
    @PreAuthorize("@ss.hasPermi('app:growth:task:create')")
    @Log(title = "每日任务管理", businessType = BusinessType.INSERT)
    @PostMapping("/task/create")
    public AjaxResult createTask(@RequestBody DailyTask dailyTask) {
        dailyTask.setCreateTime(new Date());
        return toAjax(dailyTaskMapper.insert(dailyTask));
    }

    /**
     * 查询积分商城商品列表
     */
    @PreAuthorize("@ss.hasPermi('app:growth:mall:list')")
    @GetMapping("/mall/list")
    public TableDataInfo mallList() {
        startPage();
        List<MallGoods> list = mallGoodsMapper.selectAll();
        return getDataTable(list);
    }

    /**
     * 新增积分商城商品
     */
    @PreAuthorize("@ss.hasPermi('app:growth:mall:create')")
    @Log(title = "积分商城管理", businessType = BusinessType.INSERT)
    @PostMapping("/mall/create")
    public AjaxResult createMallGoods(@RequestBody MallGoods mallGoods) {
        mallGoods.setCreateTime(new Date());
        mallGoods.setUpdateTime(new Date());
        return toAjax(mallGoodsMapper.insert(mallGoods));
    }

    /**
     * 修改积分商城商品
     */
    @PreAuthorize("@ss.hasPermi('app:growth:mall:update')")
    @Log(title = "积分商城管理", businessType = BusinessType.UPDATE)
    @PostMapping("/mall/update")
    public AjaxResult updateMallGoods(@RequestBody MallGoods mallGoods) {
        mallGoods.setUpdateTime(new Date());
        return toAjax(mallGoodsMapper.updateById(mallGoods));
    }
}
