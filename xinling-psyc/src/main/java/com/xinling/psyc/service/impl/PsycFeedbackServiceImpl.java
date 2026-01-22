package com.xinling.psyc.service.impl;

import java.util.List;
import com.xinling.common.utils.DateUtils;
import com.xinling.psyc.domain.PsycFeedback;
import com.xinling.psyc.domain.PsycFeedbackReply;
import com.xinling.psyc.mapper.PsycFeedbackMapper;
import com.xinling.psyc.service.IPsycFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.xinling.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 意见反馈Service业务层处理
 *
 * @author xinling
 * @date 2025-11-27
 */
@Service
public class PsycFeedbackServiceImpl implements IPsycFeedbackService
{
    @Autowired
    private PsycFeedbackMapper psycFeedbackMapper;

    /**
     * 查询意见反馈
     *
     * @param id 意见反馈主键
     * @return 意见反馈
     */
    @Override
    public PsycFeedback selectPsycFeedbackById(Long id)
    {
        return psycFeedbackMapper.selectPsycFeedbackById(id);
    }

    /**
     * 查询意见反馈列表
     *
     * @param psycFeedback 意见反馈
     * @return 意见反馈
     */
    @Override
    public List<PsycFeedback> selectPsycFeedbackList(PsycFeedback psycFeedback)
    {
        return psycFeedbackMapper.selectPsycFeedbackList(psycFeedback);
    }

    /**
     * 新增意见反馈
     *
     * @param psycFeedback 意见反馈
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPsycFeedback(PsycFeedback psycFeedback)
    {
        psycFeedback.setCreateTime(DateUtils.getNowDate());
        int rows = psycFeedbackMapper.insertPsycFeedback(psycFeedback);
        insertPsycFeedbackReply(psycFeedback);
        return rows;
    }

    /**
     * 修改意见反馈
     *
     * @param psycFeedback 意见反馈
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePsycFeedback(PsycFeedback psycFeedback)
    {
        psycFeedback.setUpdateTime(DateUtils.getNowDate());
        psycFeedbackMapper.deletePsycFeedbackReplyByFeedbackId(psycFeedback.getId());
        insertPsycFeedbackReply(psycFeedback);
        return psycFeedbackMapper.updatePsycFeedback(psycFeedback);
    }

    /**
     * 批量删除意见反馈
     *
     * @param ids 需要删除的意见反馈主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycFeedbackByIds(Long[] ids)
    {
        psycFeedbackMapper.deletePsycFeedbackReplyByFeedbackIds(ids);
        return psycFeedbackMapper.deletePsycFeedbackByIds(ids);
    }

    /**
     * 删除意见反馈信息
     *
     * @param id 意见反馈主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycFeedbackById(Long id)
    {
        psycFeedbackMapper.deletePsycFeedbackReplyByFeedbackId(id);
        return psycFeedbackMapper.deletePsycFeedbackById(id);
    }

    /**
     * 新增意见反馈的回复记录信息
     *
     * @param psycFeedback 意见反馈对象
     */
    public void insertPsycFeedbackReply(PsycFeedback psycFeedback)
    {
        List<PsycFeedbackReply> psycFeedbackReplyList = psycFeedback.getPsycFeedbackReplyList();
        Long id = psycFeedback.getId();
        if (StringUtils.isNotNull(psycFeedbackReplyList))
        {
            List<PsycFeedbackReply> list = new ArrayList<PsycFeedbackReply>();
            for (PsycFeedbackReply psycFeedbackReply : psycFeedbackReplyList)
            {
                psycFeedbackReply.setFeedbackId(id);
                list.add(psycFeedbackReply);
            }
            if (list.size() > 0)
            {
                psycFeedbackMapper.batchPsycFeedbackReply(list);
            }
        }
    }
}
