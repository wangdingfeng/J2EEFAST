/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.framework.sys.entity.SysMsgCcUserEntity;
import com.j2eefast.framework.sys.entity.SysMsgCommentsEntity;
import com.j2eefast.framework.sys.entity.SysMsgPushUserEntity;
import com.j2eefast.framework.sys.service.SysMsgCcUserService;
import com.j2eefast.framework.sys.service.SysMsgCommentsService;
import com.j2eefast.framework.sys.service.SysMsgPushUserService;
import com.j2eefast.framework.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.framework.sys.entity.SysMsgPushEntity;
import com.j2eefast.framework.sys.service.SysMsgPushService;

import javax.annotation.Resource;

/**
 * 内部消息页面控制器
 * @author ZhouZhou
 * @date 2021-02-21 22:43:07
 */
@Controller
@RequestMapping("/sys/msg")
public class SysMsgPushController extends BaseController{

    private String prefix = "modules/sys/msg";

    @Autowired
    private SysMsgPushService sysMsgPushService;

    @Autowired
    private SysMsgPushUserService sysMsgPushUserService;
    @Autowired
    private SysMsgCcUserService sysMsgCcUserService;

    @Autowired
    private SysMsgCommentsService SysMsgCommentsService;

    /**
     * 视图页面
     */
    @RequiresPermissions("sys:msg:view")
    @GetMapping()
    public String msg(){
        return prefix + "/msg";
    }


    @RequiresPermissions("sys:userMsg:view")
    @GetMapping("/userMsg")
    public String userMsg(){
        return prefix + "/userMsg";
    }

    /**
     * 页面表格分页查询
     * @param params
     * @param sysMsgPushEntity
     * @return
     */
    @RequiresPermissions("sys:msg:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,SysMsgPushEntity sysMsgPushEntity) {
        sysMsgPushEntity.setSendUserId(UserUtils.getUserId());
        PageUtil page = sysMsgPushService.findPage(params,sysMsgPushEntity);
		return success(page);
    }

    @RequiresPermissions("sys:userMsg:list")
    @RequestMapping(value = "/userList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData userList(@RequestParam Map<String, Object> params,SysMsgPushEntity sysMsgPushEntity) {
        sysMsgPushEntity.setUserId(UserUtils.getUserId());
        PageUtil page = sysMsgPushService.findUserPage(params,sysMsgPushEntity);
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
    @RequiresPermissions("sys:msg:add")
    @BussinessLog(title = "内部消息", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addSysMsgPush(@Validated SysMsgPushEntity sysMsgPush){
        //校验参数
        ValidatorUtil.validateEntity(sysMsgPush);
        return sysMsgPushService.addSysMsgPush(sysMsgPush)? success(): error("新增失败!");
//        return error("新增失败!");
    }


    @RequiresPermissions("sys:comment:add")
    @RequestMapping(value = "/commets/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData commetList(@RequestParam Map<String, Object> params,SysMsgCommentsEntity sysMsgComments){
        sysMsgComments.setReplyId(0L);
        PageUtil page = SysMsgCommentsService.findPage(params,sysMsgComments);
        return success(page);
    }


    @RequiresPermissions("sys:comment:del")
    @RequestMapping(value = "/commets/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData commetDel(Long id){
        return  SysMsgCommentsService.delSysMsgCommentsById(id) ? success(): error("删除失败!");
    }

    @RepeatSubmit
    @RequiresPermissions("sys:comment:add")
    @BussinessLog(title = "发布评论", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/addCommets", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addSysComments(@Validated SysMsgCommentsEntity sysMsgComments){
        //校验参数
        ValidatorUtil.validateEntity(sysMsgComments);
        return SysMsgCommentsService.addSysMsgComments(sysMsgComments)? success(): error("新增失败!");
    }


    /**
     * 查看发送的消息
     */
    @RequiresPermissions("sys:msg:view")
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, ModelMap mmap){
        SysMsgPushEntity sysMsgPush = sysMsgPushService.findSysMsgPushById(id);
        List<SysMsgPushUserEntity> pushUserList =  sysMsgPushUserService.findList(sysMsgPush.getId());
        List<SysMsgCcUserEntity>  ccUserList = sysMsgCcUserService.findList(sysMsgPush.getId());
        mmap.put("sysMsgPush", sysMsgPush);
        mmap.put("pushUserList", pushUserList);
        mmap.put("ccUserList", ccUserList);
        return prefix + "/view";
    }

    @RequiresPermissions("sys:msg:view")
    @GetMapping("/pushView/{id}")
    public String pushView(@PathVariable("id") Long id, ModelMap mmap){
        //修改阅读标志
        sysMsgPushUserService.setIsRead("1",id,UserUtils.getUserId());
        //查询消息数据
        SysMsgPushEntity sysMsgPush = sysMsgPushService.findSysMsgPushById(id);
        List<SysMsgPushUserEntity> pushUserList =  sysMsgPushUserService.findList(sysMsgPush.getId());
        List<SysMsgCcUserEntity>  ccUserList = sysMsgCcUserService.findList(sysMsgPush.getId());
        mmap.put("sysMsgPush", sysMsgPush);
        mmap.put("pushUserList", pushUserList);
        mmap.put("ccUserList", ccUserList);
        return prefix + "/view";
    }

    @RequiresPermissions("sys:msg:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        SysMsgPushEntity sysMsgPush = sysMsgPushService.findSysMsgPushById(id);
        List<SysMsgPushUserEntity> pushUserList =  sysMsgPushUserService.findList(sysMsgPush.getId());
        List<SysMsgCcUserEntity>  ccUserList = sysMsgCcUserService.findList(sysMsgPush.getId());
        mmap.put("sysMsgPush", sysMsgPush);
        String pushUserId = "";
        String pushUserName = "";
        for(SysMsgPushUserEntity pushUser: pushUserList){
            pushUserId += pushUser.getUserId() +",";
            pushUserName += pushUser.getUserName() + ",";
        }
        mmap.put("pushUserId", pushUserId.substring(0,pushUserId.length()-1));
        mmap.put("pushUserName", pushUserName.substring(0,pushUserName.length()-1));
        String ccUserId = "";
        String ccUserName = "";
        for(SysMsgCcUserEntity ccUser: ccUserList){
            ccUserId += ccUser.getUserId() +",";
            ccUserName += ccUser.getUserName() + ",";
        }
        mmap.put("ccUserId", ccUserId.substring(0,ccUserId.length()-1));
        mmap.put("ccUserName", ccUserName.substring(0,ccUserName.length()-1));
        return prefix + "/edit";
    }

    /**
     * 修改保存内部消息
     */
    @RepeatSubmit
    @RequiresPermissions("sys:msg:edit")
    @BussinessLog(title = "内部消息", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editSysMsgPush(SysMsgPushEntity sysMsgPush){
        //校验参数
		ValidatorUtil.validateEntity(sysMsgPush);
        return sysMsgPushService.updateSysMsgPushById(sysMsgPush)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("sys:msg:del")
    @BussinessLog(title = "内部消息", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return sysMsgPushService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

}
