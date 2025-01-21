package com.education;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ClassName：ContentApplication
 *
 * @author: Devil
 * @Date: 2025/1/11
 * @Description:
 * @version: 1.0
 */
@EnableFeignClients(basePackages={"com.education.content.feignclient"})
@EnableSwagger2Doc
@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class,args);
    }
}
