package com.education.content.api;

import com.education.content.model.dto.CourseCategoryTreeDto;
import com.education.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName：CourseCategoryController
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description:
 * @version: 1.0
 */
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
