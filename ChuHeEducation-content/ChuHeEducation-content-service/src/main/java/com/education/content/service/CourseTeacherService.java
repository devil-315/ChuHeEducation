package com.education.content.service;

import com.education.content.model.po.CourseTeacher;

import java.util.List;

/**
 * ClassName：CourseTeacherService
 *
 * @author: Devil
 * @Date: 2025/1/13
 * @Description:
 * @version: 1.0
 */
public interface CourseTeacherService {
    /**
     * 根据课程id查询老师
     * @param courseId
     * @return
     */
    List<CourseTeacher> findAllcourseTeacher(Long courseId);

    /**
     * 添加与修改教师
     * @param courseTeacher
     * @return
     */
    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除老师
     * @param courseId
     * @param courseTeacherId
     */
    void deleteCourseTeacher(Long courseId, Long courseTeacherId);
}
