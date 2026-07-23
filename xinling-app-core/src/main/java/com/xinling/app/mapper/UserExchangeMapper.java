package com.xinling.app.mapper;

import com.xinling.app.domain.entity.UserExchange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户兑换记录 Mapper
 */
public interface UserExchangeMapper {

    List<UserExchange> selectByUserId(@Param("userId") Long userId);

    int insert(UserExchange userExchange);
}
