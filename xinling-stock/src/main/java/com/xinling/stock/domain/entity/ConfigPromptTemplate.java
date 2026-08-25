package com.xinling.stock.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI Prompt模板配置实体
 */
public class ConfigPromptTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String scene;
    private String systemPrompt;
    private String userPrompt;
    private String outputSchema;
    private String model;
    private BigDecimal temperature;
    private Integer maxTokens;
    private String version;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public String getScene() { return scene; }
    public void setScene(String scene) { this.scene = scene; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public String getUserPrompt() { return userPrompt; }
    public void setUserPrompt(String userPrompt) { this.userPrompt = userPrompt; }

    public String getOutputSchema() { return outputSchema; }
    public void setOutputSchema(String outputSchema) { this.outputSchema = outputSchema; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
