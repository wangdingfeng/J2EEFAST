/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import com.j2eefast.framework.sys.service.SysUreportFileService;

/**
 * ureport2报配置存储页面控制器
 * @author ZhouZhou
 * @date 2021-08-14 19:57:51
 */
@Controller
@RequestMapping("/sys/ureport")
public class SysUreportFileController extends BaseController{

    private String prefix = "modules/sys/ureport";

    @Autowired
    private SysUreportFileService sysUreportFileService;

    /**
     * 视图页面
     */
    @RequiresPermissions("sys:ureport:view")
    @GetMapping()
    public String ureport(){
        return prefix + "/ureport";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param sysUreportFileEntity
     * @return
     */
    @RequiresPermissions("sys:ureport:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,SysUreportFileEntity sysUreportFileEntity) {
        PageUtil page = sysUreportFileService.findPage(params,sysUreportFileEntity);
		return success(page);
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("sys:ureport:del")
    @BussinessLog(title = "ureport2报配置存储", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return sysUreportFileService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

}
