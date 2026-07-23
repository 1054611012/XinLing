package com.xinling.app.controller;

import com.xinling.app.service.IWhitelistService;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/app/whitelist")
public class AppWhitelistController {

    private final IWhitelistService whitelistService;

    public AppWhitelistController(IWhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    /**
     * 检查是否在白名单中
     */
    @GetMapping("/check")
    public R<?> check(@RequestParam String type,
                      @RequestParam String identifier) {
        boolean whitelisted = whitelistService.isWhitelisted(type, identifier);
        Map<String, Object> result = new HashMap<>();
        result.put("whitelisted", whitelisted);
        result.put("type", type);
        result.put("identifier", identifier);
        return R.ok(result);
    }
}
