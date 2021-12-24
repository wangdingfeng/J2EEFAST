/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.legao.controller;

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
import com.j2eefast.web.legao.entity.LMemberTableEntity;
import com.j2eefast.web.legao.service.LMemberTableService;

/**
 * 会员分配桌号流水页面控制器
 * @author polaris_wang
 * @date 2021-12-22 11:57:03
 */
@Controller
@RequestMapping("/legao/table/member")
public class LMemberTableController extends BaseController{

    private String prefix = "modules/legao/table";

    @Autowired
    private LMemberTableService lMemberTableService;

    /**
     * 视图页面
     */
    @RequiresPermissions("legao:table:view")
    @GetMapping()
    public String table(){
        return prefix + "/table";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param lMemberTableEntity
     * @return
     */
    @RequiresPermissions("legao:table:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,LMemberTableEntity lMemberTableEntity) {
        PageUtil page = lMemberTableService.findPage(params,lMemberTableEntity);
		return success(page);
    }

    /**
     * 新增视图
     * @param
     * @return
     */
    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("legao:table:add")
    @BussinessLog(title = "会员分配桌号流水", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addLMemberTable(@Validated LMemberTableEntity lMemberTable){
        //校验参数
        ValidatorUtil.validateEntity(lMemberTable);
        return lMemberTableService.addLMemberTable(lMemberTable)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("legao:table:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        LMemberTableEntity lMemberTable = lMemberTableService.findLMemberTableById(id);
        mmap.put("lMemberTable", lMemberTable);
        return prefix + "/edit";
    }

    /**
     * 修改保存会员分配桌号流水
     */
    @RepeatSubmit
    @RequiresPermissions("legao:table:edit")
    @BussinessLog(title = "会员分配桌号流水", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editLMemberTable(LMemberTableEntity lMemberTable){
        //校验参数
		ValidatorUtil.validateEntity(lMemberTable);
        return lMemberTableService.updateLMemberTableById(lMemberTable)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("legao:table:del")
    @BussinessLog(title = "会员分配桌号流水", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return lMemberTableService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

}
