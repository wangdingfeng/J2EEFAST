/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.controller;

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
import com.j2eefast.web.test.entity.ExampleSynchTreeEntity;
import com.j2eefast.web.test.service.ExampleSynchTreeService;
import com.j2eefast.common.core.base.entity.Ztree;
import java.util.List;

/**
 * 测试同步树页面控制器
 * @author ZhouZhou
 * @date 2020-12-21 10:58:12
 */
@Controller
@RequestMapping("/demo/synchTree")
public class ExampleSynchTreeController extends BaseController{

    private String prefix = "modules/demo/synchTree";

    @Autowired
    private ExampleSynchTreeService exampleSynchTreeService;

    /**
     * 视图页面
     */
    @RequiresPermissions("demo:synchTree:view")
    @GetMapping()
    public String synchTree(){
        return prefix + "/synchTree";
    }

    /**
     * 同步树页面表格分页查询
     * @param params
     * @param exampleSynchTreeEntity
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @RequiresPermissions("demo:synchTree:list")
    @ResponseBody
    public List<ExampleSynchTreeEntity> list(@RequestParam Map<String, Object> params,ExampleSynchTreeEntity exampleSynchTreeEntity) {
        return  exampleSynchTreeService.findTreeList(params,exampleSynchTreeEntity);
    }

    /**
     * 加载树
     */
    @GetMapping("/treeData")
    @RequiresPermissions("demo:synchTree:add")
    @ResponseBody
    public List<Ztree> treeData(){
        List<Ztree> ztrees = exampleSynchTreeService.findTree();
        return ztrees;
    }
    
    /**
     * 新增视图
     * @param mmap
     * @return
     */
    @GetMapping("/add/{id}")
    public String add(@PathVariable("id") Long id, ModelMap mmap){
        ExampleSynchTreeEntity exampleSynchTree = null;
        if (0L != id){
            exampleSynchTree = exampleSynchTreeService.findExampleSynchTreeById(id);
        }else{
            exampleSynchTree = new ExampleSynchTreeEntity();
            exampleSynchTree.setId(0L);
            exampleSynchTree.setName("根节点");
        }
        mmap.put("exampleSynchTree", exampleSynchTree);
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("demo:synchTree:add")
    @BussinessLog(title = "测试同步树", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addExampleSynchTree(@Validated ExampleSynchTreeEntity exampleSynchTree){
        //校验参数
        ValidatorUtil.validateEntity(exampleSynchTree);
        return exampleSynchTreeService.addExampleSynchTree(exampleSynchTree)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("demo:synchTree:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        ExampleSynchTreeEntity exampleSynchTree = exampleSynchTreeService.findExampleSynchTreeById(id);
        mmap.put("exampleSynchTree", exampleSynchTree);
        return prefix + "/edit";
    }

    /**
     * 修改保存测试同步树
     */
    @RepeatSubmit
    @RequiresPermissions("demo:synchTree:edit")
    @BussinessLog(title = "测试同步树", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editExampleSynchTree(ExampleSynchTreeEntity exampleSynchTree){
        //校验参数
		ValidatorUtil.validateEntity(exampleSynchTree);
        return exampleSynchTreeService.updateExampleSynchTreeById(exampleSynchTree)? success(): error("修改失败!");
    }
    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("demo:synchTree:del")
    @BussinessLog(title = "测试同步树", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData del(@PathVariable("id") Long id) {
        return exampleSynchTreeService.delExampleSynchTreeById(id)? success(): error("删除失败!");
    }

}
