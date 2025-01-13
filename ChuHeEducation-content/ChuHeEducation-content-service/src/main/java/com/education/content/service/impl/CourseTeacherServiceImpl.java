package com.education.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.education.base.exception.ChuHeEducationException;
import com.education.content.mapper.CourseTeacherMapper;
import com.education.content.model.po.CourseTeacher;
import com.education.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName：CourseTeacherServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/13
 * @Description:
 * @version: 1.0
 */
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<CourseTeacher> findAllcourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        //有id为修改，没id为添加
        Long id = courseTeacher.getId();
        if(id == null){
            int insert = courseTeacherMapper.insert(courseTeacher);
            if(insert < 0){
                ChuHeEducationException.cast("添加教师失败");
            }
        }else {
            int i = courseTeacherMapper.updateById(courseTeacher);
            if(i < 0){
                ChuHeEducationException.cast("修改教师失败");
            }
        }
        return courseTeacher;
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long courseTeacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId)
                .eq(CourseTeacher::getId,courseTeacherId);
        int i = courseTeacherMapper.delete(queryWrapper);
        if (i < 0){
            ChuHeEducationException.cast("删除老师失败");
        }
    }
}
