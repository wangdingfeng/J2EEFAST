/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.utils.FileUploadUtil;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.framework.oss.cloud.CloudStorageService;
import com.j2eefast.framework.oss.cloud.OSSFactory;
import com.j2eefast.framework.sys.entity.SysFilesEntity;
import com.j2eefast.framework.sys.service.SysFileService;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.FileUploadUtils;
import com.j2eefast.framework.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 系统文件上传下载 控制类
 */
@Slf4j
@Controller
@RequestMapping("/sys/")
public class SysFileController extends BaseController {

    @Autowired
    private SysFileService sysFileService;

    
    
    private String prefix = "modules/sys/file";

    @RequiresPermissions("sys:file:view")
    @GetMapping("file")
    public String file(){
        return prefix + "/file";
    }

    @RequestMapping("file/list")
    @RequiresPermissions("sys:file:list")
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,SysFilesEntity sysFilesEntity) {
        PageUtil page = sysFileService.findPage(params,sysFilesEntity);
		return success(page);
    }



    /**
     * 删除
     */
    @RepeatSubmit
    @BussinessLog(title = "文件实体", businessType = BusinessType.DELETE)
    @RequestMapping(value = "file/del", method = RequestMethod.POST)
    @RequiresPermissions("sys:file:del")
    @ResponseBody
    public ResponseData del(Long[] ids) {  
    	try {
			FileUploadUtils.me().deleteFileRelation(ids);
		} catch (Exception e) {
			return  error("删除失败!"+ e.getMessage())	;	
		}
    	return success();
    }
    

    
    //以下为上传组件使用   /sys/comm *********************************************  


    /**
     * 系统上传组件上传文件接口
     * 支持上传OSS
     * @param file
     * @return
     */
    @BussinessLog(title = "文件上传", businessType = BusinessType.INSERT)
    @RequestMapping(value = "comm/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file){
        try{

            String attachPath = Global.getAttachPath() ;
            String fileName = super.getPara("name");
            //文件名称
            String fileMd5 = ToolUtil.encodingFilename(fileName);
            //文件相对路径
            String relativePath = File.separator + "file" + File.separator + 
            		DateFormatUtils.format(new Date(), "yyyy/MM/dd") + File.separator + fileMd5 +"."+ FileUploadUtil.getExtension(file);
            int ossType = OSSFactory.getOSSType();
            File file0 = null;
            SysFilesEntity sysFile = new SysFilesEntity();
            //配置的是本地
            if(ossType == Constant.CloudService.LOCAL.getValue()) {
            	attachPath = attachPath + relativePath;
                file0 = FileUtil.touch(attachPath);
                file.transferTo(file0);
                attachPath = FileUtil.getAbsolutePath(file0);
            }else if(ossType == Constant.CloudService.ALIYUN.getValue()) {
                relativePath = "attach"+relativePath;
            	//阿里云
                OSSFactory.build().upload(file.getInputStream(), relativePath);
            }
            sysFile.setFileMd5(fileMd5);
            sysFile.setFileName(fileName);
            sysFile.setFilePath(relativePath);
            sysFile.setClassify("0");
            sysFile.setOssType(Integer.toString(ossType));
            sysFile.setFileSize(new BigDecimal(file.getSize()));
            if(sysFileService.save(sysFile)){
                return  success("上传成功").put("path",attachPath).put("fileName",fileName)
                        .put("id",sysFile.getId()).put("fileMd5",sysFile.getFileMd5()).put("fileSize",sysFile.getFileSize());
            }else{
            	if(ossType == Constant.CloudService.LOCAL.getValue()) {
            		FileUtil.del(file0);
            	}
                return error("上传失败!");
            }
        }catch (Exception e){
            log.error("上传异常:",e);
            return error(e.getMessage());
        }
    }


    @RequestMapping(value = "comm/fileList", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData fileList(String bizId, String bizType){
        try{
            return success().put("fileList",sysFileService.selectSysFilesList(bizId,bizType));
        }catch (Exception e){
            return error(e.getMessage());
        }
    }


    @RequestMapping("comm/download")
    public void commDownload(HttpServletRequest request,
                             HttpServletResponse response, @RequestParam("fileId") Long fileId) {
        try {
            SysFilesEntity file =  sysFileService.getById(fileId);
            if(ToolUtil.isEmpty(file)){
                log.error("文件不存在");
                return;
            }
            int ossType = Integer.parseInt(file.getOssType());
            //获取数据库文件信息
            String relativePath = file.getFilePath();
            String  fileName = file.getFileName();
            String filePath = Global.getAttachPath() + relativePath;
            InputStream inputStream = null;
            CloudStorageService cloud = null;
            //配置的是本地
            if(ossType == Constant.CloudService.LOCAL.getValue()) {
                if(!FileUtil.exist(filePath)){
                    log.error("文件不存在");
                    return;
                }
                inputStream  = FileUtil.getInputStream(filePath);
            }else if(ossType == Constant.CloudService.ALIYUN.getValue()){
                cloud = OSSFactory.build();
                //阿里云
                inputStream = cloud.download(relativePath);
            }
            //下载文件
            ToolUtil.fileDownload(request,response,fileName,inputStream);
            if(ossType != Constant.CloudService.LOCAL.getValue()){
                cloud.shutdown();
            }
            return;
        }catch (Exception e){
            log.error("下载文件异常");
            return;
        }
    }

    @RequestMapping("comm/fileUploadView")
    public void fileUploadView(HttpServletResponse response,
                               @RequestParam("fileId") Long fileId) {
        try {
            SysFilesEntity file =  sysFileService.getById(fileId);
            if(ToolUtil.isEmpty(file)){
                log.error("文件不存在");
                return;
            }
            int ossType = Integer.parseInt(file.getOssType());
            String relativePath = file.getFilePath();
            String filePath = ((file.getClassify().equals("0") ||
                    file.getClassify().equals("1")) ? Global.getAttachPath() : Global.getEditorPath() ) + relativePath;
            String  fileName = file.getFileName();
            InputStream inputStream = null;
            CloudStorageService cloud = null;
            //配置的是本地
            if(ossType == Constant.CloudService.LOCAL.getValue()) {
                if(!FileUtil.exist(filePath)){
                    log.error("文件不存在");
                    return;
                }
                inputStream  = FileUtil.getInputStream(filePath);
            }else if(ossType == Constant.CloudService.ALIYUN.getValue()){
                //阿里云
                cloud = OSSFactory.build();
                inputStream = cloud.download(relativePath);
            }
            //转换视图
            ToolUtil.fileView(response,fileName,inputStream);
            if(ossType != Constant.CloudService.LOCAL.getValue()){
                cloud.shutdown();
            }
            return;
        }catch (Exception e){
            log.error("下载文件异常",e);
            return;
        }
    }

    /**
     * 图片裁剪展示
     * @param request
     * @param response
     * @param filePath
     */
    @RequestMapping("comm/fileAvatarView")
    public void fileavAtarView(HttpServletRequest request,
                               HttpServletResponse response,
                               @RequestParam("filePath") String filePath) {
        try {
            String path = Global.getAttachPath()+filePath;
            String  fileName = FileUtil.getName(path);
            if(FileUtil.exist(path)){
                //设置文件ContentType类型
                response.setContentType(HttpUtil.getMimeType(fileName));
                File imageFile = FileUtil.file(path);
                FileInputStream fis = new FileInputStream(imageFile);
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream bos = new ByteArrayOutputStream(fis.available());
                int len = 0;
                while (-1 != (len = fis.read(buffer, 0, buffer.length))) {
                    bos.write(buffer,0,len);
                }
                log.info("==============================下载包长度:!" + bos.size() +"   ========================");
                response.setHeader("Content-Length",bos.size()+ "");
                fis.close();
                ServletOutputStream sos = response.getOutputStream();
                sos.write(bos.toByteArray());
                sos.flush();
                sos.close();
                log.info("==============================下载完成![" + path +"]   ========================");
                return;
            }else{
                log.error("文件不存在");
                return;
            }
        }catch (Exception e){
            log.error("下载文件异常");
            return;
        }
    }


}
