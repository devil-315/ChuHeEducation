package com.education.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName：FreemarkerController
 *
 * @author: Devil
 * @Date: 2025/1/16
 * @Description:
 * @version: 1.0
 */
@Controller
public class FreemarkerController {

    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("name","小明");
        //设置模板名称
        modelAndView.setViewName("test"); //后缀名已经在配置文件中设置
        return modelAndView;
    }

}
