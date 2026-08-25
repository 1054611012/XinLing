package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 新闻/资讯实体
 */
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String source;
    private String title;
    private String summary;
    private String contentUrl;
    private Date publishTime;
    private String relatedStocks;
    private String relatedSectors;
    private Integer sentiment;
    private BigDecimal sentimentScore;
    private Integer importance;
    private String newsType;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public Date getPublishTime() { return publishTime; }
    public void setPublishTime(Date publishTime) { this.publishTime = publishTime; }

    public String getRelatedStocks() { return relatedStocks; }
    public void setRelatedStocks(String relatedStocks) { this.relatedStocks = relatedStocks; }

    public String getRelatedSectors() { return relatedSectors; }
    public void setRelatedSectors(String relatedSectors) { this.relatedSectors = relatedSectors; }

    public Integer getSentiment() { return sentiment; }
    public void setSentiment(Integer sentiment) { this.sentiment = sentiment; }

    public BigDecimal getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(BigDecimal sentimentScore) { this.sentimentScore = sentimentScore; }

    public Integer getImportance() { return importance; }
    public void setImportance(Integer importance) { this.importance = importance; }

    public String getNewsType() { return newsType; }
    public void setNewsType(String newsType) { this.newsType = newsType; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
