package com.xinling.psyc.service;

import java.util.List;
import com.xinling.psyc.domain.PsycPost;

/**
 * 动态管理Service接口
 *
 * @author xinling
 * @date 2025-10-30
 */
public interface IPsycPostService
{
    /**
     * 查询动态管理
     *
     * @param id 动态管理主键
     * @return 动态管理
     */
    public PsycPost selectPsycPostById(Long id);

    /**
     * 查询动态管理列表
     *
     * @param psycPost 动态管理
     * @return 动态管理集合
     */
    public List<PsycPost> selectPsycPostList(PsycPost psycPost);

    /**
     * 新增动态管理
     *
     * @param psycPost 动态管理
     * @return 结果
     */
    public int insertPsycPost(PsycPost psycPost);

    /**
     * 修改动态管理
     *
     * @param psycPost 动态管理
     * @return 结果
     */
    public int updatePsycPost(PsycPost psycPost);

    /**
     * 批量删除动态管理
     *
     * @param ids 需要删除的动态管理主键集合
     * @return 结果
     */
    public int deletePsycPostByIds(Long[] ids);

    /**
     * 删除动态管理信息
     *
     * @param id 动态管理主键
     * @return 结果
     */
    public int deletePsycPostById(Long id);
}
