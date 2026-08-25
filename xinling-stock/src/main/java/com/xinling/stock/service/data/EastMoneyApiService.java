package com.xinling.stock.service.data;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinling.stock.domain.entity.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 东方财富数据采集服务
 *
 * 通过东方财富开放的 HTTP API 获取股票、板块、基金、期货行情数据。
 * 数据仅供分析参考，不构成投资建议。
 *
 * 注意：东方财富 API 属于非官方接口，可能随时变更。
 * 生产环境建议使用 tushare pro / 聚宽等付费数据服务。
 */
@Service
public class EastMoneyApiService {

    private static final Logger log = LoggerFactory.getLogger(EastMoneyApiService.class);

    private final OkHttpClient client;

    public EastMoneyApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // ========================================================================
    //  股票行情 API
    // ========================================================================

    /**
     * 获取个股日K线数据
     * @param stockCode 股票代码，如 600519
     * @param market 市场: 1-上海 0-深圳
     * @param count 获取条数
     */
    public List<KlineDaily> fetchStockKLine(String stockCode, int market, int count) {
        // 东方财富K线接口: https://push2his.eastmoney.com/api/qt/stock/kline/get
        String url = "https://push2his.eastmoney.com/api/qt/stock/kline/get?"
                + "secid=" + market + "." + stockCode
                + "&fields1=f1,f2,f3,f4,f5,f6"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
                + "&klt=101&fqt=1" // 101=日K, 1=前复权
                + "&end=20500101&lmt=" + count;

        return parseKLineResponse(fetchJson(url), "stock", stockCode);
    }

    /**
     * 获取板块指数日K线
     * @param sectorCode 板块代码，如 BK0477
     * @param count 获取条数
     */
    public List<KlineDaily> fetchSectorKLine(String sectorCode, int count) {
        // 板块使用 0.BKxxxx 格式
        String url = "https://push2his.eastmoney.com/api/qt/stock/kline/get?"
                + "secid=0." + sectorCode
                + "&fields1=f1,f2,f3,f4,f5,f6"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
                + "&klt=101&fqt=1"
                + "&end=20500101&lmt=" + count;

        return parseKLineResponse(fetchJson(url), "sector", sectorCode);
    }

    /**
     * 获取实时行情快照（批量）
     * @param symbols 格式: "1.600519,0.000001,0.BK0477"
     */
    public JSONObject fetchRealtimeQuotes(String symbols) {
        String url = "https://push2.eastmoney.com/api/qt/ulist.np/get?"
                + "fltt=2&fields=f2,f3,f4,f12,f14,f15,f16,f17,f18,f19"
                + "&secids=" + symbols;
        return fetchJson(url);
    }

    // ========================================================================
    //  板块数据 API
    // ========================================================================

    /**
     * 获取全市场板块涨幅排名
     * @param type 板块类型: 2-行业板块 3-概念板块
     * @param count 数量
     */
    public List<JSONObject> fetchSectorRanking(int type, int count) {
        String url = "https://push2.eastmoney.com/api/qt/clist/get?"
                + "pn=1&pz=" + count + "&po=1&np=1"
                + "&fields=f2,f3,f4,f8,f12,f14,f15,f16,f17,f18,f20,f21"
                + "&fid=f3"
                + "&fs=m:90+t:" + type;

        JSONObject result = fetchJson(url);
        return extractDataList(result);
    }

    /**
     * 获取板块成分股列表
     */
    public List<JSONObject> fetchSectorStocks(String sectorCode) {
        String url = "https://push2.eastmoney.com/api/qt/clist/get?"
                + "pn=1&pz=100&po=1&np=1"
                + "&fields=f2,f3,f4,f12,f14,f15,f16"
                + "&fid=f3"
                + "&fs=b:" + sectorCode;
        JSONObject result = fetchJson(url);
        return extractDataList(result);
    }

    // ========================================================================
    //  基金数据 API
    // ========================================================================

    /**
     * 获取基金净值历史
     * @param fundCode 基金代码，如 110011
     * @param page 页码
     * @param size 每页条数
     */
    public List<FundNav> fetchFundNav(String fundCode, int page, int size) {
        String url = "https://api.fund.eastmoney.com/f10/lsjz?"
                + "callback=jQuery&fundCode=" + fundCode
                + "&pageIndex=" + page + "&pageSize=" + size
                + "&startDate=&endDate=";

        try {
            String html = fetchString(url);
            // 清理JSONP包装
            String json = html.replaceAll("^jQuery[^(]*\\(", "").replaceAll("\\)$", "");
            JSONObject result = JSON.parseObject(json);
            JSONArray data = result.getJSONObject("Data").getJSONArray("LSJZList");

            if (data == null || data.isEmpty()) return Collections.emptyList();

            List<FundNav> list = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < data.size(); i++) {
                JSONObject item = data.getJSONObject(i);
                FundNav nav = new FundNav();
                nav.setFundCode(fundCode);
                nav.setNavDate(sdf.parse(item.getString("FSRQ")));
                nav.setUnitNav(new BigDecimal(item.getString("DWJZ")));
                nav.setTotalNav(new BigDecimal(item.getString("LJJZ")));
                if (item.containsKey("JZZZL") && item.getString("JZZZL") != null) {
                    nav.setDailyChangePct(new BigDecimal(item.getString("JZZZL")));
                }
                list.add(nav);
            }
            return list;
        } catch (Exception e) {
            log.error("获取基金净值失败 {}: {}", fundCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 获取基金持仓（前10重仓）
     */
    public List<FundHolding> fetchFundHoldings(String fundCode) {
        String url = "https://fundf10.eastmoney.com/FundArchivesDatas.aspx?"
                + "type=jjcc&code=" + fundCode + "&topline=10&year=&month=&rt=0.5";

        try {
            String html = fetchString(url);
            // 简单解析 - 实际需解析HTML表格
            // 这里只做占位，具体解析逻辑取决于接口返回格式
            log.info("基金{}持仓数据需HTML解析", fundCode);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("获取基金持仓失败 {}: {}", fundCode, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 基金列表搜索
     */
    public List<JSONObject> searchFund(String keyword) {
        String url = "https://fundsuggest.eastmoney.com/FundSearch?"
                + "func=FundSearch&key=" + keyword + "&pageIndex=1&pageSize=20";
        JSONObject result = fetchJson(url);
        return extractDataList(result);
    }

    // ========================================================================
    //  个股资金流向 API
    // ========================================================================

    /**
     * 获取个股资金流向
     */
    public JSONObject fetchFundFlow(String stockCode, int market) {
        String url = "https://push2.eastmoney.com/api/qt/stock/fflow/kline/get?"
                + "secid=" + market + "." + stockCode
                + "&fields1=f1,f2,f3,f4,f5,f6"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61";
        return fetchJson(url);
    }

    // ========================================================================
    //  期货行情 API
    // ========================================================================

    /**
     * 获取期货主力合约行情
     */
    public List<KlineDaily> fetchFuturesKLine(String futuresCode, int count) {
        String url = "https://push2his.eastmoney.com/api/qt/stock/kline/get?"
                + "secid=0." + futuresCode
                + "&fields1=f1,f2,f3,f4,f5,f6"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
                + "&klt=101&fqt=1"
                + "&end=20500101&lmt=" + count;

        return parseKLineResponse(fetchJson(url), "futures", futuresCode);
    }

    // ========================================================================
    //  市场情绪 API
    // ========================================================================

    /**
     * 获取涨停板数据
     */
    public JSONObject fetchLimitUpData() {
        String url = "https://push2.eastmoney.com/api/qt/clist/get?"
                + "pn=1&pz=200&po=1&np=1"
                + "&fields=f2,f3,f4,f12,f14"
                + "&fid=f3"
                + "&fs=m:0+t:LIMIT_UP";
        return fetchJson(url);
    }

    /**
     * 获取跌停板数据
     */
    public JSONObject fetchLimitDownData() {
        String url = "https://push2.eastmoney.com/api/qt/clist/get?"
                + "pn=1&pz=200&po=1&np=1"
                + "&fields=f2,f3,f4,f12,f14"
                + "&fid=f3"
                + "&fs=m:0+t:LIMIT_DOWN";
        return fetchJson(url);
    }

    // ========================================================================
    //  公共数据查询
    // ========================================================================

    /**
     * 搜索股票或板块
     */
    public List<JSONObject> searchSymbol(String keyword) {
        String url = "https://searchadapter.eastmoney.com/api/suggest/get?"
                + "input=" + keyword + "&type=14&token=8C8F7BC0-2442-4A89-A428-0D03B1E70D6A"
                + "&count=10";
        JSONObject result = fetchJson(url);
        return extractDataList(result);
    }

    // ========================================================================
    //  内部工具方法
    // ========================================================================

    private JSONObject fetchJson(String url) {
        try {
            String text = fetchString(url);
            return JSON.parseObject(text);
        } catch (Exception e) {
            log.error("API请求失败: {}", e.getMessage());
            return new JSONObject();
        }
    }

    private String fetchString(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                .header("Referer", "https://quote.eastmoney.com/")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : "";
        }
    }

    private List<KlineDaily> parseKLineResponse(JSONObject result, String symbolType, String symbolCode) {
        try {
            JSONObject data = result.getJSONObject("data");
            if (data == null) return Collections.emptyList();

            String klines = data.getString("klines");
            if (klines == null) return Collections.emptyList();

            // 格式: "2026-07-28,1898.00,1902.00,1885.00,1890.00,1890.00,12345678,234567890000.00,0.12,0.89,0.56"
            String[] lines = klines.split(";");
            List<KlineDaily> list = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 11) continue;
                KlineDaily k = new KlineDaily();
                k.setSymbolType(symbolType);
                k.setSymbolCode(symbolCode);
                k.setTradeDate(sdf.parse(parts[0]));
                k.setOpenPrice(new BigDecimal(parts[1]));
                k.setClosePrice(new BigDecimal(parts[2]));
                k.setHighPrice(new BigDecimal(parts[3]));
                k.setLowPrice(new BigDecimal(parts[4]));
                k.setPreClose(new BigDecimal(parts[5]));
                k.setVolume(Long.parseLong(parts[6]));
                k.setAmount(new BigDecimal(parts[7]));
                k.setChangePct(new BigDecimal(parts[8]));
                k.setAmplitude(new BigDecimal(parts[9]));
                if (parts.length > 10 && !parts[10].isEmpty()) {
                    k.setTurnoverRate(new BigDecimal(parts[10]));
                }
                list.add(k);
            }
            return list;

        } catch (Exception e) {
            log.error("解析K线数据失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<JSONObject> extractDataList(JSONObject result) {
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                JSONArray diff = data.getJSONArray("diff");
                if (diff != null) {
                    for (int i = 0; i < diff.size(); i++) {
                        list.add(diff.getJSONObject(i));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析数据列表失败: {}", e.getMessage());
        }
        return list;
    }
}
