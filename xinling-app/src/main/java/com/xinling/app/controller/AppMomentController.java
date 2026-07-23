package com.xinling.app.controller;

import com.xinling.app.domain.entity.MomentComment;
import com.xinling.app.domain.model.MomentVO;
import com.xinling.app.service.ICommunityService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/app/moment")
public class AppMomentController {

    private final ICommunityService communityService;

    public AppMomentController(ICommunityService communityService) {
        this.communityService = communityService;
    }

    /**
     * 发布动态
     */
    @PostMapping("/publish")
    public R<MomentVO> publish(@RequestParam String content,
                               @RequestParam(required = false) String images,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) String source,
                               @RequestParam(required = false) Integer isAnonymous,
                               @RequestParam(required = false) Integer visibility) {
        Long userId = AppContextUtil.getUserId();
        MomentVO moment = communityService.publishMoment(userId, content, images, type, source, isAnonymous, visibility);
        return R.ok(moment);
    }

    /**
     * 动态列表
     */
    @GetMapping("/list")
    public R<Map<String, Object>> list(@RequestParam(required = false) String type,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        Long userId = AppContextUtil.getUserId();
        List<MomentVO> list = communityService.listMoments(userId, type, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("page", page);
        result.put("size", size);
        return R.ok(result);
    }

    /**
     * 动态详情
     */
    @GetMapping("/detail/{id}")
    public R<MomentVO> detail(@PathVariable Long id) {
        Long userId = AppContextUtil.getUserId();
        MomentVO moment = communityService.getMomentDetail(id, userId);
        return R.ok(moment);
    }

    /**
     * 获取动态评论
     */
    @GetMapping("/comments/{momentId}")
    public R<List<MomentComment>> comments(@PathVariable Long momentId) {
        List<MomentComment> list = communityService.getCommentsByMomentId(momentId);
        return R.ok(list);
    }

    /**
     * 删除动态
     */
    @PostMapping("/delete/{id}")
    public R<?> delete(@PathVariable Long id) {
        Long userId = AppContextUtil.getUserId();
        communityService.deleteMoment(userId, id);
        return R.ok();
    }

    /**
     * 点赞
     */
    @PostMapping("/like/{momentId}")
    public R<?> like(@PathVariable Long momentId) {
        Long userId = AppContextUtil.getUserId();
        communityService.likeMoment(userId, momentId);
        return R.ok();
    }

    /**
     * 取消点赞
     */
    @PostMapping("/unlike/{momentId}")
    public R<?> unlike(@PathVariable Long momentId) {
        Long userId = AppContextUtil.getUserId();
        communityService.unlikeMoment(userId, momentId);
        return R.ok();
    }

    /**
     * 评论
     */
    @PostMapping("/comment/{momentId}")
    public R<MomentComment> comment(@PathVariable Long momentId,
                                    @RequestParam String content) {
        Long userId = AppContextUtil.getUserId();
        MomentComment comment = communityService.commentMoment(userId, momentId, content);
        return R.ok(comment);
    }

    /**
     * 删除评论
     */
    @PostMapping("/deleteComment/{commentId}")
    public R<?> deleteComment(@PathVariable Long commentId) {
        Long userId = AppContextUtil.getUserId();
        communityService.deleteComment(userId, commentId);
        return R.ok();
    }

    /**
     * 分享
     */
    @PostMapping("/share/{momentId}")
    public R<?> share(@PathVariable Long momentId) {
        Long userId = AppContextUtil.getUserId();
        communityService.shareMoment(userId, momentId);
        return R.ok();
    }

    /**
     * 收藏
     */
    @PostMapping("/collect/{momentId}")
    public R<?> collect(@PathVariable Long momentId) {
        Long userId = AppContextUtil.getUserId();
        communityService.collectMoment(userId, momentId);
        return R.ok();
    }
}
