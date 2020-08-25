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
                   List<Map<String,String[]>> objList = new ArrayList<>();
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
                           System.out.println("字段:" + f.getName() + " 类型:" + f.getType().getName());
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
                                   if(value.equals(ConvertType.PHONE)){
                                       str.put(key,str.getStr(key).replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
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
               e.printStackTrace();
            }
        }
        return body;
    }
}