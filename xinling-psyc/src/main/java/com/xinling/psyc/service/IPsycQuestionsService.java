package com.xinling.psyc.service;

import java.util.List;
import com.xinling.psyc.domain.PsycQuestions;

/**
 * 题目Service接口
 *
 * @author xinling
 * @date 2025-10-29
 */
public interface IPsycQuestionsService
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
     * 批量删除题目
     *
     * @param ids 需要删除的题目主键集合
     * @return 结果
     */
    public int deletePsycQuestionsByIds(Long[] ids);

    /**
     * 删除题目信息
     *
     * @param id 题目主键
     * @return 结果
     */
    public int deletePsycQuestionsById(Long id);
}
