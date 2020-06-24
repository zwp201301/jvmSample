package com.xh.downloadFile;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 天草二十六
 */
@Slf4j
public class CrawlerUtils {

    /**
     * 使用wget下载文件
     *
     * @param download_url 下载地址
     * @param savePath     保存路径
     * @param fileName     文件名
     * @return 成功返回文件路径，失败返回null
     */
    public static String downloadFileByWget(String download_url, String fileName, String savePath) {
        String filePath = savePath + "/" + fileName;
        File file = new File(filePath);
        try {
            Files.createParentDirs(file);
        } catch (IOException e) {
            return null;
        }
        int retry = 2;
        int res = -1;
        int time = 1;
        while (retry-- > 0) {
            ProcessBuilder pb = new ProcessBuilder("wget", download_url, "-t", "40", "-T", "300", "-c", "-N", "-O", filePath);
            log.info("wget shell: {}", pb.command());
            Process ps = null;
            try {
                ps = pb.start();
            } catch (IOException e) {
                log.error("下载文件失败,[url={}]", download_url, e);
            }

            res = doWaitFor(ps, 30 * time++);
            if (res != 0) {
                log.warn("wget工具下载失败[url={}]", download_url);
            } else {
                break;
            }
        }
        if (res != 0) {
            return null;
        }
        log.info("下载成功[url={}\tfilename={}]", download_url, filePath);
        return filePath;
    }

    /**
     * @param ps      sub process
     * @param timeout 超时时间，SECONDS
     * @return 正常结束返回0
     */
    private static int doWaitFor(Process ps, int timeout) {
        int res = -1;
        if (ps == null) {
            return res;
        }
        List<String> stdoutList = new ArrayList<String>();
        List<String> erroroutList = new ArrayList<String>();
        boolean finished = false;
        int time = 0;
        ThreadUtil stdoutUtil = new ThreadUtil(ps.getInputStream(), stdoutList);
        ThreadUtil erroroutUtil = new ThreadUtil(ps.getErrorStream(), erroroutList);
        //启动线程读取缓冲区数据
        stdoutUtil.start();
        erroroutUtil.start();
        while (!finished) {
            time++;
            if (time >= timeout) {
                ps.destroy();
                break;
            }
            try {
                res = ps.exitValue();
                finished = true;
            } catch (IllegalThreadStateException e) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e1) {

                }
            }
        }
        return res;
    }
}
