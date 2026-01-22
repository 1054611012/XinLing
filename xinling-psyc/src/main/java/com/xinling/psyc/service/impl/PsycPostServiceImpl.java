package com.xinling.psyc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.xinling.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xinling.psyc.domain.PsycPostMedia;
import com.xinling.psyc.mapper.PsycPostMapper;
import com.xinling.psyc.domain.PsycPost;
import com.xinling.psyc.service.IPsycPostService;

/**
 * 动态管理Service业务层处理
 *
 * @author xinling
 * @date 2025-10-30
 */
@Service
public class PsycPostServiceImpl implements IPsycPostService
{
    @Autowired
    private PsycPostMapper psycPostMapper;

    /**
     * 查询动态管理
     *
     * @param id 动态管理主键
     * @return 动态管理
     */
    @Override
    public PsycPost selectPsycPostById(Long id)
    {
        return psycPostMapper.selectPsycPostById(id);
    }

    /**
     * 查询动态管理列表
     *
     * @param psycPost 动态管理
     * @return 动态管理
     */
    @Override
    public List<PsycPost> selectPsycPostList(PsycPost psycPost)
    {
        return psycPostMapper.selectPsycPostList(psycPost);
    }

    /**
     * 新增动态管理
     *
     * @param psycPost 动态管理
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPsycPost(PsycPost psycPost)
    {
        int rows = psycPostMapper.insertPsycPost(psycPost);
        insertPsycPostMedia(psycPost);
        return rows;
    }

    /**
     * 修改动态管理
     *
     * @param psycPost 动态管理
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePsycPost(PsycPost psycPost)
    {
        psycPostMapper.deletePsycPostMediaByPostId(psycPost.getId());
        insertPsycPostMedia(psycPost);
        return psycPostMapper.updatePsycPost(psycPost);
    }

    /**
     * 批量删除动态管理
     *
     * @param ids 需要删除的动态管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycPostByIds(Long[] ids)
    {
        psycPostMapper.deletePsycPostMediaByPostIds(ids);
        return psycPostMapper.deletePsycPostByIds(ids);
    }

    /**
     * 删除动态管理信息
     *
     * @param id 动态管理主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePsycPostById(Long id)
    {
        psycPostMapper.deletePsycPostMediaByPostId(id);
        return psycPostMapper.deletePsycPostById(id);
    }

    /**
     * 新增动态媒体资源信息
     *
     * @param psycPost 动态管理对象
     */
    public void insertPsycPostMedia(PsycPost psycPost)
    {
        List<PsycPostMedia> psycPostMediaList = psycPost.getPsycPostMediaList();
        Long id = psycPost.getId();
        if (StringUtils.isNotNull(psycPostMediaList))
        {
            List<PsycPostMedia> list = new ArrayList<PsycPostMedia>();
            for (PsycPostMedia psycPostMedia : psycPostMediaList)
            {
                psycPostMedia.setPostId(id);
                list.add(psycPostMedia);
            }
            if (list.size() > 0)
            {
                psycPostMapper.batchPsycPostMedia(list);
            }
        }
    }
}
