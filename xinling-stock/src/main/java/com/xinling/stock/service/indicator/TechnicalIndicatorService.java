package com.xinling.stock.service.indicator;

import com.xinling.stock.domain.entity.KlineDaily;
import com.xinling.stock.domain.entity.TechnicalIndicator;
import com.xinling.stock.mapper.KlineDailyMapper;
import com.xinling.stock.mapper.TechnicalIndicatorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 技术指标计算服务
 *
 * 基于日K线数据计算以下技术指标：
 * - 均线系统: MA5, MA10, MA20, MA30, MA60, MA120, MA250
 * - MACD: DIF, DEA, BAR (柱状值)
 * - KDJ: K, D, J
 * - RSI: RSI6, RSI12, RSI24
 * - BOLL: 中轨, 上轨, 下轨
 * - OBV: 能量潮
 * - 量比
 */
@Service
public class TechnicalIndicatorService {

    private static final Logger log = LoggerFactory.getLogger(TechnicalIndicatorService.class);

    private final KlineDailyMapper klineDailyMapper;
    private final TechnicalIndicatorMapper indicatorMapper;

    public TechnicalIndicatorService(KlineDailyMapper klineDailyMapper,
                                     TechnicalIndicatorMapper indicatorMapper) {
        this.klineDailyMapper = klineDailyMapper;
        this.indicatorMapper = indicatorMapper;
    }

    /**
     * 计算并保存指定标的的全部技术指标
     */
    public TechnicalIndicator calculateAndSave(String symbolType, String symbolCode, Date tradeDate) {
        // 获取足够的K线数据用于计算（需要至少250个交易日数据）
        List<KlineDaily> klineList = klineDailyMapper.selectBySymbolAndDateRange(
                symbolType, symbolCode, getStartDate(tradeDate, 260), tradeDate);

        if (klineList.isEmpty()) {
            log.warn("No kline data for {} {} on {}", symbolType, symbolCode, tradeDate);
            return null;
        }

        // 按日期排序
        klineList.sort(Comparator.comparing(KlineDaily::getTradeDate));

        // 计算所有指标
        TechnicalIndicator indicator = calculate(klineList);

        if (indicator == null) return null;

        // 设置标识
        indicator.setSymbolType(symbolType);
        indicator.setSymbolCode(symbolCode);
        indicator.setTradeDate(tradeDate);

        // 保存到数据库
        TechnicalIndicator existing = indicatorMapper.selectBySymbolAndDate(symbolType, symbolCode, tradeDate);
        if (existing != null) {
            indicator.setId(existing.getId());
            indicatorMapper.updateBySymbolAndDate(indicator);
        } else {
            indicatorMapper.insert(indicator);
        }

        return indicator;
    }

    /**
     * 从K线数据列表计算全部技术指标
     */
    public TechnicalIndicator calculate(List<KlineDaily> klineList) {
        if (klineList == null || klineList.isEmpty()) return null;

        int size = klineList.size();
        TechnicalIndicator result = new TechnicalIndicator();

        // ========== 1. 均线系统 ==========
        result.setMa5(calcMA(klineList, 5));
        result.setMa10(calcMA(klineList, 10));
        result.setMa20(calcMA(klineList, 20));
        result.setMa30(calcMA(klineList, 30));
        result.setMa60(calcMA(klineList, 60));
        result.setMa120(calcMA(klineList, 120));
        result.setMa250(calcMA(klineList, 250));

        // ========== 2. MACD ==========
        double[] closePrices = klineList.stream()
                .mapToDouble(k -> k.getClosePrice().doubleValue())
                .toArray();
        double[] macdResult = calcMACD(closePrices);
        result.setMacdDif(BigDecimal.valueOf(macdResult[0]));
        result.setMacdDea(BigDecimal.valueOf(macdResult[1]));
        result.setMacdBar(BigDecimal.valueOf(macdResult[2]));

        // ========== 3. KDJ ==========
        double[] highPrices = klineList.stream()
                .mapToDouble(k -> k.getHighPrice().doubleValue()).toArray();
        double[] lowPrices = klineList.stream()
                .mapToDouble(k -> k.getLowPrice().doubleValue()).toArray();
        double[] kdjValues = calcKDJ(closePrices, highPrices, lowPrices);
        result.setKdjK(BigDecimal.valueOf(kdjValues[0]));
        result.setKdjD(BigDecimal.valueOf(kdjValues[1]));
        result.setKdjJ(BigDecimal.valueOf(kdjValues[2]));

        // ========== 4. RSI ==========
        result.setRsi6(calcRSI(closePrices, 6));
        result.setRsi12(calcRSI(closePrices, 12));
        result.setRsi24(calcRSI(closePrices, 24));

        // ========== 5. BOLL ==========
        double[] bollValues = calcBOLL(closePrices, 20);
        result.setBollMid(BigDecimal.valueOf(bollValues[0]));
        result.setBollUpper(BigDecimal.valueOf(bollValues[1]));
        result.setBollLower(BigDecimal.valueOf(bollValues[2]));

        // ========== 6. OBV ==========
        long[] volumes = klineList.stream()
                .mapToLong(k -> k.getVolume() != null ? k.getVolume() : 0L).toArray();
        result.setObv(calcOBV(closePrices, volumes));

        // ========== 7. 量比 ==========
        result.setVolumeRatio(calcVolumeRatio(klineList));

        return result;
    }

    // ========================================================================
    //  均线计算
    // ========================================================================

    private BigDecimal calcMA(List<KlineDaily> klineList, int period) {
        int size = klineList.size();
        if (size < period) return null;
        double sum = 0;
        for (int i = size - period; i < size; i++) {
            sum += klineList.get(i).getClosePrice().doubleValue();
        }
        return BigDecimal.valueOf(sum / period).setScale(2, RoundingMode.HALF_UP);
    }

    // ========================================================================
    //  MACD 计算 (EMA算法)
    // ========================================================================

    private double[] calcMACD(double[] prices) {
        int len = prices.length;
        double[] ema12 = calcEMA(prices, 12);
        double[] ema26 = calcEMA(prices, 26);
        double[] dif = new double[len];
        for (int i = 0; i < len; i++) {
            dif[i] = ema12[i] - ema26[i];
        }
        double[] dea = calcEMA(dif, 9);
        double bar = (dif[len - 1] - dea[len - 1]) * 2;
        return new double[]{dif[len - 1], dea[len - 1], bar};
    }

    private double[] calcEMA(double[] data, int period) {
        int len = data.length;
        double[] ema = new double[len];
        double multiplier = 2.0 / (period + 1);
        ema[0] = data[0];
        for (int i = 1; i < len; i++) {
            ema[i] = (data[i] - ema[i - 1]) * multiplier + ema[i - 1];
        }
        return ema;
    }

    // ========================================================================
    //  KDJ 计算
    // ========================================================================

    private double[] calcKDJ(double[] close, double[] high, double[] low) {
        int len = close.length;
        int period = 9;

        double[] rsv = new double[len];
        for (int i = period - 1; i < len; i++) {
            double hn = high[i], ln = low[i];
            for (int j = i - period + 1; j <= i; j++) {
                if (high[j] > hn) hn = high[j];
                if (low[j] < ln) ln = low[j];
            }
            double cn = close[i];
            if (hn == ln) {
                rsv[i] = 50;
            } else {
                rsv[i] = (cn - ln) / (hn - ln) * 100;
            }
        }

        double k = 50, d = 50;
        for (int i = period - 1; i < len; i++) {
            k = 2.0 / 3 * k + 1.0 / 3 * rsv[i];
            d = 2.0 / 3 * d + 1.0 / 3 * k;
        }
        double j = 3 * k - 2 * d;

        return new double[]{round2(k), round2(d), round2(j)};
    }

    // ========================================================================
    //  RSI 计算
    // ========================================================================

    private BigDecimal calcRSI(double[] prices, int period) {
        int len = prices.length;
        if (len < period + 1) return null;

        double gain = 0, loss = 0;
        for (int i = len - period; i < len; i++) {
            double diff = prices[i] - prices[i - 1];
            if (diff > 0) gain += diff;
            else loss -= diff;
        }
        double avgGain = gain / period;
        double avgLoss = loss / period;
        if (avgLoss == 0) return BigDecimal.valueOf(100);

        double rs = avgGain / avgLoss;
        double rsi = 100 - 100 / (1 + rs);
        return BigDecimal.valueOf(round2(rsi));
    }

    // ========================================================================
    //  BOLL 布林带计算
    // ========================================================================

    private double[] calcBOLL(double[] prices, int period) {
        int len = prices.length;
        if (len < period) return new double[]{prices[len - 1], prices[len - 1], prices[len - 1]};

        double sum = 0;
        for (int i = len - period; i < len; i++) {
            sum += prices[i];
        }
        double mid = sum / period;

        double variance = 0;
        for (int i = len - period; i < len; i++) {
            variance += Math.pow(prices[i] - mid, 2);
        }
        double stddev = Math.sqrt(variance / period);

        return new double[]{round2(mid), round2(mid + 2 * stddev), round2(mid - 2 * stddev)};
    }

    // ========================================================================
    //  OBV 能量潮计算
    // ========================================================================

    private long calcOBV(double[] close, long[] volume) {
        int len = close.length;
        long obv = 0;
        for (int i = 1; i < len; i++) {
            if (close[i] > close[i - 1]) {
                obv += volume[i];
            } else if (close[i] < close[i - 1]) {
                obv -= volume[i];
            }
        }
        return obv;
    }

    // ========================================================================
    //  量比计算（当前量 / 5日均量）
    // ========================================================================

    private BigDecimal calcVolumeRatio(List<KlineDaily> klineList) {
        int size = klineList.size();
        if (size < 6) return null;

        // 最近一天的成交量
        long todayVolume = klineList.get(size - 1).getVolume() != null
                ? klineList.get(size - 1).getVolume() : 0;
        if (todayVolume == 0) return null;

        // 过去5天的平均成交量
        long sumVolume = 0;
        for (int i = size - 6; i < size - 1; i++) {
            sumVolume += klineList.get(i).getVolume() != null ? klineList.get(i).getVolume() : 0;
        }
        if (sumVolume == 0) return null;

        double avgVolume = sumVolume / 5.0;
        double ratio = todayVolume / avgVolume;
        return BigDecimal.valueOf(round2(ratio));
    }

    // ========================================================================
    //  工具方法
    // ========================================================================

    private Date getStartDate(Date endDate, int tradingDaysBack) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        // 粗略估算：交易日 ≈ 日历日 * 0.7
        cal.add(Calendar.DAY_OF_YEAR, (int) (tradingDaysBack * 1.4));
        return cal.getTime();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
