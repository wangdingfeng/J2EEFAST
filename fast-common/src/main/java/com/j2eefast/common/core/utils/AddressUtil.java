/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 远程地址查询
 * @author zhouzhou
 * @date 2017-03-12 16:41
 */
public class AddressUtil {
	
	 private static final Logger 					LOG 					= LoggerFactory.getLogger(AddressUtil.class);
	 //百度API查询 个人一天1000次
     public static final String IP_BAIDU_URL =  "http://api.map.baidu.com/location/ip?ak=gRhqOOqPOQzvM8nMRnVoQswejvggglqY&ip={}&coor=bd09ll";
     //百度用完转换
     public static final String IP_WHOIS_URL =  "http://whois.pconline.com.cn/ipJson.jsp?ip={}&json=true";
     //国外免费查询IP地址
     //http://ip-api.com/json/8.210.134.174?lang=zh-CN

     public static String getRealAddressByIP(String ip) {
        String address = "XX XX";
        // 内网不查询
        if (!Validator.isIpv4(ip) || NetUtil.isInnerIP(ip)){
            return "内网IP";
        }
        JSONObject obj;
        try{
            HttpResponse body = HttpRequest.get(StrUtil.format(IP_BAIDU_URL, ip)).charset("GBK").execute();
            if (StrUtil.isBlank(body.body())){
                LOG.error("获取地理位置异常 {}", ip);
                return address;
            }
            obj = JSONUtil.parseObj(body.body());
            //{"address":"CN|上海|上海|None|CHINANET|0|0","content":{"address_detail":{"province":"上海市","city":"上海市","street":"","district":"","street_number":"","city_code":289},"address":"上海市","point":{"x":"121.48789949","y":"31.24916171"}},"status":0}
            int error_code = obj.getInt("status",-1);
            if(error_code == 0) {
                JSONObject data = obj.getJSONObject("content").getJSONObject("address_detail");
                String country = obj.getStr("address", "CN|上海|上海|None|CHINANET|0|0").split("\\|")[0];
                String province = data.getStr("province", "上海");
                String city = data.getStr("city", "上海市");
                address = StrUtil.format("{} {}-{}", country,province,city);
            }else {
                body = HttpRequest.get(StrUtil.format(IP_WHOIS_URL, ip)).charset("GBK").execute();
                if (StrUtil.isBlank(body.body()))
                {
                    LOG.error("获取地理位置异常 {}", ip);
                    return address;
                }
                obj = JSONUtil.parseObj(body.body());
                if(ToolUtil.isNotEmpty(obj.getStr("addr"))){
                    address = obj.getStr("addr");
                }else{
                    LOG.error("获取地理位置API用尽");
                }
            }
        }
        catch (Exception e){
            LOG.error("获取地理位置异常 {}", ip);
        }
        return address;
    }
}
