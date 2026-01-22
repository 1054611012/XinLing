package com.xinling.psyc.service;

import com.xinling.psyc.domain.PsycOptions;

import java.util.List;

/**
 * 题目选项Service接口
 *
 * @author ruoyi
 * @date 2025-11-25
 */
public interface IPsycOptionsService
{
    /**
     * 查询题目选项
     *
     * @param id 题目选项主键
     * @return 题目选项
     */
    public PsycOptions selectPsycOptionsById(Long id);

    /**
     * 查询题目选项列表
     *
     * @param psycOptions 题目选项
     * @return 题目选项集合
     */
    public List<PsycOptions> selectPsycOptionsList(PsycOptions psycOptions);

    /**
     * 新增题目选项
     *
     * @param psycOptions 题目选项
     * @return 结果
     */
    public int insertPsycOptions(PsycOptions psycOptions);

    /**
     * 修改题目选项
     *
     * @param psycOptions 题目选项
     * @return 结果
     */
    public int updatePsycOptions(PsycOptions psycOptions);

    /**
     * 批量删除题目选项
     *
     * @param ids 需要删除的题目选项主键集合
     * @return 结果
     */
    public int deletePsycOptionsByIds(Long[] ids);

    /**
     * 删除题目选项信息
     *
     * @param id 题目选项主键
     * @return 结果
     */
    public int deletePsycOptionsById(Long id);
}
