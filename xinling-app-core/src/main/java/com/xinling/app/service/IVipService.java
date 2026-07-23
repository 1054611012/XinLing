package com.xinling.app.service;

import com.xinling.app.domain.entity.UserVip;
import com.xinling.app.domain.entity.VipPackage;
import com.xinling.app.domain.model.UserVipVO;

import java.util.List;

/**
 * 会员服务
 */
public interface IVipService {

    List<VipPackage> getPackages();

    UserVip getUserVipInfo(Long userId);

    void cancelAutoRenew(Long userId);

    // ========== 管理后台方法 ==========

    List<VipPackage> selectPackageList(VipPackage vipPackage);

    int insertPackage(VipPackage vipPackage);

    int updatePackage(VipPackage vipPackage);

    int updatePackageStatus(Long id, Integer status);

    List<UserVipVO> selectUserVipList(UserVip userVip);
}
        
