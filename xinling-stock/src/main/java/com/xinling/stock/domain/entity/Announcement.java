package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 公司公告实体（巨潮资讯网来源）
 */
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String stockCode;
    private String announceTitle;
    private String announceType;
    private String summary;
    private Date publishDate;
    private String attachmentUrl;
    private Integer sentiment;
    private BigDecimal sentimentScore;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }

    public String getAnnounceTitle() { return announceTitle; }
    public void setAnnounceTitle(String announceTitle) { this.announceTitle = announceTitle; }

    public String getAnnounceType() { return announceType; }
    public void setAnnounceType(String announceType) { this.announceType = announceType; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Date getPublishDate() { return publishDate; }
    public void setPublishDate(Date publishDate) { this.publishDate = publishDate; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public Integer getSentiment() { return sentiment; }
    public void setSentiment(Integer sentiment) { this.sentiment = sentiment; }

    public BigDecimal getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(BigDecimal sentimentScore) { this.sentimentScore = sentimentScore; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
