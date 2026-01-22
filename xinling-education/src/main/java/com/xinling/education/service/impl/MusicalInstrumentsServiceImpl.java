package com.xinling.education.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xinling.education.mapper.MusicalInstrumentsMapper;
import com.xinling.education.domain.MusicalInstruments;
import com.xinling.education.service.IMusicalInstrumentsService;

/**
 * 乐器信息Service业务层处理
 * 
 * @author xinling
 * @date 2025-07-31
 */
@Service
public class MusicalInstrumentsServiceImpl implements IMusicalInstrumentsService 
{
    @Autowired
    private MusicalInstrumentsMapper musicalInstrumentsMapper;

    /**
     * 查询乐器信息
     * 
     * @param id 乐器信息主键
     * @return 乐器信息
     */
    @Override
    public MusicalInstruments selectMusicalInstrumentsById(String id)
    {
        return musicalInstrumentsMapper.selectMusicalInstrumentsById(id);
    }

    /**
     * 查询乐器信息列表
     * 
     * @param musicalInstruments 乐器信息
     * @return 乐器信息
     */
    @Override
    public List<MusicalInstruments> selectMusicalInstrumentsList(MusicalInstruments musicalInstruments)
    {
        return musicalInstrumentsMapper.selectMusicalInstrumentsList(musicalInstruments);
    }

    /**
     * 新增乐器信息
     * 
     * @param musicalInstruments 乐器信息
     * @return 结果
     */
    @Override
    public int insertMusicalInstruments(MusicalInstruments musicalInstruments)
    {
        return musicalInstrumentsMapper.insertMusicalInstruments(musicalInstruments);
    }

    /**
     * 修改乐器信息
     * 
     * @param musicalInstruments 乐器信息
     * @return 结果
     */
    @Override
    public int updateMusicalInstruments(MusicalInstruments musicalInstruments)
    {
        return musicalInstrumentsMapper.updateMusicalInstruments(musicalInstruments);
    }

    /**
     * 批量删除乐器信息
     * 
     * @param ids 需要删除的乐器信息主键
     * @return 结果
     */
    @Override
    public int deleteMusicalInstrumentsByIds(String[] ids)
    {
        return musicalInstrumentsMapper.deleteMusicalInstrumentsByIds(ids);
    }

    /**
     * 删除乐器信息信息
     * 
     * @param id 乐器信息主键
     * @return 结果
     */
    @Override
    public int deleteMusicalInstrumentsById(String id)
    {
        return musicalInstrumentsMapper.deleteMusicalInstrumentsById(id);
    }
}
