package com.xinling.psyc.service.impl;

import java.util.List;

import com.xinling.psyc.domain.PsycOptions;
import com.xinling.psyc.mapper.PsycOptionsMapper;
import com.xinling.psyc.service.IPsycOptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 题目选项Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-25
 */
@Service
public class PsycOptionsServiceImpl implements IPsycOptionsService
{
    @Autowired
    private PsycOptionsMapper psycOptionsMapper;

    /**
     * 查询题目选项
     *
     * @param id 题目选项主键
     * @return 题目选项
     */
    @Override
    public PsycOptions selectPsycOptionsById(Long id)
    {
        return psycOptionsMapper.selectPsycOptionsById(id);
    }

    /**
     * 查询题目选项列表
     *
     * @param psycOptions 题目选项
     * @return 题目选项
     */
    @Override
    public List<PsycOptions> selectPsycOptionsList(PsycOptions psycOptions)
    {
        return psycOptionsMapper.selectPsycOptionsList(psycOptions);
    }

    /**
     * 新增题目选项
     *
     * @param psycOptions 题目选项
     * @return 结果
     */
    @Override
    public int insertPsycOptions(PsycOptions psycOptions)
    {
        return psycOptionsMapper.insertPsycOptions(psycOptions);
    }

    /**
     * 修改题目选项
     *
     * @param psycOptions 题目选项
     * @return 结果
     */
    @Override
    public int updatePsycOptions(PsycOptions psycOptions)
    {
        return psycOptionsMapper.updatePsycOptions(psycOptions);
    }

    /**
     * 批量删除题目选项
     *
     * @param ids 需要删除的题目选项主键
     * @return 结果
     */
    @Override
    public int deletePsycOptionsByIds(Long[] ids)
    {
        return psycOptionsMapper.deletePsycOptionsByIds(ids);
    }

    /**
     * 删除题目选项信息
     *
     * @param id 题目选项主键
     * @return 结果
     */
    @Override
    public int deletePsycOptionsById(Long id)
    {
        return psycOptionsMapper.deletePsycOptionsById(id);
    }
}
