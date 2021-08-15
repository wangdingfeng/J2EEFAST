/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.base.entity.Ztree;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.framework.sys.entity.SysTenantEntity;
import com.j2eefast.framework.sys.service.SysTenantService;
import org.apache.shiro.authz.annotation.Logical;

/**
 * 租户页面控制器
 * @author ZhouZhou
 * @date 2021-07-23 13:39:18
 */
@Controller
@RequestMapping("/sys/tenant")
public class SysTenantController extends BaseController{

    private String prefix = "modules/sys/tenant";

    @Autowired
    private SysTenantService sysTenantService;

    /**
     * 视图页面
     */
    @RequiresPermissions("sys:tenant:view")
    @GetMapping()
    public String tenant(){
        return prefix + "/tenant";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param sysTenantEntity
     * @return
     */
    @RequiresPermissions("sys:tenant:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,SysTenantEntity sysTenantEntity) {
        PageUtil page = sysTenantService.findPage(params,sysTenantEntity);
		return success(page);
    }

    /**
     * 新增视图
     * @param mmap
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
    @RequiresPermissions("sys:tenant:add")
    @BussinessLog(title = "租户", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addSysTenant(@Validated SysTenantEntity sysTenant){
        //校验参数
        ValidatorUtil.validateEntity(sysTenant);
        return sysTenantService.addSysTenant(sysTenant)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("sys:tenant:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        SysTenantEntity sysTenant = sysTenantService.findSysTenantById(id);
        mmap.put("sysTenant", sysTenant);
        return prefix + "/edit";
    }

    /**
     * 修改保存租户
     */
    @RepeatSubmit
    @RequiresPermissions("sys:tenant:edit")
    @BussinessLog(title = "租户", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editSysTenant(SysTenantEntity sysTenant){
        //校验参数
		ValidatorUtil.validateEntity(sysTenant);
        return sysTenantService.updateSysTenantById(sysTenant)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("sys:tenant:del")
    @BussinessLog(title = "租户", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return sysTenantService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

    /**
     * 校验租户ID
     */
    @RequestMapping(value = "/checkTenantIdUnique", method = RequestMethod.POST)
    @RequiresPermissions(value = {"sys:tenant:add", "sys:tenant:edit"}, logical = Logical.OR)
    @ResponseBody
    public ResponseData checkTenantIdUnique(SysTenantEntity sysTenant) {
        if(sysTenantService.checkTenantIdUnique(sysTenant)){
            return success();
        }
        return error("租户ID已经存在!");
    }

    @RequiresPermissions("sys:tenant:tree")
    @GetMapping("/findTenantTree")
    @RequiresRoles(Constant.SU_ADMIN)
    @ResponseBody
    public List<Ztree> findTenantTree(){
        if(!UserUtils.isSuperTenant()){
            throw new RxcException(ToolUtil.message("illegal request"),"50001");
        }
        return sysTenantService.findTenantTree();
    }

    //switchTenant
    @RequiresPermissions("sys:tenant:switch")
    @PostMapping("/switchTenant")
    @RequiresRoles(Constant.SU_ADMIN)
    @ResponseBody
    public ResponseData switchTenant(SysTenantEntity sysTenant){
        if(!UserUtils.isSuperTenant()){
            throw new RxcException(ToolUtil.message("illegal request"),"50001");
        }
        if(sysTenantService.isTenantNull(sysTenant.getTenantId())){
            LoginUserEntity loginUser = UserUtils.getUserInfo();
            //切换租户 从新替换用户信息
            loginUser.setTenantId(sysTenant.getTenantId());
            loginUser.setTenanName(sysTenant.getTenantName());
            UserUtils.reloadUser(loginUser);
            return success();
        }else{
            return error("租户不存在!");
        }
    }

}
