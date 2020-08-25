package com.j2eefast.common.core.listener;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.j2eefast.common.core.base.entity.annotaion.JsonListFiledIgnore;
import com.j2eefast.common.core.enums.ConvertType;
import com.j2eefast.common.core.utils.JSON;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

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
        if(body instanceof ResponseData ){
            ResponseData rd = (ResponseData) body;
            try{
               if( rd.get("data") instanceof PageUtil){
                   PageUtil page = (PageUtil) rd.get("data");
                   List<String> rmfields = new ArrayList<>();
                   List<Map<String,ConvertType>> conver = new ArrayList<>();
                   if(page.getList().size() > 0){
                       //获取List 对象 遍历 是否有禁止输出或者转换的字段
                       Class clazz = page.getList().get(0).getClass();
                       List fieldsList = new ArrayList<Field>();
                       while (clazz != null) {
                           // 遍历所有父类字节码对象
                           Field[] declaredFields = clazz.getDeclaredFields();
                           //将`Filed[]`数组转换为`List<>`然后再将其拼接至`ArrayList`上
                           fieldsList.addAll(Arrays.asList(declaredFields));
                           // 获得父类的字节码对象
                           clazz = clazz.getSuperclass();
                       }

                       for (Object field : fieldsList) {
                           Field f = ((java.lang.reflect.Field)field);
                           if(f.isAnnotationPresent(JsonListFiledIgnore.class)){
                               for (Annotation anno : f.getDeclaredAnnotations()) {
                                   if(anno.annotationType().equals(JsonListFiledIgnore.class) ){
                                       if(((JsonListFiledIgnore)anno).exist()){
                                           rmfields.add(f.getName());
                                       }else{
                                           ((JsonListFiledIgnore)anno).convert();
                                           Map<String,ConvertType> map = new HashMap<>();
                                           map.put(f.getName(),((JsonListFiledIgnore)anno).convert());
                                           conver.add(map);
                                       }
                                   }
                               }
                           }
                       }
                   }
                   if(ToolUtil.isEmpty(rmfields)){
                       return body;
                   }
                   String q = JSON.marshal(rd);
                   cn.hutool.json.JSONObject c = JSONUtil.parseObj(q);
                   JSONArray a =  c.getJSONObject("data").getJSONArray("list");
                   for (Iterator iter = a.iterator(); iter.hasNext();) {
                       cn.hutool.json.JSONObject str = (cn.hutool.json.JSONObject)iter.next();
                       rmfields.forEach(x->{
                           str.remove(x);
                       });
                       if(ToolUtil.isNotEmpty(conver)){
                           conver.forEach(x->{
                               for(String key : x.keySet()){
                                   ConvertType value = x.get(key);
                                   if(value.equals(ConvertType.PHONE)){
                                       str.put(key,str.getStr(key).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                                   }
                               }
                           });
                       }
                   }
                   System.out.println("之后:" + c.toString());
                   JSONObject jsonObject = JSONObject.parseObject(c.toString());
                   return  jsonObject;
               }
            }catch (Exception e){
               e.printStackTrace();
            }
        }
//
//        Object o = rd.get("data");
//        System.out.println(o.toString());
        return body;
    }
}