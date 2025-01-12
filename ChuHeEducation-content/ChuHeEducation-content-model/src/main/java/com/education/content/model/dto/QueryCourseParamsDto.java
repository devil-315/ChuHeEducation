package com.education.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * ClassName：QueryCourseParamsDto
 *
 * @author: Devil
 * @Date: 2025/1/11
 * @Description:课程查询条件模型
 * @version: 1.0
 */
@Data
@ToString
public class QueryCourseParamsDto {
    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
