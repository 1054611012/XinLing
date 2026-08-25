# AI 股票分析系统 — API 接口文档

**版本**: 1.0.0  
**基础路径**: `/api/stock/analysis`  
**数据格式**: 请求/响应均为 `application/json`  
**字符编码**: UTF-8  

---

## 目录

- [1. 接口概览](#1-接口概览)
- [2. 个股分析](#2-个股分析)
- [3. 板块分析](#3-板块分析)
- [4. 基金分析](#4-基金分析)
- [5. 智能分析](#5-智能分析)
- [6. 健康检查](#6-健康检查)
- [7. 响应结构详解](#7-响应结构详解)
- [8. 评分维度说明](#8-评分维度说明)
- [9. 数据采集API](#9-数据采集API)
- [10. 错误处理](#10-错误处理)

---

## 1. 接口概览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/stock/analysis/stock/{stockCode}` | 个股分析 |
| GET | `/api/stock/analysis/sector/{sectorCode}` | 板块分析 |
| GET | `/api/stock/analysis/fund/{fundCode}` | 基金分析 |
| POST | `/api/stock/analysis/analyze` | 智能分析（自动识别类型）|
| GET | `/api/stock/analysis/health` | 健康检查 |

---

## 2. 个股分析

### 2.1 请求

```
GET /api/stock/analysis/stock/{stockCode}?type=daily&deep=false
```

**路径参数**

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| stockCode | String | 是 | A股股票代码 | `600519` |

**查询参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| type | String | 否 | `daily` | 分析周期: `daily` / `weekly` / `monthly` |
| deep | Boolean | 否 | `false` | 是否调用 LLM 深度分析（需配置 AI 模型） |

### 2.2 响应

```json
{
  "comprehensiveScore": 78.50,
  "scoreLevel": "★★★★☆",
  "dimensions": [
    {
      "dimensionCode": "sector_trend",
      "dimensionName": "板块趋势",
      "weight": 0.15,
      "score": 95.00,
      "weightedScore": 14.25,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "ma_trend",
      "dimensionName": "均线趋势",
      "weight": 0.15,
      "score": 85.00,
      "weightedScore": 12.75,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "volume",
      "dimensionName": "成交量",
      "weight": 0.15,
      "score": 90.00,
      "weightedScore": 13.50,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "fund_flow",
      "dimensionName": "主力资金",
      "weight": 0.15,
      "score": 80.00,
      "weightedScore": 12.00,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "macd",
      "dimensionName": "MACD指标",
      "weight": 0.10,
      "score": 82.00,
      "weightedScore": 8.20,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "kdj",
      "dimensionName": "KDJ指标",
      "weight": 0.05,
      "score": 65.00,
      "weightedScore": 3.25,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "futures_sync",
      "dimensionName": "期货联动",
      "weight": 0.10,
      "score": 70.00,
      "weightedScore": 7.00,
      "description": null,
      "signal": "positive"
    },
    {
      "dimensionCode": "market_sentiment",
      "dimensionName": "市场情绪",
      "weight": 0.05,
      "score": 85.00,
      "weightedScore": 4.25,
      "description": null,
      "signal": "positive"
    }
  ],
  "trendAnalysis": null,
  "maAnalysis": "MA5=1520.50 MA10=1505.20 MA20=1488.00 MA60=1450.00。均线呈多头排列，中期趋势偏强",
  "macdAnalysis": "DIF=12.50 DEA=8.30 BAR=8.40。MACD处于金叉状态，红柱放大，动能增强，位于零轴上方",
  "kdjAnalysis": "K=75.20 D=68.50 J=88.60。J值处于中高位",
  "volumeAnalysis": "量比=1.35 成交额=85.60亿。放量上涨，资金认可度高",
  "sectorAnalysis": "所属板块: 白酒。涨幅=+3.20% 成交额=850.00亿 上涨=25家 下跌=2家 涨停=3家 领涨股=贵州茅台。板块排名第3，表现强势",
  "futuresLinkage": null,
  "fundAnalysis": "主力净流入=+12.50亿，主力资金呈净流入状态，流入规模较大",
  "newsSentiment": null,
  "marketSentiment": "涨停=85家 跌停=3家 上涨=3200家 下跌=800家 封板率=78%。市场情绪偏暖",
  "aiSummary": "贵州茅台(600519)综合评分78.50分（★★★★☆），整体趋势尚可，但部分维度存在不确定性。已持仓者可继续持有，新开仓建议等待更明确的信号。",
  "riskAnalysis": "- 暂无显著风险信号，但需持续关注市场变化",
  "buyProbability": 78.50,
  "riskLevel": "mid",
  "suggestedPosition": 40.00,
  "buyZone": "1490.00 - 1550.00",
  "sellZone": null,
  "stopLoss": 1413.60,
  "takeProfit": 1748.08,
  "recommendation": "hold",
  "isDeepAnalysis": false
}
```

---

## 3. 板块分析

### 3.1 请求

```
GET /api/stock/analysis/sector/{sectorCode}?type=daily&deep=false
```

**路径参数**

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| sectorCode | String | 是 | 板块代码 | `BK0477` |

**查询参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| type | String | 否 | `daily` | 分析周期 |
| deep | Boolean | 否 | `false` | 深度分析 |

### 3.2 响应

与个股分析响应结构相同（兼容 `AnalysisResultVO`），其中 `sectorAnalysis` 字段会填充板块具体表现。

```json
{
  "comprehensiveScore": 82.00,
  "scoreLevel": "★★★★☆",
  "dimensions": [ ... ],
  "maAnalysis": "MA5=1850.00 MA10=1800.00 ... 均线多头排列",
  "sectorAnalysis": "涨幅=+4.60% 成交额=1800.00亿 上涨=95家 下跌=3家 涨停=18家 领涨股=机器人。板块排名第1，表现强势",
  "futuresLinkage": "关联期货螺纹钢(+3%)铁矿石(+5%)焦煤(+4%)全部上涨，产业链共振，板块上涨具有持续性",
  "recommendation": "buy",
  ...
}
```

---

## 4. 基金分析

### 4.1 请求

```
GET /api/stock/analysis/fund/{fundCode}?deep=false
```

**路径参数**

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| fundCode | String | 是 | 基金代码（6位） | `110011` |

**查询参数**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| deep | Boolean | 否 | `false` | 深度分析 |

### 4.2 响应

基金评分 7 维度和个股不同：

```json
{
  "comprehensiveScore": 72.50,
  "scoreLevel": "★★★☆☆",
  "dimensions": [
    {
      "dimensionCode": "fund_return",
      "dimensionName": "阶段收益",
      "weight": 0.20,
      "score": 65.00,
      "weightedScore": 13.00,
      "signal": "positive"
    },
    {
      "dimensionCode": "fund_risk",
      "dimensionName": "风控能力",
      "weight": 0.20,
      "score": 75.00,
      "weightedScore": 15.00,
      "signal": "positive"
    },
    {
      "dimensionCode": "fund_manager",
      "dimensionName": "基金经理",
      "weight": 0.15,
      "score": 60.00,
      "weightedScore": 9.00,
      "signal": "neutral"
    },
    {
      "dimensionCode": "fund_holdings",
      "dimensionName": "持仓质量",
      "weight": 0.15,
      "score": 80.00,
      "weightedScore": 12.00,
      "signal": "positive"
    },
    {
      "dimensionCode": "fund_scale",
      "dimensionName": "规模适宜",
      "weight": 0.10,
      "score": 85.00,
      "weightedScore": 8.50,
      "signal": "positive"
    },
    {
      "dimensionCode": "fund_fee",
      "dimensionName": "费率水平",
      "weight": 0.10,
      "score": 60.00,
      "weightedScore": 6.00,
      "signal": "neutral"
    },
    {
      "dimensionCode": "fund_rating",
      "dimensionName": "外部评级",
      "weight": 0.10,
      "score": 85.00,
      "weightedScore": 8.50,
      "signal": "positive"
    }
  ],
  "trendAnalysis": "近1周=+1.20% 近1月=+3.50% 近3月=+8.20% 近6月=+12.50% 近1年=+18.00% 近3年=+35.00% 同类排名=前10%",
  "riskAnalysis": "近1年最大回撤=-12.50% 夏普比率=1.85",
  "sectorAnalysis": "前5重仓股: 贵州茅台(9.5%) 宁德时代(8.2%) 腾讯控股(7.8%) 招商银行(6.5%) 美的集团(5.2%)",
  "fundAnalysis": "管理费=1.50% 托管费=0.25% 合计=1.75%",
  "aiSummary": "易方达中小盘混合(110011) 综合评分72.50分，近1年收益18.00%，晨星评级5星。该基金综合表现优秀，适合作为长期配置选择。",
  "recommendation": "hold",
  "buyProbability": 72.50,
  "riskLevel": "mid",
  "suggestedPosition": 20.00,
  ...
}
```

---

## 5. 智能分析

### 5.1 请求

```
POST /api/stock/analysis/analyze
Content-Type: application/json
```

**请求体**

```json
{
  "symbolType": "stock",
  "symbolCode": "600519",
  "analysisType": "daily",
  "deepAnalysis": false
}
```

**参数说明**

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| symbolType | String | 是 | — | 标的类型: `stock` / `sector` / `fund` |
| symbolCode | String | 是 | — | 标的代码 |
| analysisType | String | 否 | `daily` | 分析周期（仅 stock/sector 有效） |
| deepAnalysis | Boolean | 否 | `false` | 是否深度分析（调用 LLM） |

### 5.2 响应

与对应的 GET 接口返回相同的 `AnalysisResultVO` 结构。

---

## 6. 健康检查

### 6.1 请求

```
GET /api/stock/analysis/health
```

### 6.2 响应

```json
{
  "status": "UP",
  "module": "xinling-stock",
  "version": "1.0.0",
  "features": ["stock", "sector", "futures", "fund", "technical_indicators"]
}
```

---

## 7. 响应结构详解

所有分析接口返回统一的 `AnalysisResultVO` 结构，完整字段如下：

### 7.1 顶层字段

| 字段 | 类型 | 说明 |
|------|------|------|
| comprehensiveScore | BigDecimal | 综合评分（0-100） |
| scoreLevel | String | 星级评级: `★☆☆☆☆` ~ `★★★★★` |
| dimensions | Array | 各维度评分明细（见 7.2） |
| trendAnalysis | String | 趋势分析文本 |
| maAnalysis | String | 均线分析文本 |
| macdAnalysis | String | MACD 分析文本 |
| kdjAnalysis | String | KDJ 分析文本 |
| volumeAnalysis | String | 成交量分析文本 |
| sectorAnalysis | String | 板块分析文本 |
| futuresLinkage | String | 期货联动分析文本 |
| fundAnalysis | String | 资金面分析文本 |
| newsSentiment | String | 消息面分析文本 |
| marketSentiment | String | 市场情绪分析文本 |
| aiSummary | String | AI 综合总结 |
| riskAnalysis | String | 风险分析 |
| buyProbability | BigDecimal | 买入概率（%） |
| riskLevel | String | 风险等级: `low` / `mid` / `high` |
| suggestedPosition | BigDecimal | 建议仓位（%） |
| buyZone | String | 买入区间 |
| sellZone | String | 卖出区间 |
| stopLoss | BigDecimal | 止损位 |
| takeProfit | BigDecimal | 止盈位 |
| recommendation | String | 操作建议: `strong_buy` / `buy` / `hold` / `watch` / `sell` / `strong_sell` |
| isDeepAnalysis | Boolean | 是否调用了 LLM 深度分析 |

### 7.2 ScoreDimensionVO 维度评分

| 字段 | 类型 | 说明 |
|------|------|------|
| dimensionCode | String | 维度编码 |
| dimensionName | String | 维度中文名 |
| weight | BigDecimal | 权重（0.00-1.00） |
| score | BigDecimal | 该维度得分（0-100） |
| weightedScore | BigDecimal | 加权得分 = score × weight |
| description | String | 评分说明 |
| signal | String | 信号: `positive` / `neutral` / `negative` |

---

## 8. 评分维度说明

### 8.1 个股/板块评分维度（9 项）

| 维度编码 | 维度名称 | 默认权重 | 评分依据 |
|----------|----------|----------|----------|
| `sector_trend` | 板块趋势 | 15% | 涨跌幅、成交量、上涨家数比、板块排名 |
| `futures_sync` | 期货联动 | 10% | 关联期货方向一致性、偏离程度 |
| `ma_trend` | 均线趋势 | 15% | 多头/空头排列、价格与均线关系 |
| `macd` | MACD指标 | 10% | 金叉/死叉、红绿柱、零轴位置 |
| `kdj` | KDJ指标 | 5% | J 值位置、超买超卖 |
| `volume` | 成交量 | 15% | 量比、量价关系、换手率 |
| `fund_flow` | 主力资金 | 15% | 主力净流入流出、大单比例 |
| `news_sentiment` | 消息面 | 10% | 新闻情感分析（需配置） |
| `market_sentiment` | 市场情绪 | 5% | 涨停跌停比、封板率、涨跌比 |

### 8.2 基金评分维度（7 项）

| 维度编码 | 维度名称 | 默认权重 | 评分依据 |
|----------|----------|----------|----------|
| `fund_return` | 阶段收益 | 20% | 近1年/3年收益率、同类排名 |
| `fund_risk` | 风控能力 | 20% | 最大回撤、夏普比率、年化波动率 |
| `fund_manager` | 基金经理 | 15% | 从业年限、管理规模、历史业绩 |
| `fund_holdings` | 持仓质量 | 15% | 重仓股基本面、行业集中度 |
| `fund_scale` | 规模适宜 | 10% | 基金规模（5-50亿最佳） |
| `fund_fee` | 费率水平 | 10% | 管理费+托管费合计 |
| `fund_rating` | 外部评级 | 10% | 晨星/银河评级 |

---

## 9. 数据采集 API

`EastMoneyApiService` 封装了东方财富开放接口，供定时任务或数据初始化调用。

### 内部方法摘要

| 方法 | 说明 | 返回数据 |
|------|------|----------|
| `fetchStockKLine(code, market, count)` | 获取个股日K线 | List\<KlineDaily\> |
| `fetchSectorKLine(code, count)` | 获取板块指数K线 | List\<KlineDaily\> |
| `fetchFuturesKLine(code, count)` | 获取期货主力K线 | List\<KlineDaily\> |
| `fetchRealtimeQuotes(symbols)` | 批量获取实时行情 | JSONObject |
| `fetchSectorRanking(type, count)` | 获取板块涨幅排名 | List\<JSONObject\> |
| `fetchSectorStocks(code)` | 获取板块成分股 | List\<JSONObject\> |
| `fetchFundNav(code, page, size)` | 获取基金净值历史 | List\<FundNav\> |
| `fetchFundHoldings(code)` | 获取基金持仓 | List\<FundHolding\> |
| `searchFund(keyword)` | 搜索基金 | List\<JSONObject\> |
| `searchSymbol(keyword)` | 搜索股票/板块 | List\<JSONObject\> |
| `fetchLimitUpData()` | 获取涨停板列表 | JSONObject |
| `fetchLimitDownData()` | 获取跌停板列表 | JSONObject |

---

## 10. 错误处理

### 10.1 正常流程

分析失败时返回带错误提示的有效响应：

```json
{
  "comprehensiveScore": 0,
  "scoreLevel": "★☆☆☆☆",
  "aiSummary": "未找到股票: 999999",
  "recommendation": "watch",
  "riskLevel": "mid",
  ...
}
```

### 10.2 HTTP 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功（含业务错误） |
| 404 | 路径不存在 |
| 500 | 服务器内部错误 |

### 10.3 AnalysisReport 关键状态码

- `analysisStatus = 0`: 分析失败（`errorMsg` 存原因）
- `analysisStatus = 1`: 分析成功
- `analysisStatus = 2`: 部分成功

---

## 附录：数据库表清单

| 表名 | 说明 | 归属维度 |
|------|------|----------|
| stk_stock | 股票基本信息 | 基础数据 |
| stk_sector | 板块定义 | 板块分析 |
| stk_sector_stock | 板块成分股 | 板块分析 |
| stk_futures_symbol | 期货品种定义 | 期货联动 |
| stk_sector_futures_mapping | 板块↔期货映射 | 期货联动 |
| stk_kline_daily | 日K线（统一存储） | 行情数据 |
| stk_kline_weekly | 周K线 | 行情数据 |
| stk_kline_monthly | 月K线 | 行情数据 |
| stk_technical_indicator | 技术指标计算结果 | 技术指标 |
| stk_sector_performance | 板块每日表现 | 板块分析 |
| stk_sector_rank_snapshot | 板块强度排名 | 板块分析 |
| stk_sector_rotation | 板块轮动记录 | 板块分析 |
| stk_futures_quote | 期货行情 | 期货联动 |
| stk_linkage_analysis | 期货联动分析结果 | 期货联动 |
| stk_fund_flow | 个股资金流向 | 资金分析 |
| stk_northbound_flow | 北向资金 | 资金分析 |
| stk_sector_fund_flow | 板块资金流向 | 资金分析 |
| stk_market_sentiment | 市场情绪指标 | 情绪分析 |
| stk_news | 新闻/资讯 | 消息面 |
| stk_announcement | 公司公告 | 消息面 |
| stk_analysis_report | AI分析报告 | 分析结果 |
| stk_config_score_weight | 评分权重配置 | 系统配置 |
| stk_config_prompt_template | Prompt模板配置 | 系统配置 |
| stk_fund | 基金基本信息 | 基金分析 |
| stk_fund_nav | 基金净值历史 | 基金分析 |
| stk_fund_holdings | 基金持仓 | 基金分析 |
| stk_fund_manager | 基金经理 | 基金分析 |
| stk_fund_performance | 基金阶段表现 | 基金分析 |
