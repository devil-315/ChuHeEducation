package com.education.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.education.base.exception.ChuHeEducationException;
import com.education.base.model.PageParams;
import com.education.base.model.PageResult;
import com.education.content.mapper.CourseBaseMapper;
import com.education.content.mapper.CourseCategoryMapper;
import com.education.content.mapper.CourseMarketMapper;
import com.education.content.model.dto.AddCourseDto;
import com.education.content.model.dto.CourseBaseInfoDto;
import com.education.content.model.dto.EditCourseDto;
import com.education.content.model.dto.QueryCourseParamsDto;
import com.education.content.model.po.CourseBase;
import com.education.content.model.po.CourseCategory;
import com.education.content.model.po.CourseMarket;
import com.education.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ClassName：CourseBaseInfoServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/11
 * @Description:课程信息管理业务接口实现类
 * @version: 1.0
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //拼装查询条件
        LambdaQueryWrapper<CourseBase> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        wrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        wrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        //开始查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, wrapper);

        //查询结果
        PageResult<CourseBase> result = new PageResult<>(courseBasePage.getRecords(), courseBasePage.getTotal(), pageParams.getPageNo(), pageParams.getPageSize());
        return result;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //参数的合法性校验
        /*if (StringUtils.isBlank(dto.getName())) {
//            throw new RuntimeException("课程名称为空");
            throw new ChuHeEducationException("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
//            throw new RuntimeException("课程大分类为空");
            throw new ChuHeEducationException("课程大分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程小分类为空");
            throw new ChuHeEducationException("课程小分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
            throw new ChuHeEducationException("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
            throw new ChuHeEducationException("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
            throw new ChuHeEducationException("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
            throw new ChuHeEducationException("收费规则为空");
        }*/

        //向课程基本信息表 course_base 保存课程信息
        CourseBase courseBaseNew = new CourseBase();
        //将传入页面的参数放到courseBaseNew对象
        BeanUtils.copyProperties(dto,courseBaseNew); //只要属性名称一致就可以拷贝
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //审核状态默认为未提交
        courseBaseNew.setAuditStatus("202002");
        //发布状态为未发布
        courseBaseNew.setStatus("203001");
        //插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0){
//            throw new RuntimeException("添加课程失败");
            throw new ChuHeEducationException("添加课程失败");
        }

        //向课程营销表 course_market 保存课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        //课程的id
        Long id = courseBaseNew.getId();
        courseMarketNew.setId(id);
        //保存营销信息
        saveCourseMarket(courseMarketNew);
        //从数据库查询课程的详细信息，包括两部分
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(id);
        return courseBaseInfo;
    }



    //保存营销信息，存在则更新，不存在则添加
    private int saveCourseMarket(CourseMarket courseMarket){
        //参数的合法性校验
        String charge = courseMarket.getCharge();
        if(StringUtils.isEmpty(charge)){
            throw new RuntimeException("收费规则异常");
        }
        //如果课程收费，价格异常也要抛出
        if(charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <=0 ){
                throw new RuntimeException("课程为收费价格不能为空且必须大于0");
            }
        }
        //数据库查询营销信息，存在则更新，不存在则添加
        Long id = courseMarket.getId();
        CourseMarket market = courseMarketMapper.selectById(id);
        if(market == null){
            //插入数据库
            int insert = courseMarketMapper.insert(courseMarket);
            return insert;
        }else {
            //拷贝到查出的对象上
            BeanUtils.copyProperties(courseMarket,market);
            market.setId(courseMarket.getId());
            //更新
            int i = courseMarketMapper.updateById(market);
            return i;
        }
    }

    @Override
    //根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        //从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }

        //从课程营销标查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);


        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        //组装到一起
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //课程id
        Long id = editCourseDto.getId();
        //查询课程信息并校验
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if(courseBase == null){
            ChuHeEducationException.cast("课程不存在");
        }
        //数据合法性校验
        //根据具体的业务逻辑去校验
        //本机构只能修改本机构的课程
        if(!companyId.equals(courseBase.getCompanyId())){
            ChuHeEducationException.cast("本机构只能修改本机构的课程");
        }
        //封装数据
        BeanUtils.copyProperties(editCourseDto,courseBase);
        //修改时间
        courseBase.setChangeDate(LocalDateTime.now());

        //更新数据库
        int i = courseBaseMapper.updateById(courseBase);
        if(i <= 0){
            ChuHeEducationException.cast("修改课程失败");
        }
        //查询课程信息
        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(id);
        return courseBaseInfo;
    }
}
