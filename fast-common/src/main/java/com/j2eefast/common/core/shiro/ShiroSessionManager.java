package com.j2eefast.common.core.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * @author: wangsaichao/J2eeFAST
 * @description: 解决单次请求需要多次访问redis, 降低Session更新的频率
 */
@Slf4j
public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L * 60 * 3;

    /**
     * 获取session
     * 优化单次请求需要多次访问redis的问题
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        //尝试从request 获取
        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
        }
        //从缓存获取
        Session session = super.retrieveSession(sessionKey);
        if (request != null && null != sessionId) {
            //保存
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }

    /**
     * 重写touch()方法，降低Session更新的频率
     * @param key
     * @throws InvalidSessionException
     */
    @Override
    public void touch(SessionKey key) throws InvalidSessionException {
        Session session = doGetSession(key);
        if (session != null) {
            long oldTime = session.getLastAccessTime().getTime();
            //更新访问时间
            session.touch();
            long newTime = session.getLastAccessTime().getTime();
            // 如果两次访问的时间间隔大于3分钟，主动持久化Session
            if (newTime - oldTime > DEFAULT_SESSION_IN_MEMORY_TIMEOUT) {
                onChange(session);
            }
        }
    }
}