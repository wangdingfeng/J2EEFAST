/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.modules.demo.contorller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.framework.utils.Constant;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * 前端演示Demo
 * @author huanzhou
 * @Date 2020-06-19
 */
@Controller
@RequestMapping("/sys/demo")
public class SysDemoController extends BaseController {

//    @Autowired
//    private RedisTemplate 							redisTemplate;

    private String urlPrefix = "modules/sys/demo";

    /**
     * 前端模板引擎说明
     * @param mmap
     * @return
     */
    @RequiresPermissions("sys:demo:mode")
    @GetMapping("/mode")
    public String mode(ModelMap mmap) {
        mmap.put("mode",FileUtil.readUtf8String(Constant.BASE_WEB_HTML +urlPrefix+ "/mode.txt"));
        return urlPrefix + "/mode";
    }

    @RequiresPermissions("sys:demo:component")
    @GetMapping("/component")
    public String component() {
        return urlPrefix + "/component";
    }

    @GetMapping("/table")
    public String table(){
        return urlPrefix + "/table";
    }

    @GetMapping("/fromTable")
    public String fromTable(){
        return urlPrefix + "/fromTable";
    }
    /**
     * 创建表单视图
     * @return
     */
    @RequiresPermissions("sys:demo:addfrom")
    @GetMapping("/addfrom")
    public String addfrom() {
        return urlPrefix + "/addfrom";
    }

    /**
     * 图表示例
     * @return
     */
    @RequiresPermissions("sys:demo:addrpert")
    @GetMapping("/addrpert")
    public String addrpert() {
        return urlPrefix + "/addrpert";
    }
    
    
    /**
     * 多表联动示例
     */
    @RequiresPermissions("sys:demo:multiTable")
    @GetMapping("/multiTable")
    public String multiTable() {
        return urlPrefix + "/multiTable";
    }
    
    /**
     * 消息组件
     * @return
     */
    @RequiresPermissions("sys:demo:msg")
    @GetMapping("/msg")
    public String msg() {
        return urlPrefix + "/msg";
    }



    /**
     * 表单导向案例
     * @return
     */
    @RequiresPermissions("sys:demo:wizard")
    @GetMapping("/wizard")
    public String wizard(ModelMap mmap) {

        /** 测试数据*/
        List<Map<String,String>> listMap = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("key","default");//, arrows, dots, progress
        map.put("value","default");
        listMap.add(map);
        Map<String, String> map1 = new HashMap<>();
        map1.put("key","arrows");//, arrows, dots, progress
        map1.put("value","arrows");
        listMap.add(map1);
        Map<String, String> map2 = new HashMap<>();
        map2.put("key","dots");//, arrows, dots, progress
        map2.put("value","dots");
        listMap.add(map2);
        Map<String, String> map3 = new HashMap<>();
        map3.put("key","progress");//, arrows, dots, progress
        map3.put("value","progress");
        listMap.add(map3);
        mmap.put("themeLists",listMap);

        List<Map<String,String>> listMap2 = new ArrayList<>();
        Map<String, String> maap = new HashMap<>();
        maap.put("key","none");
        maap.put("value","None");
        listMap2.add(maap);
        Map<String, String> maap1 = new HashMap<>();
        maap1.put("key","fade");
        maap1.put("value","Fade");
        listMap2.add(maap1);
        Map<String, String> maap2 = new HashMap<>();
        maap2.put("key","slide-horizontal");
        maap2.put("value","Slide Horizontal");
        listMap2.add(maap2);
        Map<String, String> maap3 = new HashMap<>();
        maap3.put("key","slide-vertical");
        maap3.put("value","Slide Vertical");
        listMap2.add(maap3);
        Map<String, String> maap4 = new HashMap<>();
        maap4.put("key","slide-swing");
        maap4.put("value","Slide Swing");
        listMap2.add(maap4);
        mmap.put("animationLists",listMap2);


        List<Map<String,String>> listMap3 = new ArrayList<>();
        Map<String, String> maasp1 = new HashMap<>();
        maasp1.put("key","1");
        maasp1.put("value","1");
        listMap3.add(maasp1);
        Map<String, String> maasp2 = new HashMap<>();
        maasp2.put("key","2");
        maasp2.put("value","2");
        listMap3.add(maasp2);
        Map<String, String> maasp3 = new HashMap<>();
        maasp3.put("key","3");
        maasp3.put("value","3");
        listMap3.add(maasp3);
        Map<String, String> maasp4 = new HashMap<>();
        maasp4.put("key","4");
        maasp4.put("value","4");
        listMap3.add(maasp4);
        mmap.put("stepLists",listMap3);

        List<Map<String,String>> listMap4 = new ArrayList<>();
        Map<String, String> maamp1 = new HashMap<>();
        maamp1.put("key","751312449@qq.com");
        maamp1.put("value","751312449@qq.com");
        listMap4.add(maamp1);
        Map<String, String> maamp2 = new HashMap<>();
        maamp2.put("key","534354354334543");
        maamp2.put("value","534354354334543");
        listMap4.add(maasp2);
        mmap.put("cardLists",listMap4);
        return urlPrefix + "/wizard";
    }



    @GetMapping("/test")
    public String test(ModelMap mmap) {
        mmap.put("mode",FileUtil.readUtf8String(Constant.BASE_WEB_HTML +urlPrefix+ "/mode1.txt"));
        return urlPrefix + "/test";
    }



//    获取redis信息
//    @RequestMapping(value = "/getRedis", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseData getRedis() {
//        Properties info = (Properties) redisTemplate.execute((RedisCallback) redisConnection -> redisConnection.info());
//        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback) redisConnection -> redisConnection.info("commandstats"));
//        Object dbSize = redisTemplate.execute((RedisCallback) redisConnection -> redisConnection.dbSize());
//        Map<String, Object> result = new HashMap<>(4);
//        result.put("info", info);
//        result.put("dbSize", dbSize);
//        result.put("time", DateUtil.format(new Date(), DatePattern.NORM_TIME_PATTERN));
//        return success(result);
//    }

}
