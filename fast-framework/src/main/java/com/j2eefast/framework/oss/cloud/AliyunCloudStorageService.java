/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.oss.cloud;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.j2eefast.common.core.exception.RxcException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云存储OSS
 * @author huanzhou
 */
@Slf4j
public class AliyunCloudStorageService extends CloudStorageService {

	private OSS ossClient;

	public AliyunCloudStorageService(CloudStorageConfig config) {
		this.config = config;
		// 初始化
		init();
	}

	private void init() {
		ossClient = new OSSClientBuilder().build(config.getAliyunEndPoint(),
				config.getAliyunAccessKeyId(), config.getAliyunAccessKeySecret());
	}

	@Override
	public String upload(byte[] data, String path) {
		return upload(new ByteArrayInputStream(data), path);
	}

	/**
	 * 上传
	 */
	@Override
	public String upload(InputStream inputStream, String path) {
		try {
			ossClient.putObject(config.getAliyunBucketName(), path, inputStream);
		} catch (Exception e) {
			throw new RxcException("上传文件失败，请检查配置信息", e);
		}finally {
			if(null != ossClient) {
				try {
					ossClient.shutdown();
				} catch(Exception e) {
				}
			}
		}
		return config.getAliyunDomain() + "/" + path;
	}

	@Override
	public InputStream download(String path) {
		InputStream input = null;
		try{
			OSSObject ossObject =  ossClient.getObject(config.getAliyunBucketName(),path);
			input = ossObject.getObjectContent();
		} catch (Exception e){
			throw new RxcException("下载文件失败，请检查配置信息", e);
		}
		return input;
	}

	@Override
	public void delFileOss(String path) {
		try{
			ossClient.deleteObject(config.getAliyunBucketName(),path);
		}catch (Exception e){
			log.error("阿里云OSS删除失败!",e);
		}finally {
			if(null != ossClient) {
				try {
					ossClient.shutdown();
				} catch(Exception e) {
				}
			}
		}
	}

	@Override
	public String uploadSuffix(byte[] data, String suffix) {
		return upload(data, getPath(config.getAliyunurlPrefix(), suffix));
	}

	@Override
	public String uploadSuffix(InputStream inputStream, String suffix) {
		return upload(inputStream, getPath(config.getAliyunurlPrefix(), suffix));
	}

	@Override
	public void shutdown() {
		if(null != ossClient) {
			try {
				ossClient.shutdown();
			} catch(Exception e) {
			}
		}
	}
}
