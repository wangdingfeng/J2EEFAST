package com.j2eefast.common.core.redis;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.SpringUtil;
import org.apache.ibatis.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

/**
 * @author shuangyueliao
 * @create 2019/9/10 14:02
 * @Version 0.1
 */
@Slf4j
public class MybatisRedisCache implements Cache {


	// 读写锁
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

	private RedisUtil redisUtil;

	private String id;

	public MybatisRedisCache(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void putObject(Object key, Object value) {
		if (redisUtil == null) {
			//由于启动期间注入失败，只能运行期间注入，这段代码可以删除
			redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");
		}
		if (value != null) {
			redisUtil.hset(this.id.toString(),this.MD5Encrypt(key),value);
		}
	}

	@Override
	public Object getObject(Object key) {
		try {
			if (redisUtil == null) {
				//由于启动期间注入失败，只能运行期间注入，这段代码可以删除
				redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");
			}
			if (key != null) {
				return redisUtil.hget(this.id.toString(),this.MD5Encrypt(key));
			}
		} catch (Exception e) {
			log.error("缓存出错:",e);
		}
		return null;
	}

	@Override
	public Object removeObject(Object key) {
		if (key != null) {
			if (redisUtil == null) {
				//由于启动期间注入失败，只能运行期间注入，这段代码可以删除
				redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");
			}
			redisUtil.del(this.MD5Encrypt(key));
		}
		return null;
	}

	@Override
	public void clear() {
		log.debug("清空缓存");
		if (redisUtil == null) {
			//由于启动期间注入失败，只能运行期间注入，这段代码可以删除
			redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");
		}
		redisUtil.del(this.id.toString());
	}

	@Override
	public int getSize() {
		if (redisUtil == null) {
			//由于启动期间注入失败，只能运行期间注入，这段代码可以删除
			redisUtil = (RedisUtil) SpringUtil.getBean("redisUtil");
		}
		Long size = (Long) redisUtil.execute((RedisCallback<Long>) RedisServerCommands::dbSize);
		return size.intValue();
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return this.readWriteLock;
	}


	/**
	 * MD5加密存储key,以节约内存
	 */
	private String MD5Encrypt(Object key){
		String s = DigestUtils.md5DigestAsHex(key.toString().getBytes());
		return s;
	}
}
