package com.education.content;

import com.education.content.config.MultipartSupportConfig;
import com.education.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @description 测试使用feign远程上传文件
 */
@SpringBootTest
public class FeignUploadTest {

    @Autowired
    MediaServiceClient mediaServiceClient;

    //远程调用，上传文件
    @Test
    public void test() {

        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\environment\\Java\\javaProject\\Education\\test\\134.html"));
        String uploadFile = mediaServiceClient.uploadFile(multipartFile, "course/134.html");
        if (uploadFile == null){
            System.out.println("走了降级逻辑");
        }
    }

}