package com.education.content.service;

import com.education.base.model.PageParams;
import com.education.base.model.PageResult;
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
}
