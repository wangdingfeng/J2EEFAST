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
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;
import java.util.*;

@Slf4j
public class RedisSessionDAO extends AbstractSessionDAO {

    private static final String DEFAULT_SESSION_KEY_PREFIX = "shiro:session:";
    private String keyPrefix = DEFAULT_SESSION_KEY_PREFIX;

    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;

    @Autowired
    private RedisUtil redisUtil;
    @Value("${fast.csrf.enabled: false}")
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

    // expire time in seconds
    private static final int DEFAULT_EXPIRE = -2;
    private static final int NO_EXPIRE = -1;

    /**
     * Please make sure expire is longer than sesion.getTimeout()
     */
    private int expire = DEFAULT_EXPIRE;

    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    private static ThreadLocal sessionsInThread = new ThreadLocal();

    @Override
    public void update(Session session) throws UnknownSessionException {
        //如果会话过期/停止 没必要再更新了
        try {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                return;
            }

            if (session instanceof ShiroSession) {
                // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                ShiroSession ss = (ShiroSession) session;
                if (!ss.isChanged()) {
                    return;
                }
                //如果没有返回 证明有调用 setAttribute往redis 放的时候永远设置为false
                ss.setChanged(false);
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
                if (session instanceof ShiroSession) {
                    // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                    ShiroSession ss = (ShiroSession) session;
                    if (!ss.isChanged()) {
                        break;
                    }
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
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
        String key = getRedisSessionKey(session.getId());
        if (expire == DEFAULT_EXPIRE) {
            this.redisUtil.setSession(key, session, (int) (session.getTimeout() / MILLISECONDS_IN_A_SECOND));
            if(csrfEnabled) {
                this.redisUtil.setSession("sys_csrfToken:" + key, Base64Encoder.encode(MD5.create().digestHex16(session.getId().toString())) + Base64Encoder.encode(session.getId().toString()), (int) (session.getTimeout() / MILLISECONDS_IN_A_SECOND));
            }
            return;
        }
        if (expire != NO_EXPIRE && expire * MILLISECONDS_IN_A_SECOND < session.getTimeout()) {
            log.warn("Redis session expire time: "
                    + (expire * MILLISECONDS_IN_A_SECOND)
                    + " is less than Session timeout: "
                    + session.getTimeout()
                    + " . It may cause some problems.");
        }
        this.redisUtil.setSession(key, session, expire);
        if(csrfEnabled) {
            this.redisUtil.setSession("sys_csrfToken:" + key, Base64Encoder.encode(MD5.create().digestHex16(session.getId().toString())) + Base64Encoder.encode(session.getId().toString()), expire);
        }
    }

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

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.warn("session id is null");
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

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public long getSessionInMemoryTimeout() {
        return sessionInMemoryTimeout;
    }

    public void setSessionInMemoryTimeout(long sessionInMemoryTimeout) {
        this.sessionInMemoryTimeout = sessionInMemoryTimeout;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public boolean getSessionInMemoryEnabled() {
        return sessionInMemoryEnabled;
    }

    public void setSessionInMemoryEnabled(boolean sessionInMemoryEnabled) {
        this.sessionInMemoryEnabled = sessionInMemoryEnabled;
    }

    public static ThreadLocal getSessionsInThread() {
        return sessionsInThread;
    }
}
