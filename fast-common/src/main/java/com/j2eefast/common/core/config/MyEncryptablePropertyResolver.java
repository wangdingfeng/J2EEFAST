/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.crypto.EnctryptTools;
import com.j2eefast.common.core.crypto.MyEncryptablePropertyDetector;
import com.j2eefast.common.core.utils.JasyptUtils;
import com.j2eefast.common.core.utils.ToolUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.springframework.beans.factory.annotation.Value;

/**
 * <p>yml 文件敏感信息处理类</p>
 *
 * @author: zhouzhou Emall:loveingowp@163.com
 * @date: 2019-03-24 20:45
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {

    @Value("${fast.encrypt.key: ''}")
    private String key;

    @Override
    public String resolvePropertyValue(String str) {

        if (ToolUtil.isNotEmpty(str)) {

            try{
                //对配置文件加密值进行解密。加密方式可以自定义
                if(StrUtil.startWith(str,MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_ENC,true)){
                    str = StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,
                            MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_ENC),"}");
                    if(ToolUtil.isEmpty(key)){
                        key = ConfigConstant.KEY;
                    }
                    str = JasyptUtils.decyptPwd(str,key);
                }

                if(StrUtil.startWith(str,MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_DES,true)){
                    str = StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,
                            MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_DES),"}");
                    if(ToolUtil.isEmpty(key)){
                        key = ConfigConstant.KEY;
                    }
                    str = EnctryptTools.DesDecode(str,key);
                }

                if(StrUtil.startWith(str,MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_SM4,true)){
                    str = StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,
                            MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT_SM4),"}");
                    if(ToolUtil.isEmpty(key)){
                        key = ConfigConstant.KEY;
                    }
                    str = EnctryptTools.SM4Decode(str,key);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return str;
    }



}
