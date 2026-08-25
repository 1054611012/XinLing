# AI 股票分析系统 — API 接口速查

## 基础信息

- **基础路径**: `/api/stock/analysis`
- **数据格式**: JSON
- **基类库**: `doc/xinling-ai-接口文档.md` | `xinling-stock/sql/API_DOCUMENT.md`（完整版）

---

## 5 个 API 端点

### 1. 个股分析

```
GET /api/stock/analysis/stock/{stockCode}?type=daily&deep=false
```

参数:
| 参数 | 必填 | 默认 | 说明 |
|------|------|------|------|
| stockCode | 是 | — | 股票代码: `600519` |
| type | 否 | `daily` | 周期: daily / weekly / monthly |
| deep | 否 | `false` | 是否 LLM 深度分析 |

### 2. 板块分析

```
GET /api/stock/analysis/sector/{sectorCode}?type=daily&deep=false
```

| 参数 | 必填 | 说明 |
|------|------|------|
| sectorCode | 是 | 板块代码: `BK0477`（机器人）|

### 3. 基金分析

```
GET /api/stock/analysis/fund/{fundCode}?deep=false
```

| 参数 | 必填 | 说明 |
|------|------|------|
| fundCode | 是 | 基金代码: `110011` |

### 4. 智能分析

```
POST /api/stock/analysis/analyze
Content-Type: application/json

{
  "symbolType": "stock",     // stock / sector / fund
  "symbolCode": "600519",    // 代码
  "analysisType": "daily",   // 周期
  "deepAnalysis": false       // 深度分析
}
```

### 5. 健康检查

```
GET /api/stock/analysis/health
```

---

## 响应核心字段速查

```json
{
  "comprehensiveScore": 78.50,     // 综合评分 0-100
  "scoreLevel": "★★★★☆",          // 星级
  "recommendation": "hold",        // strong_buy/buy/hold/watch/sell/strong_sell
  "riskLevel": "mid",              // low/mid/high
  "suggestedPosition": 40.00,      // 建议仓位 %
  "buyProbability": 78.50,         // 买入概率
  "buyZone": "1490.00 - 1550.00", // 买入区间
  "stopLoss": 1413.60,             // 止损
  "takeProfit": 1748.08,           // 止盈
  "dimensions": [                  // 各维度评分
    { "dimensionCode": "sector_trend", "dimensionName": "板块趋势",
      "score": 95.00, "weight": 0.15, "signal": "positive" }
  ],
  "maAnalysis": "MA5=1520.50 ... 多头排列",
  "macdAnalysis": "金叉 红柱放大 零轴上",
  "sectorAnalysis": "涨幅=+3.20% 上涨=25家 涨停=3家",
  "fundAnalysis": "主力净流入=+12.50亿",
  "marketSentiment": "涨停=85家 跌停=3家",
  "aiSummary": "综合评分78.50，整体趋势偏强...",
  "riskAnalysis": "- 暂无显著风险信号"
}
```

---

## 评分维度

### 个股/板块（9 项）

| 维度 | 权重 | 评分依据 |
|------|------|----------|
| 板块趋势 | 15% | 涨幅/成交额/涨跌比/排名 |
| 期货联动 | 10% | 关联期货方向一致性 |
| 均线趋势 | 15% | 多头/空头排列 |
| MACD | 10% | 金叉/死叉/红绿柱 |
| KDJ | 5% | J值/超买超卖 |
| 成交量 | 15% | 量比/量价关系 |
| 主力资金 | 15% | 净流入/大单 |
| 消息面 | 10% | 新闻情感（需配置） |
| 市场情绪 | 5% | 涨停跌停/封板率 |

### 基金（7 项）

| 维度 | 权重 | 评分依据 |
|------|------|----------|
| 阶段收益 | 20% | 收益率/同类排名 |
| 风控能力 | 20% | 最大回撤/夏普 |
| 基金经理 | 15% | 从业年限/业绩 |
| 持仓质量 | 15% | 重仓股/集中度 |
| 规模适宜 | 10% | 规模 5-50亿最佳 |
| 费率水平 | 10% | 管理+托管费 |
| 外部评级 | 10% | 晨星/银河评级 |

---

## 数据采集

`EastMoneyApiService` 提供东方财富数据接口封装:

- 股票/板块/期货 K线
- 实时行情快照
- 板块排名 + 成分股
- 基金净值 + 持仓
- 涨停/跌停统计
- 资金流向
- 关键字搜索
