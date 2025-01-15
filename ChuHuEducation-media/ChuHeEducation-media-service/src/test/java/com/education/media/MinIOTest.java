package com.education.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ClassName：MinIOTest
 *
 * @author: Devil
 * @Date: 2025/1/14
 * @Description:
 * @version: 1.0
 */
public class MinIOTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    @Test
    public void testUpload() throws Exception{
        //通过扩展名得到媒体文件类型
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".jpg");
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; //通用mimeType 字节流
        if(extensionMatch != null){
            mimeType = extensionMatch.getMimeType();
        }
        //上传文件的参数信息
        UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                .bucket("testbucket")//桶
                .filename("C:\\Users\\Lenovo\\Pictures\\2.jpg")//指定本地文件路径
                .object("2.jpg") //对象名 在桶下存储该文件
//                .object("test/01/2,jpg") //在子目录下存储
                .contentType(mimeType)//设置媒体文件类型
                .build();

        //上传文件
        minioClient.uploadObject(uploadObjectArgs);
    }
    @Test
    public void delete() throws Exception{
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("2.jpg")
                .build();
        //删除文件
        minioClient.removeObject(removeObjectArgs);
    }
    @Test
    public void getFile() throws Exception{
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket("testbucket")
                .object("2.jpg")
                .build();

        //查询远程服务器获取的流
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);

        //指定输出流
        File file = new File("C:\\Users\\Lenovo\\Pictures\\3.jpg");
        FileOutputStream outputStream = new FileOutputStream(file);

        IOUtils.copy(inputStream,outputStream);

        //校验文件的完整性对文件的内容进行md5
//        String source_md5 = DigestUtils.md5Hex(inputStream);//不能传远程流，会有问题
        //本地文件流
        String source_md5 = DigestUtils.md5Hex(new FileInputStream(new File("C:\\Users\\Lenovo\\Pictures\\2.jpg")));
        String local_md5 = DigestUtils.md5Hex(new FileInputStream(file));
        if(source_md5.equals(local_md5)){
            System.out.println("下载成功");
        }

    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk() throws Exception{
        String chunkFolderPath = "C:\\Users\\Lenovo\\Videos\\chunk";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i) //对象名 放在子目录下
                        .filename(files[i].getAbsolutePath()) //本地文件路径
                        .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传分块成功"+i);
        }
    }

    //合并文件 要求分块文件最小5M
    //如果是最后一块可以小于5M
    @Test
    public void test_merge() throws Exception{
        //源文件 方法一
//        ArrayList<ComposeSource> sources = new ArrayList<>();
//        for (int i = 0;i < 1;i++){
//            //指定分块文件的信息
//            ComposeSource composeSource = ComposeSource.builder()
//                    .bucket("testbucket")
//                    .object("chunk/" + i)
//                    .build();
//            sources.add(composeSource);
//        }
        //方法二
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(1)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .build())
                .collect(Collectors.toList());

        //指定合并后的信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge01.mp4")
                .sources(sources)//指定源文件
                .build();
        //合并文件
        minioClient.composeObject(composeObjectArgs);
    }
}
