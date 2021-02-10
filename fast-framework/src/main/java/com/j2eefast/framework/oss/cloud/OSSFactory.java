/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.oss.cloud;

import com.j2eefast.common.config.service.SysConfigService;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.framework.utils.Constant;

/**
 * 文件上传Factory
 */
public final class OSSFactory {

	private static SysConfigService sysConfigService;

	static {
		OSSFactory.sysConfigService = SpringUtil.getBean(SysConfigService.class);
	}

	public static CloudStorageService build() {
		// 获取云存储配置信息
		CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY,
				CloudStorageConfig.class);

		if (config.getType() == Constant.CloudService.QINIU.getValue()) {
			return new QiniuCloudStorageService(config);
		} else if (config.getType() == Constant.CloudService.ALIYUN.getValue()) {
			return new AliyunCloudStorageService(config);
		} else if (config.getType() == Constant.CloudService.QCLOUD.getValue()) {
			return new QcloudCloudStorageService(config);
		} else if (config.getType() == Constant.CloudService.FTP.getValue()) {
			return new FTPCloudStorageService(config);
		}
		return null;
	}

	/**
     * 获取系统上传信息配置
	 * @return
     */
	public static int getOSSType(){
		// 获取云存储配置信息
		CloudStorageConfig config = sysConfigService.getConfigObject(ConfigConstant.CLOUD_STORAGE_CONFIG_KEY,
				CloudStorageConfig.class);
		return config.getType();
	}

}
