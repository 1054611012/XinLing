package com.xinling.psyc.mapper;

import java.util.List;
import com.xinling.psyc.domain.PsycTest;
import com.xinling.psyc.domain.PsycAssessmentRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 心理测评Mapper接口
 *
 * @author xinling
 * @date 2025-10-28
 */
public interface PsycTestMapper
{
    /**
     * 查询心理测评
     *
     * @param id 心理测评主键
     * @return 心理测评
     */
    public PsycTest selectPsycTestById(Long id);

    /**
     * 查询心理测评列表
     *
     * @param psycTest 心理测评
     * @return 心理测评集合
     */
    public List<PsycTest> selectPsycTestList(PsycTest psycTest);

    /**
     * 新增心理测评
     *
     * @param psycTest 心理测评
     * @return 结果
     */
    public int insertPsycTest(PsycTest psycTest);

    /**
     * 修改心理测评
     *
     * @param psycTest 心理测评
     * @return 结果
     */
    public int updatePsycTest(PsycTest psycTest);

    /**
     * 删除心理测评
     *
     * @param id 心理测评主键
     * @return 结果
     */
    public int deletePsycTestById(Long id);

    /**
     * 批量删除心理测评
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycTestByIds(Long[] ids);

    /**
     * 批量删除测评动态评分区间规则
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycAssessmentRuleByTestIds(Long[] ids);

    /**
     * 批量新增测评动态评分区间规则
     *
     * @param psycAssessmentRuleList 测评动态评分区间规则列表
     * @return 结果
     */
    public int batchPsycAssessmentRule(List<PsycAssessmentRule> psycAssessmentRuleList);


    /**
     * 通过心理测评主键删除测评动态评分区间规则信息
     *
     * @param id 心理测评ID
     * @return 结果
     */
    public int deletePsycAssessmentRuleByTestId(Long id);
}
