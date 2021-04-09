package com.changgou.file;

import com.changgou.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/1/29 15:59\
 */
@RestController
@CrossOrigin
public class FileController
{

    /***
     * 文件上传
     * @return
     */
    @PostMapping(value = "/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            //1. 创建图片文件对象(封装)
            //2. 调用工具类实现图片上传

            //String substring = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            FastDFSFile fastdfsfile = new FastDFSFile(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename())
            );
            String[] upload = FastDFSClient.upload(fastdfsfile);

            //  upload[0] group1
            //  upload[1] M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
            //3. 拼接图片的全路径返回

            // http://192.168.211.132:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg

            // http://192.168.211.132:8080  +
            return FastDFSClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
