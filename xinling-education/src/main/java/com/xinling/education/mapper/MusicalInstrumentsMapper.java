package com.xinling.education.mapper;

import java.util.List;
import com.xinling.education.domain.MusicalInstruments;

/**
 * 乐器信息Mapper接口
 *
 * @author xinling
 * @date 2025-07-31
 */
public interface MusicalInstrumentsMapper
{
    /**
     * 查询乐器信息
     *
     * @param id 乐器信息主键
     * @return 乐器信息
     */
    public MusicalInstruments selectMusicalInstrumentsById(String id);

    /**
     * 查询乐器信息列表
     *
     * @param musicalInstruments 乐器信息
     * @return 乐器信息集合
     */
    public List<MusicalInstruments> selectMusicalInstrumentsList(MusicalInstruments musicalInstruments);

    /**
     * 新增乐器信息
     *
     * @param musicalInstruments 乐器信息
     * @return 结果
     */
    public int insertMusicalInstruments(MusicalInstruments musicalInstruments);

    /**
     * 修改乐器信息
     *
     * @param musicalInstruments 乐器信息
     * @return 结果
     */
    public int updateMusicalInstruments(MusicalInstruments musicalInstruments);

    /**
     * 删除乐器信息
     *
     * @param id 乐器信息主键
     * @return 结果
     */
    public int deleteMusicalInstrumentsById(String id);

    /**
     * 批量删除乐器信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMusicalInstrumentsByIds(String[] ids);
}
