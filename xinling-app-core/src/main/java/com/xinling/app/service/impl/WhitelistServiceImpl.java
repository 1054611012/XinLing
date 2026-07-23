package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.Whitelist;
import com.xinling.app.mapper.WhitelistMapper;
import com.xinling.app.service.IWhitelistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 白名单服务实现
 */
@Service
public class WhitelistServiceImpl implements IWhitelistService {

    private static final Logger log = LoggerFactory.getLogger(WhitelistServiceImpl.class);

    private final WhitelistMapper whitelistMapper;

    public WhitelistServiceImpl(WhitelistMapper whitelistMapper) {
        this.whitelistMapper = whitelistMapper;
    }

    @Override
    public boolean isWhitelisted(String type, String identifier) {
        Whitelist whitelist = whitelistMapper.selectByTypeAndIdentifier(type, identifier);
        return whitelist != null;
    }

    @Override
    public List<Whitelist> selectWhitelistList(Whitelist whitelist) {
        return whitelistMapper.selectList();
    }

    @Override
    public int insertWhitelist(Whitelist whitelist) {
        return whitelistMapper.insert(whitelist);
    }

    @Override
    public int deleteById(Long id) {
        return whitelistMapper.deleteById(id);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        Whitelist w = new Whitelist();
        w.setId(id);
        w.setStatus(status);
        return whitelistMapper.updateById(w);
    }
}
