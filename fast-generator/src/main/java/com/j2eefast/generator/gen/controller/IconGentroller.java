/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.generator.gen.controller;

import com.j2eefast.common.core.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author: zhouzhou Emall:loveingowp@163.com
 * @time 2020/2/5 19:54
 * @version V1.0
 */
@Controller
@RequestMapping("/tool/icon")
public class IconGentroller  extends BaseController {

    private String urlPrefix = "modules/tool/icon";

    @RequiresPermissions("tool:icon:view")
    @GetMapping()
    public String icon()
    {
        return urlPrefix + "/icon";
    }
}
