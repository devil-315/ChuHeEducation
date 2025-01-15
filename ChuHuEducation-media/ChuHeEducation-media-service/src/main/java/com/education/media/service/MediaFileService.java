package com.education.media.service;

import com.education.base.model.PageParams;
import com.education.base.model.PageResult;
import com.education.media.model.dto.QueryMediaParamsDto;
import com.education.media.model.dto.UploadFileParamsDto;
import com.education.media.model.dto.UploadFileResultDto;
import com.education.media.model.po.MediaFiles;
import com.education.media.model.po.RestResponse;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

  /**
   * @description 媒资文件查询方法
   * @param pageParams 分页参数
   * @param queryMediaParamsDto 查询条件
   * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
   * @author Mr.M
   * @date 2022/9/10 8:57
  */
  public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

  /**
   * 上传文件
   * @param companyId 机构id
   * @param uploadFileParamsDto 上传文件信息
   * @param localFilePath 文件磁盘路径
   * @return 文件信息
   */
  public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

  /**
   * @description 将文件信息添加到文件表
   * @param companyId  机构id
   * @param fileMd5  文件md5值
   * @param uploadFileParamsDto  上传文件的信息
   * @param bucket  桶
   * @param objectName 对象名称
   * @return com.education.media.model.po.MediaFiles
   */

  public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);


  /**
   * @description 检查文件是否存在
   * @param fileMd5 文件的md5
   * @return com.education.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
   */
  public RestResponse<Boolean> checkFile(String fileMd5);

  /**
   * @description 检查分块是否存在
   * @param fileMd5  文件的md5
   * @param chunkIndex  分块序号
   * @return com.education.base.model.RestResponse<java.lang.Boolean> false不存在，true存在
   */
  public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

  /**
   * @description 上传分块
   * @param fileMd5  文件md5
   * @param chunk  分块序号
   * @param localChunkFilePath  分块文件本地路径
   */
  public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

  /**
   * @description 合并分块
   * @param companyId  机构id
   * @param fileMd5  文件md5
   * @param chunkTotal 分块总和
   * @param uploadFileParamsDto 文件信息
   */
  public RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
}
