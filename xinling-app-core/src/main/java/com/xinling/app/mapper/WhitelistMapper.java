package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Whitelist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 白名单 Mapper
 */
public interface WhitelistMapper {

    Whitelist selectByTypeAndIdentifier(@Param("type") String type,
                                         @Param("identifier") String identifier);

    List<Whitelist> selectList();

    int insert(Whitelist whitelist);

    int updateById(Whitelist whitelist);

    int deleteById(@Param("id") Long id);
}
