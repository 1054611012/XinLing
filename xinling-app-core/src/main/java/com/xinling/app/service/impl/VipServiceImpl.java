package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.domain.entity.VipPackage;
import com.xinling.app.domain.model.UserVipVO;
import com.xinling.app.mapper.UserVipMapper;
import com.xinling.app.mapper.VipPackageMapper;
import com.xinling.app.service.IVipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员服务实现
 */
@Service
public class VipServiceImpl implements IVipService {

    private static final Logger log = LoggerFactory.getLogger(VipServiceImpl.class);

    private final VipPackageMapper vipPackageMapper;
    private final UserVipMapper userVipMapper;

    public VipServiceImpl(VipPackageMapper vipPackageMapper,
                           UserVipMapper userVipMapper) {
        this.vipPackageMapper = vipPackageMapper;
        this.userVipMapper = userVipMapper;
    }

    @Override
    public List<VipPackage> getPackages() {
        return vipPackageMapper.selectList(1);
    }

    @Override
    public UserVip getUserVipInfo(Long userId) {
        return userVipMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void cancelAutoRenew(Long userId) {
        UserVip userVip = userVipMapper.selectByUserId(userId);
        if (userVip == null) {
            log.warn("用户未开通会员，userId: {}", userId);
            return;
        }
        userVipMapper.updateAutoRenew(userId, 0);
        log.info("用户已取消自动续费，userId: {}", userId);
    }

    @Override
    public List<VipPackage> selectPackageList(VipPackage vipPackage) {
        return vipPackageMapper.selectAll();
    }

    @Override
    public int insertPackage(VipPackage vipPackage) {
        return vipPackageMapper.insert(vipPackage);
    }

    @Override
    public int updatePackage(VipPackage vipPackage) {
        return vipPackageMapper.updateById(vipPackage);
    }

    @Override
    public int updatePackageStatus(Long id, Integer status) {
        VipPackage pkg = new VipPackage();
        pkg.setId(id);
        pkg.setStatus(status);
        return vipPackageMapper.updateById(pkg);
    }

    @Override
    public List<UserVipVO> selectUserVipList(UserVip userVip) {
        return userVipMapper.selectList(userVip.getUserId(), null, null);
    }
}
