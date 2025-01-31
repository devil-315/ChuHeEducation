package com.education.content;

import com.education.content.mapper.CourseCategoryMapper;
import com.education.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


/**
 * ClassName：CourseBaseMapperTests
 *
 * @author: Devil
 * @Date: 2025/1/11
 * @Description:
 * @version: 1.0
 */
@SpringBootTest
public class CourseCategoryMapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryMapper(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println("查询结果： " + courseCategoryTreeDtos);

    }
}
