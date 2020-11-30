package com.j2eefast.generator.gen.controller;

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
import java.util.Date;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.generator.gen.entity.ExampleTestEntity;
import com.j2eefast.generator.gen.service.ExampleTestService;
import com.j2eefast.generator.gen.entity.ExampleTestChildEntity;
import com.j2eefast.generator.gen.service.ExampleTestChildService;
import java.util.List;

/**
 * 单范例页面控制器
 * @author ZhouZhou
 * @date 2020-11-24 20:40
 */
@Controller
@RequestMapping("/gen/test")
public class ExampleTestController extends BaseController{

    private String prefix = "modules/gen/test";
    @Autowired
    private ExampleTestService exampleTestService;
    
    @Autowired
    private ExampleTestChildService exampleTestChildService;

    
    @RequiresPermissions("gen:test:view")
    @GetMapping()
    public String test(){
        return prefix + "/test";
    }
        
    @RequiresPermissions("gen:test:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,ExampleTestEntity exampleTestEntity) {        
        PageUtil page = exampleTestService.findPage(params,exampleTestEntity);        
		return success(page);
    }    
            
    @GetMapping("/add")
    public String add(ModelMap mmap){
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("gen:test:add")
    @BussinessLog(title = "单范例", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addExampleTest(@Validated ExampleTestEntity exampleTest){
        //校验参数
        ValidatorUtil.validateEntity(exampleTest);
        return exampleTestService.addExampleTest(exampleTest)? success(): error("新增失败!");
    }    
    
    @RequiresPermissions("gen:test:list")
    @RequestMapping(value = "/child/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData childList(@RequestParam Map<String, Object> params) {
        PageUtil page = exampleTestChildService.findPage(params);
        return success(page);
    }    

    
    /**
     * 修改
     */
    @RequiresPermissions("gen:test:edit")
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
    @RequiresPermissions("gen:test:edit")
    @BussinessLog(title = "单范例", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editExampleTest(ExampleTestEntity exampleTest){
		ValidatorUtil.validateEntity(exampleTest);
        return exampleTestService.updateExampleTestById(exampleTest)? success(): error("修改失败!");
    }    

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("gen:test:del")
    @BussinessLog(title = "单范例", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return exampleTestService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }    
}
