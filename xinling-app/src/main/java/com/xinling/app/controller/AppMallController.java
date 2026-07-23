package com.xinling.app.controller;

import com.xinling.app.domain.entity.MallGoods;
import com.xinling.app.service.IGrowthService;
import com.xinling.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分商城接口 —— mobile-web 前端使用 /api/app/mall/ 前缀
 */
@RestController
@RequestMapping("/api/app/mall")
public class AppMallController {

    private final IGrowthService growthService;

    public AppMallController(IGrowthService growthService) {
        this.growthService = growthService;
    }

    @GetMapping("/goods")
    public R<List<MallGoods>> goods() {
        List<MallGoods> list = growthService.getMallGoods();
        return R.ok(list);
    }

    @PostMapping("/exchange/{goodsId}")
    public R<?> exchange(@PathVariable Long goodsId) {
        Long userId = com.xinling.app.utils.AppContextUtil.getUserId();
        growthService.exchangeGoods(userId, goodsId);
        return R.ok("兑换成功");
    }
}
