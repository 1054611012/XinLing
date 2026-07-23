package com.xinling.app.service;

import com.xinling.app.domain.entity.Teacher;

import java.util.List;

/**
 * 老师管理 Service 接口
 *
 * @author xinling
 */
public interface ITeacherService {

    /**
     * 获取老师详情
     */
    Teacher getById(Long id);

    /**
     * 查询老师列表
     */
    List<Teacher> getList(String keyword);

    /**
     * 新增老师
     */
    Teacher create(Teacher teacher);

    /**
     * 修改老师
     */
    Teacher update(Long id, Teacher teacher);

    /**
     * 删除老师
     */
    void delete(Long id);
}
