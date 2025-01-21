package com.education.content.service.jobhandler;

import com.education.base.exception.ChuHeEducationException;
import com.education.content.feignclient.CourseIndex;
import com.education.content.feignclient.SearchServiceClient;
import com.education.content.mapper.CoursePublishMapper;
import com.education.content.model.po.CoursePublish;
import com.education.content.service.CoursePublishService;
import com.education.messagesdk.mapper.MqMessageHistoryMapper;
import com.education.messagesdk.mapper.MqMessageMapper;
import com.education.messagesdk.model.po.MqMessage;
import com.education.messagesdk.model.po.MqMessageHistory;
import com.education.messagesdk.service.MessageProcessAbstract;
import com.education.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * ClassName：CoursePublishTask
 *
 * @author: Devil
 * @Date: 2025/1/19
 * @Description:
 * @version: 1.0
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    MqMessageMapper mqMessageMapper;

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    SearchServiceClient searchServiceClient;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageHistoryMapper mqMessageHistoryMapper;
    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    //执行课程发布的逻辑  如果此方法抛出异常，说明任务失败
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage拿到课程id
        Long courseId =Long.parseLong(mqMessage.getBusinessKey1());

        //课程镜头和上传到minio
        generateCourseHtml(mqMessage,courseId);

        //向elasticsearch写索引数据
        saveCourseIndex(mqMessage,courseId);

        //向redis写缓存
        saveCourseCache(mqMessage,courseId);

        //消息表删除记录
        mqMessageMapper.deleteById(mqMessage.getId());

        //消息历史表插入记录
        MqMessageHistory mqMessageHistory = new MqMessageHistory();
        BeanUtils.copyProperties(mqMessage,mqMessageHistory);
        try {
            mqMessageHistoryMapper.insert(mqMessageHistory);
        } catch (Exception e) {
            log.error("消息历史表已插入该记录");
        }
        //返回true表示任务完成
        return false;
    }

    //生成课程静态化页面并上传至文件系统
    public void generateCourseHtml(MqMessage mqMessage,long courseId){
        MqMessageService mqMessageService = this.getMqMessageService();
        //消息id
        Long taskId = mqMessage.getId();

        //做任务幂等性处理
        //取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0){
            log.debug("课程静态化任务完成，无需处理...");
            return;
        }

        //开始进行课程静态化  生成html页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null){
            ChuHeEducationException.cast("生成的静态文件为空");
        }
        //将html页面上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);

        //任务处理写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }


    //保存课程索引信息  第二阶段任务
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        MqMessageService mqMessageService = this.getMqMessageService();
        //任务id
        Long taskId = mqMessage.getId();

        //做任务幂等性处理
        //取出第二阶段执行状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0){
            log.debug("课程索引信息已写入，无需执行...");
            return;
        }

        //查询课程信息，调用搜索服务添加索引接口
        //从课程发布表查询课程信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //远程调用
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add){
            ChuHeEducationException.cast("远程调用添加索引失败");
        }


        //完成本阶段任务
        mqMessageService.completedStageTwo(taskId);
    }

    //将课程信息缓存至redis
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        MqMessageService mqMessageService = this.getMqMessageService();
        //任务id
        Long taskId = mqMessage.getId();

        //做任务幂等性处理
        int stageThree = mqMessageService.getStageThree(taskId);
        if (stageThree > 0){
            log.debug("课程索引信息已缓存至redis，无需执行...");
            return;
        }

        //将课程信息缓存至redis

        //完成本阶段任务
        mqMessageService.completedStageThree(taskId);

    }
}
