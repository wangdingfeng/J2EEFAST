/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.oss.cloud;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import com.j2eefast.common.core.exception.RxcException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * FTP 数据存储
 * @author huanzhou
 */
@Slf4j
public class FTPCloudStorageService extends CloudStorageService {


    private Ftp ftp;

    public FTPCloudStorageService(CloudStorageConfig config){
        this.config = config;
        init();
    }

    private void init() {
        String[] ip = config.getQiniuDomain().split(":");
        ftp = new Ftp(ip[0], Integer.parseInt(ip[1]),
                config.getQiniuAccessKey(), config.getQiniuSecretKey());
    }


    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getAliyunurlPrefix(), suffix));
    }

    /**
     * 上传
     * @param inputStream 字节流
     * @param path        文件路径，包含文件名
     * @return
     */
    @Override
    public String upload(InputStream inputStream, String path) {
        path = FileUtil.normalize(StrUtil.nullToDefault(config.getQiniuBucketName(),StrUtil.EMPTY) + File.separator +path);
        ftp.upload(getPath(path),FileUtil.getName(path),inputStream);
        return path;
    }

    @Override
    public InputStream download(String path) {
        path = FileUtil.normalize(StrUtil.nullToDefault(config.getQiniuBucketName(),StrUtil.EMPTY) + File.separator +path);
        ByteArrayOutputStream bos  = new ByteArrayOutputStream();
        try{
            ftp.download(getPath(path),FileUtil.getName(path),bos);
        } catch (Exception e){
            throw new RxcException("下载文件失败，请检查配置信息", e);
        }finally {
            if(null != ftp) {
                try {
                    ftp.close();
                } catch(Exception e) {
                }
            }
        }
        ByteArrayInputStream fis = new ByteArrayInputStream(
                bos.toByteArray());
        return fis;
    }

    @Override
    public void delFileOss(String path) {
        try{
            ftp.delFile(path);
        }catch (Exception e){
            log.error("阿里云OSS删除失败!",e);
        }finally {
            if(null != ftp) {
                try {
                    ftp.close();
                } catch(Exception e) {
                }
            }
        }
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getAliyunurlPrefix(), suffix));
    }

    @Override
    public void shutdown() {
        if(null != ftp) {
            try {
                ftp.close();
            } catch(Exception e) {
            }
        }
    }

    public static String getPath(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (CharUtil.isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (CharUtil.isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(0, begin);
    }
}
