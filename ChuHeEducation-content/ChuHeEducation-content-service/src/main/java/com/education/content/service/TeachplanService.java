package com.education.content.service;

import com.education.content.model.dto.BindTeachplanMediaDto;
import com.education.content.model.dto.SaveTeachplanDto;
import com.education.content.model.dto.TeachplanDto;
import com.education.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * ClassName：TeachplanService
 *
 * @author: Devil
 * @Date: 2025/1/13
 * @Description:
 * @version: 1.0
 */
public interface TeachplanService {

    /**
     * 查询课程计划树型结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree (Long courseId);

    /***
     * 新增/修改/保存 课程计划
     * @param saveTeachplanDto
     */
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     */
    void deleteTeachplan(Long teachplanId);

    /**
     * 上移
     * @param teachplanId
     */
    void moveupTeachplan(Long teachplanId);

    /**
     * 下移
     * @param teachplanId
     */
    void movedownTeachplan(Long teachplanId);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     * @return com.education.content.model.po.TeachplanMedia
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * @description 教学计划解除绑定媒资
     * @param
     * @return
     */
    void deleteAssociationMedia(Long teachPlanId, String mediaId);

}
