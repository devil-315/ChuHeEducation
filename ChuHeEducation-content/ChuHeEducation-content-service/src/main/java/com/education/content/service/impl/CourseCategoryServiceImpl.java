package com.education.content.service.impl;

import com.education.content.mapper.CourseCategoryMapper;
import com.education.content.model.dto.CourseCategoryTreeDto;
import com.education.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName：CourseCategoryServiceImpl
 *
 * @author: Devil
 * @Date: 2025/1/12
 * @Description:
 * @version: 1.0
 */
@Service
@Slf4j
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        //filter(item->!id.equals(item.getId()))表示在转stream流的时候过滤根节点，也就是忽略根节点，根节点不参与后面的转map
        //将list转map  转换后的map的key就是每条list数据的id、value就是每条list数据的本身  如果转换时map中已经同样的key，就以新key为主
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).collect(Collectors.toMap(list1 -> list1.getId(), list2 -> list2, (key1, key2) -> key2));
        //最终返回给前端的list结构
        List<CourseCategoryTreeDto> categoryTreeDtos = new ArrayList<>();
        //依次遍历每个元素(里面的数据除了根节点，其它都是合规的，可以返回给前端的，但是此时还没有区分出一级、二级、三级节点，所以下面会进行区分)
        courseCategoryTreeDtos.stream().filter(item->!id.equals(item.getId())).forEach(item->{//排除根节点
            //id是根节点(一级节点)
            if(item.getParentid().equals(id)){
                //存入二级节点
                categoryTreeDtos.add(item);
            }
            //找到当前节点的父节点是存在的(此时这个父节点就是二级节点，也就是下面为了存入三级节点或更多级节点)
            CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
            if(courseCategoryTreeDto!=null){
                //虽然当前父节点存在，但是可能父节点的属性为空
                if(courseCategoryTreeDto.getChildrenTreeNodes() ==null){
                    //如果父节点属性为空，为了避免它的子节点存不进该父节点，我们就需要先给当前这个父节点set进去相关属性
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                //存入子节点(我们这个项目最多只有三级节点，作用这里就是存入三级节点)
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        //把最终的整理好节点层次的数据返回给前端
        return categoryTreeDtos;
    }
}
