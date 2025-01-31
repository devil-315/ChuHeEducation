package com.education.media.service;

import com.education.media.model.po.MediaProcess;

import java.util.List;

/**
 * ClassName：MediaFileProcessService
 *
 * @author: Devil
 * @Date: 2025/1/15
 * @Description:媒资文件处理业务方法
 * @version: 1.0
 */
public interface MediaFileProcessService {
    /**
     * @description 获取待处理任务
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return java.util.List<com.education.media.model.po.MediaProcess>
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     *  开启一个任务
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);

    /**
     * @description 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     * @return void
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);

}
