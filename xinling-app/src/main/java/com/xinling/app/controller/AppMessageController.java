package com.xinling.app.controller;

import com.xinling.app.domain.entity.PrivateMessage;
import com.xinling.app.service.ICommunityService;
import com.xinling.app.utils.AppContextUtil;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私信消息 Controller —— 对应 mobile-web 的 /api/app/message/ 前缀
 */
@RestController
@RequestMapping("/api/app/message")
public class AppMessageController {

    private final ICommunityService communityService;

    public AppMessageController(ICommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/list")
    public R<List<PrivateMessage>> list() {
        Long userId = AppContextUtil.getUserId();
        List<PrivateMessage> list = communityService.getMessageList(userId);
        return R.ok(list);
    }

    @PostMapping("/send")
    public R<PrivateMessage> send(@RequestParam Long toUserId,
                                  @RequestParam String content) {
        Long fromUserId = AppContextUtil.getUserId();
        PrivateMessage message = communityService.sendMessage(fromUserId, toUserId, content);
        return R.ok(message);
    }
}
