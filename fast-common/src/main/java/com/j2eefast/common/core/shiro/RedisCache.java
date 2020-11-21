package com.j2eefast.common.core.shiro;

import com.j2eefast.common.core.exception.CacheManagerPrincipalIdNotAssignedException;
import com.j2eefast.common.core.exception.PrincipalIdNullException;
import com.j2eefast.common.core.exception.PrincipalInstanceException;
import com.j2eefast.common.core.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.data.redis.serializer.SerializationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class RedisCache<K, V> implements Cache<K, V> {


    private RedisUtil redisUtil;
    private String keyPrefix = RedisCacheManager.DEFAULT_CACHE_KEY_PREFIX;
    private int expire = RedisCacheManager.DEFAULT_EXPIRE;
    private String principalIdFieldName = RedisCacheManager.DEFAULT_PRINCIPAL_ID_FIELD_NAME;

    /**
     * @param redisUtil         redisUtil
     * @param prefix               authorization prefix
     * @param expire               expire
     * @param principalIdFieldName id field name of principal object
     */
    public RedisCache(RedisUtil redisUtil, String prefix, int expire, String principalIdFieldName) {
        if (redisUtil == null) {
            throw new IllegalArgumentException("redisManager cannot be null.");
        }
        this.redisUtil = redisUtil;
        if (prefix != null && !"".equals(prefix)) {
            this.keyPrefix = prefix;
        }
        if (expire != -1) {
            this.expire = expire;
        }
        if (principalIdFieldName != null) {
            this.principalIdFieldName = principalIdFieldName;
        }
    }

    /**
     * get shiro authorization redis key-value
     *
     * @param key key
     * @return value
     * @throws CacheException get cache exception
     */
    @Override
    public V get(K key) throws CacheException {
        log.debug("get key [" + key + "]");

        if (key == null) {
            return null;
        }

        try {
            String redisCacheKey = getRedisCacheKey(key);
            Object rawValue = redisUtil.getSession(redisCacheKey);
            if (rawValue == null) {
                return null;
            }
            @SuppressWarnings("unchecked")
			V value = (V) rawValue;
            return value;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (key == null) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }
        try {
            String redisCacheKey = getRedisCacheKey(key);
            log.debug("put key [" + redisCacheKey + "]");
            redisUtil.setSession(redisCacheKey, value != null ? value : null, expire);
            return value;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        log.debug("remove key [{}]",key);
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = getRedisCacheKey(key);
            Object rawValue = redisUtil.getSession(redisCacheKey);
            @SuppressWarnings("unchecked")
			V previous = (V) rawValue;
            redisUtil.delSession(redisCacheKey);
            return previous;
        } catch (SerializationException e) {
            throw new CacheException(e);
        }
    }

    private String getRedisCacheKey(K key) {
        if (key == null) {
            return null;
        }
        return this.keyPrefix + getStringRedisKey(key);
    }

    private String getStringRedisKey(K key) {
        String redisKey;
        if (key instanceof PrincipalCollection) {
            redisKey = getRedisKeyFromPrincipalIdField((PrincipalCollection) key);
        } else {
            redisKey = key.toString();
        }
        return redisKey;
    }

    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        Object principalObject = key.getPrimaryPrincipal();
        if (principalObject instanceof String) {
            return principalObject.toString();
        }
        Method pincipalIdGetter = getPrincipalIdGetter(principalObject);
        return getIdObj(principalObject, pincipalIdGetter);
    }

    private String getIdObj(Object principalObject, Method pincipalIdGetter) {
        String redisKey;
        try {
            Object idObj = pincipalIdGetter.invoke(principalObject);
            if (idObj == null) {
                throw new PrincipalIdNullException(principalObject.getClass(), this.principalIdFieldName);
            }
            redisKey = idObj.toString();
        } catch (IllegalAccessException e) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName, e);
        } catch (InvocationTargetException e) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName, e);
        }
        return redisKey;
    }

    private Method getPrincipalIdGetter(Object principalObject) {
        Method pincipalIdGetter = null;
        String principalIdMethodName = this.getPrincipalIdMethodName();
        try {
            pincipalIdGetter = principalObject.getClass().getMethod(principalIdMethodName);
        } catch (NoSuchMethodException e) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName);
        }
        return pincipalIdGetter;
    }

    private String getPrincipalIdMethodName() {
        if (this.principalIdFieldName == null || "".equals(this.principalIdFieldName)) {
            throw new CacheManagerPrincipalIdNotAssignedException();
        }
        return "get" + this.principalIdFieldName.substring(0, 1).toUpperCase() + this.principalIdFieldName.substring(1);
    }


    @Override
    public void clear() throws CacheException {
        log.debug("clear cache");
        Set<String> keys = null;
        try {
            keys = redisUtil.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            log.error("get keys error", e);
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (String key: keys) {
            redisUtil.delSession(key);
        }
    }

    /**
     * get all authorization key-value quantity
     *
     * @return key-value size
     */
    @Override
    public int size() {
        Long longSize = 0L;
        try {
            longSize = new Long(redisUtil.scanSize(this.keyPrefix + "*"));
        } catch (Exception e) {
            log.error("get keys error", e);
        }
        return longSize.intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        Set<String> keys = null;
        try {
            keys = redisUtil.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            log.error("get keys error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> convertedKeys = new HashSet<K>();
        for (String key:keys) {
            try {
                convertedKeys.add((K) key);
            } catch (Exception e) {
                log.error("deserialize keys error", e);
            }
        }
        return convertedKeys;
    }

    @SuppressWarnings("unchecked")
	@Override
    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisUtil.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            log.error("get values error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (String key : keys) {
            V value = null;
            try {
                value = (V) redisUtil.getSession(key);
            } catch (Exception e) {
                log.error("deserialize values= error", e);
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getPrincipalIdFieldName() {
        return principalIdFieldName;
    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
    }
}
