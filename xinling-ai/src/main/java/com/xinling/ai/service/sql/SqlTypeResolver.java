package com.xinling.ai.service.sql;

import java.util.Set;

public final class SqlTypeResolver {

    private static final Set<String> AGG_FUNCS =
            Set.of("count", "sum", "max", "min", "avg");

    private SqlTypeResolver() {}

    public static SqlType resolve(String sql) {
        String normalized = normalize(sql);

        String selectPart = extractSelectPart(normalized);
        String[] columns = splitColumns(selectPart);

        // 多列
        if (columns.length > 1) {
            return SqlType.MULTI_COLUMN;
        }

        // 单列聚合
        if (isAggregate(columns[0])) {
            return SqlType.SCALAR;
        }

        // 单列普通查询
        return SqlType.SINGLE_COLUMN;
    }

    private static String normalize(String sql) {
        return sql.toLowerCase()
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String extractSelectPart(String sql) {
        int selectIdx = sql.indexOf("select") + 6;
        int fromIdx = sql.indexOf(" from ");
        if (fromIdx < 0) {
            throw new IllegalArgumentException("非法 SQL，缺少 FROM");
        }
        return sql.substring(selectIdx, fromIdx).trim();
    }

    private static String[] splitColumns(String selectPart) {
        return selectPart.split("\\s*,\\s*");
    }

    private static boolean isAggregate(String column) {
        return AGG_FUNCS.stream()
                .anyMatch(func -> column.startsWith(func + "("));
    }
}
