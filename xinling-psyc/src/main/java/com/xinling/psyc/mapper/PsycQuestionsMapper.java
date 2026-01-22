package com.xinling.psyc.mapper;

import java.util.List;
import com.xinling.psyc.domain.PsycQuestions;
import com.xinling.psyc.domain.PsycOptions;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目Mapper接口
 *
 * @author xinling
 * @date 2025-10-29
 */
public interface PsycQuestionsMapper
{
    /**
     * 查询题目
     *
     * @param id 题目主键
     * @return 题目
     */
    public PsycQuestions selectPsycQuestionsById(Long id);

    /**
     * 查询题目列表
     *
     * @param psycQuestions 题目
     * @return 题目集合
     */
    public List<PsycQuestions> selectPsycQuestionsList(PsycQuestions psycQuestions);

    /**
     * 新增题目
     *
     * @param psycQuestions 题目
     * @return 结果
     */
    public int insertPsycQuestions(PsycQuestions psycQuestions);

    /**
     * 修改题目
     *
     * @param psycQuestions 题目
     * @return 结果
     */
    public int updatePsycQuestions(PsycQuestions psycQuestions);

    /**
     * 删除题目
     *
     * @param id 题目主键
     * @return 结果
     */
    public int deletePsycQuestionsById(Long id);

    /**
     * 批量删除题目
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycQuestionsByIds(Long[] ids);

    /**
     * 批量删除题目选项
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycOptionsByQuestionIds(Long[] ids);

    /**
     * 批量新增题目选项
     *
     * @param psycOptionsList 题目选项列表
     * @return 结果
     */
    public int batchPsycOptions(List<PsycOptions> psycOptionsList);


    /**
     * 通过题目主键删除题目选项信息
     *
     * @param id 题目ID
     * @return 结果
     */
    public int deletePsycOptionsByQuestionId(Long id);


}
