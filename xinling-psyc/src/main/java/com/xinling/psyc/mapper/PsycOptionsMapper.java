package com.xinling.psyc.mapper;

import com.xinling.psyc.domain.PsycOptions;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 题目选项Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-25
 */
public interface PsycOptionsMapper
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
     * 删除题目选项
     *
     * @param id 题目选项主键
     * @return 结果
     */
    public int deletePsycOptionsById(Long id);

    /**
     * 批量删除题目选项
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycOptionsByIds(Long[] ids);

    /**
     * 根据题目ID查询题目选项
     *
     * @param questionIds
     * @return
     */
    List<PsycOptions> selectPsycOptionsListByQuestionIds(List<Long> questionIds);
}
