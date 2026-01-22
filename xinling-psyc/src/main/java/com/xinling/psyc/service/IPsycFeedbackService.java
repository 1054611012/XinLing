package com.xinling.psyc.service;

import com.xinling.psyc.domain.PsycFeedback;

import java.util.List;

/**
 * 意见反馈Service接口
 *
 * @author xinling
 * @date 2025-11-27
 */
public interface IPsycFeedbackService
{
    /**
     * 查询意见反馈
     *
     * @param id 意见反馈主键
     * @return 意见反馈
     */
    public PsycFeedback selectPsycFeedbackById(Long id);

    /**
     * 查询意见反馈列表
     *
     * @param psycFeedback 意见反馈
     * @return 意见反馈集合
     */
    public List<PsycFeedback> selectPsycFeedbackList(PsycFeedback psycFeedback);

    /**
     * 新增意见反馈
     *
     * @param psycFeedback 意见反馈
     * @return 结果
     */
    public int insertPsycFeedback(PsycFeedback psycFeedback);

    /**
     * 修改意见反馈
     *
     * @param psycFeedback 意见反馈
     * @return 结果
     */
    public int updatePsycFeedback(PsycFeedback psycFeedback);

    /**
     * 批量删除意见反馈
     *
     * @param ids 需要删除的意见反馈主键集合
     * @return 结果
     */
    public int deletePsycFeedbackByIds(Long[] ids);

    /**
     * 删除意见反馈信息
     *
     * @param id 意见反馈主键
     * @return 结果
     */
    public int deletePsycFeedbackById(Long id);
}
