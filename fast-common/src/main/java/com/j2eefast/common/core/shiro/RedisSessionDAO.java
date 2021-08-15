package com.j2eefast.common.core.shiro;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.MD5;
import com.j2eefast.common.core.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.serializer.SerializationException;
import java.io.Serializable;
import java.util.*;

/**
 * 实现shiro Session 共享存放 redis
 * @author huanzhou
 */
@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    private static final String DEFAULT_SESSION_KEY_PREFIX = "shiro:session:";
    private String keyPrefix = DEFAULT_SESSION_KEY_PREFIX;

    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;

    @Autowired
    @Lazy
    private RedisUtil redisUtil;
    @Value("#{ @environment['fast.csrf.enabled'] ?: false }")
    private boolean csrfEnabled;

    /**
     * doReadSession be called about 10 times when login.
     * Save Session in ThreadLocal to resolve this problem. sessionInMemoryTimeout is expiration of Session in ThreadLocal.
     * The default value is 1000 milliseconds (1s).
     * Most of time, you don't need to change it.
     */
    private long sessionInMemoryTimeout = DEFAULT_SESSION_IN_MEMORY_TIMEOUT;

    private static final boolean DEFAULT_SESSION_IN_MEMORY_ENABLED = true;

    private boolean sessionInMemoryEnabled = DEFAULT_SESSION_IN_MEMORY_ENABLED;


    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    @SuppressWarnings("rawtypes")
    private static ThreadLocal sessionsInThread = new ThreadLocal();

    /**
     * 更新到redis
     * @param session
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        //如果会话过期/停止 没必要再更新了
        try {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                return;
            }
            this.saveSession(session);
            if (this.sessionInMemoryEnabled) {
                this.setSessionToThreadLocal(session.getId(), session);
            }
        } catch (Exception e) {
            log.warn("update Session is failed", e);
        }
    }


    public boolean checkNumber(int num){
        if(num == -1){
            return true;
        }
        Collection<Session> list = getActiveSessions();
        int flag = 0;
        for (Session session : list) {
            try{
                if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                    break;
                }
                Subject s = new Subject.Builder().session(session).buildSubject();
                if(s.isAuthenticated()){
                    flag ++;
                }
            }catch (Exception e){
                log.warn("check session failure",e);
            }
        }
        return num > flag;
    }

    /**
     * save session
     * @param session
     * @throws UnknownSessionException
     */
    private void saveSession(Session session) throws UnknownSessionException {
        log.info("更新保存:{}",session.getId());
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
        String key = getRedisSessionKey(session.getId());
        this.redisUtil.setSession(key, session, (int) (session.getTimeout() / MILLISECONDS_IN_A_SECOND));
        if(csrfEnabled) {
            this.redisUtil.setSession("sys_csrfToken:" + key, Base64Encoder.encode(MD5.create().digestHex16(session.getId().toString())) + Base64Encoder.encode(session.getId().toString()), (int) (session.getTimeout() / MILLISECONDS_IN_A_SECOND));
        }
        return;
    }

    /**
     * 删除会话
     * @param session
     */
    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        try {
            redisUtil.delSession(getRedisSessionKey(session.getId()));
            if(csrfEnabled) {
                redisUtil.delSession("sys_csrfToken:" + getRedisSessionKey(session.getId()));
            }
        } catch (SerializationException e) {
            log.error("delete session error. session id= {}",session.getId());
        }
    }

    /**
     * 获取所有活跃的会话
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<Session>();
        try {
            Set<String> keys = redisUtil.scan(this.keyPrefix + "*");
            if (keys != null && keys.size() > 0) {
                for (String key:keys) {
                    Session s = (Session) redisUtil.getSession(key);
                    sessions.add(s);
                }
            }
        } catch (SerializationException e) {
            log.error("get active sessions error.");
        }
        return sessions;
    }

    public Long getActiveSessionsSize() {
        Long size = 0L;
        try {
            size = redisUtil.scanSize(this.keyPrefix + "*");
        } catch (Exception e) {
            log.error("get active sessions error.");
        }
        return size;
    }


    /**
     * 创建会话
     * @param session
     * @return
     */
    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            log.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    /**
     * 读取Session 会话
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.debug("doReadSession-sessionId:{}",sessionId);
        if (sessionId == null) {
            log.info("session id is null");
            return null;
        }
        if (this.sessionInMemoryEnabled) {
            Session session = getSessionFromThreadLocal(sessionId);
            if (session != null) {
                return session;
            }
        }
        Session session = null;
        log.debug("read session from redis");
        try {
            session = (Session) redisUtil.getSession(getRedisSessionKey(sessionId));
            if (this.sessionInMemoryEnabled) {
                setSessionToThreadLocal(sessionId, session);
            }
        } catch (SerializationException e) {
            log.error("read session error. settionId= {}",sessionId);
        }
        return session;
    }


    /*** 单次请求只服务器redis一次限制**/
    @SuppressWarnings("unchecked")
    private void setSessionToThreadLocal(Serializable sessionId, Session s) {
        Map<Serializable, SessionInMemory> sessionMap = (Map<Serializable, SessionInMemory>) sessionsInThread.get();
        if (sessionMap == null) {
            sessionMap = new HashMap<Serializable, SessionInMemory>();
            sessionsInThread.set(sessionMap);
        }
        removeExpiredSessionInMemory(sessionMap);
        SessionInMemory sessionInMemory = new SessionInMemory();
        sessionInMemory.setCreateTime(new Date());
        sessionInMemory.setSession(s);
        sessionMap.put(sessionId, sessionInMemory);
    }

    private void removeExpiredSessionInMemory(Map<Serializable, SessionInMemory> sessionMap) {
        Iterator<Serializable> it = sessionMap.keySet().iterator();
        while (it.hasNext()) {
            Serializable sessionId = it.next();
            SessionInMemory sessionInMemory = sessionMap.get(sessionId);
            if (sessionInMemory == null) {
                it.remove();
                continue;
            }
            long liveTime = getSessionInMemoryLiveTime(sessionInMemory);
            if (liveTime > sessionInMemoryTimeout) {
                it.remove();
            }
        }
    }

    private Session getSessionFromThreadLocal(Serializable sessionId) {
        if (sessionsInThread.get() == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<Serializable, SessionInMemory> sessionMap = (Map<Serializable, SessionInMemory>) sessionsInThread.get();
        SessionInMemory sessionInMemory = sessionMap.get(sessionId);
        if (sessionInMemory == null) {
            return null;
        }
        long liveTime = getSessionInMemoryLiveTime(sessionInMemory);
        if (liveTime > sessionInMemoryTimeout) {
            sessionMap.remove(sessionId);
            return null;
        }
        log.debug("read session from memory");
        return sessionInMemory.getSession();
    }

    private long getSessionInMemoryLiveTime(SessionInMemory sessionInMemory) {
        Date now = new Date();
        return now.getTime() - sessionInMemory.getCreateTime().getTime();
    }

    private String getRedisSessionKey(Serializable sessionId) {
        return this.keyPrefix + sessionId;
    }
}
