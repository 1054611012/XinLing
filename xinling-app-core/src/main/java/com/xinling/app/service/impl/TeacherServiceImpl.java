package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.Teacher;
import com.xinling.app.mapper.TeacherMapper;
import com.xinling.app.service.ITeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 老师管理 Service 实现
 *
 * @author xinling
 */
@Service
public class TeacherServiceImpl implements ITeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private final TeacherMapper teacherMapper;

    public TeacherServiceImpl(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    @Override
    public Teacher getById(Long id) {
        return teacherMapper.selectById(id);
    }

    @Override
    public List<Teacher> getList(String keyword) {
        return teacherMapper.selectList(keyword);
    }

    @Override
    @Transactional
    public Teacher create(Teacher teacher) {
        teacherMapper.insert(teacher);
        log.info("新增老师: id={}, name={}", teacher.getId(), teacher.getName());
        return teacher;
    }

    @Override
    @Transactional
    public Teacher update(Long id, Teacher teacher) {
        Teacher exist = teacherMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("老师不存在: " + id);
        }
        teacher.setId(id);
        teacherMapper.updateById(teacher);
        return teacherMapper.selectById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Teacher exist = teacherMapper.selectById(id);
        if (exist == null) {
            throw new RuntimeException("老师不存在: " + id);
        }
        teacherMapper.deleteById(id);
        log.info("删除老师: id={}", id);
    }
}
