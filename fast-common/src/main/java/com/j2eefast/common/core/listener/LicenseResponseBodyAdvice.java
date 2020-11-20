/**
 * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.j2eefast.common.core.base.entity.BaseEntity;
import com.j2eefast.common.core.base.entity.annotaion.JsonListBaselgonre;
import com.j2eefast.common.core.base.entity.annotaion.JsonListFiledIgnore;
import com.j2eefast.common.core.enums.ConvertType;
import com.j2eefast.common.core.utils.JSON;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * pageList 数据拦截
 * @author huanzhou
 * @date: 2020-03-18 09:15
 */
@Slf4j
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
                   //需要删除输出的字段
                   List<String> rmfields = new ArrayList<>();
                   //需要转换的字段
                   List<Map<String,ConvertType>> conver = new ArrayList<>();
                   List<Map<String,String[]>> objList = new ArrayList<>();
                   if(page.getList().size() > 0){
                       //获取List 对象 遍历 是否有禁止输出或者转换的字段
                       Class clazz = page.getList().get(0).getClass();
                       //获取类上所有注解
                       Annotation[] annotations = clazz.getAnnotations();
                       for(Annotation annotation: annotations){
                           //类上有基类输出注解
                           if(annotation.annotationType().equals(JsonListBaselgonre.class)){
                                if(ToolUtil.isEmpty(((JsonListBaselgonre)annotation).fileds())){
                                    Field[] fields = BaseEntity.class.getDeclaredFields();
                                    for(Field f: fields){
                                        rmfields.add(f.getName());
                                    }
                                }else{
                                    //剔除没有标注的字段
                                    Field[] fields = BaseEntity.class.getDeclaredFields();
                                    String[] fs = ((JsonListBaselgonre)annotation).fileds();
                                    for(Field f: fields){
                                        for(String ft: fs){
                                            boolean temp = false;
                                            if(f.getName().equals(ft)){
                                                temp = true;
                                            }
                                            if(!temp){
                                                rmfields.add(f.getName());
                                            }
                                        }
                                    }
                                }
                           }
                       }
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
                                           if(!((JsonListFiledIgnore)anno).convert().equals(ConvertType.EMPTY)){
                                               Map<String,ConvertType> map = new HashMap<>();
                                               map.put(f.getName(),((JsonListFiledIgnore)anno).convert());
                                               conver.add(map);
                                           }
                                           if(ToolUtil.isNotEmpty(((JsonListFiledIgnore)anno).objectFiled())){
                                               Map<String,String[]> map = new HashMap<>();
                                               map.put(f.getName(),((JsonListFiledIgnore)anno).objectFiled());
                                               objList.add(map);
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }
                   if(ToolUtil.isEmpty(rmfields) && ToolUtil.isEmpty(conver) && ToolUtil.isEmpty(objList)){
                       return body;
                   }
                   String q = JSON.marshal(rd);
                   cn.hutool.json.JSONObject c = JSONUtil.parseObj(q);
                   JSONArray a =  c.getJSONObject("data").getJSONArray("list");
                   for (Iterator iter = a.iterator(); iter.hasNext();) {
                       cn.hutool.json.JSONObject str = (cn.hutool.json.JSONObject)iter.next();

                       //剔除不输出前端数据
                       if(ToolUtil.isNotEmpty(rmfields)){
                           rmfields.forEach(x->{
                               str.remove(x);
                           });
                       }

                       //需要转换输出前端的数据
                       if(ToolUtil.isNotEmpty(conver)){
                           conver.forEach(x->{
                               for(String key : x.keySet()){
                                   ConvertType value = x.get(key);
                                   // 手机号码隐藏转换
                                   if(value.equals(ConvertType.PHONE)){
                                       if(!ToolUtil.isEmpty(str.getStr(key))){
                                           str.put(key,str.getStr(key).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
                                       }else {
                                           str.put(key, StrUtil.EMPTY);
                                       }

                                   }
                                   // 邮箱隐藏转换
                                   if(value.equals(ConvertType.EMAIL)){
                                       if(!ToolUtil.isEmpty(str.getStr(key))){
                                           str.put(key,str.getStr(key).replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4"));
                                       }else {
                                           str.put(key, StrUtil.EMPTY);
                                       }
                                   }
                               }
                           });
                       }

                       //字段对象中输出前端的数据
                       if(ToolUtil.isNotEmpty(objList)){
                           objList.forEach(x->{
                               for(String key : x.keySet()){
                                 String[]  value = x.get(key);
                                   cn.hutool.json.JSONObject ob =  str.getJSONObject(key);
                                   List<String> show = new ArrayList<>();
                                   for(Map.Entry<String, Object> entry :ob.entrySet()){
                                       boolean mark = true;
                                       for(String s: value){
                                          if(entry.getKey().equals(s)){
                                              mark = false;
                                              break;
                                          }
                                       }
                                       if(mark){
                                           show.add(entry.getKey());
                                       }
                                   }
                                   if(ToolUtil.isNotEmpty(show)){
                                       show.forEach(k->{
                                           str.getJSONObject(key).remove(k);
                                       });
                                   }
                               }
                           });
                       }
                   }
                   JSONObject jsonObject = JSONObject.parseObject(c.toString());
                   return  jsonObject;
               }
            }catch (Exception e){
                log.error("转换输出数据异常:",e);
            }
        }
        return body;
    }
}