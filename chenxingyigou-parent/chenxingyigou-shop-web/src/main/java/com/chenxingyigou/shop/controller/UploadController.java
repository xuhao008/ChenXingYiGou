package com.chenxingyigou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        String filename = file.getOriginalFilename();//获取文件名
        System.out.println("获取文件名"+filename);
        String extName=filename.substring(filename.lastIndexOf(".")+1);//得到扩展名
        System.out.println("得到扩展名"+extName);
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String file1 = client.uploadFile(file.getBytes(), extName);
            String url=FILE_SERVER_URL+file1;//图片完整地址
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败！");
        }

    }
}
