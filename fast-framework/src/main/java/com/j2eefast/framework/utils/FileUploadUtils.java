package com.j2eefast.framework.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.alibaba.excel.util.FileUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.j2eefast.common.core.utils.HttpContextUtil;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.constant.factory.IConstantFactory;
import com.j2eefast.framework.sys.entity.SysFileUploadEntity;
import com.j2eefast.framework.sys.entity.SysFilesEntity;
import com.j2eefast.framework.sys.service.SysFileService;
import com.j2eefast.framework.sys.service.SysFileUploadService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FileUploadUtils {

//	private static  SysFileUploadService sysFileUploadService = SpringUtil.getBean(SysFileUploadService.class);
//
//	private static  SysFileService sysFileService = SpringUtil.getBean(SysFileService.class);

    public static FileUploadUtils me() {
        return new FileUploadUtils();
    }

    /**
     * 保存文件与业务关联数据  --- 在用户保存和更新业务表单成功后时使用，
     * @param bizId  业务主键ID
     * @param bizType 业务类型
     */
    public static void saveFileUpload(Long bizId,String bizType){
        HttpServletRequest request = HttpContextUtil.getRequest();
        String fileUploads = request.getParameter(bizType);
        String fileDels = request.getParameter(bizType+"__del");
        //删除关联
        if(ToolUtil.isNotEmpty(fileDels)){
            String[] dels = StrUtil.split(fileDels,StrUtil.COMMA);
            for(String fId: dels){
                ConstantFactory.me().getSysFileUploadService().removeByBizId(fId,String.valueOf(bizId));
            }
        }
        //新增关联
        if(ToolUtil.isNotEmpty(fileUploads)){
            String[] files = StrUtil.split(fileUploads,StrUtil.COMMA);
            SysFileService sysFileService = SpringUtil.getBean(SysFileService.class);
            for(String fileId: files){
                if(ConstantFactory.me().getSysFileUploadService().getSysFileUploadByBizId(fileId,String.valueOf(bizId))){
                    SysFilesEntity filesEntity = sysFileService.getSysFileById(Long.valueOf(fileId));
                    SysFileUploadEntity sysFileUploadEntity  = new SysFileUploadEntity();
                    sysFileUploadEntity.setBizId(bizId);
                    sysFileUploadEntity.setFileName(filesEntity.getFileName());
                    sysFileUploadEntity.setFileId(filesEntity.getId());
                    sysFileUploadEntity.setBizType(bizType);
                    ConstantFactory.me().getSysFileUploadService().saveSysFileUpload(sysFileUploadEntity);
                }
            }
        }
    }
    
    /**
    * @Title: removeFileUpload 
    * @Description: 删除业务时同时删除其附件及附件关联,可直接controller中调用
    * @param bizId  业务主键ID
    * @param bizType   业务类型
    * @author mfksn001@163.com
    * @Date: 2020年8月14日
     */
    public void removeFileUpload(Long bizId,String bizType) {
    	SysFileUploadEntity temp = new SysFileUploadEntity();
    	temp.setBizType(bizType);
    	temp.setBizId(bizId);   	
    	List<SysFileUploadEntity> list = ConstantFactory.me().getSysFileUploadService().selectList(temp);
    	if (null != list && list.size() > 0) {
    		for (SysFileUploadEntity up :list ) {
    			deleteFileRelation(up.getFileId());
    		}
    	}    	
    }
    
  
    /**
    * @Title: deleteFile 
    * @Description: 删除物理文件及sys_file，file_upload(如果)   attach目录
    * @param fileId  void 
    * @author mfksn001@163.com
    * @Date: 2020年8月13日
     */
	@Transactional(rollbackFor = Exception.class)
	public void deleteFileRelation(Long fileId) {
		SysFilesEntity fileEntity = ConstantFactory.me().getSysFileService().getById(fileId);
		if (null != fileEntity) {
			String divPath = (fileEntity.getClassify().equals("0") || fileEntity.getClassify().equals("1")) ? Global.getAttachPath() : Global.getEditorPath() ;
			String filePath = divPath + fileEntity.getFilePath() ;
			File file = new File(filePath);
			//删除文件
			if (file.exists() && file.isFile()) {
				FileUtils.delete(new File(filePath));
			}
			
			//删除业务关联 sys_file_upload
			if (null != fileEntity.getFileUpload() && null != fileEntity.getFileUpload().getId()) {
                ConstantFactory.me().getSysFileUploadService().deleteSysFileUploadById(fileEntity.getFileUpload().getId());
			}
			//删除sys_file
            ConstantFactory.me().getSysFileService().delSysFilesById(fileId);
		}
	}
    
	/**
	* @Title: deleteFileRelation 
	* @Description:  批量删除物理文件及sys_file，file_upload(如果)   attach目录
	* @param fileIds  void 
	* @author mfksn001@163.com
	* @Date: 2020年8月14日
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteFileRelation(Long[] fileIds) {
		if (null != fileIds && fileIds.length > 0) {
			for (Long fileId : fileIds) {
				deleteFileRelation(fileId);
			}
		}
	}

    /**
     * 业务逻辑层获取保存的裁剪图片路径
     * @param imgName 页面控件Name
     * @return
     */
	@Deprecated
    public static String getAvatarImg(String imgName){
        HttpServletRequest request = HttpContextUtil.getRequest();
        String imgBase64 = request.getParameter(imgName);
        return saveImgBase64(imgBase64);
    }

     /**
     * 图片裁剪保存图片信息
     * @param imgBase64
     * @return 返回裁剪控件图片存放相对路径
     */
	@Deprecated
    public static String saveImgBase64(String imgBase64) {
    	return saveImgBase64(imgBase64,null,null,null)   ; 	
    }
    
    
    /**
    * @Title: saveImgBase64 
    * @Description: 图片裁剪保存图片信息，当一个业务ID,包括多个avatar属性时，需要传入文件名，本方法在业务实体Save 方法后使用
    * @param imgBase64  
    * @param bizType  业务类型
    * @param bizId   业务ID
    * @param fileName  为空时,使用bizId+后缀名 命名
    * @return  String  保文件的路径名
    * @author mfksn001@163.com
    * @Date: 2020年8月24日
     */
    public static String saveImgBase64(String imgBase64,String bizType,Long bizId,String fileName) {
    	if (imgBase64.equals(StrUtil.EMPTY)) {
            return null;
        }
    	
    	

        String extension = null;
        String type = StrUtil.subBetween(imgBase64, "data:", ";base64,");
        if (StrUtil.containsIgnoreCase(type, "image/jpeg")) {
            extension = "jpg";
        } else if (StrUtil.containsIgnoreCase(type, "image/gif")) {
            extension = "gif";
        } else {
            extension = "png";
        }
        String saveName = IdUtil.fastSimpleUUID() + "." + extension ;
        if (StringUtils.isNotBlank(fileName)) {     	
        	saveName = fileName + "." + extension; 
        }        

        String imageUrl = File.separator + "avatar"
                + File.separator +
                DateFormatUtils.format(new Date(), "yyyy/MM/dd") + File.separator + saveName ;

        String base64 = StrUtil.subAfter(imgBase64, "base64,", true);
        if (StrUtil.isBlank(base64)) {
            return null;
        }

        byte[] data = Base64.decode(base64);

        try {
            //删除
            FileUploadUtils.me().removeFileUpload(bizId,bizType);

        	String fullPath =  Global.getAttachPath() + imageUrl ;
            FileUtil.writeBytes(data, Global.getAttachPath() + imageUrl);
            SysFilesEntity sysFile = new SysFilesEntity();
            sysFile.setFileMd5(ToolUtil.encodingFilename(saveName));
            sysFile.setFileName(saveName);
            sysFile.setFilePath(imageUrl);
            sysFile.setClassify("1");
            sysFile.setFileSize(new BigDecimal(FileUtil.size(new File(fullPath))));
            ConstantFactory.me().getSysFileService().save(sysFile);
            if (StringUtils.isNotBlank(bizType)) {
	             SysFileUploadEntity sysFileUploadEntity  = new SysFileUploadEntity();
	             sysFileUploadEntity.setBizId(bizId);
	             sysFileUploadEntity.setFileName(saveName);
	             sysFileUploadEntity.setFileId(sysFile.getId());
	             sysFileUploadEntity.setBizType(bizType);
                ConstantFactory.me().getSysFileUploadService().saveSysFileUpload(sysFileUploadEntity);
            }
        } catch (IORuntimeException e) {
            e.printStackTrace();
        }

        return imageUrl;
    }
    /*
     * 使用业务ID作为文件名
     */
    public static String saveImgBase64(String imgBase64,String bizType,Long bizId) {
       return saveImgBase64( imgBase64, bizType, bizId,null);
    }
    
 
 
    
}
