package com.xinling.education.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.xinling.common.annotation.Excel;
import com.xinling.common.core.domain.BaseEntity;

/**
 * 乐器信息对象 musical_instruments
 * 
 * @author xinling
 * @date 2025-07-31
 */
public class MusicalInstruments extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private String id;

    /** 乐器图片路径 */
    @Excel(name = "乐器图片路径")
    private String image;

    /** 乐器名称 */
    @Excel(name = "乐器名称")
    private String instrumentName;

    /** 一级分类 */
    @Excel(name = "一级分类")
    private String firstCategory;

    /** 排序号（值越小越靠前） */
    @Excel(name = "排序号", readConverterExp = "值=越小越靠前")
    private Long sortNumber;

    /** 是否会员专属：0=否，1=是 */
    @Excel(name = "是否会员专属：0=否，1=是")
    private Integer isMemberOnly;

    /** 是否显示：0=隐藏，1=显示 */
    @Excel(name = "是否显示：0=隐藏，1=显示")
    private Integer isDisplayed;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setImage(String image) 
    {
        this.image = image;
    }

    public String getImage() 
    {
        return image;
    }

    public void setInstrumentName(String instrumentName) 
    {
        this.instrumentName = instrumentName;
    }

    public String getInstrumentName() 
    {
        return instrumentName;
    }

    public void setFirstCategory(String firstCategory) 
    {
        this.firstCategory = firstCategory;
    }

    public String getFirstCategory() 
    {
        return firstCategory;
    }

    public void setSortNumber(Long sortNumber) 
    {
        this.sortNumber = sortNumber;
    }

    public Long getSortNumber() 
    {
        return sortNumber;
    }

    public void setIsMemberOnly(Integer isMemberOnly) 
    {
        this.isMemberOnly = isMemberOnly;
    }

    public Integer getIsMemberOnly() 
    {
        return isMemberOnly;
    }

    public void setIsDisplayed(Integer isDisplayed) 
    {
        this.isDisplayed = isDisplayed;
    }

    public Integer getIsDisplayed() 
    {
        return isDisplayed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("image", getImage())
            .append("instrumentName", getInstrumentName())
            .append("firstCategory", getFirstCategory())
            .append("sortNumber", getSortNumber())
            .append("isMemberOnly", getIsMemberOnly())
            .append("isDisplayed", getIsDisplayed())
            .toString();
    }
}
