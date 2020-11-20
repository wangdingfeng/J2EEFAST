/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.modules.sys.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.common.core.license.annotation.FastLicense;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.shiro.LoginType;
import com.j2eefast.framework.shiro.UserToken;
import com.j2eefast.framework.sys.entity.SysDictDataEntity;
import com.j2eefast.framework.sys.service.SysDictDataService;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.Global;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.j2eefast.common.core.crypto.SoftEncryption;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.exception.ServiceException;
import com.j2eefast.common.core.io.file.MimeType;
import com.j2eefast.common.core.manager.AsyncManager;
import com.j2eefast.framework.utils.RedisKeys;
import com.j2eefast.framework.utils.UserUtils;

/**
 * 
 * 登陆控制类
 * @author zhouzhou
 * @date 2018-11-14 23:28
 */
@Controller
public class SysLoginController extends BaseController {

	@Autowired
	private SysDictDataService sysDictDataService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private CaptchaService captchaService;
	
	/**
	 * <p>生成验证码图片 系统参数管理配置</p>
	 * <p>默认0 图形 1 算数 2 图形算数随机出现</p>
	 * @author zhouzhou
	 * @date 2020-03-07 14:46
	 */
	@RequestMapping(value = "captcha.gif", method = RequestMethod.GET)
	public void captcha(HttpServletResponse response) throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType(MimeType.IMAGE_GIF);
		if(Global.getDbKey(ConfigConstant.SYS_LOGIN_CAPTACHA_TYPE,Constant.SYS_DEFAULT_VALUE_ZERO)
				.equals(Constant.SYS_DEFAULT_VALUE_ONE)){
			ArithmeticCaptcha gifCaptcha = new ArithmeticCaptcha();
			// 几位数运算，默认是两位
			gifCaptcha.setLen(3);
			// 获取运算的公式：3+2=?
			gifCaptcha.getArithmeticString();
			// 获取运算的结果：5
			String result =  gifCaptcha.text();
			UserUtils.setSessionAttribute(Constant.KAPTCHA_SESSION_KEY, result);
			gifCaptcha.out(response.getOutputStream());
			return;
		}else if(Global.getDbKey(ConfigConstant.SYS_LOGIN_CAPTACHA_TYPE,Constant.SYS_DEFAULT_VALUE_ZERO)
				.equals(Constant.SYS_DEFAULT_VALUE_TWO)){
			int rd= Math.random()>0.5?1:0;
			if(rd == 1){
				GifCaptcha gifCaptcha = new GifCaptcha(130,48,4);
				gifCaptcha.setCharType(Captcha.TYPE_DEFAULT);
				String result = gifCaptcha.text();
				UserUtils.setSessionAttribute(Constant.KAPTCHA_SESSION_KEY, result);
				gifCaptcha.out(response.getOutputStream());
				return;
			}else{
				ArithmeticCaptcha gifCaptcha = new ArithmeticCaptcha();
				// 几位数运算，默认是两位
				gifCaptcha.setLen(3);
				// 获取运算的公式：3+2=?
				gifCaptcha.getArithmeticString();
				// 获取运算的结果：5
				String result =  gifCaptcha.text();
				UserUtils.setSessionAttribute(Constant.KAPTCHA_SESSION_KEY, result);
				gifCaptcha.out(response.getOutputStream());
				return;
			}
		}else{
			GifCaptcha gifCaptcha = new GifCaptcha(130,48,4);
			gifCaptcha.setCharType(Captcha.TYPE_DEFAULT);
			String result = gifCaptcha.text();
			UserUtils.setSessionAttribute(Constant.KAPTCHA_SESSION_KEY, result);
			gifCaptcha.out(response.getOutputStream());
			return;
		}
	}


	/**
	 * 后台登录页面
	 * @param mmp
	 * @return
	 */
	@RequestMapping("login")
	public String login(ModelMap mmp) throws IOException {
		if(UserUtils.isLogin()){
			UserUtils.logout();
		}
		String view = super.getPara("view");
		if(ToolUtil.isEmpty(view)){
			view = Global.getDbKey(ConfigConstant.SYS_LOGIN_DEFAULT_VIEW,Constant.ADMIN_LTE);
		}else{
			List<SysDictDataEntity> listView = sysDictDataService.selectDictDataByType("sys_login_view");
			boolean flag = false;
			for(SysDictDataEntity dict: listView){
				if(dict.getDictValue().equals(view)){
					flag = true;
					break;
				}
			}
			if(!flag){
				view = Global.getDbKey(ConfigConstant.SYS_LOGIN_DEFAULT_VIEW,Constant.ADMIN_LTE);
			}
		}
		mmp.put("loginView",view);
		mmp.put("verification",Global.getDbKey(ConfigConstant.SYS_LOGIN_VERIFICATION,Constant.SYS_DEFAULT_VALUE_ONE)
				.equals(Constant.SYS_DEFAULT_VALUE_ONE));
		return "login-" + view;
	}




	/**
	 * 登陆
	 * @author zhouzhou
	 * @date 2020-03-07 14:47
	 */
//	@FastLicense(vertifys = {"online","detection"})
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseData login(String username, String password,Boolean rememberMe) {

		if(Global.getDbKey(ConfigConstant.SYS_LOGIN_VERIFICATION,Constant.SYS_DEFAULT_VALUE_ONE)
				.equals(Constant.SYS_DEFAULT_VALUE_ONE)){
			//后台二次认证 图形验证码
			CaptchaVO captchaVO = new CaptchaVO();
			captchaVO.setCaptchaVerification(super.getPara("__captchaVerification"));
			if(!captchaService.verification(captchaVO).isSuccess()){
				return error("50004","图形验证码不正确!");
			}
		}

		String secretKey = super.getCookie(ConfigConstant.SECRETKEY);
		Subject subject = null;
		try {
			//前端账号密码解密
			username =new String(SoftEncryption.decryptBySM4(Base64.decode(username),
					HexUtil.decodeHex(secretKey)).get("bytes",byte[].class)).trim();
			password =new String(SoftEncryption.decryptBySM4(Base64.decode(password),
					HexUtil.decodeHex(secretKey)).get("bytes",byte[].class)).trim();
			//账号密码登录
			UserToken token = new UserToken(username, password, LoginType.NORMAL.getDesc(),rememberMe);
			subject = UserUtils.getSubject();
			subject.login(token);
			//清除安全key
			super.deleteCookieByName(ConfigConstant.SECRETKEY);
		}catch (ServiceException e){
			return error("50006",ToolUtil.message("sys.login.sm4"));
		}
		catch (AuthenticationException e) {
			RxcException ex = (RxcException) e.getCause();
			String msg = ToolUtil.message("sys.login.failure");
			if(!ToolUtil.isEmpty(e.getMessage())){
				msg = e.getMessage();
			}
			if("50004".equals(ex.getCode())) {
				return error(ex.getCode(),ex.getMessage());
			}
			return error(msg);
		}
		return success("登录成功!");
	}

	/**
	 * 锁屏
	 * @param mmap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Account/Lock",method = RequestMethod.GET)
	public String lock(ModelMap mmap) throws Exception {
		mmap.put("avatar", UserUtils.getUserInfo().getAvatar());
		LoginUserEntity loginUser = UserUtils.getUserInfo();
		loginUser.setLoginStatus(-1);
		UserUtils.reloadUser(loginUser);
		return "lock";
	}


	/**
	 * 锁屏页面登录
	 * @param username 账号
	 * @param password 密码
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/Account/login", method = RequestMethod.POST)
	public ResponseData login(String username, String password, HttpServletRequest request) {

		if(ToolUtil.isNotEmpty(username) && ToolUtil.isNotEmpty(password)){
			LoginUserEntity loginUser = UserUtils.getUserInfo();

			String secretKey = "";
			Cookie[] Cookies = request.getCookies();
			for(int i =0;Cookies !=null && i<Cookies.length;i++){
				Cookie c = Cookies[i];
				if(c.getName().equals(ConfigConstant.SECRETKEY)) {
					request.setAttribute(ConfigConstant.SECRETKEY, c.getValue());
					secretKey = c.getValue();
				}
			}
			try{
				username =new String(SoftEncryption.decryptBySM4(Base64.decode(username),
						HexUtil.decodeHex(secretKey)).get("bytes",byte[].class)).trim();

				password =new String(SoftEncryption.decryptBySM4(Base64.decode(password),
						HexUtil.decodeHex(secretKey)).get("bytes",byte[].class)).trim();

			}catch (Exception e){
				throw new RxcException("解密失败,数据异常","50004");
			}
			if(loginUser.getUsername().equals(username)){
				Integer number = redisUtil.get(RedisKeys.getUserLoginKey(username),Integer.class); //用户锁屏密码错误次数

				if( number != null  && number >= Global.getLoginNumCode()) {
					String kaptcha = UserUtils.getKaptcha(Constant.KAPTCHA_SESSION_KEY);
					String captcha = (String) ServletUtil.getRequest().getParameter("captcha");
					if (ToolUtil.isEmpty(captcha) || !captcha.equalsIgnoreCase(kaptcha)) {
						throw new RxcException(ToolUtil.message("sys.login.code.error"),"50004");
					}
				}
				password = UserUtils.sha256(password, loginUser.getSalt());
				if (password.equals(loginUser.getPassword())){
					loginUser.setLoginStatus(0);
					UserUtils.reloadUser(loginUser);
					UserUtils.getSession().setAttribute("__unlock","unlock");
					//删除登陆错误
					redisUtil.delete(RedisKeys.getUserLoginKey(username));
					return success();
				}else{
					if(number == null) {
						number = 1;
						redisUtil.set(RedisKeys.getUserLoginKey(username), number, RedisUtil.MINUTE * Global.getLockTime());
					}else {
						number++;
						redisUtil.set(RedisKeys.getUserLoginKey(username), number, RedisUtil.MINUTE * Global.getLockTime());
					}
					AsyncManager.me().execute(AsyncFactory.recordLogininfor(username,loginUser.getCompId(),loginUser.getDeptId(), "50010","锁屏账号或密码不正确,输入错误"+number+" 次!"));
					if(number >= Global.getLoginNumCode()) { //错误3次
						throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50004"); //错误3次
					}
					throw new RxcException(ToolUtil.message("sys.login.password.retry.limit.count",Global.getLoginMaxCount()),"50005");
				}
			}else {
				return error("用户名错误");
			}
		}else{
			return error("不能为空!");
		}

	}

	/**
	 * 退出
	 */
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout() {
		UserUtils.getSession().stop();
		UserUtils.logout();
		return REDIRECT+"login";
	}

}
