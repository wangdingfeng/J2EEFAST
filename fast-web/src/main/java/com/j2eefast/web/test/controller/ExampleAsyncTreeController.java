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
import com.j2eefast.web.test.entity.ExampleAsyncTreeEntity;
import com.j2eefast.web.test.service.ExampleAsyncTreeService;
import com.j2eefast.common.core.base.entity.Ztree;
import java.util.List;

/**
 * 测试异步树页面控制器
 * @author ZhouZhou
 * @date 2020-12-21 11:03:28
 */
@Controller
@RequestMapping("/demo/asyncTree")
public class ExampleAsyncTreeController extends BaseController{

    private String prefix = "modules/demo/asyncTree";

    @Autowired
    private ExampleAsyncTreeService exampleAsyncTreeService;

    /**
     * 视图页面
     */
    @RequiresPermissions("demo:asyncTree:view")
    @GetMapping()
    public String asyncTree(){
        return prefix + "/asyncTree";
    }

    /**
     * 页面请求分页
     * @param params 请求分页参数
     * @param exampleAsyncTreeEntity 请求对象
     * @return
     */
    @RequiresPermissions("demo:asyncTree:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,ExampleAsyncTreeEntity exampleAsyncTreeEntity) {
        PageUtil page = exampleAsyncTreeService.findPage(params,exampleAsyncTreeEntity);
        return success(page);
    }

    /**
     * 异步请求数据
     * @param exampleAsyncTreeEntity
     * @return
     */
    @RequestMapping(value = "/listChild" , method = RequestMethod.POST)
    @RequiresPermissions("demo:asyncTree:list")
    @ResponseBody
    public ResponseData listChild(ExampleAsyncTreeEntity exampleAsyncTreeEntity) {
        List<ExampleAsyncTreeEntity> list = exampleAsyncTreeService.findList(exampleAsyncTreeEntity);
        return success().put("list", list);
    }

    /**
     * 加载树
     */
    @GetMapping("/treeData")
    @RequiresPermissions("demo:asyncTree:add")
    @ResponseBody
    public List<Ztree> treeData(){
        List<Ztree> ztrees = exampleAsyncTreeService.findTree();
        return ztrees;
    }
    
    /**
     * 新增视图
     * @param mmap
     * @return
     */
    @GetMapping("/add/{id}")
    public String add(@PathVariable("id") Long id, ModelMap mmap){
        ExampleAsyncTreeEntity exampleAsyncTree = null;
        if (0L != id){
            exampleAsyncTree = exampleAsyncTreeService.findExampleAsyncTreeById(id);
        }else{
            exampleAsyncTree = new ExampleAsyncTreeEntity();
            exampleAsyncTree.setId(0L);
            exampleAsyncTree.setName("根节点");
        }
        mmap.put("exampleAsyncTree", exampleAsyncTree);
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("demo:asyncTree:add")
    @BussinessLog(title = "测试异步树", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addExampleAsyncTree(@Validated ExampleAsyncTreeEntity exampleAsyncTree){
        //校验参数
        ValidatorUtil.validateEntity(exampleAsyncTree);
        return exampleAsyncTreeService.addExampleAsyncTree(exampleAsyncTree)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("demo:asyncTree:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        ExampleAsyncTreeEntity exampleAsyncTree = exampleAsyncTreeService.findExampleAsyncTreeById(id);
        mmap.put("exampleAsyncTree", exampleAsyncTree);
        return prefix + "/edit";
    }

    /**
     * 修改保存测试异步树
     */
    @RepeatSubmit
    @RequiresPermissions("demo:asyncTree:edit")
    @BussinessLog(title = "测试异步树", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editExampleAsyncTree(ExampleAsyncTreeEntity exampleAsyncTree){
        //校验参数
		ValidatorUtil.validateEntity(exampleAsyncTree);
        return exampleAsyncTreeService.updateExampleAsyncTreeById(exampleAsyncTree)? success(): error("修改失败!");
    }
    
    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("demo:asyncTree:del")
    @BussinessLog(title = "测试异步树", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseData del(@PathVariable("id") Long id) {
        return exampleAsyncTreeService.delExampleAsyncTreeById(id)? success(): error("删除失败!");
    }

}
