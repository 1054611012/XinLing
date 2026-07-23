package com.xinling.app.service;

import com.xinling.app.domain.entity.AutoRenew;

/**
 * 自动续费服务
 */
public interface IAutoRenewService {

    /**
     * 获取用户自动续费状态
     */
    AutoRenew getAutoRenewStatus(Long userId);

    /**
     * 开启自动续费
     */
    void openAutoRenew(Long userId, Long packageId, String payType);

    /**
     * 关闭自动续费
     */
    void closeAutoRenew(Long userId);
}
