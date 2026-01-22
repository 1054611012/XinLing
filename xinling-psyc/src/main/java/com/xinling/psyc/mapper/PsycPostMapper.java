package com.xinling.psyc.mapper;

import java.util.List;
import com.xinling.psyc.domain.PsycPost;
import com.xinling.psyc.domain.PsycPostMedia;

/**
 * 动态管理Mapper接口
 *
 * @author xinling
 * @date 2025-10-30
 */
public interface PsycPostMapper
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
     * 删除动态管理
     *
     * @param id 动态管理主键
     * @return 结果
     */
    public int deletePsycPostById(Long id);

    /**
     * 批量删除动态管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycPostByIds(Long[] ids);

    /**
     * 批量删除动态媒体资源
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePsycPostMediaByPostIds(Long[] ids);

    /**
     * 批量新增动态媒体资源
     *
     * @param psycPostMediaList 动态媒体资源列表
     * @return 结果
     */
    public int batchPsycPostMedia(List<PsycPostMedia> psycPostMediaList);


    /**
     * 通过动态管理主键删除动态媒体资源信息
     *
     * @param id 动态管理ID
     * @return 结果
     */
    public int deletePsycPostMediaByPostId(Long id);
}
