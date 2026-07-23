package com.xinling.psyc.service.impl;

import java.util.List;
import com.xinling.common.utils.DateUtils;
import com.xinling.psyc.domain.PsycOptions;
import com.xinling.psyc.domain.PsycQuestions;
import com.xinling.psyc.mapper.PsycOptionsMapper;
import com.xinling.psyc.mapper.PsycQuestionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import com.xinling.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xinling.psyc.domain.PsycAssessmentRule;
import com.xinling.psyc.mapper.PsycTestMapper;
import com.xinling.psyc.domain.PsycTest;
import com.xinling.psyc.service.IPsycTestService;

/**
 * 心理测评Service业务层处理
 *
 * @author xinling
 * @date 2025-10-28
 */
@Service
public class PsycTestServiceImpl implements IPsycTestService
{
    @Autowired
    private PsycTestMapper psycTestMapper;

    @Autowired
    private PsycQuestionsMapper psycQuestionsMapper;

    @Autowired
    private PsycOptionsMapper psycOptionsMapper;

    /**
     * 查询心理测评
     *
     * @param id 心理测评主键
     * @return 心理测评
     */
    @Override
    public PsycTest selectPsycTestById(Long id)
    {
        PsycTest psycTest = psycTestMapper.selectPsycTestById(id);

        if (psycTest != null) {
            // 显式查询题目列表并设置
            PsycQuestions query = new PsycQuestions();
            query.setTestId(id);
            List<PsycQuestions> questionsList = psycQuestionsMapper.selectPsycQuestionsList(query);

            // 题目对应选项，把题目ids查询选项，同时组装选项对于题。
            List<Long> questionIds = questionsList.stream().map(PsycQuestions::getId).collect(Collectors.toList());
            List<PsycOptions> psycOptions = psycOptionsMapper.selectPsycOptionsListByQuestionIds(questionIds);

            // 判断psycOptions是否为空
            if (psycOptions != null) {
                // 将选项列表根据题目id进行分组
                Map<Long, List<PsycOptions>> questionIdsOptions = psycOptions.stream().collect(Collectors.groupingBy(PsycOptions::getQuestionId));
                questionsList.forEach(question -> {
                    List<PsycOptions> options = questionIdsOptions.get(question.getId());
                    question.setPsycOptionsList(options != null ? options : new ArrayList<>());
                });
            }
            psycTest.setPsycQuestionsList(questionsList);
        }

        return psycTest;
    }


    /**
     * 查询心理测评列表
     *
     * @param psycTest 心理测评
     * @return 心理测评
     */
    @Override
    public List<PsycTest> selectPsycTestList(PsycTest psycTest)
    {
        return psycTestMapper.selectPsycTestList(psycTest);
    }

    /**
     * 新增心理测评
     *
     * @param psycTest 心理测评
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPsycTest(PsycTest psycTest)
    {
        psycTest.setCreateTime(DateUtils.getNowDate());
        int rows = psycTestMapper.insertPsycTest(psycTest);
        insertPsycAssessmentRule(psycTest);
        return rows;
    }

    /**
     * 修改心理测评
     *
     * @param psycTest 心理测评
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePsycTest(PsycTest psycTest)
    {
        psycTest.setUpdateTime(DateUtils.getNowDate());
        psycTestMapper.deletePsycAssessmentRuleByTestId(psycTest.getId());
        insertPsycAssessmentRule(psycTest);
        return psycTestMapper.updatePsycTest(psycTest);
    }

    /**
     * 批量删除心理测评
     *
     * @param ids 需要删除的心理测评主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycTestByIds(Long[] ids)
    {
        psycTestMapper.deletePsycAssessmentRuleByTestIds(ids);
        return psycTestMapper.deletePsycTestByIds(ids);
    }

    /**
     * 删除心理测评信息
     *
     * @param id 心理测评主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycTestById(Long id)
    {
        psycTestMapper.deletePsycAssessmentRuleByTestId(id);
        return psycTestMapper.deletePsycTestById(id);
    }

    /**
     * 新增测评动态评分区间规则信息
     *
     * @param psycTest 心理测评对象
     */
    public void insertPsycAssessmentRule(PsycTest psycTest)
    {
        List<PsycAssessmentRule> psycAssessmentRuleList = psycTest.getPsycAssessmentRuleList();
        Long id = psycTest.getId();
        if (StringUtils.isNotNull(psycAssessmentRuleList))
        {
            List<PsycAssessmentRule> list = new ArrayList<PsycAssessmentRule>();
            for (PsycAssessmentRule psycAssessmentRule : psycAssessmentRuleList)
            {
                psycAssessmentRule.setTestId(id);
                // 设置默认值：未删除
                if (psycAssessmentRule.getIsDeleted() == null) {
                    psycAssessmentRule.setIsDeleted(0L);
                }
                list.add(psycAssessmentRule);
            }
            if (list.size() > 0)
            {
                psycTestMapper.batchPsycAssessmentRule(list);
            }
        }
    }
}
