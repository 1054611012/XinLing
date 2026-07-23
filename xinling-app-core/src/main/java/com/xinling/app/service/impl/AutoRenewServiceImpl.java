package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.AutoRenew;
import com.xinling.app.mapper.AutoRenewMapper;
import com.xinling.app.service.IAutoRenewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自动续费服务实现
 */
@Service
public class AutoRenewServiceImpl implements IAutoRenewService {

    private static final Logger log = LoggerFactory.getLogger(AutoRenewServiceImpl.class);

    private final AutoRenewMapper autoRenewMapper;

    public AutoRenewServiceImpl(AutoRenewMapper autoRenewMapper) {
        this.autoRenewMapper = autoRenewMapper;
    }

    @Override
    public AutoRenew getAutoRenewStatus(Long userId) {
        return autoRenewMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void openAutoRenew(Long userId, Long packageId, String payType) {
        AutoRenew existing = autoRenewMapper.selectByUserIdAndPackageId(userId, packageId);
        if (existing != null) {
            existing.setStatus(1);
            existing.setPayType(payType);
            autoRenewMapper.updateById(existing);
            log.info("自动续费已重新开启，userId: {}, packageId: {}", userId, packageId);
            return;
        }

        AutoRenew autoRenew = new AutoRenew();
        autoRenew.setUserId(userId);
        autoRenew.setPackageId(packageId);
        autoRenew.setPayType(payType);
        autoRenew.setStatus(1);
        autoRenewMapper.insert(autoRenew);
        log.info("自动续费已开启，userId: {}, packageId: {}", userId, packageId);
    }

    @Override
    @Transactional
    public void closeAutoRenew(Long userId) {
        AutoRenew autoRenew = autoRenewMapper.selectByUserId(userId);
        if (autoRenew != null) {
            autoRenewMapper.updateStatus(autoRenew.getId(), 0);
            log.info("自动续费已关闭，userId: {}", userId);
        }
    }
}
