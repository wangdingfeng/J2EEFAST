/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.config.service.SysConfigService;
import com.j2eefast.framework.ueditor.ActionEnter;
import com.j2eefast.framework.utils.Global;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <p>自定义百度富文本控制类</p>
 *
 * @author: zhouzhou
 * @date: 2019-03-29 17:22
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Controller
@RequestMapping({"/file/ueditor/"})
public class SysUeditorController {

    @Autowired
    private SysConfigService sysConfigService;

    @RequestMapping({"/config"})
    @ResponseBody
    public void config(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json");
            String rootPth = Global.getRootPath();
            String exec = new ActionEnter(request, rootPth,"config").exec();
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
