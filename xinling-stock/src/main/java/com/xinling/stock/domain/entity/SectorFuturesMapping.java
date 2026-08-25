package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 板块↔期货映射实体（产业链联动分析核心）
 */
public class SectorFuturesMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sectorId;
    private Long futuresId;
    private String correlationType;
    private BigDecimal weight;
    private String logicDesc;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }

    public Long getFuturesId() { return futuresId; }
    public void setFuturesId(Long futuresId) { this.futuresId = futuresId; }

    public String getCorrelationType() { return correlationType; }
    public void setCorrelationType(String correlationType) { this.correlationType = correlationType; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public String getLogicDesc() { return logicDesc; }
    public void setLogicDesc(String logicDesc) { this.logicDesc = logicDesc; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
