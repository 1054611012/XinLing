package com.xinling.psyc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.xinling.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xinling.psyc.domain.PsycOptions;
import com.xinling.psyc.mapper.PsycQuestionsMapper;
import com.xinling.psyc.domain.PsycQuestions;
import com.xinling.psyc.service.IPsycQuestionsService;

/**
 * 题目Service业务层处理
 *
 * @author xinling
 * @date 2025-10-29
 */
@Service
public class PsycQuestionsServiceImpl implements IPsycQuestionsService
{
    @Autowired
    private PsycQuestionsMapper psycQuestionsMapper;

    /**
     * 查询题目
     *
     * @param id 题目主键
     * @return 题目
     */
    @Override
    public PsycQuestions selectPsycQuestionsById(Long id)
    {
        return psycQuestionsMapper.selectPsycQuestionsById(id);
    }

    /**
     * 查询题目列表
     *
     * @param psycQuestions 题目
     * @return 题目
     */
    @Override
    public List<PsycQuestions> selectPsycQuestionsList(PsycQuestions psycQuestions)
    {
        return psycQuestionsMapper.selectPsycQuestionsList(psycQuestions);
    }

    /**
     * 新增题目
     *
     * @param psycQuestions 题目
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPsycQuestions(PsycQuestions psycQuestions)
    {
        int rows = psycQuestionsMapper.insertPsycQuestions(psycQuestions);
        insertPsycOptions(psycQuestions);
        return rows;
    }

    /**
     * 修改题目
     *
     * @param psycQuestions 题目
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePsycQuestions(PsycQuestions psycQuestions)
    {
        psycQuestionsMapper.deletePsycOptionsByQuestionId(psycQuestions.getId());
        insertPsycOptions(psycQuestions);
        return psycQuestionsMapper.updatePsycQuestions(psycQuestions);
    }

    /**
     * 批量删除题目
     *
     * @param ids 需要删除的题目主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycQuestionsByIds(Long[] ids)
    {
        psycQuestionsMapper.deletePsycOptionsByQuestionIds(ids);
        return psycQuestionsMapper.deletePsycQuestionsByIds(ids);
    }

    /**
     * 删除题目信息
     *
     * @param id 题目主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycQuestionsById(Long id)
    {
        psycQuestionsMapper.deletePsycOptionsByQuestionId(id);
        return psycQuestionsMapper.deletePsycQuestionsById(id);
    }

    /**
     * 新增题目选项信息
     *
     * @param psycQuestions 题目对象
     */
    public void insertPsycOptions(PsycQuestions psycQuestions)
    {
        List<PsycOptions> psycOptionsList = psycQuestions.getPsycOptionsList();
        Long id = psycQuestions.getId();
        if (StringUtils.isNotNull(psycOptionsList))
        {
            List<PsycOptions> list = new ArrayList<PsycOptions>();
            for (PsycOptions psycOptions : psycOptionsList)
            {
                psycOptions.setQuestionId(id);
                list.add(psycOptions);
            }
            if (list.size() > 0)
            {
                psycQuestionsMapper.batchPsycOptions(list);
            }
        }
    }
}
