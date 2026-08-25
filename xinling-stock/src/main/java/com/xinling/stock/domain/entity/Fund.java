package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 基金基本信息实体
 */
public class Fund implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fundCode;
    private String fundName;
    private String fundFullName;
    private String fundType;
    private Date establishDate;
    private BigDecimal fundSize;
    private String manager;
    private String custodian;
    private BigDecimal managementFee;
    private BigDecimal custodianFee;
    private BigDecimal purchaseFee;
    private BigDecimal redeemFee;
    private String purchaseStatus;
    private String redeemStatus;
    private String riskLevel;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }

    public String getFundFullName() { return fundFullName; }
    public void setFundFullName(String fundFullName) { this.fundFullName = fundFullName; }

    public String getFundType() { return fundType; }
    public void setFundType(String fundType) { this.fundType = fundType; }

    public Date getEstablishDate() { return establishDate; }
    public void setEstablishDate(Date establishDate) { this.establishDate = establishDate; }

    public BigDecimal getFundSize() { return fundSize; }
    public void setFundSize(BigDecimal fundSize) { this.fundSize = fundSize; }

    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }

    public String getCustodian() { return custodian; }
    public void setCustodian(String custodian) { this.custodian = custodian; }

    public BigDecimal getManagementFee() { return managementFee; }
    public void setManagementFee(BigDecimal managementFee) { this.managementFee = managementFee; }

    public BigDecimal getCustodianFee() { return custodianFee; }
    public void setCustodianFee(BigDecimal custodianFee) { this.custodianFee = custodianFee; }

    public BigDecimal getPurchaseFee() { return purchaseFee; }
    public void setPurchaseFee(BigDecimal purchaseFee) { this.purchaseFee = purchaseFee; }

    public BigDecimal getRedeemFee() { return redeemFee; }
    public void setRedeemFee(BigDecimal redeemFee) { this.redeemFee = redeemFee; }

    public String getPurchaseStatus() { return purchaseStatus; }
    public void setPurchaseStatus(String purchaseStatus) { this.purchaseStatus = purchaseStatus; }

    public String getRedeemStatus() { return redeemStatus; }
    public void setRedeemStatus(String redeemStatus) { this.redeemStatus = redeemStatus; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
