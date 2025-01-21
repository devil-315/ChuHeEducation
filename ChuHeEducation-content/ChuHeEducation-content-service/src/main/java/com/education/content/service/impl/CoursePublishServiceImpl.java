package com.education.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.education.base.exception.ChuHeEducationException;
import com.education.base.exception.CommonError;
import com.education.content.config.MultipartSupportConfig;
import com.education.content.feignclient.MediaServiceClient;
import com.education.content.mapper.CourseBaseMapper;
import com.education.content.mapper.CourseMarketMapper;
import com.education.content.mapper.CoursePublishMapper;
import com.education.content.mapper.CoursePublishPreMapper;
import com.education.content.model.dto.CourseBaseInfoDto;
import com.education.content.model.dto.CoursePreviewDto;
import com.education.content.model.dto.TeachplanDto;
import com.education.content.model.po.*;
import com.education.content.service.CourseBaseInfoService;
import com.education.content.service.CoursePublishService;
import com.education.content.service.CourseTeacherService;
import com.education.content.service.TeachplanService;
import com.education.messagesdk.model.po.MqMessage;
import com.education.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName：CoursePublishServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/19
 * @Description:
 * @version: 1.0
 */
@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseTeacherService courseTeacherService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //课程计划信息
        List<TeachplanDto> teachplanTree= teachplanService.findTeachplanTree(courseId);

        //师资信息
        List<CourseTeacher> allcourseTeacher = courseTeacherService.findAllcourseTeacher(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        coursePreviewDto.setTeachers(allcourseTeacher);
        return coursePreviewDto;
    }

    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null){
            ChuHeEducationException.cast("课程不存在");
        }
        //审核状态
        String auditStatus = courseBaseInfo.getAuditStatus();
        //如果课程的审核状态为为已提交则不允许提交
        if (auditStatus.equals("202003")){
            ChuHeEducationException.cast("课程已提交等待审核");
        }
        //TODO 本机构只能提交本机构的课程

        //课程的图片、计划信息等没有填写也不允许提交
        String pic = courseBaseInfo.getPic();
        if (StringUtils.isEmpty(pic)){
            ChuHeEducationException.cast("请求上传课程图片");
        }
        //查询课程计划
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null||teachplanTree.size() == 0){
            ChuHeEducationException.cast("请编写课程计划");
        }
        //查询课程基本信息、营销信息、计划等信息插入到课程预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //设置机构的id
        coursePublishPre.setCompanyId(companyId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转json
        String s = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(s);
        //老师信息
        List<CourseTeacher> allcourseTeacher = courseTeacherService.findAllcourseTeacher(courseId);
        //转json
        String teacher = JSON.toJSONString(allcourseTeacher);
        coursePublishPre.setTeachers(teacher);
        //计划信息
        //转json
        String teachplan = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplan);
        //状态为已提交
        coursePublishPre.setStatus("202003");
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //查询预发布表，如果有记录则更新，没有就插入
        CoursePublishPre coursePublishPreObj = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreObj == null){
            //插入
            coursePublishPreMapper.insert(coursePublishPre);
        }else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //变更课程基本信息表的审核状态为已提交
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * @description 课程发布接口
     * @param companyId 机构id
     * @param courseId 课程id
     * @return void
     */
    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        //查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null){
            ChuHeEducationException.cast("课程没有审核记录，无法发布");
        }
        //本机构只允许提交本机构的课程
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            ChuHeEducationException.cast("不允许提交其它机构的课程。");
        }
        //状态
        String status = coursePublishPre.getStatus();
        //课程审核不通过不允许发布
        if (!status.equals("202004")){
            ChuHeEducationException.cast("课程没有审核通过不允许发布");
        }

        //向课程发布表写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //先查询课程发布表，如果有就更新，没有就插入
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if (coursePublishObj == null){
            coursePublishMapper.insert(coursePublish);
        }else {
            coursePublishMapper.updateById(coursePublish);
        }

        //TODO 向消息表写入数据
        saveCoursePublishMessage(courseId);
        //将预发布表数据删除
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * @description 课程静态化
     * @param courseId  课程id
     * @return File 静态化文件
     */
    @Override
    public File generateCourseHtml(Long courseId) {
        //静态化文件
        File htmlFile  = null;

        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            //指定模板目录
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            //            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}",e.toString());
            ChuHeEducationException.cast("课程静态化异常");
        }

        return htmlFile;
    }

    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     * @return void
     */
    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        //将file转为MultipartFile
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        //远程调用得到返回值
        String course = mediaServiceClient.uploadFile(multipartFile, "course/"+courseId+".html");
        if(course==null){
            log.error("远程调用走降级逻辑得到上传结果为null,课程id:{}",courseId);
            ChuHeEducationException.cast("上传静态文件异常");
        }
    }


    /**
     * @description 保存消息表记录
     * @param courseId  课程id
     * @return void
     */
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage==null){
            ChuHeEducationException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
