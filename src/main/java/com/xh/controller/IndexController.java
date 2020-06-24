package com.xh.controller;

import com.xh.downloadFile.CrawlerUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    public void index() {
        for (int i = 0; i < 5; i++) {
            CrawlerUtils.downloadFileByWget("https://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz", "apache-maven-3.6.3-bin.tar.gz", "/home/zhuwenping");
        }
    }

}