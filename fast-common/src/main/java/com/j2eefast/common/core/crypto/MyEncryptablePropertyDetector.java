/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.crypto;

import cn.hutool.core.util.StrUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;

/**
 * <p>分割截取配置文件加密信息</p>
 *
 * @author: zhouzhou Emall:loveingowp@163.com
 * @date: 2019-03-24 20:37
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
public class MyEncryptablePropertyDetector implements EncryptablePropertyDetector {

    public static final String ENCODED_PASSWORD_HINT_ENC = "ENC{";
    public static final String ENCODED_PASSWORD_HINT_DES = "DES{";
    public static final String ENCODED_PASSWORD_HINT_SM4 = "SM4{";

    //判断是否为预定的值
    @Override
    public boolean isEncrypted(String str) {
        if (null != str) {
            return StrUtil.startWith(str,ENCODED_PASSWORD_HINT_DES,true) ||
                    StrUtil.startWith(str,ENCODED_PASSWORD_HINT_SM4,true) ||
                    StrUtil.startWith(str,ENCODED_PASSWORD_HINT_ENC,true)
                    ;
        }
        return false;
    }

    //截取真正的加密串
    @Override
    public String unwrapEncryptedValue(String str) {
        if(StrUtil.startWith(str,ENCODED_PASSWORD_HINT_DES,true)){
            return StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,ENCODED_PASSWORD_HINT_DES),"}");
        }
        if(StrUtil.startWith(str,ENCODED_PASSWORD_HINT_SM4,true)){
            return StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,ENCODED_PASSWORD_HINT_SM4),"}");
        }
        if(StrUtil.startWith(str,ENCODED_PASSWORD_HINT_ENC,true)){
            return StrUtil.removeSuffixIgnoreCase(StrUtil.removePrefixIgnoreCase(str,ENCODED_PASSWORD_HINT_ENC),"}");
        }
        return str;
    }
}