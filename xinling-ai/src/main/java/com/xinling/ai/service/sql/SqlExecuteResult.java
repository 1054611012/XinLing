package com.xinling.ai.service.sql;

public class SqlExecuteResult {

    private final SqlType type;
    private final Object data;

    public SqlExecuteResult(SqlType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public SqlType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
