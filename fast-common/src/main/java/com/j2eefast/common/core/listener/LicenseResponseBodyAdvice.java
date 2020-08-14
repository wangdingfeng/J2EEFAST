package com.j2eefast.common.core.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.j2eefast.common.core.utils.JSON;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;
import java.util.Iterator;

@ControllerAdvice
public class LicenseResponseBodyAdvice implements ResponseBodyAdvice {


    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
//        if(body instanceof ResponseData ){
//            ResponseData rd = (ResponseData) body;
//            try{
//               if( rd.get("data") instanceof PageUtil){
//                   PageUtil page = (PageUtil) rd.get("data");
//                   if(page.getList().size() > 0){
//                      System.out.println("--->>>>>>>>>>>>:"+page.getList().get(0).getClass());
//                   }
//                   String q = JSON.marshal(rd);
//                   cn.hutool.json.JSONObject c = JSONUtil.parseObj(q);
//                   JSONArray a =  c.getJSONObject("data").getJSONArray("list");
//                   for (Iterator iter = a.iterator(); iter.hasNext();) {
//                       cn.hutool.json.JSONObject str = (cn.hutool.json.JSONObject)iter.next();
//                       str.remove("nowLoginLocation");
//                       str.remove("deptIdList");
//                       str.remove("roleIdList");
//                       str.remove("remark");
//                       str.remove("pwdUpdateDate");
//                       str.remove("sql_filter");
//                   }
//                   System.out.println("之后:" + c.toString());
////                System.out.println(JSONUtil.parseObj(q).getJSONObject("data").getJSONArray("list").toString());
//                   JSONObject jsonObject = JSONObject.parseObject(c.toString());
//                   return  jsonObject;
//               }
//            }catch (Exception e){
//
//            }
//        }
//
//        Object o = rd.get("data");
//        System.out.println(o.toString());
        return body;
    }
}