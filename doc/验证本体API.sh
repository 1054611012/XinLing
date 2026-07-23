#!/bin/bash
# =====================================================
# 心聆本体扩展模块 API 验证脚本
# 前置条件：
#   1. MySQL 已运行且 su_crm 库已导入
#   2. 项目已编译：mvn compile -pl xinling-ai -am
#   3. 应用已启动：mvn spring-boot:run -pl xinling-admin
#   4. 获取有效 token（登录后从响应头提取）
# =====================================================

BASE_URL="http://localhost:8080"
TOKEN=""  # 先登录获取 token

# ============================================
# 第一步：登录获取 token
# ============================================
echo "========== 1. 登录获取 Token =========="
LOGIN_RESP=$(curl -s -c /tmp/xinling_cookies.txt \
  -X POST "${BASE_URL}/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')
echo "$LOGIN_RESP" | python3 -m json.tool 2>/dev/null || echo "$LOGIN_RESP"

# 从 cookie 或响应中提取 token
TOKEN=$(grep 'Admin-Token' /tmp/xinling_cookies.txt 2>/dev/null | awk '{print $NF}')
if [ -z "$TOKEN" ]; then
  TOKEN=$(echo "$LOGIN_RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('token',''))" 2>/dev/null)
fi
echo "Token: ${TOKEN:0:30}..."

AUTH="Admin-Token: ${TOKEN}"

# ============================================
# 公共权限头（所有需要权限的接口）
# ============================================
LIST_AUTH="${AUTH}"
EXPORT_AUTH="${AUTH}"
ADD_AUTH="${AUTH}"
EDIT_AUTH="${AUTH}"
REMOVE_AUTH="${AUTH}"

echo ""
echo "========== 2. 概念管理（已有接口验证） =========="

echo "--- 2.1 查询概念列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/concept/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -20

echo "--- 2.2 新增测试概念 ---"
curl -s -X POST "${BASE_URL}/ai/ontology/concept" \
  -H "${ADD_AUTH}" -H "Content-Type: application/json" \
  -d '{"conceptName":"测试验证","conceptCode":"test_verify","description":"用于API验证的临时概念","category":"测试","status":"0"}' \
  | python3 -m json.tool 2>/dev/null

echo ""
echo "========== 3. 属性管理接口验证 =========="

echo "--- 3.1 查询属性列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/property/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -25

echo "--- 3.2 按概念查询属性（概念ID=11=音频素材） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/property/concept/11" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -20

echo "--- 3.3 新增属性 ---"
curl -s -X POST "${BASE_URL}/ai/ontology/property" \
  -H "${ADD_AUTH}" -H "Content-Type: application/json" \
  -d '{"propertyName":"验证属性","propertyCode":"verify_prop","propertyType":"STRING","conceptId":11,"description":"API验证临时属性","sortOrder":99,"status":"0"}' \
  | python3 -m json.tool 2>/dev/null

echo "--- 3.4 获取属性详情（假设ID=1） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/property/1" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -20

echo "--- 3.5 修改属性 ---"
curl -s -X PUT "${BASE_URL}/ai/ontology/property" \
  -H "${EDIT_AUTH}" -H "Content-Type: application/json" \
  -d '{"propertyId":1,"propertyName":"时长（秒）-已更新","description":"更新描述"}' \
  | python3 -m json.tool 2>/dev/null

echo "--- 3.6 删除属性（清理测试数据） ---"
curl -s -X DELETE "${BASE_URL}/ai/ontology/property/99" \
  -H "${REMOVE_AUTH}" 2>/dev/null
# 如无ID=99的属性，会返回错误但不会影响验证
echo "   （如无ID=99则返回错误，此为正常）"

echo ""
echo "========== 4. 实例管理接口验证 =========="

echo "--- 4.1 查询实例列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/instance/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -25

echo "--- 4.2 按概念查询实例（概念ID=21=专注） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/instance/concept/21" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -20

echo "--- 4.3 新增实例 ---"
curl -s -X POST "${BASE_URL}/ai/ontology/instance" \
  -H "${ADD_AUTH}" -H "Content-Type: application/json" \
  -d '{"instanceName":"验证实例","instanceCode":"test_inst","conceptId":21,"description":"API验证临时实例","status":"0"}' \
  | python3 -m json.tool 2>/dev/null

echo ""
echo "========== 5. 实例属性值接口验证 =========="

echo "--- 5.1 查询某实例属性值（实例ID=1） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/instance/value/by-instance/1" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -20

echo "--- 5.2 新增属性值 ---"
curl -s -X POST "${BASE_URL}/ai/ontology/instance/value" \
  -H "${ADD_AUTH}" -H "Content-Type: application/json" \
  -d '{"instanceId":1,"propertyId":1,"propertyValue":"测试值"}' \
  | python3 -m json.tool 2>/dev/null

echo ""
echo "========== 6. 规则管理接口验证 =========="

echo "--- 6.1 查询规则列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/rule/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -25

echo "--- 6.2 查询已启用规则 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/rule/enabled" \
  | python3 -m json.tool 2>/dev/null | head -15

echo "--- 6.3 按概念查询规则（概念ID=21=专注） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/rule/concept/21" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -15

echo ""
echo "========== 7. 行为管理接口验证 =========="

echo "--- 7.1 查询行为列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/action/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -25

echo "--- 7.2 按概念查询行为（概念ID=21=专注） ---"
curl -s -X GET "${BASE_URL}/ai/ontology/action/concept/21" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -15

echo ""
echo "========== 8. 字段映射接口验证 =========="

echo "--- 8.1 查询字段映射列表 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/field-mapping/list?pageNum=1&pageSize=5" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -15

echo "--- 8.2 按映射查询字段映射 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/field-mapping/by-mapping/1" \
  -H "${LIST_AUTH}" | python3 -m json.tool 2>/dev/null | head -15

echo ""
echo "========== 9. 本体推理接口验证 =========="

echo "--- 9.1 获取本体知识图谱 ---"
curl -s -X GET "${BASE_URL}/ai/ontology/knowledge" \
  | python3 -m json.tool 2>/dev/null | head -30

echo "--- 9.2 本体推理问答 ---"
curl -s -X POST "${BASE_URL}/ai/ontology/reason" \
  -H "Content-Type: application/json" \
  -d '{"query":"什么是专注功能？它和哪些概念有关？"}' \
  | python3 -m json.tool 2>/dev/null | head -20

echo ""
echo "========== 10. 清理测试数据 =========="

echo "--- 10.1 删除测试实例 ---"
curl -s -X DELETE "${BASE_URL}/ai/ontology/instance/99" -H "${REMOVE_AUTH}"
echo ""

echo "--- 10.2 删除测试概念 ---"
curl -s -X DELETE "${BASE_URL}/ai/ontology/concept/99" -H "${REMOVE_AUTH}"
echo ""

echo ""
echo "========== 验证完成 =========="
echo ""
echo "提示："
echo "  - 如果某步骤返回 401，说明 token 无效，请手动登录后替换 TOKEN 变量"
echo "  - 如果返回 500，检查启动日志是否有 SQL/配置错误"
echo "  - 属性值新增（5.2）如果报唯一键冲突，说明该实例已有该属性的值，这是正常约束"
echo "  - 清理步骤（10）如果报错，可能是测试数据没有成功创建，无关紧要"
