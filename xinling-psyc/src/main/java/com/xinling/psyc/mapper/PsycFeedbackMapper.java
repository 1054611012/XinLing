package com.xinling.psyc.mapper;

import com.xinling.psyc.domain.PsycFeedback;
import com.xinling.psyc.domain.PsycFeedbackReply;

import java.util.List;

/**
 * 意见反馈Mapper接口
 *
 * @author xinling
 * @date 2025-11-27
 */
public interface PsycFeedbackMapper
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
     * 删除意见反馈
     *
     * @param id 意见反馈主键
     * @return 结果
     */
    public int deletePsycFeedbackById(Long id);

    /**
     * 批量删除意见反馈
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycFeedbackByIds(Long[] ids);

    /**
     * 批量删除意见反馈的回复记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycFeedbackReplyByFeedbackIds(Long[] ids);

    /**
     * 批量新增意见反馈的回复记录
     *
     * @param psycFeedbackReplyList 意见反馈的回复记录列表
     * @return 结果
     */
    public int batchPsycFeedbackReply(List<PsycFeedbackReply> psycFeedbackReplyList);


    /**
     * 通过意见反馈主键删除意见反馈的回复记录信息
     *
     * @param id 意见反馈ID
     * @return 结果
     */
    public int deletePsycFeedbackReplyByFeedbackId(Long id);
}
