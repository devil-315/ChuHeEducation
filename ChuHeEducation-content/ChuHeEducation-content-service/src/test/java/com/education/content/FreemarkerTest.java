package com.education.content;

import com.education.content.model.dto.CoursePreviewDto;
import com.education.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.ietf.jgss.Oid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * ClassName：FreemarkerTest
 *
 * @author: Devil
 * @Date: 2025/1/19
 * @Description:
 * @version: 1.0
 */
@SpringBootTest
public class FreemarkerTest {
    @Autowired
    CoursePublishService coursePublishService;
    //测试页面静态化
    @Test
    public void testGenerateHtmlByTemplate() throws Exception{
        Configuration configuration = new Configuration(Configuration.getVersion());
        //拿到classpath路径
        String path = this.getClass().getResource("/").getPath();
        //指定模板的目录
        configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
        //指定编码
        configuration.setDefaultEncoding("utf-8");
        //得到模板
        Template template = configuration.getTemplate("course_template.ftl");
        //准备数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(134L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model",coursePreviewInfo);

        //静态化
        //参数1：模板，参数2：数据模型
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        //输入类
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        //输出文件
        FileOutputStream outputStream = new FileOutputStream("D:\\environment\\Java\\javaProject\\Education\\test\\134.html");
        //使用html流写入文件
        IOUtils.copy(inputStream,outputStream);
    }
}
