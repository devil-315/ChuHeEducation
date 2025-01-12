package com.education.content.service;

import com.education.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * ClassName：CourseCategoryService
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description:
 * @version: 1.0
 */
public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
