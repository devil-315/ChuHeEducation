package com.education.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.base.model.PageParams;
import com.education.base.model.PageResult;
import com.education.content.mapper.CourseBaseMapper;
import com.education.content.mapper.TeachplanMapper;
import com.education.content.model.dto.QueryCourseParamsDto;
import com.education.content.model.dto.TeachplanDto;
import com.education.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
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
public class TeachplanMapperTests {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117L);
        System.out.println("查询结果：" + teachplanDtos);
    }
}