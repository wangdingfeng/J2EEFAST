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
import com.j2eefast.web.test.entity.ExampleTestEntity;
import com.j2eefast.web.test.service.ExampleTestService;
import com.j2eefast.web.test.service.ExampleTestChildService;
import org.apache.shiro.authz.annotation.Logical;

/**
 * 单范例页面控制器
 * @author ZhouZhou
 * @date 2021-08-16 20:41:56
 */
@Controller
@RequestMapping("/demo/testData")
public class ExampleTestController extends BaseController{

    private String prefix = "modules/demo/testData";

    @Autowired
    private ExampleTestService exampleTestService;
    @Autowired
    private ExampleTestChildService exampleTestChildService;

    /**
     * 视图页面
     */
    @RequiresPermissions("demo:testData:view")
    @GetMapping()
    public String testData(){
        return prefix + "/testData";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param exampleTestEntity
     * @return
     */
    @RequiresPermissions("demo:testData:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,ExampleTestEntity exampleTestEntity) {
        PageUtil page = exampleTestService.findPage(params,exampleTestEntity);
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
    @RequiresPermissions("demo:testData:add")
    @BussinessLog(title = "单范例", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addExampleTest(@Validated ExampleTestEntity exampleTest){
        //校验参数
        ValidatorUtil.validateEntity(exampleTest);
        return exampleTestService.addExampleTest(exampleTest)? success(): error("新增失败!");
    }

    @RequiresPermissions("demo:testData:list")
    @RequestMapping(value = "/child/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData childList(@RequestParam Map<String, Object> params) {

        PageUtil page = exampleTestChildService.findPage(params);

        return success(page);
    }

    /**
     * 修改
     */
    @RequiresPermissions("demo:testData:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        ExampleTestEntity exampleTest = exampleTestService.findExampleTestById(id);
        mmap.put("exampleTest", exampleTest);
        return prefix + "/edit";
    }

    /**
     * 修改保存单范例
     */
    @RepeatSubmit
    @RequiresPermissions("demo:testData:edit")
    @BussinessLog(title = "单范例", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editExampleTest(ExampleTestEntity exampleTest){
        //校验参数
		ValidatorUtil.validateEntity(exampleTest);
        return exampleTestService.updateExampleTestById(exampleTest)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("demo:testData:del")
    @BussinessLog(title = "单范例", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return exampleTestService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

    /**
     * 校验员工编号
     */
    @RequestMapping(value = "/checkCodeUnique", method = RequestMethod.POST)
    @RequiresPermissions(value = {"demo:testData:add", "demo:testData:edit"}, logical = Logical.OR)
    @ResponseBody
    public ResponseData checkCodeUnique(ExampleTestEntity exampleTest) {
        if(exampleTestService.checkCodeUnique(exampleTest)){
            return success();
        }
        return error("员工编号已经存在!");
    }

}
