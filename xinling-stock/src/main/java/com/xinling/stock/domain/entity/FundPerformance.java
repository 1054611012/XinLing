package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 基金阶段表现与风险指标实体
 */
public class FundPerformance implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fundCode;
    private Date statDate;
    private BigDecimal return1w;
    private BigDecimal return1m;
    private BigDecimal return3m;
    private BigDecimal return6m;
    private BigDecimal return1y;
    private BigDecimal return3y;
    private BigDecimal return5y;
    private BigDecimal returnSinceEstablish;
    private BigDecimal maxDrawdown1y;
    private BigDecimal maxDrawdown3y;
    private BigDecimal sharpRatio1y;
    private BigDecimal sharpRatio3y;
    private BigDecimal volatility1y;
    private BigDecimal excessReturn1y;
    private String rank1y;
    private Integer morningstarRating;
    private Integer galaxyRating;
    private Date createTime;
    private Date updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public Date getStatDate() { return statDate; }
    public void setStatDate(Date statDate) { this.statDate = statDate; }

    public BigDecimal getReturn1w() { return return1w; }
    public void setReturn1w(BigDecimal return1w) { this.return1w = return1w; }

    public BigDecimal getReturn1m() { return return1m; }
    public void setReturn1m(BigDecimal return1m) { this.return1m = return1m; }

    public BigDecimal getReturn3m() { return return3m; }
    public void setReturn3m(BigDecimal return3m) { this.return3m = return3m; }

    public BigDecimal getReturn6m() { return return6m; }
    public void setReturn6m(BigDecimal return6m) { this.return6m = return6m; }

    public BigDecimal getReturn1y() { return return1y; }
    public void setReturn1y(BigDecimal return1y) { this.return1y = return1y; }

    public BigDecimal getReturn3y() { return return3y; }
    public void setReturn3y(BigDecimal return3y) { this.return3y = return3y; }

    public BigDecimal getReturn5y() { return return5y; }
    public void setReturn5y(BigDecimal return5y) { this.return5y = return5y; }

    public BigDecimal getReturnSinceEstablish() { return returnSinceEstablish; }
    public void setReturnSinceEstablish(BigDecimal returnSinceEstablish) { this.returnSinceEstablish = returnSinceEstablish; }

    public BigDecimal getMaxDrawdown1y() { return maxDrawdown1y; }
    public void setMaxDrawdown1y(BigDecimal maxDrawdown1y) { this.maxDrawdown1y = maxDrawdown1y; }

    public BigDecimal getMaxDrawdown3y() { return maxDrawdown3y; }
    public void setMaxDrawdown3y(BigDecimal maxDrawdown3y) { this.maxDrawdown3y = maxDrawdown3y; }

    public BigDecimal getSharpRatio1y() { return sharpRatio1y; }
    public void setSharpRatio1y(BigDecimal sharpRatio1y) { this.sharpRatio1y = sharpRatio1y; }

    public BigDecimal getSharpRatio3y() { return sharpRatio3y; }
    public void setSharpRatio3y(BigDecimal sharpRatio3y) { this.sharpRatio3y = sharpRatio3y; }

    public BigDecimal getVolatility1y() { return volatility1y; }
    public void setVolatility1y(BigDecimal volatility1y) { this.volatility1y = volatility1y; }

    public BigDecimal getExcessReturn1y() { return excessReturn1y; }
    public void setExcessReturn1y(BigDecimal excessReturn1y) { this.excessReturn1y = excessReturn1y; }

    public String getRank1y() { return rank1y; }
    public void setRank1y(String rank1y) { this.rank1y = rank1y; }

    public Integer getMorningstarRating() { return morningstarRating; }
    public void setMorningstarRating(Integer morningstarRating) { this.morningstarRating = morningstarRating; }

    public Integer getGalaxyRating() { return galaxyRating; }
    public void setGalaxyRating(Integer galaxyRating) { this.galaxyRating = galaxyRating; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
