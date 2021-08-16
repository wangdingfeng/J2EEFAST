/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysMenuEntity;
import com.j2eefast.framework.sys.entity.SysMsgPushEntity;
import com.j2eefast.framework.sys.service.SysMenuService;
import com.j2eefast.framework.sys.service.SysModuleService;
import com.j2eefast.framework.sys.service.SysMsgPushService;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>通用控制器</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-07 16:33
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Controller
public class SystemController extends BaseController {

	@Autowired
	private SysMsgPushService sysMsgPushService;

	/**
	 * 首页控制
	 */
	@Value("#{${web.view.roleMain} ?: null}")
	private LinkedHashMap<String, String> roleMainMap ;

	/**
	 * 主页
	 * @param mmap
	 * @return
	 */
	@GetMapping(value = { "/", "/index","/index.html" })
	public String index(ModelMap mmap) {
		LoginUserEntity user = UserUtils.getUserInfo();
		List<Map<String, Object>> modules = user.getModules();
		Map<String, List<SysMenuEntity>> menuList = new HashMap<>();
		for(Map<String, Object> s: modules){
			List<SysMenuEntity> menu = ConstantFactory.me().getMenuByUserIdModuleCode(user.getId(),
					(String) s.get("moduleCode"),user);
			menuList.put((String) s.get("moduleCode"),menu);
		}
		mmap.put("modules",modules);
		mmap.put("menuList",menuList);
		mmap.put("user",user);
		//是否为管理员租户
		mmap.put("superTenant",user.getSuperTenant());
		return "index";
	}


	// 切换主题
	@GetMapping("/sys/switchSkin")
	public String switchSkin(ModelMap mmap){
		return "skin";
	}

	// 浏览器版本过低
	@GetMapping("upbw/index")
	public String upbw(ModelMap mmap){
		return "modules/sys/upbw/index";
	}

	@RequestMapping(value = "/sys/getMsgPush", method = RequestMethod.POST)
	@ResponseBody
	public ResponseData getMsgPush(){
		SysMsgPushEntity sysMsgPushEntity = new SysMsgPushEntity();
		sysMsgPushEntity.setUserId(UserUtils.getUserId());
		sysMsgPushEntity.setIsRead("0");
		List<SysMsgPushEntity>  sysMsgPushEntityList = sysMsgPushService.findUserList(sysMsgPushEntity);
		return ToolUtil.isNotEmpty(sysMsgPushEntityList)?ResponseData.success(sysMsgPushEntityList):ResponseData.error("00001","未有新的消息");
	}

	@GetMapping("static/notice/{htmlNo}")
	public String statics(@PathVariable("htmlNo") String htmlNo){
		return "modules/static/" + htmlNo;
	}



	@RequestMapping("main")
	public String main(ModelMap mmap) {

		//默认主页
		String url = "main";

		if(ToolUtil.isNotEmpty(roleMainMap)){
			for(String key:roleMainMap.keySet()){
				//key
				if(key.indexOf("|") > 0){
					if(UserUtils.getUserInfo().getRoleKey().size() > 1){
						continue;
					}
					String[] keys = key.split("\\|");
					if(UserUtils.hasAnyRoleKeys(keys)){
						url = roleMainMap.get(key);
						break;
					}
				}else if(key.indexOf("&") > 0){
					String[] keys = key.split("&");
					if(keys.length == UserUtils.getUserInfo().getRoleKey().size()){
						if(UserUtils.hasAnyRoleKeys(keys)){
							url = roleMainMap.get(key);
							break;
						}
					}
				}else{
					if(UserUtils.hasRole(StrUtil.trim(key))){
						url = roleMainMap.get(key);
						break;
					}
				}
			}
		}

		mmap.put("compId",UserUtils.getUserInfo().getCompId());
		mmap.put("user",UserUtils.getUserInfo());
		mmap.put("compName",UserUtils.getUserInfo().getCompName());

		return url;
	}

	@RequestMapping("404.html")
	public String notFound() {
		return "404";
	}
}
