package com.education.content.model.dto;

import com.education.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName：CourseCategoryTreeDto
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description: 课程分类树型结点dto
 * @version: 1.0
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //子节点
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
