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
import com.j2eefast.web.legao.entity.LMemberEntity;
import com.j2eefast.web.legao.service.LMemberService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/**
 * 会员页面控制器
 * @author polaris_wang
 * @date 2021-12-21 14:31:55
 */
@Controller
@RequestMapping("/legao/member")
public class LMemberController extends BaseController{

    private String prefix = "modules/legao/member";

    @Autowired
    private LMemberService lMemberService;

    /**
     * 视图页面
     */
    @RequiresPermissions("legao:member:view")
    @GetMapping()
    public String member(){
        return prefix + "/member";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param lMemberEntity
     * @return
     */
    @RequiresPermissions("legao:member:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,LMemberEntity lMemberEntity) {
        PageUtil page = lMemberService.findPage(params,lMemberEntity);
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
    @RequiresPermissions("legao:member:add")
    @BussinessLog(title = "会员", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addLMember(@Validated LMemberEntity lMember){
        //校验参数
        ValidatorUtil.validateEntity(lMember);
        return lMemberService.addLMember(lMember)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("legao:member:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        LMemberEntity lMember = lMemberService.findLMemberById(id);
        mmap.put("lMember", lMember);
        return prefix + "/edit";
    }

    /**
     * 通过手机号查询 模糊
     * @param phone
     * @return
     */
    @GetMapping("/phone")
    public ResponseData select2Phone(@RequestParam(value = "phone") String phone){
        return ResponseData.success(lMemberService.getPhoneSelect(phone));
    }

    /**
     * 修改保存会员
     */
    @RepeatSubmit
    @RequiresPermissions("legao:member:edit")
    @BussinessLog(title = "会员", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editLMember(LMemberEntity lMember){
        //校验参数
		ValidatorUtil.validateEntity(lMember);
        return lMemberService.updateLMemberById(lMember)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("legao:member:del")
    @BussinessLog(title = "会员", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return lMemberService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }


    @GetMapping("/{id}")
    @ResponseBody
    public ResponseData get(@PathVariable("id") Long id){
        return success(lMemberService.findLMemberById(id));
    }

}
