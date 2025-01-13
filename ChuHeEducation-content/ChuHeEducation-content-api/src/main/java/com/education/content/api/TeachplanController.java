package com.education.content.api;

import com.education.content.model.dto.SaveTeachplanDto;
import com.education.content.model.dto.TeachplanDto;
import com.education.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName：TeachplanController
 *
 * @author: Devil
 * @Date: 2025/1/13
 * @Description:课程计划管理相关的接口
 * @version: 1.0
 */
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        return teachplanTree;
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto){
          teachplanService.saveTeachplan(saveTeachplanDto);
    }

    @ApiOperation("课程计划删除")
    @DeleteMapping("/teachplan/{teachplanId}")
    public void deleteTeachplan(@PathVariable Long teachplanId){
            teachplanService.deleteTeachplan(teachplanId);
    }

    @ApiOperation("课程计划排序")
    @PostMapping("/teachplan/{move}/{teachplanId}")
    public void moveTeachplan(@PathVariable String move , @PathVariable Long teachplanId){
        if(move.equals("moveup")){
            teachplanService.moveupTeachplan(teachplanId);
        }else if(move.equals("movedown")){
            teachplanService.movedownTeachplan(teachplanId);
        }
    }
}
