package com.education.content.service;

import com.education.base.model.PageParams;
import com.education.base.model.PageResult;
import com.education.content.model.dto.AddCourseDto;
import com.education.content.model.dto.CourseBaseInfoDto;
import com.education.content.model.dto.QueryCourseParamsDto;
import com.education.content.model.po.CourseBase;

/**
 * ClassName：CourseBaseInfoService
 *
 * @author: Devil
 * @Date: 2025/1/11
 * @Description:课程信息管理接口
 * @version: 1.0
 */
public interface CourseBaseInfoService {
    //课程分页查询
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @description 添加课程基本信息
     * @param companyId  教学机构id
     * @param addCourseDto  课程基本信息
     * @return CourseBaseInfoDto 课程详细信息
     */
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
}
