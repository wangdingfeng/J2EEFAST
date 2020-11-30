/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.common.core.shiro.ShiroSession;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.entity.server.OnlineEntity;
import com.j2eefast.framework.sys.service.SysUserService;
import com.j2eefast.framework.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: fast
 * @Package: com.j2eefast.system.monitor.controller
 * @ClassName: SysUserOnlineController
 * @Author: zhouzhou Emall:loveingowp@163.com
 * @Description:
 * @Date: 2019/12/23 17:58
 * @Version: 1.0
 */
@Slf4j
@Controller
@RequestMapping("/sys/online")
public class SysUserOnlineController extends BaseController {
    private String urlPrefix = "modules/sys/monitor";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisUtil redisUtil;

    @Lazy
    @Resource
    private RedisSessionDAO redisSessionDAO;

    @RequiresPermissions("sys:online:view")
    @GetMapping()
    public String online() {
        return urlPrefix + "/online";
    }

    /**
     * 获取用户监控
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:online:list")
    @ResponseBody
    public ResponseData monitor(@RequestParam Map<String, Object> params) {

        //当前页
        int currPage = Integer.parseInt((String) params.get("__page"));
        //每页条数
        int limit = Integer.parseInt((String) params.get("__limit"));

        Collection<Session> list = redisSessionDAO.getActiveSessions();
        List<OnlineEntity> userList = new ArrayList<>();
        for (Session session : list) {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                break;
            }
            if (session instanceof ShiroSession) {
                // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                ShiroSession ss = (ShiroSession) session;
                if (!ss.isChanged()) {
                    break;
                }
            }
            Subject s = new Subject.Builder().session(session).buildSubject();
            if (s.isAuthenticated()) {
                OnlineEntity online = new OnlineEntity();
                LoginUserEntity user = (LoginUserEntity) s.getPrincipal();
                online.setUserId(user.getId());
                online.setLoginIp(session.getHost());
                online.setName(user.getName());
                online.setUsername(user.getUsername());
                online.setCompName(user.getCompName());
                online.setLoginLocation(user.getLoginLocation());
                online.setLoginStatus(ToolUtil.isNotEmpty(user.getLoginStatus())?user.getLoginStatus():1);
                online.setSId(session.getId().toString());
                online.setLoginTime(session.getStartTimestamp());
                userList.add(online);
            }
        }
        int totalCount = userList.size();
        if(userList.size() <= limit){
            return success(new PageUtil(userList,totalCount,limit,1));
        }else{
            List<OnlineEntity> viewList = new ArrayList<>();
            int total = currPage * limit;
            int qTotal = (currPage -1) * limit;
            if(total > totalCount){
                for(int i = qTotal; i < userList.size(); i++){
                    viewList.add(userList.get(i));
                }
            }
            if(total <= totalCount){
                for(int i = qTotal; i < (userList.size() - limit); i++){
                    viewList.add(userList.get(i));
                }
            }
            return success(new PageUtil(viewList,totalCount,limit,currPage));
        }
    }


    @BussinessLog(title = "用户管理", businessType = BusinessType.FORCE)
    @RequestMapping(value = "/batchForceLogout", method = RequestMethod.POST)
    @RequiresPermissions("sys:online:forceLogout")
    @ResponseBody
    public ResponseData monitorOut(String[] ids) {
        try{
            for(String sessionId: ids){
                if (UserUtils.getSession().getId().equals(sessionId)) {
                    return error("自己不能踢自己下线操作!");
                }
                Session session = redisSessionDAO.readSession(sessionId);
                Subject subject = new Subject.Builder().session(session).buildSubject();
                LoginUserEntity loginUserEntity =  (LoginUserEntity) subject.getPrincipal();
                if(ToolUtil.isNotEmpty(loginUserEntity.getLoginStatus()) && loginUserEntity.getLoginStatus() != -9){
                    ((LoginUserEntity) subject.getPrincipal()).setLoginStatus(-9);
                    redisSessionDAO.update(session);
                }
                if(ToolUtil.isEmpty(loginUserEntity.getLoginStatus())){
                    ((LoginUserEntity) subject.getPrincipal()).setLoginStatus(-9);
                    redisSessionDAO.update(session);
                }
            }
            return success();
        }catch (Exception e){
            log.error("提出用户异常!",e);
            return error("提出用户异常!");
        }
    }
}
