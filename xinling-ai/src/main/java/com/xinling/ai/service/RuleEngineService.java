package com.xinling.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinling.ai.domain.ontology.OntologyRule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 规则引擎服务
 * 解析和执行 OntologyRule 中的 condition/action JSON
 *
 * @author SuXia
 * @date 2026/07/20
 */
@Slf4j
@Service
public class RuleEngineService {

    @Autowired
    private OntologyService ontologyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 评估一条规则是否匹配给定上下文
     *
     * @param rule   规则定义
     * @param context 上下文数据 Map（字段名 → 值）
     * @return 评估结果
     */
    public RuleEvalResult evaluate(OntologyRule rule, Map<String, Object> context) {
        if (!"1".equals(rule.getEnabled())) {
            return RuleEvalResult.disabled(rule);
        }

        String conditionJson = rule.getCondition();
        if (conditionJson == null || conditionJson.isBlank()) {
            // 无条件的规则直接触发
            return RuleEvalResult.matched(rule, executeAction(rule.getAction(), context));
        }

        try {
            Map<String, Object> condition = objectMapper.readValue(conditionJson,
                    new TypeReference<Map<String, Object>>() {});
            boolean matched = evaluateCondition(condition, context);

            if (matched) {
                String actionResult = executeAction(rule.getAction(), context);
                return RuleEvalResult.matched(rule, actionResult);
            }
            return RuleEvalResult.unmatched(rule);
        } catch (Exception e) {
            log.error("规则评估异常: ruleId={}, err={}", rule.getRuleId(), e.getMessage());
            return RuleEvalResult.error(rule, "规则解析异常: " + e.getMessage());
        }
    }

    /**
     * 评估指定概念的所有启用规则
     */
    public List<RuleEvalResult> evaluateRulesForConcept(Long conceptId, Map<String, Object> context) {
        List<OntologyRule> rules = ontologyService.getRulesByConceptId(conceptId);
        List<RuleEvalResult> results = new ArrayList<>();
        for (OntologyRule rule : rules) {
            RuleEvalResult result = evaluate(rule, context);
            if (result.isMatched()) {
                results.add(result);
            }
        }
        results.sort(Comparator.comparingInt(r -> r.getRule().getPriority() != null ? r.getRule().getPriority() : 0));
        return results;
    }

    /**
     * 评估所有全局启用规则
     */
    public List<RuleEvalResult> evaluateGlobalRules(Map<String, Object> context) {
        List<OntologyRule> rules = ontologyService.getAllEnabledRules();
        List<RuleEvalResult> results = new ArrayList<>();
        for (OntologyRule rule : rules) {
            if (rule.getConceptId() == null) {
                RuleEvalResult result = evaluate(rule, context);
                if (result.isMatched()) {
                    results.add(result);
                }
            }
        }
        return results;
    }

    // ========== 条件评估 ==========

    @SuppressWarnings("unchecked")
    private boolean evaluateCondition(Map<String, Object> condition, Map<String, Object> context) {
        // 复合条件：and / or
        if (condition.containsKey("and")) {
            List<Map<String, Object>> subConditions = (List<Map<String, Object>>) condition.get("and");
            return subConditions.stream().allMatch(c -> evaluateCondition(c, context));
        }
        if (condition.containsKey("or")) {
            List<Map<String, Object>> subConditions = (List<Map<String, Object>>) condition.get("or");
            return subConditions.stream().anyMatch(c -> evaluateCondition(c, context));
        }
        if (condition.containsKey("not")) {
            Map<String, Object> subCondition = (Map<String, Object>) condition.get("not");
            return !evaluateCondition(subCondition, context);
        }

        // 简单条件：{"field": "...", "operator": "...", "value": ...}
        String field = (String) condition.get("field");
        String operator = (String) condition.get("operator");
        Object expectedValue = condition.get("value");

        if (field == null || operator == null) {
            return false;
        }

        Object actualValue = context.get(field);

        // 如果条件中有 user_field，则从上下文中取用户相关值做额外检查
        if (condition.containsKey("user_field")) {
            String userField = (String) condition.get("user_field");
            Object userActual = context.get(userField);
            String userOp = (String) condition.getOrDefault("user_operator", "eq");
            Object userExpected = condition.get("user_value");
            if (!compareValues(userActual, userOp, userExpected)) {
                return false;
            }
        }

        return compareValues(actualValue, operator, expectedValue);
    }

    private boolean compareValues(Object actual, String operator, Object expected) {
        if (actual == null) {
            return "null".equals(operator) || "eq".equals(operator) && expected == null;
        }

        return switch (operator) {
            case "eq", "==" -> {
                yield String.valueOf(actual).equalsIgnoreCase(String.valueOf(expected));
            }
            case "ne", "!=" -> {
                yield !String.valueOf(actual).equalsIgnoreCase(String.valueOf(expected));
            }
            case "gt", ">" -> {
                yield compareNumeric(actual, expected) > 0;
            }
            case "gte", ">=" -> {
                yield compareNumeric(actual, expected) >= 0;
            }
            case "lt", "<" -> {
                yield compareNumeric(actual, expected) < 0;
            }
            case "lte", "<=" -> {
                yield compareNumeric(actual, expected) <= 0;
            }
            case "contains" -> {
                yield String.valueOf(actual).toLowerCase()
                        .contains(String.valueOf(expected).toLowerCase());
            }
            case "in" -> {
                if (expected instanceof List) {
                    yield ((List<?>) expected).stream()
                            .anyMatch(e -> String.valueOf(e).equalsIgnoreCase(String.valueOf(actual)));
                }
                yield String.valueOf(expected).toLowerCase()
                        .contains(String.valueOf(actual).toLowerCase());
            }
            case "within_days" -> {
                // 日期在 N 天内
                try {
                    long days = Long.parseLong(String.valueOf(expected));
                    // 支持 "vip_end_time" 等日期字段
                    if (actual instanceof Date) {
                        long diff = ((Date) actual).getTime() - new Date().getTime();
                        long diffDays = diff / (1000 * 60 * 60 * 24);
                        yield diffDays >= 0 && diffDays <= days;
                    }
                } catch (NumberFormatException e) {
                    yield false;
                }
                yield false;
            }
            default -> {
                log.warn("未支持的运算符: {}", operator);
                yield false;
            }
        };
    }

    private double compareNumeric(Object actual, Object expected) {
        try {
            double a = Double.parseDouble(String.valueOf(actual));
            double e = Double.parseDouble(String.valueOf(expected));
            return a - e;
        } catch (NumberFormatException ex) {
            return String.valueOf(actual).compareTo(String.valueOf(expected));
        }
    }

    // ========== 动作执行 ==========

    @SuppressWarnings("unchecked")
    private String executeAction(String actionJson, Map<String, Object> context) {
        if (actionJson == null || actionJson.isBlank()) {
            return "无动作";
        }
        try {
            Map<String, Object> action = objectMapper.readValue(actionJson,
                    new TypeReference<Map<String, Object>>() {});
            String type = (String) action.getOrDefault("type", "unknown");
            String message = (String) action.getOrDefault("message", "");

            // 替换消息中的模板变量 {fieldName}
            if (message != null && context != null) {
                for (Map.Entry<String, Object> entry : context.entrySet()) {
                    message = message.replace("{" + entry.getKey() + "}",
                            entry.getValue() != null ? String.valueOf(entry.getValue()) : "");
                }
            }

            return switch (type) {
                case "suggest" -> "【建议】" + message;
                case "notify" -> "【通知】" + message;
                case "block" -> "【拦截】" + message;
                case "reward" -> {
                    Object points = action.get("points");
                    yield "【奖励】" + message + (points != null ? " (+" + points + "积分)" : "");
                }
                case "coupon" -> "【优惠券】" + message + " (券: " + action.get("value") + ")";
                case "override" -> "【覆盖】" + message + " (设置 " + action.get("field") + "=" + action.get("value") + ")";
                default -> "【" + type + "】" + message;
            };
        } catch (Exception e) {
            log.error("规则动作执行失败: {}", actionJson, e);
            return "动作执行异常: " + e.getMessage();
        }
    }

    // ========== 内部类 ==========

    @Data
    public static class RuleEvalResult {
        private OntologyRule rule;
        private boolean matched;
        private String actionMessage;
        private String error;

        public static RuleEvalResult matched(OntologyRule rule, String actionMessage) {
            RuleEvalResult r = new RuleEvalResult();
            r.rule = rule;
            r.matched = true;
            r.actionMessage = actionMessage;
            return r;
        }

        public static RuleEvalResult unmatched(OntologyRule rule) {
            RuleEvalResult r = new RuleEvalResult();
            r.rule = rule;
            r.matched = false;
            return r;
        }

        public static RuleEvalResult disabled(OntologyRule rule) {
            RuleEvalResult r = new RuleEvalResult();
            r.rule = rule;
            r.matched = false;
            r.actionMessage = "规则已禁用";
            return r;
        }

        public static RuleEvalResult error(OntologyRule rule, String error) {
            RuleEvalResult r = new RuleEvalResult();
            r.rule = rule;
            r.matched = false;
            r.error = error;
            return r;
        }
    }
}
