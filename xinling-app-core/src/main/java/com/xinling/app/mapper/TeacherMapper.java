package com.xinling.app.mapper;

import com.xinling.app.domain.entity.Teacher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 老师 Mapper
 *
 * @author xinling
 */
public interface TeacherMapper {
    Teacher selectById(@Param("id") Long id);
    List<Teacher> selectList(@Param("keyword") String keyword);
    int insert(Teacher teacher);
    int updateById(Teacher teacher);
    int deleteById(@Param("id") Long id);
}
