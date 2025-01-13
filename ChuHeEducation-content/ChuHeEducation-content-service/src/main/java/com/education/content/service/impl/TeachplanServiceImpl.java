package com.education.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.education.base.exception.ChuHeEducationException;
import com.education.content.mapper.TeachplanMapper;
import com.education.content.mapper.TeachplanMediaMapper;
import com.education.content.model.dto.SaveTeachplanDto;
import com.education.content.model.dto.TeachplanDto;
import com.education.content.model.po.Teachplan;
import com.education.content.model.po.TeachplanMedia;
import com.education.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName：TeachplanServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/13
 * @Description:
 * @version: 1.0
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        //通过课程计划的id来确实是新增还是保存
        Long id = saveTeachplanDto.getId();
        if(id == null){
            //新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            //确实排序字段，找到同级节点的个数，排序字段就是个数+1
            int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
            //设置排序号
            teachplan.setOrderby(count + 1);
            teachplanMapper.insert(teachplan);
        }else {
            //修改
            Teachplan teachplan = teachplanMapper.selectById(id);
            //复制参数
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Override
    public void deleteTeachplan(Long teachplanId) {
        //判断是否是大章节，如果是，要大章节下面没有小章节再删除
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        Long parentid = teachplan.getParentid();
        if(parentid == 0){
            //大章节
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid,teachplanId);
            Integer count = teachplanMapper.selectCount(queryWrapper);
            if(count > 0){
                ChuHeEducationException.cast("课程计划信息还有子信息，无法删除");
            }else {
                teachplanMapper.deleteById(teachplanId);
                //同时将teachplan_media表关联的信息也删除
                LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TeachplanMedia::getTeachplanId,teachplanId);
                teachplanMediaMapper.delete(wrapper);

            }
        }else {
            //小章节，直接删
            teachplanMapper.deleteById(teachplanId);
            //同时将teachplan_media表关联的信息也删除
            LambdaQueryWrapper<TeachplanMedia> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TeachplanMedia::getTeachplanId,teachplanId);
            teachplanMediaMapper.delete(wrapper);
        }
    }

    @Override
    public void moveupTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //查询具有相同父id和课程id的课程计划，并按照order by排序
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,teachplan.getParentid())
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .orderByDesc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        //找到当前课程计划在列表中的索引
        int index = teachplans.indexOf(teachplan);
        if(index == -1 || index == 0){
            ChuHeEducationException.cast("当前课程位置无法上移");
        }
        //获取上一个课程
        Teachplan teachplan2 = teachplans.get(index - 1);
        //交换当前课程和上一个课程
        Integer orderby1 = teachplan.getOrderby();
        Integer orderby2 = teachplan2.getOrderby();
        teachplan.setOrderby(orderby2);
        teachplan2.setOrderby(orderby1);

        //更新数据库
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplan2);

    }

    @Override
    public void movedownTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //查询具有相同父id和课程id的课程计划，并按照order by排序
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,teachplan.getParentid())
                .eq(Teachplan::getCourseId,teachplan.getCourseId())
                .orderByDesc(Teachplan::getOrderby);
        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);
        //找到当前课程计划在列表中的索引
        int index = teachplans.indexOf(teachplan);
        if(index == -1 || index == teachplans.size() - 1){
            ChuHeEducationException.cast("当前课程位置无法下移");
        }
        //获取下一个课程
        Teachplan teachplan2 = teachplans.get(index + 1);
        //交换当前课程和下一个课程
        Integer orderby1 = teachplan.getOrderby();
        Integer orderby2 = teachplan2.getOrderby();
        teachplan.setOrderby(orderby2);
        teachplan2.setOrderby(orderby1);

        //更新数据库
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplan2);
    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<>();
        //select (1) from teachplan where course_id = ? and parentid =?
        wrapper.eq(Teachplan::getCourseId,courseId)
                .eq(Teachplan::getParentid,parentId);
        Integer integer = teachplanMapper.selectCount(wrapper);
        return integer;
    }
}
