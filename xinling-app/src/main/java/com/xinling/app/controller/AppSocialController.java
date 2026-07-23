package com.xinling.app.controller;

import com.xinling.app.domain.entity.PrivateMessage;
import com.xinling.app.domain.entity.UserFollow;
import com.xinling.app.service.ICommunityService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/social")
public class AppSocialController {

    private final ICommunityService communityService;

    public AppSocialController(ICommunityService communityService) {
        this.communityService = communityService;
    }

    /**
     * 关注用户
     */
    @PostMapping("/follow/{userId}")
    public R<?> follow(@PathVariable Long userId) {
        Long followerId = AppContextUtil.getUserId();
        if (followerId.equals(userId)) {
            return R.fail("不能关注自己");
        }
        communityService.followUser(followerId, userId);
        return R.ok();
    }

    /**
     * 取消关注
     */
    @PostMapping("/unfollow/{userId}")
    public R<?> unfollow(@PathVariable Long userId) {
        Long followerId = AppContextUtil.getUserId();
        communityService.unfollowUser(followerId, userId);
        return R.ok();
    }

    /**
     * 粉丝列表
     */
    @GetMapping("/followers")
    public R<List<UserFollow>> followers() {
        Long userId = AppContextUtil.getUserId();
        List<UserFollow> list = communityService.getFollowers(userId);
        return R.ok(list);
    }

    /**
     * 关注列表
     */
    @GetMapping("/following")
    public R<List<UserFollow>> following() {
        Long userId = AppContextUtil.getUserId();
        List<UserFollow> list = communityService.getFollowing(userId);
        return R.ok(list);
    }

    /**
     * 发送私信
     */
    @PostMapping("/message/send")
    public R<PrivateMessage> sendMessage(@RequestParam Long toUserId,
                                         @RequestParam String content) {
        Long fromUserId = AppContextUtil.getUserId();
        PrivateMessage message = communityService.sendMessage(fromUserId, toUserId, content);
        return R.ok(message);
    }

    /**
     * 私信列表
     */
    @GetMapping("/message/list")
    public R<List<PrivateMessage>> messageList() {
        Long userId = AppContextUtil.getUserId();
        List<PrivateMessage> list = communityService.getMessageList(userId);
        return R.ok(list);
    }
}
