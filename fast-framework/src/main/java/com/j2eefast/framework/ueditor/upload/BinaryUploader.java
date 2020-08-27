package com.j2eefast.framework.ueditor.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.ImageUtils;
import com.j2eefast.common.core.utils.Md5Util;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysFileUploadEntity;
import com.j2eefast.framework.sys.entity.SysFilesEntity;
import com.j2eefast.framework.sys.service.SysFileService;
import com.j2eefast.framework.sys.service.SysFileUploadService;
import com.j2eefast.framework.ueditor.PathFormat;
import com.j2eefast.framework.ueditor.define.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class BinaryUploader {
	public static final String FILEUEDITOR_BASE_URL = "editor";
	
	/**
	 * 保存文件
	 * @param request
	 * @param conf
	 * @return
	 */
	public static final State save(HttpServletRequest request,
                                   Map<String, Object> conf) {
		FileItemStream fileStream = null; //原始上传
		MultipartFile fileStream2 = null; // Spring MVC 上传
		
		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;
		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(
				new DiskFileItemFactory());
        if ( isAjaxUpload ) {
            upload.setHeaderEncoding( "UTF-8" );
        }

		try {
			FileItemIterator iterator = upload.getItemIterator(request);
			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField()) {
					break;
				}
				fileStream = null;
			}

			if (fileStream == null) {
				// 原始上传无文件，则检查是否是Spring MVC上传 j2eefast
				MultipartFile file = null;
				if (request instanceof MultipartHttpServletRequest){
					MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
					Iterator<String> it = multiRequest.getFileNames();
					while (it.hasNext()) {
						file = multiRequest.getFile(it.next());
						break;
					}
				}
				if (file != null && !file.isEmpty() && file.getOriginalFilename() != null) {
					fileStream2 = file;
				}
			}

			if (fileStream == null && fileStream2 == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream != null ? fileStream.getName() : fileStream2.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;

			InputStream is = fileStream != null ? fileStream.openStream() : fileStream2.getInputStream();
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				int actionCode = ((Integer) conf.get("actionCode")).intValue();
				// 上传图片后，进行图片压缩
				if (actionCode == ActionMap.UPLOAD_IMAGE){

					// 如果开启了压缩图片
					if ((Boolean)conf.get("imageCompressEnable")){
						Integer maxWidth = (Integer)conf.get("imageCompressBorder");
						ImageUtils.thumbnails(new File(physicalPath), maxWidth, -1, null);
					}
				}
				String url = PathFormat.format(savePath);
				int index = url.indexOf(FILEUEDITOR_BASE_URL);
				if(index >= 0) {
					url = StrUtil.subAfter(url,FILEUEDITOR_BASE_URL,false);
				}
				//保存数据库
				//文件名称
				String fileMd5 = Md5Util.hash(FileUtil.getAbsolutePath(physicalPath));
				SysFilesEntity sysFile = new SysFilesEntity();
				sysFile.setFileMd5(fileMd5);
				sysFile.setFileName(storageState.getStr("title"));
				sysFile.setFilePath(url);
				sysFile.setClassify("2");
				sysFile.setFileSize(new BigDecimal(storageState.getLong("size")));
				SpringUtil.getBean(SysFileService.class).save(sysFile);
				
				String bizId = request.getParameter("bizId");
				String bizType = request.getParameter("bizType");

                SysFileUploadEntity sysFileUploadEntity  = new SysFileUploadEntity();
                sysFileUploadEntity.setBizId(Long.parseLong(bizId));
                sysFileUploadEntity.setFileName(sysFile.getFileName());
                sysFileUploadEntity.setFileId(sysFile.getId());
                sysFileUploadEntity.setBizType(bizType);
                SpringUtil.getBean(SysFileUploadService.class).saveSysFileUpload(sysFileUploadEntity);
				
				//解决返回路径问题 j2eefast
				storageState.putInfo("url", request.getContextPath()+ ConfigConstant.RESOURCE_URLPREFIX  + PathFormat.format(savePath) );
				storageState.putInfo("type", suffix);
				storageState.putInfo("fileId", sysFile.getId());
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
			log.error("IO错误:",e);
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
