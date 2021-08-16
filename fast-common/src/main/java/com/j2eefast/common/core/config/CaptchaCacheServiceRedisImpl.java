/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import com.anji.captcha.service.CaptchaCacheService;
import com.google.auto.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 图形滑动验证码 使用redis缓存
 * @date 2020-10-07
 * @author huanzhou
 */
@AutoService(CaptchaCacheService.class)
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {


    /**
     * 缓存过期时间(秒)
     */
    @Value("#{ @environment['aj.captcha.cache-number'] ?: 1800 }")
    private long timingClear;

    @Override
    public String type() {
        return "redis";
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, timingClear, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
