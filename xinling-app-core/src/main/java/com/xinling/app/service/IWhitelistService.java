package com.xinling.app.service;

import com.xinling.app.domain.entity.Whitelist;

import java.util.List;

/**
 * 白名单服务
 */
public interface IWhitelistService {

    /**
     * 判断是否在白名单中
     */
    boolean isWhitelisted(String type, String identifier);

    // ========== 管理后台方法 ==========

    List<Whitelist> selectWhitelistList(Whitelist whitelist);

    int insertWhitelist(Whitelist whitelist);

    int deleteById(Long id);

    int updateStatus(Long id, Integer status);
}
