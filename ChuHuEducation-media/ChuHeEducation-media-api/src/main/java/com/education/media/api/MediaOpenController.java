package com.education.media.api;

import com.education.base.exception.ChuHeEducationException;
import com.education.base.model.RestResponse;
import com.education.media.model.po.MediaFiles;
import com.education.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
@RequestMapping("/open")
public class MediaOpenController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
            ChuHeEducationException.cast("视频还没有转码处理");
        }
        String url ="/video/" + mediaFiles.getUrl();
        int i = url.lastIndexOf(".");
        url = url.substring(0, i);
        String newUrl = url + "." + "avi";
        return RestResponse.success(newUrl);

    }

}