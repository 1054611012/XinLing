package com.xinling.ai.service.sql;

public enum SqlType {

    /**
     * 单值聚合，如 COUNT / SUM / MAX
     */
    SCALAR,

    /**
     * 单列多行，如 SELECT user_name FROM ...
     */
    SINGLE_COLUMN,

    /**
     * 多列结果，如 SELECT id, name FROM ...
     */
    MULTI_COLUMN
}
