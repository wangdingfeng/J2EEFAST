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
import com.j2eefast.web.legao.entity.LMemberTableEntity;
import com.j2eefast.web.legao.service.LMemberService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.web.legao.entity.LTableEntity;
import com.j2eefast.web.legao.service.LTableService;

/**
 * 乐高店桌号页面控制器
 * @author polaris_wang
 * @date 2021-12-21 15:12:50
 */
@Controller
@RequestMapping("/legao/table")
public class LTableController extends BaseController{

    private String prefix = "modules/legao/table";

    @Autowired
    private LTableService lTableService;

    @Autowired
    private LMemberService lMemberService;

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
     * @param lTableEntity
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,LTableEntity lTableEntity) {
        PageUtil page = lTableService.findPage(params,lTableEntity);
		return success(page);
    }

    /**
     * 新增视图
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
    @BussinessLog(title = "乐高店桌号", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addLTable(@Validated LTableEntity lTable){
        //校验参数
        ValidatorUtil.validateEntity(lTable);
        return lTableService.addLTable(lTable)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("legao:table:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        LTableEntity lTable = lTableService.findLTableById(id);
        mmap.put("lTable", lTable);
        return prefix + "/edit";
    }

    /**
     * 修改保存乐高店桌号
     */
    @RepeatSubmit
    @RequiresPermissions("legao:table:edit")
    @BussinessLog(title = "乐高店桌号", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editLTable(LTableEntity lTable){
        //校验参数
		ValidatorUtil.validateEntity(lTable);
        return lTableService.updateLTableById(lTable)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("legao:table:del")
    @BussinessLog(title = "乐高店桌号", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return lTableService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

    /**
     * 跳转到分配页面
     * @param id
     * @param mmap
     * @return
     */
    @GetMapping("/dist/{id}")
    public String dist(@PathVariable("id") Long id, ModelMap mmap){
        mmap.put("id",id);
        mmap.put("memberSelect",lMemberService.getAllPhoneSelect());
        return prefix + "/dist";
    }

    /**
     * 分配用户
     * @param lMemberTable
     * @return
     */
    @RepeatSubmit
    @RequestMapping(value = "/dist", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData distMemberTable(LMemberTableEntity lMemberTable){
        lTableService.distMemberTable(lMemberTable);
        return success();
    }

    /**
     * 停止使用
     * @param lMemberTable
     * @return
     */
    @RepeatSubmit
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData stopMemberTable(LMemberTableEntity lMemberTable){
        lTableService.stopMemberTable(lMemberTable);
        return success();
    }

}
