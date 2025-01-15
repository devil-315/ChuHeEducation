package com.education.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * ClassName：BigFileTest
 *
 * @author: Devil
 * @Date: 2025/1/15
 * @Description:
 * @version: 1.0
 */
public class BigFileTest {
    //测试文件分块方法
    @Test
    public void testChunk() throws Exception{
        //源文件
        File sourceFile = new File("C:\\Users\\Lenovo\\Videos\\1.mp4");
        //分开文件存储路径
        String chunkPath = "C:\\Users\\Lenovo\\Videos\\chunk\\";
        //分开文件大小
        int chunkSize = 1024 * 1024 * 5;
        //分块文件数量
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        //使用流从源文件读数据，向分块文件写数据
        RandomAccessFile raf_r = new RandomAccessFile(sourceFile, "r");
        //缓存区
        byte[] bytes = new byte[1024];
        for (int i = 0;i < chunkNum;i++){
            //创建分块文件
            File file = new File(chunkPath + i);
            //分块文件写入流
            RandomAccessFile raf_rw = new RandomAccessFile(file, "rw");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1){
                raf_rw.write(bytes,0,len);
                if (file.length() >= chunkSize){
                    break;
                }
            }
            raf_rw.close();
            System.out.println("完成分块"+i);
        }
        raf_r.close();
    }

    //测试文件合并方法
    @Test
    public void testMerge() throws Exception{
        //分块文件目录
        File chunkFolder = new File("C:\\Users\\Lenovo\\Videos\\chunk");
        //原始文件
        File originalFile = new File("C:\\Users\\Lenovo\\Videos\\1.mp4");
        //合并后文件
        File mergeFile = new File("C:\\Users\\Lenovo\\Videos\\2.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //向合并文件写的流
        RandomAccessFile raf_rw = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_rw.seek(0);
        //缓存区
        byte[] bytes = new byte[1024];
        //取出所有分块文件
        File[] files = chunkFolder.listFiles();
        //将数组转为list
        List<File> fileList = Arrays.asList(files);

        //对分块文件升序排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        //遍历分块文件，向合并的文件写
        for (File file: fileList){
            //读分块的流
            RandomAccessFile raf_r = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_r.read(bytes)) != -1){
                raf_rw.write(bytes,0,len);
            }
            raf_r.close();
        }
        raf_rw.close();
        //合并完成后对合并的文件校验
        FileInputStream fileInputStream_merge = new FileInputStream(mergeFile);
        FileInputStream fileInputStream_original = new FileInputStream(originalFile);
        String s1 = DigestUtils.md5Hex(fileInputStream_merge);
        String s2 = DigestUtils.md5Hex(fileInputStream_original);
        if (s2.equals(s1)) {
            System.out.println("合并文件成功");
        } else {
            System.out.println("合并文件失败");
        }
    }
}
