package com.xinling.psyc.service;

import java.util.List;
import com.xinling.psyc.domain.PsycTest;

/**
 * 心理测评Service接口
 *
 * @author xinling
 * @date 2025-10-28
 */
public interface IPsycTestService
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
     * 批量删除心理测评
     *
     * @param ids 需要删除的心理测评主键集合
     * @return 结果
     */
    public int deletePsycTestByIds(Long[] ids);

    /**
     * 删除心理测评信息
     *
     * @param id 心理测评主键
     * @return 结果
     */
    public int deletePsycTestById(Long id);
}
