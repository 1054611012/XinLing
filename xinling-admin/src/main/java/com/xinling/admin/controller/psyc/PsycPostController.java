package com.xinling.admin.controller.psyc;

import com.xinling.app.domain.entity.Moment;
import com.xinling.app.domain.entity.MomentComment;
import com.xinling.app.domain.model.MomentVO;
import com.xinling.app.mapper.MomentCommentMapper;
import com.xinling.app.mapper.MomentMapper;
import com.xinling.common.annotation.Log;
import com.xinling.common.core.controller.BaseController;
import com.xinling.common.core.domain.AjaxResult;
import com.xinling.common.core.page.TableDataInfo;
import com.xinling.common.enums.BusinessType;
import com.xinling.common.utils.poi.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态管理Controller
 * <p>
 * 管理后台 - 全面管理 APP 端社区动态（moment 表），
 * 支持查询、编辑、软删/恢复、物理删除、导出、评论管理。
 *
 * @author xinling
 */
@RestController
@RequestMapping("/psyc/post")
public class PsycPostController extends BaseController {

    private final MomentMapper momentMapper;
    private final MomentCommentMapper momentCommentMapper;

    public PsycPostController(MomentMapper momentMapper,
                               MomentCommentMapper momentCommentMapper) {
        this.momentMapper = momentMapper;
        this.momentCommentMapper = momentCommentMapper;
    }

    // ==================== 动态管理 ====================

    /**
     * 查询动态列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:list')")
    @GetMapping("/list")
    public TableDataInfo list(Moment moment,
                               @RequestParam(required = false) String content,
                               @RequestParam(required = false) Integer isDeleted) {
        startPage();
        List<MomentVO> list = momentMapper.selectAdminList(
                moment.getUserId(), content, moment.getType(),
                moment.getVisibility(), isDeleted,
                null, null);
        return getDataTable(list);
    }

    /**
     * 获取动态详细信息（含用户昵称/头像）
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        MomentVO moment = momentMapper.selectByIdRaw(id);
        if (moment == null) {
            return error("动态不存在");
        }
        return success(moment);
    }

    /**
     * 修改动态（内容、可见范围等）
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:edit')")
    @Log(title = "动态管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Moment moment) {
        MomentVO exist = momentMapper.selectByIdRaw(moment.getId());
        if (exist == null) {
            return error("动态不存在");
        }
        return toAjax(momentMapper.updateByIdForce(moment));
    }

    /**
     * 软删除动态
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:delete')")
    @Log(title = "动态管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult delete(@PathVariable Long id) {
        MomentVO moment = momentMapper.selectByIdRaw(id);
        if (moment == null) {
            return error("动态不存在");
        }
        return toAjax(momentMapper.deleteById(id));
    }

    /**
     * 恢复已删除的动态
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:edit')")
    @Log(title = "动态管理", businessType = BusinessType.UPDATE)
    @PostMapping("/restore/{id}")
    public AjaxResult restore(@PathVariable Long id) {
        MomentVO moment = momentMapper.selectByIdRaw(id);
        if (moment == null) {
            return error("动态不存在");
        }
        return toAjax(momentMapper.restoreById(id));
    }

    /**
     * 物理删除动态（永久删除）
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:remove')")
    @Log(title = "动态管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            momentMapper.deleteByIdForce(id);
            rows++;
        }
        return toAjax(rows);
    }

    /**
     * 导出动态列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:export')")
    @Log(title = "动态管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Moment moment) {
        List<MomentVO> list = momentMapper.selectAdminList(
                moment.getUserId(), null, moment.getType(),
                null, null, null, null);
        List<MomentExportVO> exportList = new ArrayList<>();
        for (MomentVO m : list) {
            MomentExportVO vo = new MomentExportVO();
            vo.setId(m.getId());
            vo.setUserId(m.getUserId());
            vo.setNickname(m.getNickname());
            vo.setContent(m.getContent());
            vo.setImages(m.getImages() != null ? String.join(",", m.getImages()) : null);
            vo.setType(m.getType());
            vo.setIsAnonymous(m.getIsAnonymous());
            vo.setVisibility(m.getVisibility());
            vo.setLikeCount(m.getLikeCount());
            vo.setCommentCount(m.getCommentCount());
            vo.setShareCount(m.getShareCount());
            vo.setCreateTime(m.getCreateTime());
            vo.setIsDeleted(m.getIsDeleted());
            exportList.add(vo);
        }
        ExcelUtil<MomentExportVO> util = new ExcelUtil<>(MomentExportVO.class);
        util.exportExcel(response, exportList, "动态数据");
    }

    // ==================== 评论管理 ====================

    /**
     * 查询动态的评论列表
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:list')")
    @GetMapping("/comment/list/{momentId}")
    public TableDataInfo commentList(@PathVariable Long momentId) {
        startPage();
        List<MomentComment> list = momentCommentMapper.selectByMomentId(momentId);
        return getDataTable(list);
    }

    /**
     * 删除评论
     */
    @PreAuthorize("@ss.hasPermi('psyc:post:remove')")
    @Log(title = "动态管理-评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/comment/{id}")
    public AjaxResult deleteComment(@PathVariable Long id) {
        MomentComment comment = momentCommentMapper.selectById(id);
        if (comment == null) {
            return error("评论不存在");
        }
        momentCommentMapper.deleteById(id);
        // 同步减少评论计数
        momentMapper.decrementCommentCount(comment.getMomentId());
        return success();
    }

    /**
     * 导出用 VO（Excel 适配）
     */
    public static class MomentExportVO {
        @com.xinling.common.annotation.Excel(name = "动态ID")
        private Long id;

        @com.xinling.common.annotation.Excel(name = "用户ID")
        private Long userId;

        @com.xinling.common.annotation.Excel(name = "昵称")
        private String nickname;

        @com.xinling.common.annotation.Excel(name = "内容")
        private String content;

        @com.xinling.common.annotation.Excel(name = "图片JSON")
        private String images;

        @com.xinling.common.annotation.Excel(name = "类型")
        private String type;

        @com.xinling.common.annotation.Excel(name = "匿名", readConverterExp = "0=否,1=是")
        private Integer isAnonymous;

        @com.xinling.common.annotation.Excel(name = "可见范围", readConverterExp = "0=公开,1=私密")
        private Integer visibility;

        @com.xinling.common.annotation.Excel(name = "点赞数")
        private Integer likeCount;

        @com.xinling.common.annotation.Excel(name = "评论数")
        private Integer commentCount;

        @com.xinling.common.annotation.Excel(name = "分享数")
        private Integer shareCount;

        @com.xinling.common.annotation.Excel(name = "发布时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
        private java.util.Date createTime;

        @com.xinling.common.annotation.Excel(name = "状态", readConverterExp = "0=正常,1=已删除")
        private Integer isDeleted;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getImages() { return images; }
        public void setImages(String images) { this.images = images; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getIsAnonymous() { return isAnonymous; }
        public void setIsAnonymous(Integer isAnonymous) { this.isAnonymous = isAnonymous; }
        public Integer getVisibility() { return visibility; }
        public void setVisibility(Integer visibility) { this.visibility = visibility; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
        public Integer getShareCount() { return shareCount; }
        public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }
        public java.util.Date getCreateTime() { return createTime; }
        public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }
        public Integer getIsDeleted() { return isDeleted; }
        public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    }
}